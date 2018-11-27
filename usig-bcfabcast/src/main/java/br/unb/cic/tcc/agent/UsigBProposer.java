package br.unb.cic.tcc.agent;

import br.unb.cic.tcc.component.IUsig;
import br.unb.cic.tcc.component.UsigComponent;
import br.unb.cic.tcc.definitions.Constants;
import br.unb.cic.tcc.defintions.CurrentInstanceBProposer;
import br.unb.cic.tcc.messages.BProtocolMessage;
import br.unb.cic.tcc.messages.ClientMessage;
import br.unb.cic.tcc.messages.Message1B;
import br.unb.cic.tcc.messages.ProposerClientMessage;
import br.unb.cic.tcc.messages.ProtocolMessage;
import br.unb.cic.tcc.messages.ProtocolMessageType;
import br.unb.cic.tcc.messages.UsigBProtocolMessage;
import quorum.communication.MessageType;
import quorum.communication.QuorumMessage;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class UsigBProposer extends BProposer{

    private final IUsig usigComponent = new UsigComponent();
    private final Integer[] contadorRespostasAgentes;

    public UsigBProposer(int id, String host, int port, Integer qtdAgentes, Map<String, Set<Integer>> agentsMap) {
        super(id, host, port, agentsMap);
        contadorRespostasAgentes = new Integer[qtdAgentes];
        this.currentRound = 1;

        for (int i=0; i<contadorRespostasAgentes.length;i++){
            contadorRespostasAgentes[i] = 1;
        }
    }


    @Override
    public void phase2A(ProtocolMessage protocolMessage) {
        CurrentInstanceBProposer instanciaAtual =
                getInstanciaAtual(protocolMessage.getInstanciaExecucao());
//        System.out.println("Proposer(" + getAgentId() + ") começou a fase 2A");

        boolean condicao1 = protocolMessage.getProtocolMessageType() == ProtocolMessageType.MESSAGE_PROPOSE && protocolMessage.getMessage() != null;
        boolean condicao2 = protocolMessage.getProtocolMessageType() == ProtocolMessageType.MESSAGE_2A
                && usigComponent.verifyUI((UsigBProtocolMessage) protocolMessage)
                && verifyCnt(((UsigBProtocolMessage) protocolMessage).getAssinaturaUsig(), (protocolMessage).getAgentSend()-1)
                && isColisionFastProposer(protocolMessage.getAgentSend());

        if (verifyMsg((BProtocolMessage) protocolMessage)
                && instanciaAtual.getRound().equals(protocolMessage.getRound())
                && instanciaAtual.getProposedValueOnRound(protocolMessage.getRound()) == null
                && (condicao1 || condicao2)) {

            instanciaAtual.getProposedValuesOnRound().put(protocolMessage.getRound(), protocolMessage.getMessage());

            ProposerClientMessage valResponseMsg;
            if (protocolMessage.getMessage() instanceof ClientMessage) {
                valResponseMsg = new ProposerClientMessage(getAgentId(), (ClientMessage) protocolMessage.getMessage());
            } else if (protocolMessage.getMessage() instanceof ProposerClientMessage) {
                valResponseMsg = (ProposerClientMessage) protocolMessage.getMessage();
            } else {
                System.out.println("Erro");
                return;
            }

            ProtocolMessageType msgType = ProtocolMessageType.MESSAGE_2A;
            UsigBProtocolMessage responseMsg = usigComponent.createUI(createAssignedMessage(new ProtocolMessage(
                    msgType, protocolMessage.getRound(), getAgentId(), instanciaAtual.getInstanciaAtual(), valResponseMsg), null, keyPair));
            QuorumMessage quorumMessage = new QuorumMessage(MessageType.QUORUM_REQUEST, responseMsg, getQuorumSender().getProcessId());

            getQuorumSender().sendTo(idAccetprosAndLearnersAndCFProposers(getAgentId()), quorumMessage);
        }
    }

    @Override
    public void phase2Prepare(ProtocolMessage protocolMessage) {
        CurrentInstanceBProposer instanciaAtual = getInstanciaAtual(protocolMessage.getInstanciaExecucao());
        UsigBProtocolMessage usigBProtocolMessage = (UsigBProtocolMessage) protocolMessage;
//        System.out.println("Proposer(" + getAgentId() + ") começou a fase 2Prepare");
        if (verifyMsg(usigBProtocolMessage)
                && instanciaAtual.getRound() < protocolMessage.getRound()
                && goodRoundValue(usigBProtocolMessage.getProofs())
                && usigComponent.verifyUI(usigBProtocolMessage)
                && verifyCnt(usigBProtocolMessage.getAssinaturaUsig(), usigBProtocolMessage.getAgentSend()-1)) {

            instanciaAtual.setRound(protocolMessage.getRound());

            Map<Constants, Object> msgVal = (Map<Constants, Object>) protocolMessage.getMessage();
            Map<Integer, Set<ClientMessage>> msgFromCoordinator = (Map<Integer, Set<ClientMessage>>) msgVal.get(Constants.V_VAL);
            if (msgFromCoordinator != null && msgFromCoordinator.get(getAgentId()) != null) {
                instanciaAtual.saveProposedValueOnRound(protocolMessage.getRound(), msgFromCoordinator.get(getAgentId()));
            } else {
                instanciaAtual.saveProposedValueOnRound(protocolMessage.getRound(), null);
            }
        }
    }

    private boolean verifyCnt(Integer valorRecebido, Integer agentId){
        if(valorRecebido.equals(contadorRespostasAgentes[agentId])){
            contadorRespostasAgentes[agentId] = contadorRespostasAgentes[agentId]+1;
            return true;
        }
        return false;
    }

    private boolean goodRoundValue(Set<ProtocolMessage> protocolMessages) { // TODO REFAZER
        int kMax = protocolMessages.stream()
                .map(p -> (Message1B) p.getMessage())
                .mapToInt(Message1B::getRoundAceitouUltimaVez)
                .max().getAsInt();

        List<Map<Integer, Set<ClientMessage>>> s = protocolMessages.stream()
                .map(p -> (Message1B) p.getMessage())
                .filter(p -> p.getRoundAceitouUltimaVez().equals(kMax))
                .map(Message1B::getvMapLastRound)
                .collect(Collectors.toList());

        return s.size() >= QTD_MINIMA_RESPOSTAS_QUORUM_ACCEPTORS_USIG;
    }
}
