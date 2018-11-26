package br.unb.cic.tcc.agent;

import br.unb.cic.tcc.definitions.Constants;
import br.unb.cic.tcc.definitions.CurrentInstanceProposer;
import br.unb.cic.tcc.defintions.CurrentInstanceBProposer;
import br.unb.cic.tcc.entity.Proposer;
import br.unb.cic.tcc.messages.BProtocolMessage;
import br.unb.cic.tcc.messages.ClientMessage;
import br.unb.cic.tcc.messages.Message1B;
import br.unb.cic.tcc.messages.ProposerClientMessage;
import br.unb.cic.tcc.messages.ProtocolMessage;
import br.unb.cic.tcc.messages.ProtocolMessageType;
import br.unb.cic.tcc.util.RsaUtil;
import quorum.communication.MessageType;
import quorum.communication.QuorumMessage;

import java.security.KeyPair;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class BProposer extends Proposer implements BAgent {
    protected final KeyPair keyPair;
    protected Map<Integer, Set<ProtocolMessage>> proofs = new HashMap<>();

    public BProposer(int id, String host, int port, Map<String, Set<Integer>> agentsMap) {
        super(id, host, port, agentsMap);
        keyPair = RsaUtil.generateKeyPair();
    }

    @Override
    public void propose(ClientMessage clientMessage) {
        super.propose(clientMessage); // Copia do metodo da superClasse, exceto pela assinatura
    }

    @Override
    protected ProtocolMessage getMessageToPropose(ClientMessage clientMessage) {
        BProtocolMessage protocolMessage = createAssignedMessage(super.getMessageToPropose(clientMessage),
                null, keyPair) ;
        return protocolMessage;
    }

    @Override
    protected CurrentInstanceProposer defineTypeInstance(Integer instanciaExecucao) {
        return new CurrentInstanceBProposer(instanciaExecucao);
    }

    @Override
    public void phase2A(ProtocolMessage protocolMessage) {
//        System.out.println("Proposer(" + getAgentId() + ") começou a fase 2A");
        CurrentInstanceProposer instanciaAtual = getInstanciaAtual(protocolMessage.getInstanciaExecucao());


        boolean condicao1 = protocolMessage.getProtocolMessageType() == ProtocolMessageType.MESSAGE_PROPOSE && protocolMessage.getMessage() != null;
        boolean condicao2 = protocolMessage.getProtocolMessageType() == ProtocolMessageType.MESSAGE_2A && isColisionFastProposer(protocolMessage.getAgentSend());

        if (verifyMsg((BProtocolMessage) protocolMessage)
                && instanciaAtual.getRound().equals(protocolMessage.getRound())
                && instanciaAtual.getProposedValueOnRound(protocolMessage.getRound())  == null
                && (condicao1 || condicao2)) {

            instanciaAtual.getProposedValuesOnRound().put(instanciaAtual.getRound(), protocolMessage.getMessage());

            ProposerClientMessage valResponseMsg;
            if (protocolMessage.getMessage() instanceof ClientMessage) {
                valResponseMsg = new ProposerClientMessage(getAgentId(), (ClientMessage) protocolMessage.getMessage());
            } else if (protocolMessage.getMessage() instanceof ProposerClientMessage) {
                valResponseMsg = (ProposerClientMessage) protocolMessage.getMessage();
            } else {
                System.out.println("Erro");
                return;
            }
            ProtocolMessageType messageType = ProtocolMessageType.MESSAGE_2A;
            BProtocolMessage responseMsg = createAssignedMessage(new ProtocolMessage(messageType, protocolMessage.getRound(),
                    getAgentId(), valResponseMsg), proofs.get(currentRound), keyPair);
            QuorumMessage quorumMessage = new QuorumMessage(MessageType.QUORUM_REQUEST, responseMsg, getQuorumSender().getProcessId());

            if (protocolMessage.getMessage() != null) {
//                System.out.println("Proposer (" + getAgentId() + ") enviou msg to acceptors and cfProposers");
                getQuorumSender().sendTo(idAcceptorsAndCFProposers(getAgentId()), quorumMessage);
            } else {
//                System.out.println("Proposer (" + getAgentId() + ") enviou msg to leaners");
                getQuorumSender().sendTo(idLearners(), quorumMessage);
            }
        }
    }

    @Override
    public void phase2Prepare(ProtocolMessage protocolMessage) {
        System.out.println("Proposer(" + getAgentId() + ") começou a fase 2Prepare");
        CurrentInstanceBProposer instanciaAtual = getInstanciaAtual(protocolMessage.getInstanciaExecucao());
        if (verifyMsg((BProtocolMessage) protocolMessage)
                && instanciaAtual.getRound() < protocolMessage.getRound()
                && goodRoundValue(((BProtocolMessage) protocolMessage).getProofs())) {

            instanciaAtual.setRound(protocolMessage.getRound());
            instanciaAtual.getProofs().put(protocolMessage.getRound(), ((BProtocolMessage) protocolMessage).getProofs());

            Map<Constants, Object> msgVal = (Map<Constants, Object>) protocolMessage.getMessage();
            Map<Integer, Set<ClientMessage>> msgFromCoordinator = (Map<Integer, Set<ClientMessage>>) msgVal.get(Constants.V_VAL);
            if (msgFromCoordinator != null && msgFromCoordinator.get(getAgentId()) != null) {
                instanciaAtual.saveProposedValueOnRound(protocolMessage.getRound(), msgFromCoordinator.get(getAgentId()));
            } else {
                instanciaAtual.saveProposedValueOnRound(protocolMessage.getRound(), null);
            }
        }
    }

    private boolean goodRoundValue(Set<ProtocolMessage> protocolMessages) {
        int kMax = protocolMessages.stream()
                .map(p -> (Message1B) p.getMessage())
                .mapToInt(Message1B::getRoundAceitouUltimaVez)
                .max().getAsInt();

        List<Map<Integer, Set<ClientMessage>>> s = protocolMessages.stream()
                .map(p -> (Message1B) p.getMessage())
                .filter(p -> p.getRoundAceitouUltimaVez().equals(kMax))
                .map(Message1B::getvMapLastRound)
                .collect(Collectors.toList());

        return s.size() >= QTD_MINIMA_RESPOSTAS_QUORUM_ACCEPTORS_BIZANTINO;
    }

    @Override
    protected CurrentInstanceBProposer getInstanciaAtual(Integer instanciaExecucao) {
        return (CurrentInstanceBProposer) super.getInstanciaAtual(instanciaExecucao);
    }
}
