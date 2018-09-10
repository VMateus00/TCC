package br.unb.cic.tcc.agent;

import br.unb.cic.tcc.component.UsigComponent;
import br.unb.cic.tcc.definitions.Constants;
import br.unb.cic.tcc.messages.ClientMessage;
import br.unb.cic.tcc.messages.Message1B;
import br.unb.cic.tcc.messages.ProposerClientMessage;
import br.unb.cic.tcc.messages.ProtocolMessage;
import br.unb.cic.tcc.messages.ProtocolMessageType;
import br.unb.cic.tcc.messages.UsigBProtocolMessage;
import br.unb.cic.tcc.quorum.Quoruns;
import quorum.communication.MessageType;
import quorum.communication.QuorumMessage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class UsigBProposer extends BProposer {

    private Map<Integer, Set<ProtocolMessage>> proofs = new HashMap<>();

    public UsigBProposer(int id, String host, int port) {
        super(id, host, port);
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
                && UsigComponent.singleton().verifyUI(usigBProtocolMessage)
                && verifyCnt(usigBProtocolMessage.getAssinaturaUsig(), usigBProtocolMessage.getAgentSend())
                && Quoruns.isCFProposerOnRound(protocolMessage.getAgentSend(), currentRound);

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

            UsigBProtocolMessage responseMsg = UsigComponent.singleton().createUI(ProtocolMessageType.MESSAGE_2A, protocolMessage.getRound(), getAgentId(), getAgentId(), valResponseMsg, proofs.get(currentRound));
            QuorumMessage quorumMessage = new QuorumMessage(MessageType.QUORUM_REQUEST, responseMsg, getQuorumSender().getProcessId());

            getQuorumSender().sendTo(Quoruns.idAcceptorsLearnersCFProposers(currentRound), quorumMessage);
        }
    }

    @Override
    public void phase2Prepare(ProtocolMessage protocolMessage) {
        UsigBProtocolMessage usigBProtocolMessage = (UsigBProtocolMessage) protocolMessage;
        System.out.println("Proposer(" + getAgentId() + ") começou a fase 2Prepare");
        if (currentRound < protocolMessage.getRound()
                && goodRoundValue(usigBProtocolMessage.getProofs(), protocolMessage.getRound())
                && UsigComponent.singleton().verifyUI(usigBProtocolMessage)
                && verifyCnt(usigBProtocolMessage.getAssinaturaUsig(), usigBProtocolMessage.getAgentSend())) {

            currentRound = protocolMessage.getRound();

            proofs.put(currentRound, ((UsigBProtocolMessage) protocolMessage).getProofs());

            Map<Constants, Object> msgVal = (Map<Constants, Object>) protocolMessage.getMessage();
            Map<Integer, Set<ClientMessage>> msgFromCoordinator = (Map<Integer, Set<ClientMessage>>) msgVal.get(Constants.V_VAL);
            if (msgFromCoordinator != null && msgFromCoordinator.get(getAgentId()) != null) {
                currentValue.put(currentRound, msgFromCoordinator.get(getAgentId()));
            } else {
                currentValue.put(currentRound, null);
            }
        }
    }

    private boolean verifyCnt(Integer valorRecebido, Integer agentId){ // é assim?
        return true; // TODO
    }

    private boolean goodRoundValue(Set<ProtocolMessage> protocolMessages, Integer round) {
        int kMax = protocolMessages.stream()
                .map(p -> (Message1B) p.getMessage())
                .mapToInt(Message1B::getRoundAceitouUltimaVez)
                .max().getAsInt();

        List<Map<Integer, Set<ClientMessage>>> s = protocolMessages.stream()
                .map(p -> (Message1B) p.getMessage())
                .filter(p -> p.getRoundAceitouUltimaVez().equals(kMax))
                .map(Message1B::getvMapLastRound)
                .collect(Collectors.toList());

        return s.size() >= Quoruns.QTD_QUORUM_ACCEPTORS_BIZANTINO;
    }

    @Override
    public void limpaDadosExecucao() {
        super.limpaDadosExecucao();
        proofs = new HashMap<>();
    }
}
