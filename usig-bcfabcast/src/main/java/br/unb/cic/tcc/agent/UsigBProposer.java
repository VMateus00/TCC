package br.unb.cic.tcc.agent;

import br.unb.cic.tcc.component.IUsig;
import br.unb.cic.tcc.component.UsigComponent;
import br.unb.cic.tcc.definitions.Constants;
import br.unb.cic.tcc.messages.BProtocolMessage;
import br.unb.cic.tcc.messages.ClientMessage;
import br.unb.cic.tcc.messages.Message1B;
import br.unb.cic.tcc.messages.ProposerClientMessage;
import br.unb.cic.tcc.messages.ProtocolMessage;
import br.unb.cic.tcc.messages.ProtocolMessageType;
import br.unb.cic.tcc.messages.UsigBProtocolMessage;
import quorum.communication.MessageType;
import quorum.communication.QuorumMessage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class UsigBProposer extends BProposer {

    private final IUsig usigComponent = new UsigComponent();
    private final Integer[] contadorRespostasAgentes;

    public UsigBProposer(int id, String host, int port, Integer qtdAgentes, Map<String, Set<Integer>> agentsMap) {
        super(id, host, port, agentsMap);
        contadorRespostasAgentes = new Integer[qtdAgentes+1];
    }

    @Override
    public void propose(ClientMessage clientMessage) {
        super.propose(clientMessage); // Copia do metodo da superClasse
    }

    @Override
    public void phase2A(ProtocolMessage protocolMessage) {
        UsigBProtocolMessage usigBProtocolMessage = (UsigBProtocolMessage) protocolMessage;
        System.out.println("Proposer(" + getAgentId() + ") começou a fase 2A");

        boolean condicao1 = protocolMessage.getProtocolMessageType() == ProtocolMessageType.MESSAGE_PROPOSE && protocolMessage.getMessage() != null;
        boolean condicao2 = protocolMessage.getProtocolMessageType() == ProtocolMessageType.MESSAGE_2A
                && usigComponent.verifyUI(usigBProtocolMessage)
                && verifyCnt(usigBProtocolMessage.getAssinaturaUsig(), usigBProtocolMessage.getAgentSend())
                && isColisionFastProposer(protocolMessage.getAgentSend());

        if (currentRound == protocolMessage.getRound()
                && currentValue.get(currentRound) == null
                && (condicao1 || condicao2)) {

            currentValue.put(currentRound, protocolMessage.getMessage());

            ProposerClientMessage valResponseMsg = null;
            if (protocolMessage.getMessage() instanceof ClientMessage) {
                valResponseMsg = new ProposerClientMessage(getAgentId(), (ClientMessage) protocolMessage.getMessage());
            } else if (protocolMessage.getMessage() instanceof ProposerClientMessage) {
                valResponseMsg = (ProposerClientMessage) protocolMessage.getMessage();
            } else {
                System.out.println("Erro");
//                throw new Exception("Nao pode entrar como ProposerClientMessage nesse ponto");
            }

            ProtocolMessageType msgType = ProtocolMessageType.MESSAGE_2A;
            UsigBProtocolMessage responseMsg = usigComponent.createUI(new BProtocolMessage(
                    msgType, protocolMessage.getRound(), getAgentId(), encrypt(msgType, keyPair.getPrivate()), keyPair.getPublic(), valResponseMsg));
            QuorumMessage quorumMessage = new QuorumMessage(MessageType.QUORUM_REQUEST, responseMsg, getQuorumSender().getProcessId());

            getQuorumSender().sendTo(idAccetprosAndLearnersAndCFProposers(), quorumMessage);
        }
    }

    @Override
    public void phase2Prepare(ProtocolMessage protocolMessage) {
        UsigBProtocolMessage usigBProtocolMessage = (UsigBProtocolMessage) protocolMessage;
        System.out.println("Proposer(" + getAgentId() + ") começou a fase 2Prepare");
        if (currentRound < protocolMessage.getRound()
                && goodRoundValue(usigBProtocolMessage.getProofs(), protocolMessage.getRound())
                && usigComponent.verifyUI(usigBProtocolMessage)
                && verifyCnt(usigBProtocolMessage.getAssinaturaUsig(), usigBProtocolMessage.getAgentSend())) {

            currentRound = protocolMessage.getRound();

            Map<Constants, Object> msgVal = (Map<Constants, Object>) protocolMessage.getMessage();
            Map<Integer, Set<ClientMessage>> msgFromCoordinator = (Map<Integer, Set<ClientMessage>>) msgVal.get(Constants.V_VAL);
            if (msgFromCoordinator != null && msgFromCoordinator.get(getAgentId()) != null) {
                currentValue.put(currentRound, msgFromCoordinator.get(getAgentId()));
            } else {
                currentValue.put(currentRound, null);
            }
        }
    }

    private boolean verifyCnt(Integer valorRecebido, Integer agentId){
        if(valorRecebido.equals(contadorRespostasAgentes[agentId])){
            contadorRespostasAgentes[agentId] = contadorRespostasAgentes[agentId]++;
            return true;
        }
        return false;
    }

    private boolean goodRoundValue(Set<ProtocolMessage> protocolMessages, Integer round) { // TODO REFAZER
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

    @Override
    public void limpaDadosExecucao() {
        super.limpaDadosExecucao();
        proofs = new HashMap<>();
    }
}
