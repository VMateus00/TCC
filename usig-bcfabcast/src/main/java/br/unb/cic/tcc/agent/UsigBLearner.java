package br.unb.cic.tcc.agent;

import br.unb.cic.tcc.component.IUsig;
import br.unb.cic.tcc.component.UsigComponent;
import br.unb.cic.tcc.definitions.Constants;
import br.unb.cic.tcc.entity.Learner;
import br.unb.cic.tcc.messages.ClientMessage;
import br.unb.cic.tcc.messages.ProposerClientMessage;
import br.unb.cic.tcc.messages.ProtocolMessage;
import br.unb.cic.tcc.messages.ProtocolMessageType;
import br.unb.cic.tcc.messages.UsigBProtocolMessage;
import br.unb.cic.tcc.quorum.LearnerReplica;
import br.unb.cic.tcc.quorum.Quoruns;
import br.unb.cic.tcc.quorum.UsigLearnerReplica;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;

public class UsigBLearner extends Learner implements BAgent {

    private IUsig usigComponent = new UsigComponent();
    private final Integer[] contadorRespostasAgentes;

    public UsigBLearner(int id, String host, int port, Integer qtdAgentes, Map<String, Set<Integer>> agentsMap) {
        super(id, host, port, agentsMap);
        contadorRespostasAgentes = new Integer[qtdAgentes];

        for (int i=0; i<contadorRespostasAgentes.length;i++){
            contadorRespostasAgentes[i] = 1;
        }
    }

    @Override
    public synchronized void learn(ProtocolMessage protocolMessage) {
        UsigBProtocolMessage usigBProtocolMessage = (UsigBProtocolMessage) protocolMessage;
        if(!verifyMsg(usigBProtocolMessage)){
            return;
        }
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

        if (protocolMessage.getProtocolMessageType() == ProtocolMessageType.MESSAGE_2A
                && usigComponent.verifyUI(usigBProtocolMessage)
                && verifyCnt(usigBProtocolMessage.getAssinaturaUsig(), usigBProtocolMessage.getAgentSend()-1)) {
            protocolMessagesFromProposers.add(protocolMessage);
        } else if (protocolMessage.getProtocolMessageType() == ProtocolMessageType.MESSAGE_2B
                && usigComponent.verifyUI(usigBProtocolMessage)
                && verifyCnt(usigBProtocolMessage.getAssinaturaUsig(), usigBProtocolMessage.getAgentSend()-1)) {
            protocolMessagesFromAcceptors.add(protocolMessage);
        }

        if (protocolMessagesFromAcceptors.size() == QTD_MINIMA_RESPOSTAS_QUORUM_ACCEPTORS_USIG) {
            List<ProtocolMessage> msgWithNilValue = protocolMessagesFromProposers.stream()
                    .filter(p -> ((ProposerClientMessage) p.getMessage()).getClientMessage() == null)
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
//            Quoruns.liberaAtualizacaoRound(getAgentId(), learnedThisRound);
        }
    }

    public void updateCnt(UsigBProtocolMessage protocolMessage){
        verifyCnt(protocolMessage.getAssinaturaUsig(), protocolMessage.getAgentSend()-1);
    }

    @Override
    protected LearnerReplica defineLearnerReplica(int id, String host, int port) {
        return new UsigLearnerReplica(id, host, port, this);
    }

    private boolean verifyCnt(Integer valorRecebido, Integer agentId){
        if(valorRecebido.equals(contadorRespostasAgentes[agentId])){
            contadorRespostasAgentes[agentId] = contadorRespostasAgentes[agentId]+1;
            return true;
        }
        return false;
    }
}
