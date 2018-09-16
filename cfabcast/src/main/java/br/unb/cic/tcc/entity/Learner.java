package br.unb.cic.tcc.entity;

import br.unb.cic.tcc.definitions.Constants;
import br.unb.cic.tcc.messages.ClientMessage;
import br.unb.cic.tcc.messages.ProtocolMessage;
import br.unb.cic.tcc.messages.ProtocolMessageType;
import br.unb.cic.tcc.quorum.AgentSender;
import br.unb.cic.tcc.quorum.LearnerReplica;
import br.unb.cic.tcc.quorum.Quoruns;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;

public class Learner extends Agent<LearnerReplica, AgentSender> {

    protected Map<Integer, Set<ProtocolMessage>> messagesFromAcceptors = new ConcurrentHashMap<>();
    protected Map<Integer, Set<ProtocolMessage>> messagesFromProposers = new ConcurrentHashMap<>();

    public Learner(int id, String host, int port, Map<String, Set<Integer>> agentsMap) {
        AgentSender leanerSender = new AgentSender(id);
        LearnerReplica learnerReplica = new LearnerReplica(id, host, port, this);

        setAgentId(id);
        idAgentes = agentsMap;
        setQuorumSender(leanerSender);
        setQuorumReplica(learnerReplica);
    }

    public synchronized void learn(ProtocolMessage protocolMessage) {
        Set<ProtocolMessage> protocolMessagesFromAcceptors = messagesFromAcceptors.get(protocolMessage.getRound());
        Set<ProtocolMessage> protocolMessagesFromProposers = messagesFromProposers.get(protocolMessage.getRound()); // só quem envia sao os CF

        if (protocolMessagesFromAcceptors == null) {
            messagesFromAcceptors.put(protocolMessage.getRound(), new ConcurrentSkipListSet<>());
            protocolMessagesFromAcceptors = messagesFromAcceptors.get(protocolMessage.getRound());
        }
        if (protocolMessagesFromProposers == null) {
            messagesFromProposers.put(protocolMessage.getRound(), new ConcurrentSkipListSet<>());
            protocolMessagesFromProposers = messagesFromProposers.get(protocolMessage.getRound());
        }

        if (protocolMessage.getProtocolMessageType() == ProtocolMessageType.MESSAGE_2A) {
            protocolMessagesFromProposers.add(protocolMessage);
        } else if (protocolMessage.getProtocolMessageType() == ProtocolMessageType.MESSAGE_2B) {
            protocolMessagesFromAcceptors.add(protocolMessage);
        }

        if (protocolMessagesFromAcceptors.size() >= Quoruns.getAcceptors().size()) {
            // ACTIONS:

            List<ProtocolMessage> msgWithNilValue = protocolMessagesFromProposers.stream()
                    .filter(p -> ((Map<Constants, Object>) p.getMessage()).get(Constants.V_VAL) == null)
                    .collect(Collectors.toList());

            Map<Integer, Set<ClientMessage>> q2bVals = new HashMap<>();
            protocolMessagesFromAcceptors.forEach(protocolMsgAcceptor -> {
                Map<Integer, Set<ClientMessage>> message = (Map<Integer, Set<ClientMessage>>) protocolMsgAcceptor.getMessage();
                message.forEach(q2bVals::put);
            });

            Map<Integer, Set<ClientMessage>> w = new HashMap<>();
            msgWithNilValue.forEach(p -> {
                Map<Constants, Object> message = (Map<Constants, Object>) p.getMessage();
                w.put((Integer) message.get(Constants.AGENT_ID), null);
            });

            Map<Integer, Set<ClientMessage>> learnedThisRound = getLearnedThisRound(protocolMessage.getRound());

            q2bVals.forEach(learnedThisRound::put);
            w.forEach(learnedThisRound::put);

            System.out.println("Learner (" + getAgentId() + ") - aprendeu no round ("+protocolMessage.getRound()+"): " + learnedThisRound);
            Quoruns.liberaAtualizacaoRound(getAgentId(), learnedThisRound);
        }
    }

    protected Map<Integer, Set<ClientMessage>> getLearnedThisRound(Integer currentRound) {
        getvMap().putIfAbsent(currentRound, new HashMap<>());
        return getvMap().get(currentRound);
    }

    @Override
    public void limpaDadosExecucao() {
        messagesFromAcceptors = new ConcurrentHashMap<>();
        messagesFromProposers = new ConcurrentHashMap<>();
        setvMap(new HashMap<>());
    }
}
