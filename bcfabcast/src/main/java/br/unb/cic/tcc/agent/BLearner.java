package br.unb.cic.tcc.agent;

import br.unb.cic.tcc.entity.Learner;
import br.unb.cic.tcc.messages.ClientMessage;
import br.unb.cic.tcc.messages.ProtocolMessage;
import br.unb.cic.tcc.quorum.Quoruns;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

public class BLearner extends Learner {

    private Map<Integer, Set<ProtocolMessage>> messagesFromAcceptors = new ConcurrentHashMap<>();

    public BLearner(int id, String host, int port, Map<String, Set<Integer>> agentsMap) {
        super(id, host, port, agentsMap);
    }

    @Override
    public synchronized void learn(ProtocolMessage protocolMessage) {
        Set<ProtocolMessage> protocolMessagesFromAcceptors = messagesFromAcceptors.get(protocolMessage.getRound());

        if (protocolMessagesFromAcceptors == null) {
            messagesFromAcceptors.put(protocolMessage.getRound(), new ConcurrentSkipListSet<>());
            protocolMessagesFromAcceptors = messagesFromAcceptors.get(protocolMessage.getRound());
        }

        protocolMessagesFromAcceptors.add(protocolMessage);

        if (protocolMessagesFromAcceptors.size() >= QTD_MINIMA_RESPOSTAS_QUORUM_ACCEPTORS_BIZANTINO) {
            // ACTIONS:

            Map<Integer, Set<ClientMessage>> q2bVals = new HashMap<>();
            protocolMessagesFromAcceptors.forEach(protocolMsgAcceptor -> {
                Map<Integer, Set<ClientMessage>> message = (Map<Integer, Set<ClientMessage>>) protocolMsgAcceptor.getMessage();
                message.forEach(q2bVals::put);
            });

            Map<Integer, Set<ClientMessage>> learnedThisRound = getLearnedThisRound(protocolMessage.getRound());

            q2bVals.forEach(learnedThisRound::put);

            System.out.println("Learner (" + getAgentId() + ") - aprendeu no round ("+protocolMessage.getRound()+"): " + learnedThisRound);
            Quoruns.liberaAtualizacaoRound(getAgentId(), learnedThisRound);
        }
    }
}
