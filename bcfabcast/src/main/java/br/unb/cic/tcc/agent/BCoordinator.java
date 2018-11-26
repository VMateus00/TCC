package br.unb.cic.tcc.agent;

import br.unb.cic.tcc.entity.Coordinator;
import br.unb.cic.tcc.messages.BProtocolMessage;
import br.unb.cic.tcc.messages.ClientMessage;
import br.unb.cic.tcc.messages.Message1B;
import br.unb.cic.tcc.messages.ProtocolMessage;
import br.unb.cic.tcc.messages.ProtocolMessageType;
import br.unb.cic.tcc.util.RsaUtil;
import quorum.communication.MessageType;
import quorum.communication.QuorumMessage;

import java.security.KeyPair;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;

public class BCoordinator extends Coordinator implements BAgent {
    protected final KeyPair keyPair;

    public BCoordinator(int id, String host, int port, Map<String, Set<Integer>> agentsMap) {
        super(id, host, port, agentsMap);
        keyPair = RsaUtil.generateKeyPair();
    }

    @Override
    public void phase1A() { // Copia do metodo da superClasse, exceto pela assinatura
        if (currentRound < getRoundAtual()) {
            currentRound = getRoundAtual();
            getvMap().put(currentRound, new HashMap<>());

            ProtocolMessageType messageType = ProtocolMessageType.MESSAGE_1A;
            BProtocolMessage protocolMessage = createAssignedMessage(new ProtocolMessage(messageType, currentRound, getAgentId(), null), null, keyPair);
            QuorumMessage quorumMessage = new QuorumMessage(MessageType.QUORUM_REQUEST, protocolMessage, getQuorumSender().getProcessId());
            getQuorumSender().sendTo(idAcceptors(), quorumMessage);
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
                && protocolMessages.size() == QTD_MINIMA_RESPOSTAS_QUORUM_ACCEPTORS_BIZANTINO) {

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
                agentsToSendMsg = idCFProposers(getAgentId());
            } else {
                if (s.size() < QTD_MINIMA_RESPOSTAS_QUORUM_ACCEPTORS_BIZANTINO) {
                    return; // Só pode executar se tiver tamanho minimo;
                }
                s.forEach((map) ->
                        map.forEach((k, v) -> getMapFromRound(currentRound).put(k, v)));

                for (Integer idCFProposer : idCFProposers(getAgentId())) {
                    getMapFromRound(currentRound).putIfAbsent(idCFProposer, new ConcurrentSkipListSet<>());
                }
                agentsToSendMsg = idAcceptorsAndCFProposers(getAgentId());
            }
            ProtocolMessageType messageType = ProtocolMessageType.MESSAGE_2S;
            BProtocolMessage msgToSend = createAssignedMessage(new ProtocolMessage(messageType, currentRound, getAgentId(), getMapFromRound(currentRound)), protocolMessages, keyPair);

            QuorumMessage quorumMessage = new QuorumMessage(MessageType.QUORUM_REQUEST, msgToSend, getQuorumSender().getProcessId());
            getQuorumSender().sendTo(agentsToSendMsg, quorumMessage);
        }
    }
}
