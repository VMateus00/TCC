package br.unb.cic.tcc.agent;

import br.unb.cic.tcc.component.IUsig;
import br.unb.cic.tcc.component.UsigComponent;
import br.unb.cic.tcc.definitions.CurrentInstanceAcceptor;
import br.unb.cic.tcc.messages.ClientMessage;
import br.unb.cic.tcc.messages.Message1B;
import br.unb.cic.tcc.messages.ProposerClientMessage;
import br.unb.cic.tcc.messages.ProtocolMessage;
import br.unb.cic.tcc.messages.ProtocolMessageType;
import br.unb.cic.tcc.messages.UsigBProtocolMessage;
import br.unb.cic.tcc.quorum.AcceptorReplica;
import br.unb.cic.tcc.quorum.UsigAcceptorReplica;
import quorum.communication.MessageType;
import quorum.communication.QuorumMessage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class UsigBAcceptor extends BAcceptor{

    private final IUsig usigComponenet = new UsigComponent(); // vetor com uma quantidade maior(vamos ignora a posicao zero)
    private final Integer[] contadorRespostasAgentes;

    public UsigBAcceptor(int id, String host, int port, Integer qtdAgentes, Map<String, Set<Integer>> agentsMap) {
        super(id, host, port, agentsMap);
        contadorRespostasAgentes = new Integer[qtdAgentes];

        for (int i=0; i<contadorRespostasAgentes.length;i++){
            contadorRespostasAgentes[i] = 1;
        }
    }

    @Override
    public void phase1b(ProtocolMessage protocolMessage) {
        CurrentInstanceAcceptor instanciaAtual = getInstanciaAtual(protocolMessage.getInstanciaExecucao());
        UsigBProtocolMessage usigBProtocolMessage = (UsigBProtocolMessage)protocolMessage;
        if(verifyMsg(usigBProtocolMessage)
                && instanciaAtual.getRound() < protocolMessage.getRound()
                && protocolMessage.getProtocolMessageType() == ProtocolMessageType.MESSAGE_1A
                && verifyCnt(usigBProtocolMessage.getAssinaturaUsig(), usigBProtocolMessage.getAgentSend()-1)){
            instanciaAtual.setRound(protocolMessage.getRound());

            Message1B message1B = new Message1B(instanciaAtual.getRoundAceitouUltimaVez(), getAgentId(), instanciaAtual.getVmapLastRound());

            ProtocolMessageType messageType = ProtocolMessageType.MESSAGE_1B;
            UsigBProtocolMessage protocolMessageToSend = usigComponenet.createUI(createAssignedMessage(new ProtocolMessage(messageType,
                    instanciaAtual.getRound(), getAgentId(), instanciaAtual.getInstanciaAtual(), message1B), null, keyPair));
            QuorumMessage quorumMessage = new QuorumMessage(MessageType.QUORUM_REQUEST, protocolMessageToSend, getQuorumSender().getProcessId());
            getQuorumSender().sendTo(idCoordinatorAndLearners(), quorumMessage);
        }
    }

    @Override
    public void phase2b(ProtocolMessage protocolMessage) {
        CurrentInstanceAcceptor instanciaAtual = getInstanciaAtual(protocolMessage.getInstanciaExecucao());
        UsigBProtocolMessage usigBProtocolMessage = (UsigBProtocolMessage) protocolMessage;
//        System.out.println("Acceptor(" + getAgentId() + ") começou a fase 2b");
        Map<Integer, Set<ClientMessage>> vMapLastRound = instanciaAtual.getVmapLastRound();

        int round = usigBProtocolMessage.getRound();

        ProposerClientMessage clientMessage = null;
        if(usigBProtocolMessage.getMessage() instanceof ProposerClientMessage){
            clientMessage = (ProposerClientMessage) usigBProtocolMessage.getMessage();
        }

        boolean condicao1 = usigBProtocolMessage.getProtocolMessageType() == ProtocolMessageType.MESSAGE_2S &&
                (vMapLastRound.isEmpty() ||
                (goodRoundValue(usigBProtocolMessage.getProofs())
                        && usigComponenet.verifyUI(usigBProtocolMessage)
                        && verifyCnt(usigBProtocolMessage.getAssinaturaUsig(), usigBProtocolMessage.getAgentSend()-1)
                        && (usigBProtocolMessage.getMessage() != null && instanciaAtual.getRoundAceitouUltimaVez() < round)));

        boolean condicao2 = usigBProtocolMessage.getProtocolMessageType() == ProtocolMessageType.MESSAGE_2A
                && verifyCnt(usigBProtocolMessage.getAssinaturaUsig(), usigBProtocolMessage.getAgentSend()-1)
                && usigComponenet.verifyUI(usigBProtocolMessage)
                && (clientMessage).getClientMessage() != null;

        if (verifyMsg(usigBProtocolMessage)
                && instanciaAtual.getRound() <= round
                && (condicao1 || condicao2)) {
            vMapLastRound = instanciaAtual.getVmapLastRound();

            Integer agentId = protocolMessage.getAgentSend();
            if (condicao1) {
                ((HashMap<Integer, Set<ClientMessage>>)usigBProtocolMessage.getMessage())
                        .forEach((k,v)->instanciaAtual.getVmapLastRound().put(k,v));
            } else if (condicao2 && (instanciaAtual.getRoundAceitouUltimaVez()< round || vMapLastRound.isEmpty())) {
                vMapLastRound = instanciaAtual.getVmapLastRound();
                vMapLastRound.putIfAbsent(agentId, new HashSet<>());

                for (Integer proposerId : idNCFProposers()) {
                    Set<ClientMessage> proposedValues = vMapLastRound.get(proposerId);
                    if(proposedValues == null){
                        vMapLastRound.put(proposerId, new HashSet<>());
                        proposedValues = vMapLastRound.get(proposerId);
                    }
                    proposedValues .add(null);
                }
                vMapLastRound.get(agentId).add(clientMessage.getClientMessage());
            } else {
                vMapLastRound.putIfAbsent(agentId, new HashSet<>());
                vMapLastRound.get(agentId).add(clientMessage.getClientMessage());
            }
            instanciaAtual.setRoundAceitouUltimaVez(round);
            instanciaAtual.setRound(round);

            ProtocolMessage msgToSend = new ProtocolMessage(ProtocolMessageType.MESSAGE_2B, round, getAgentId(), instanciaAtual.getInstanciaAtual(), vMapLastRound);
            UsigBProtocolMessage protocolSendMsg = usigComponenet.createUI(createAssignedMessage(msgToSend, null, keyPair));

            QuorumMessage quorumMessage = new QuorumMessage(MessageType.QUORUM_REQUEST, protocolSendMsg, getQuorumSender().getProcessId());
            getQuorumSender().sendTo(idLearners(), quorumMessage);
        } else {
            System.out.println("nao pode executar fase 2b - acceptor");
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
    protected Integer qtdMinimaRespostas() {
        return QTD_MINIMA_RESPOSTAS_QUORUM_ACCEPTORS_USIG;
//        return 1;
    }

    public void updateCnt(UsigBProtocolMessage protocolMessage){
        verifyCnt(protocolMessage.getAssinaturaUsig(), protocolMessage.getAgentSend()-1);
    }

    @Override
    protected AcceptorReplica defineAcceptorReplica(int id, String host, int port) {
        return new UsigAcceptorReplica(id, host, port, this);
    }
}
