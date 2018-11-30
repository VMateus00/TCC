package br.unb.cic.tcc.agent;

import br.unb.cic.tcc.definitions.CurrentInstanceLearner;
import br.unb.cic.tcc.entity.Learner;
import br.unb.cic.tcc.messages.BProtocolMessage;
import br.unb.cic.tcc.messages.ClientMessage;
import br.unb.cic.tcc.messages.ProtocolMessage;
import br.unb.cic.tcc.messages.ProtocolMessageType;
import quorum.communication.MessageType;
import quorum.communication.QuorumMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class BLearner extends Learner implements BAgent {

    public BLearner(int id, String host, int port, Map<String, Set<Integer>> agentsMap) {
        super(id, host, port, agentsMap);
    }

    @Override
    public synchronized void learn(ProtocolMessage protocolMessage) {
        if(!verifyMsg((BProtocolMessage) protocolMessage)){
            return;
        }
        CurrentInstanceLearner currentInstance = getCurrentInstance(protocolMessage.getInstanciaExecucao());
        Set<ProtocolMessage> protocolMessagesFromAcceptors = currentInstance.messagesFromAcceptorsOnRound(protocolMessage.getRound());
        protocolMessagesFromAcceptors.add(protocolMessage);

        if (protocolMessagesFromAcceptors.size() >= QTD_MINIMA_RESPOSTAS_QUORUM_ACCEPTORS_BIZANTINO
                && !currentInstance.getEnviouResultado()) {
            currentInstance.setEnviouResultado(Boolean.TRUE);
            // ACTIONS:

            Map<Integer, Set<ClientMessage>> q2bVals = new HashMap<>();
            protocolMessagesFromAcceptors.forEach(protocolMsgAcceptor -> {
                Map<Integer, Set<ClientMessage>> message = (Map<Integer, Set<ClientMessage>>) protocolMsgAcceptor.getMessage();
                message.forEach(q2bVals::put);
            });

            Map<Integer, Set<ClientMessage>> learnedThisRound = getLearnedOnInstance(currentInstance, protocolMessage.getRound());
            q2bVals.forEach(learnedThisRound::put);

            ProtocolMessage learnedMsg = new ProtocolMessage(ProtocolMessageType.MESSAGE_LEARNED, protocolMessage.getRound(),
                    getAgentId(), protocolMessage.getInstanciaExecucao(), getMessagemAprendidaNaInstancia(learnedThisRound));

            learnedThisRound.forEach((k,v)->
                    currentInstance.setIdCliente(v.stream().map(ClientMessage::getIdClient).collect(Collectors.toList()).get(0)));

            int[] clientId = {currentInstance.getIdCliente()};
            getQuorumSender().sendTo(clientId, new QuorumMessage(MessageType.QUORUM_REQUEST, learnedMsg, getAgentId()));

//            System.out.println("Learner (" + getAgentId() + ") - aprendeu no round ("+protocolMessage.getRound()+"): " + learnedThisRound);
        }
    }
}
