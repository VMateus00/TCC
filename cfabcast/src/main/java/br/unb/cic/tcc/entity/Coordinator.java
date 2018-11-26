package br.unb.cic.tcc.entity;

import br.unb.cic.tcc.messages.ClientMessage;
import br.unb.cic.tcc.messages.Message1B;
import br.unb.cic.tcc.messages.ProtocolMessage;
import br.unb.cic.tcc.messages.ProtocolMessageType;
import br.unb.cic.tcc.quorum.AgentSender;
import br.unb.cic.tcc.quorum.CoordinatorReplica;
import quorum.communication.MessageType;
import quorum.communication.QuorumMessage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;

public class Coordinator extends Agent<CoordinatorReplica, AgentSender> {

    protected int currentRound = 0;
    protected Map<Integer, Set<ProtocolMessage>> msgsRecebidas = new ConcurrentHashMap<>(); // round /msgs from acceptors (só o coordinator usa)

    public Coordinator(int id, String host, int port, Map<String, Set<Integer>> agentsMap) {
        AgentSender proposerSender = new AgentSender(id);
        CoordinatorReplica proposerReplica = new CoordinatorReplica(id, host, port, this);

        setAgentId(id);
        idAgentes = agentsMap;
        setQuorumReplica(proposerReplica);
        setQuorumSender(proposerSender);
    }

    public void phase1A() {
        if (currentRound < getRoundAtual()) {
            currentRound = getRoundAtual();
            getvMap().put(currentRound, new HashMap<>());

            ProtocolMessage protocolMessage = new ProtocolMessage(ProtocolMessageType.MESSAGE_1A, currentRound, getAgentId(), null);
            QuorumMessage quorumMessage = new QuorumMessage(MessageType.QUORUM_REQUEST, protocolMessage, getQuorumSender().getProcessId());
            getQuorumSender().sendTo(idAcceptors(), quorumMessage);
        }
    }

    public synchronized void phase2Start(ProtocolMessage protocolMessage) {
        System.out.println("Coordinator começou a fase 2Start");
        Set<ProtocolMessage> protocolMessages = msgsRecebidas.get(protocolMessage.getRound());
        if (protocolMessages == null) {
            protocolMessages = new ConcurrentSkipListSet<>();
            msgsRecebidas.put(protocolMessage.getRound(), protocolMessages);
        }
        protocolMessages.add(protocolMessage);

        if (currentRound == protocolMessage.getRound()
                && getMapFromRound(currentRound).isEmpty()
                && protocolMessages.size() == QTD_MINIMA_RESPOSTAS_QUORUM_ACCEPTORS_CRASH) {

            int max = protocolMessages.stream()
                    .map(p -> (Message1B) p.getMessage())
                    .mapToInt(Message1B::getRoundAceitouUltimaVez)
                    .max().getAsInt();

            List<Map<Integer, Set<ClientMessage>>> s = protocolMessages.stream()
                    .map(p -> (Message1B) p.getMessage())
                    .filter(p -> p.getRoundAceitouUltimaVez().equals(max))
                    .map(Message1B::getvMapLastRound)
                    .collect(Collectors.toList());

            int[] agentsToSendMsg;
            if (s.isEmpty()) {
                getvMap().put(currentRound, null); // deixa vazio nesse caso
                agentsToSendMsg = idProposers();
            } else {
                s.forEach((map) ->
                        map.forEach((k, v) ->
                                getMapFromRound(currentRound).put(k, v))); // TODO verificar caso onde isso entra de novo

                for (int idProposer: idProposers()){
                    getMapFromRound(currentRound).putIfAbsent(idProposer, null);
                }
                agentsToSendMsg = idAcceptorsAndProposers();
            }
            ProtocolMessage msgToSend = new ProtocolMessage(ProtocolMessageType.MESSAGE_2S, currentRound, getAgentId(), getMapFromRound(currentRound));
            QuorumMessage quorumMessage = new QuorumMessage(MessageType.QUORUM_REQUEST, msgToSend, getQuorumSender().getProcessId());
            getQuorumSender().sendTo(agentsToSendMsg, quorumMessage);
        }
    }
}
