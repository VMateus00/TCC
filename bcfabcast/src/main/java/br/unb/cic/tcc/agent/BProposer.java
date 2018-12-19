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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;

public class BProposer extends Proposer implements BAgent {
    protected final KeyPair keyPair;

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
        ProtocolMessage messageToPropose = super.getMessageToPropose(clientMessage);
        BProtocolMessage protocolMessage = createAssignedMessage(messageToPropose, null, keyPair) ;
        return protocolMessage;
    }

    @Override
    protected CurrentInstanceProposer defineTypeInstance(Integer instanciaExecucao) {
        return new CurrentInstanceBProposer(instanciaExecucao);
    }

    @Override
    public void phase1A(ProtocolMessage protocolMessage) {
        super.phase1A(protocolMessage);
    }

    @Override
    protected ProtocolMessage msgFromPhase1A(CurrentInstanceProposer currentInstance) {
        ProtocolMessage protocolMessage = super.msgFromPhase1A(currentInstance);
        return createAssignedMessage(protocolMessage, null, keyPair);
    }

    @Override
    public synchronized void phase2Start(ProtocolMessage protocolMessage) {
//        System.out.println("Coordinator começou a fase 2 - Start");
        CurrentInstanceBProposer instanciaAtual = getInstanciaAtual(protocolMessage.getInstanciaExecucao());

        Set<ProtocolMessage> protocolMessages = instanciaAtual.getMsgsRecebidasOnRound(protocolMessage.getRound());
        protocolMessages.add(protocolMessage);

        if (instanciaAtual.getRound().equals(protocolMessage.getRound())
                && instanciaAtual.getVmapCriadoOnRound(instanciaAtual.getRound()).isEmpty()
                && protocolMessages.size() == QTD_MINIMA_RESPOSTAS_QUORUM_ACCEPTORS_BIZANTINO) {

            int kMax = protocolMessages.stream()
                    .map(p -> (Message1B) p.getMessage())
                    .mapToInt(Message1B::getRoundAceitouUltimaVez)
                    .max().getAsInt();

            List<Map<Integer, Set<ClientMessage>>> s = protocolMessages.stream()
                    .map(p -> (Message1B) p.getMessage())
                    .filter(p -> p.getRoundAceitouUltimaVez().equals(kMax))
                    .map(Message1B::getvMapLastRound)
                    .collect(Collectors.toList());

            int[] agentsToSendMsg;
            if (s.isEmpty()) {
                instanciaAtual.getvMap().put(protocolMessage.getRound(), null);
                agentsToSendMsg = idCFProposers(getAgentId());
            } else {
                if (s.size() < QTD_MINIMA_RESPOSTAS_QUORUM_ACCEPTORS_BIZANTINO) {
                    return; // Só pode executar se tiver tamanho minimo;
                }
                s.forEach((map) ->
                        map.forEach((k, v) -> instanciaAtual.getVmapCriadoOnRound(instanciaAtual.getRound()).put(k, v)));

                for (Integer idCFProposer : idCFProposers(getAgentId())) {
                    instanciaAtual.getVmapCriadoOnRound(instanciaAtual.getRound()).putIfAbsent(idCFProposer, new ConcurrentSkipListSet<>());
                }
                agentsToSendMsg = idAcceptorsAndCFProposers(getAgentId());
            }
            ProtocolMessageType messageType = ProtocolMessageType.MESSAGE_2S;
            ProtocolMessage messageToSend = new ProtocolMessage(messageType, currentRound, getAgentId(), protocolMessage.getInstanciaExecucao(), instanciaAtual.getVmapCriadoOnRound(instanciaAtual.getRound()));
            BProtocolMessage msgToSend = createAssignedMessage(messageToSend, protocolMessages, keyPair);

            QuorumMessage quorumMessage = new QuorumMessage(MessageType.QUORUM_REQUEST, msgToSend, getQuorumSender().getProcessId());
            getQuorumSender().sendTo(agentsToSendMsg, quorumMessage);
        }
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
                System.out.println("O CF-proposer "+getAgentId()+" não conseguiu reconhecer a mensagem recebida. E portanto nao executou a phase2 corretamente.");
                return;
            }
            ProtocolMessage message = new ProtocolMessage(ProtocolMessageType.MESSAGE_2A, protocolMessage.getRound(),
                    getAgentId(), protocolMessage.getInstanciaExecucao(), valResponseMsg);
            Set<ProtocolMessage> proofsFromRound = ((CurrentInstanceBProposer) instanciaAtual).getProofsFromRound();
            BProtocolMessage responseMsg = createAssignedMessage(message, proofsFromRound, keyPair);
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

    protected boolean goodRoundValue(Set<ProtocolMessage> protocolMessages) {
        int kMax = protocolMessages.stream()
                .map(p -> (Message1B) p.getMessage())
                .mapToInt(Message1B::getRoundAceitouUltimaVez)
                .max().getAsInt();

        List<Map<Integer, Set<ClientMessage>>> s = protocolMessages.stream()
                .map(p -> (Message1B) p.getMessage())
                .filter(p -> p.getRoundAceitouUltimaVez().equals(kMax))
                .map(Message1B::getvMapLastRound)
                .collect(Collectors.toList());

        return s.size() >= qtdMsgMinima();
//        return s.size() >= 1;
    }

    protected Integer qtdMsgMinima() {
        return QTD_MINIMA_RESPOSTAS_QUORUM_ACCEPTORS_BIZANTINO;
    }

    @Override
    protected CurrentInstanceBProposer getInstanciaAtual(Integer instanciaExecucao) {
        return (CurrentInstanceBProposer) super.getInstanciaAtual(instanciaExecucao);
    }
}
