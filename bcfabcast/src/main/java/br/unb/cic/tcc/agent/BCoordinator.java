package br.unb.cic.tcc.agent;

import br.unb.cic.tcc.entity.Coordinator;
import br.unb.cic.tcc.messages.BProtocolMessage;
import br.unb.cic.tcc.messages.ClientMessage;
import br.unb.cic.tcc.messages.Message1B;
import br.unb.cic.tcc.messages.ProtocolMessage;
import br.unb.cic.tcc.messages.ProtocolMessageType;
import br.unb.cic.tcc.quorum.Quoruns;
import quorum.communication.MessageType;
import quorum.communication.QuorumMessage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;

public class BCoordinator extends Coordinator {
    public BCoordinator(int id, String host, int port) {
        super(id, host, port);
    }

    @Override
    public void phase1A() { // Copia do metodo da superClasse, exceto pela assinatura
        Quoruns.atualizaRound();
        if (currentRound < Quoruns.getRoundAtual()) {
            currentRound = Quoruns.getRoundAtual();
            getvMap().put(currentRound, new HashMap<>());

            BProtocolMessage protocolMessage = new BProtocolMessage(ProtocolMessageType.MESSAGE_1A, currentRound, getAgentId(), null);
            QuorumMessage quorumMessage = new QuorumMessage(MessageType.QUORUM_REQUEST, protocolMessage, getQuorumSender().getProcessId());
            getQuorumSender().sendTo(Quoruns.idAcceptors(), quorumMessage);
        }
    }

    @Override
    public synchronized void phase2Start(ProtocolMessage protocolMessage) {
        System.out.println("Coordinator começou a fase 2 - Start");

        Set<ProtocolMessage> protocolMessages = msgsRecebidas.get(protocolMessage.getRound());
        if (protocolMessages == null) {
            protocolMessages = new ConcurrentSkipListSet<>();
            msgsRecebidas.put(protocolMessage.getRound(), protocolMessages);
        }
        protocolMessages.add(protocolMessage);

        if (currentRound == protocolMessage.getRound()
                && getMapFromRound(currentRound).isEmpty()
                && protocolMessages.size() == Quoruns.getAcceptors().size()) {

            int kMax = protocolMessages.stream()
                    .map(p -> (Message1B) p.getMessage())
                    .mapToInt(Message1B::getRoundAceitouUltimaVez)
                    .max().getAsInt();

            List<Map<Integer, Set<ClientMessage>>> s = protocolMessages.stream()
                    .map(p -> (Message1B) p.getMessage())
                    .filter(p -> p.getRoundAceitouUltimaVez().equals(kMax))
                    .map(Message1B::getvMapLastRound)
                    .collect(Collectors.toList());

            int[] agentsToSendMsg;
            if (s.isEmpty()) {
                getvMap().put(currentRound, new ConcurrentHashMap<>()); // deixa vazio nesse caso
                agentsToSendMsg = Quoruns.idCFProposers(currentRound);
            } else {
                if (s.size() < Quoruns.TAMANHO_MINIMO_QUORUM_ACCEPTORS_BIZANTINO) {
                    return; // Só pode executar se tiver tamanho minimo;
                }
                s.forEach((map) ->
                        map.forEach((k, v) -> getMapFromRound(currentRound).put(k, v)));

                Quoruns.getCFProposersOnRound(currentRound).forEach(proposer ->
                        getMapFromRound(currentRound).putIfAbsent(proposer.getAgentId(), new ConcurrentSkipListSet<>()));
                agentsToSendMsg = Quoruns.idAcceptorsAndCFProposers(currentRound);
            }
            BProtocolMessage msgToSend = new BProtocolMessage(ProtocolMessageType.MESSAGE_2S, currentRound, getAgentId(), getAgentId(), getMapFromRound(currentRound), protocolMessages);

            QuorumMessage quorumMessage = new QuorumMessage(MessageType.QUORUM_REQUEST, msgToSend, getQuorumSender().getProcessId());
            getQuorumSender().sendTo(agentsToSendMsg, quorumMessage);
        }
    }
}
