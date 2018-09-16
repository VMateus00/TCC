package br.unb.cic.tcc.agent;

import br.unb.cic.tcc.component.IUsig;
import br.unb.cic.tcc.component.UsigComponent;
import br.unb.cic.tcc.messages.BProtocolMessage;
import br.unb.cic.tcc.messages.ClientMessage;
import br.unb.cic.tcc.messages.Message1B;
import br.unb.cic.tcc.messages.ProtocolMessage;
import br.unb.cic.tcc.messages.ProtocolMessageType;
import br.unb.cic.tcc.messages.UsigBProtocolMessage;
import br.unb.cic.tcc.quorum.Quoruns;
import quorum.communication.MessageType;
import quorum.communication.QuorumMessage;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;

public class UsigBCoordinator extends BCoordinator {

    private final IUsig usigComponent = new UsigComponent();
    private final Integer[] contadorRespostasAgentes;

    public UsigBCoordinator(int id, String host, int port, Integer qtdAgentes) {
        super(id, host, port);
        contadorRespostasAgentes = new Integer[qtdAgentes+1];
    }

    @Override
    public void phase1A() {
        super.phase1A(); // Copia do metodo da superClasse
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
                && protocolMessages.size() == Quoruns.QTD_MINIMA_RESPOSTAS_QUORUM_ACCEPTORS_USIG) {

            int kMax = protocolMessages.stream()
                    .map(p -> (Message1B) p.getMessage())
                    .mapToInt(Message1B::getRoundAceitouUltimaVez)
                    .max().getAsInt();

            List<Map<Integer, Set<ClientMessage>>> s = protocolMessages.stream()
                    .map(p -> (Message1B) p.getMessage())
                    .filter(p -> p.getRoundAceitouUltimaVez().equals(kMax))
                    .map(Message1B::getvMapLastRound)
                    .collect(Collectors.toList());

            if (s.isEmpty()) {
                getvMap().put(currentRound, new ConcurrentHashMap<>()); // deixa vazio nesse caso
            } else {
                s.forEach((map) ->
                        map.forEach((k, v) -> getMapFromRound(currentRound).put(k, v)));

                Quoruns.getCFProposersOnRound(currentRound).forEach(proposer ->
                        getMapFromRound(currentRound).putIfAbsent(proposer.getAgentId(), new ConcurrentSkipListSet<>()));
            }

            ProtocolMessageType msgType = ProtocolMessageType.MESSAGE_2S;
            UsigBProtocolMessage msgToSend = usigComponent.createUI(new BProtocolMessage(
                    msgType, currentRound, getAgentId(), encrypt(msgType, keyPair.getPrivate()), keyPair.getPublic(), getMapFromRound(currentRound), protocolMessages));

            QuorumMessage quorumMessage = new QuorumMessage(MessageType.QUORUM_REQUEST, msgToSend, getQuorumSender().getProcessId());
            getQuorumSender().sendTo(Quoruns.idAcceptorsAndCFProposers(currentRound), quorumMessage);
        }
    }

    private boolean verifyCnt(Integer valorRecebido, Integer agentId){
        if(valorRecebido.equals(contadorRespostasAgentes[agentId])){
            contadorRespostasAgentes[agentId] = contadorRespostasAgentes[agentId]++;
            return true;
        }
        return false;
    }
}
