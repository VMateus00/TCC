package br.unb.cic.tcc.agent;

import br.unb.cic.tcc.component.IUsig;
import br.unb.cic.tcc.component.UsigComponent;
import br.unb.cic.tcc.definitions.Constants;
import br.unb.cic.tcc.definitions.CurrentInstanceProposer;
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
import java.util.concurrent.ConcurrentSkipListSet;
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
    public void phase1A(ProtocolMessage protocolMessage) {
        super.phase1A(protocolMessage); // igual acima, somente a msg é assinada
    }

    @Override
    protected void sendMsgFromPhase1AToQuorum(QuorumMessage quorumMessage) {
        getQuorumSender().sendTo(idAcceptorsAndLearnersAndCFProposers(getAgentId()), quorumMessage);
    }

    @Override
    protected ProtocolMessage msgFromPhase1A(CurrentInstanceProposer currentInstance) {
        ProtocolMessage protocolMessage = super.msgFromPhase1A(currentInstance);
        return usigComponent.createUI((BProtocolMessage) protocolMessage);
    }

    @Override
    public synchronized void phase2Start(ProtocolMessage protocolMessage) {
//        System.out.println("Coordinator começou a fase 2 - Start");
        CurrentInstanceBProposer instanciaAtual = getInstanciaAtual(protocolMessage.getInstanciaExecucao());

        Set<ProtocolMessage> protocolMessages = instanciaAtual.getMsgsRecebidasOnRound(protocolMessage.getRound());
        protocolMessages.add(protocolMessage);

        if (verifyMsg((BProtocolMessage) protocolMessage)
                && currentRound == protocolMessage.getRound()
                && getMapFromRound(currentRound).isEmpty()
                && verifyCnt(((UsigBProtocolMessage) protocolMessage).getAssinaturaUsig(), (protocolMessage).getAgentSend()-1)
                && protocolMessages.size() == QTD_MINIMA_RESPOSTAS_QUORUM_ACCEPTORS_USIG) {

            int kMax = protocolMessages.stream()
                    .map(p -> (Message1B) p.getMessage())
                    .mapToInt(Message1B::getRoundAceitouUltimaVez)
                    .max().getAsInt();

            List<Map<Integer, Set<ClientMessage>>> s = protocolMessages.stream()
                    .map(p -> (Message1B) p.getMessage())
                    .filter(p -> p.getRoundAceitouUltimaVez().equals(kMax))
                    .map(Message1B::getvMapLastRound)
                    .collect(Collectors.toList());

            if (s.isEmpty()) {
                instanciaAtual.getvMap().put(protocolMessage.getRound(), null);
//                getvMap().put(currentRound, new ConcurrentHashMap<>()); // deixa vazio nesse caso
            } else {
                s.forEach((map) ->
                        map.forEach((k, v) -> getMapFromRound(currentRound).put(k, v)));

                for(Integer idCFProposer : idCFProposers(getAgentId())){
                    getMapFromRound(currentRound).putIfAbsent(idCFProposer, new ConcurrentSkipListSet<>());
                }
            }

            ProtocolMessage messageToSend = new ProtocolMessage(ProtocolMessageType.MESSAGE_2S, currentRound,
                    getAgentId(), instanciaAtual.getInstanciaAtual(), instanciaAtual.getVmapCriadoOnRound(instanciaAtual.getRound()));
            UsigBProtocolMessage msgToSend = usigComponent.createUI(createAssignedMessage(messageToSend, protocolMessages, keyPair));

            QuorumMessage quorumMessage = new QuorumMessage(MessageType.QUORUM_REQUEST, msgToSend, getQuorumSender().getProcessId());
            getQuorumSender().sendTo(idAcceptorsAndCFProposers(getAgentId()), quorumMessage);
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

            ProtocolMessage messageToSend = new ProtocolMessage(ProtocolMessageType.MESSAGE_2A, protocolMessage.getRound(),
                    getAgentId(), instanciaAtual.getInstanciaAtual(), valResponseMsg);
            UsigBProtocolMessage responseMsg = usigComponent.createUI(createAssignedMessage(messageToSend, null, keyPair));
            QuorumMessage quorumMessage = new QuorumMessage(MessageType.QUORUM_REQUEST, responseMsg, getQuorumSender().getProcessId());

            getQuorumSender().sendTo(idAcceptorsAndLearnersAndCFProposers(getAgentId()), quorumMessage);
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

    @Override
    protected Integer qtdMsgMinima() {
        return QTD_MINIMA_RESPOSTAS_QUORUM_ACCEPTORS_USIG;
    }
}
