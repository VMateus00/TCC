package br.unb.cic.tcc.agent;

import br.unb.cic.tcc.definitions.Constants;
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
        currentRound = 0;
    }

    @Override
    public void propose(ClientMessage clientMessage) { // Copia do metodo da superClasse, exceto pela assinatura
        ProtocolMessageType messageType = ProtocolMessageType.MESSAGE_PROPOSE;
        BProtocolMessage protocolMessage = createAssignedMessage(new ProtocolMessage(messageType,
                currentRound, getAgentId(), clientMessage), null, keyPair) ;

        QuorumMessage quorumMessage = new QuorumMessage(MessageType.QUORUM_REQUEST, protocolMessage, getQuorumSender().getProcessId());
        getQuorumSender().sendTo(idCFProposers(), quorumMessage);

        System.out.println("Proposer (" + getAgentId() + ") enviou uma proposta para os CFProposers");
    }

    @Override
    public void phase2A(ProtocolMessage protocolMessage) {
        System.out.println("Proposer(" + getAgentId() + ") começou a fase 2A");

        boolean condicao1 = protocolMessage.getProtocolMessageType() == ProtocolMessageType.MESSAGE_PROPOSE && protocolMessage.getMessage() != null;
        boolean condicao2 = protocolMessage.getProtocolMessageType() == ProtocolMessageType.MESSAGE_2A && isColisionFastProposer(protocolMessage.getAgentSend());

        if (verifyMsg((BProtocolMessage) protocolMessage)
                && currentRound == protocolMessage.getRound()
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
            }
            ProtocolMessageType messageType = ProtocolMessageType.MESSAGE_2A;
            BProtocolMessage responseMsg = createAssignedMessage(new ProtocolMessage(messageType, protocolMessage.getRound(),
                    getAgentId(), valResponseMsg), proofs.get(currentRound), keyPair);
            QuorumMessage quorumMessage = new QuorumMessage(MessageType.QUORUM_REQUEST, responseMsg, getQuorumSender().getProcessId());

            if (protocolMessage.getMessage() != null) {
                System.out.println("Proposer (" + getAgentId() + ") enviou msg to acceptors and cfProposers");
                getQuorumSender().sendTo(idAcceptorsAndCFProposers(), quorumMessage);
            } else {
                System.out.println("Proposer (" + getAgentId() + ") enviou msg to leaners");
                getQuorumSender().sendTo(idLearners(), quorumMessage);
            }
        }
    }

    @Override
    public void phase2Prepare(ProtocolMessage protocolMessage) {
        System.out.println("Proposer(" + getAgentId() + ") começou a fase 2Prepare");
        if (verifyMsg((BProtocolMessage) protocolMessage)
                && currentRound < protocolMessage.getRound()
                && goodRoundValue(((BProtocolMessage) protocolMessage).getProofs(), protocolMessage.getRound())) {
            currentRound = protocolMessage.getRound();

            proofs.put(currentRound, ((BProtocolMessage) protocolMessage).getProofs());

            Map<Constants, Object> msgVal = (Map<Constants, Object>) protocolMessage.getMessage();
            Map<Integer, Set<ClientMessage>> msgFromCoordinator = (Map<Integer, Set<ClientMessage>>) msgVal.get(Constants.V_VAL);
            if (msgFromCoordinator != null && msgFromCoordinator.get(getAgentId()) != null) {
                currentValue.put(currentRound, msgFromCoordinator.get(getAgentId()));
            } else {
                currentValue.put(currentRound, null);
            }
        }
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

        return s.size() >= QTD_MINIMA_RESPOSTAS_QUORUM_ACCEPTORS_BIZANTINO;
    }

    @Override
    public void limpaDadosExecucao() {
        super.limpaDadosExecucao();
        proofs = new HashMap<>();
    }
}
