package br.unb.cic.tcc.entity;

import br.unb.cic.tcc.definitions.Constants;
import br.unb.cic.tcc.messages.ClientMessage;
import br.unb.cic.tcc.messages.ProtocolMessage;
import br.unb.cic.tcc.messages.ProtocolMessageType;
import br.unb.cic.tcc.quorum.ProposerReplica;
import br.unb.cic.tcc.quorum.ProposerSender;
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

public class Proposer extends Agent<ProposerReplica, ProposerSender> {

    private int currentRound = 0;
    private Map<Integer, Object> currentValue = new HashMap<>(); // round, valorProposto
    private Boolean isColisionFastProposer = true; // TODO deixar aleatorio (verificar quantos sao necessarios ter)
    Map<Integer, Set<ProtocolMessage>> msgsRecebidas = new ConcurrentHashMap<>(); // round /msgs from acceptors (só o coordinator usa)

    public Proposer(int id, String host, int port) {
        ProposerSender proposerSender = new ProposerSender(id);
        ProposerReplica proposerReplica = new ProposerReplica(id, host, port, this, proposerSender);

        setAgentId(id);
        setQuorumReplica(proposerReplica);
        setQuorumSender(proposerSender);
    }

    // Phase 1A só é executada por coordinator
    public void phase1A() {
        if (currentRound < Quoruns.roundAtual) {
            currentRound = Quoruns.roundAtual;
            getvMap().put(currentRound, new HashMap<>());

            ProtocolMessage protocolMessage = new ProtocolMessage(ProtocolMessageType.MESSAGE_1A, currentRound, null);
            QuorumMessage quorumMessage = new QuorumMessage(MessageType.QUORUM_REQUEST, protocolMessage, getQuorumSender().getProcessId());
            getQuorumSender().sendTo(Quoruns.idAcceptors(), quorumMessage);
        }
    }

    public void phase2A(ProtocolMessage protocolMessage) {
        System.out.println("Proposer(" + getAgentId() + ") começou a fase 2A");
        // nao precisa verificar aqui se é CFproposer, pois quem chama verifica
        Map<Constants, Object> messageMap = (Map<Constants, Object>) protocolMessage.getMessage();

        Object valMsgRecebida = messageMap.get(Constants.V_VAL);
        boolean condicao1 = protocolMessage.getProtocolMessageType() == ProtocolMessageType.MESSAGE_PROPOSE && valMsgRecebida != null;
        boolean condicao2 = protocolMessage.getProtocolMessageType() == ProtocolMessageType.MESSAGE_2A && Quoruns.isCFProposerOnRound((Integer) messageMap.get(Constants.AGENT_ID), currentRound);

        if (currentRound == protocolMessage.getRound()
                && currentValue.get(currentRound) == null
                && (condicao1 || condicao2)) {

            currentValue.put(currentRound, valMsgRecebida);

            HashMap<Constants, Object> map = new HashMap<>();
            map.put(Constants.AGENT_ID, this.getAgentId());
            map.put(Constants.V_VAL, valMsgRecebida);

            ProtocolMessage responseMsg = new ProtocolMessage(ProtocolMessageType.MESSAGE_2A, protocolMessage.getRound(), map);
            QuorumMessage quorumMessage = new QuorumMessage(MessageType.QUORUM_REQUEST, responseMsg, getQuorumSender().getProcessId());

            if (valMsgRecebida != null) {
                getQuorumSender().sendTo(Quoruns.idAcceptorsAndCFProposers(currentRound), quorumMessage);
            } else {
                getQuorumSender().sendTo(Quoruns.idLeaners(), quorumMessage);
            }
        }
    }

    public void phase2Start(ProtocolMessage protocolMessage) {
        Set<ProtocolMessage> protocolMessages = msgsRecebidas.get(protocolMessage.getRound());
        if (protocolMessages == null) {
            protocolMessages = new ConcurrentSkipListSet<>();
            msgsRecebidas.put(protocolMessage.getRound(), protocolMessages);
        }
        protocolMessages.add(protocolMessage);

        if (currentRound == protocolMessage.getRound()
                && getMapFromRound(currentRound).isEmpty()
                && protocolMessages.size() == Quoruns.getAcceptors().size()) {

            int max = protocolMessages.stream()
                    .map(p -> (Map<Constants, Object>) p.getMessage())
                    .mapToInt(p -> (Integer) p.get(Constants.V_RND))
                    .max().getAsInt();

            List<Map<Integer, Set<ClientMessage>>> s = protocolMessages.stream()
                    .map(p -> (Map<Constants, Object>) p.getMessage())
                    .filter(p -> p.get(Constants.V_RND).equals(max))
                    .map(p -> (Map<Integer, Set<ClientMessage>>) p.get(Constants.V_VAL))
                    .collect(Collectors.toList());

            int[] agentsToSendMsg;
            if (s.isEmpty()) {
                getvMap().put(currentRound, new ConcurrentHashMap<>()); // deixa vazio nesse caso
                agentsToSendMsg = Quoruns.idProposers();
            } else {
                s.forEach((map)->
                        map.forEach((k,v)->
                                getMapFromRound(currentRound).put(k,v)));

                Quoruns.getProposers().forEach(proposer->
                        getMapFromRound(currentRound).putIfAbsent(proposer.getAgentId(), new ConcurrentSkipListSet<>()));
                agentsToSendMsg = Quoruns.idAcceptorsAndProposers();
            }
            ProtocolMessage msgToSend = new ProtocolMessage(ProtocolMessageType.MESSAGE_2S, currentRound, getMapFromRound(currentRound));
            QuorumMessage quorumMessage = new QuorumMessage(MessageType.QUORUM_REQUEST, msgToSend, getQuorumSender().getProcessId());
            getQuorumSender().sendTo(agentsToSendMsg, quorumMessage);
        }
    }

    public void phase2Prepare(int round, Map<String, Object> vMapCoordinator) {
        // TODO após concluir parte 1
    }

    public void propose(ClientMessage clientMessage) {
        HashMap<Constants, Object> map = new HashMap<>();
        map.put(Constants.V_VAL, clientMessage);
        map.put(Constants.AGENT_ID, this.getAgentId());

        ProtocolMessage protocolMessage = new ProtocolMessage(ProtocolMessageType.MESSAGE_PROPOSE, currentRound, map);
        QuorumMessage quorumMessage = new QuorumMessage(MessageType.QUORUM_REQUEST, protocolMessage, getQuorumSender().getProcessId());
        getQuorumSender().sendTo(Quoruns.idCFProposers(currentRound), quorumMessage);

        System.out.println("Proposer (" + getAgentId() + ") enviou uma proposta para os CFProposers");
    }

    public boolean isCoordinator() {
        return getAgentId() == 1;
    }

    public Boolean isColisionFastProposer() {
        return isColisionFastProposer;
    }

    private Map<Integer, Set<ClientMessage>> getMapFromRound(Integer round) {
        Map<Integer, Set<ClientMessage>> mapOfRound = getvMap().get(round);
        if (mapOfRound == null) {
            mapOfRound = new ConcurrentHashMap<>();
            getvMap().put(round, mapOfRound);
        }
        return mapOfRound;
    }
}
