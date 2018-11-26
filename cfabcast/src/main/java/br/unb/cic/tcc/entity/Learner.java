package br.unb.cic.tcc.entity;

import br.unb.cic.tcc.definitions.Constants;
import br.unb.cic.tcc.definitions.CurrentInstanceLearner;
import br.unb.cic.tcc.main.AgentMapUtil;
import br.unb.cic.tcc.main.Initializer;
import br.unb.cic.tcc.messages.ClientMessage;
import br.unb.cic.tcc.messages.ProtocolMessage;
import br.unb.cic.tcc.messages.ProtocolMessageType;
import br.unb.cic.tcc.quorum.AgentSender;
import br.unb.cic.tcc.quorum.LearnerReplica;
import br.unb.cic.tcc.quorum.Quoruns;
import quorum.communication.MessageType;
import quorum.communication.QuorumMessage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;

public class Learner extends Agent<LearnerReplica, AgentSender> {

    protected Map<Integer, Set<ProtocolMessage>> messagesFromAcceptors = new ConcurrentHashMap<>();
    protected Map<Integer, Set<ProtocolMessage>> messagesFromProposers = new ConcurrentHashMap<>();

    protected HashSet<CurrentInstanceLearner> instancias = new HashSet<>();

    private Integer teste = 0;

    public Learner(int id, String host, int port, Map<String, Set<Integer>> agentsMap) {
        AgentSender leanerSender = new AgentSender(id);
        LearnerReplica learnerReplica = defineLearnerReplica(id, host, port);

        setAgentId(id);
        idAgentes = agentsMap;
        setQuorumSender(leanerSender);
        setQuorumReplica(learnerReplica);
    }

    protected LearnerReplica defineLearnerReplica(int id, String host, int port) {
        return new LearnerReplica(id, host, port, this);
    }

    public synchronized void learn(ProtocolMessage protocolMessage) {
        CurrentInstanceLearner currentInstance = getCurrentInstance(protocolMessage.getInstanciaExecucao());
        currentInstance.atualizaMessagensRecebidasBaseadoNoRound(protocolMessage.getRound());

        Set<ProtocolMessage> protocolMessagesFromAcceptors = currentInstance.messagesFromAcceptorsOnRound(protocolMessage.getRound());
        Set<ProtocolMessage> protocolMessagesFromProposers = currentInstance.messagesFromProposersOnRound(protocolMessage.getRound());

        if (protocolMessage.getProtocolMessageType() == ProtocolMessageType.MESSAGE_2A) {
            protocolMessagesFromProposers.add(protocolMessage);
        } else if (protocolMessage.getProtocolMessageType() == ProtocolMessageType.MESSAGE_2B) {
            protocolMessagesFromAcceptors.add(protocolMessage);
        }

        if(protocolMessagesFromAcceptors.size() >= QTD_MINIMA_RESPOSTAS_QUORUM_ACCEPTORS_CRASH && !currentInstance.getEnviouResultado()){
            currentInstance.setEnviouResultado(Boolean.TRUE);
//        }
//
//        if (protocolMessagesFromAcceptors.size() >= Quoruns.getAcceptors().size()) {
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

            ProtocolMessage learnedMsg = new ProtocolMessage(ProtocolMessageType.MESSAGE_LEARNED, protocolMessage.getRound(),
                    getAgentId(), protocolMessage.getInstanciaExecucao(), learnedThisRound);

            learnedThisRound.forEach((k,v)->
                teste = v.stream().map(ClientMessage::getIdClient).collect(Collectors.toList()).get(0));

//            Integer[] protocol = getIdAgentes().get(Initializer.CLIENTS).stream().toArray(Integer[]::new);
            int[] clientId = {teste};
            getQuorumSender().sendTo(clientId,
                    new QuorumMessage(MessageType.QUORUM_REQUEST, learnedMsg, getAgentId()));

            System.out.println("Learner (" + getAgentId() + ") - aprendeu na instancia ("+currentInstance.getInstanciaAtual()+"): " + learnedThisRound);
        }
    }

    protected CurrentInstanceLearner getCurrentInstance(Integer instanciaAtual){
        Optional<CurrentInstanceLearner> first = instancias.stream()
                .filter(p -> instanciaAtual.equals(p.getInstanciaAtual()))
                .findFirst();

        if(first.isPresent()){
            return first.get();
        } else {
            CurrentInstanceLearner currentInstanceLearner = new CurrentInstanceLearner(instanciaAtual);
            instancias.add(currentInstanceLearner);

            return currentInstanceLearner;
        }
    }

    protected Map<Integer, Set<ClientMessage>> getLearnedThisRound(Integer currentRound) {
        getvMap().putIfAbsent(currentRound, new HashMap<>());
        return getvMap().get(currentRound);
    }

}
