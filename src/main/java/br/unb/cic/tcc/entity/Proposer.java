package br.unb.cic.tcc.entity;

import br.unb.cic.tcc.definitions.Constants;
import br.unb.cic.tcc.messages.ClientMessage;
import br.unb.cic.tcc.messages.ProtocolMessage;
import br.unb.cic.tcc.messages.ProtocolMessageType;
import br.unb.cic.tcc.quorum.ProposerReplica;
import br.unb.cic.tcc.quorum.ProposerSender;
import br.unb.cic.tcc.quorum.Quoruns;
import quorum.communication.MessageType;
import quorum.communication.QuorumMessage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Proposer extends Agent<ProposerReplica, ProposerSender> {
    public static Integer roundAtual = 1;


    private int currentRound = 0;
    private Map<Integer, Object> currentValue = new HashMap<>();
    private Boolean isColisionFastProposer = true; // TODO deixar aleatorio (verificar quantos sao necessarios ter)
    List<ProtocolMessage> msgsRecebidas = new ArrayList<>();

    public Proposer(int id, String host, int port) {
        ProposerSender proposerSender = new ProposerSender(id);
        ProposerReplica proposerReplica = new ProposerReplica(id, host, port, this, proposerSender);

        setAgentId(id);
        setQuorumReplica(proposerReplica);
        setQuorumSender(proposerSender);
    }

    // Phase 1A só é executada por coordinator
    public void phase1A(int round) {
        // TODO após concluir parte 1
    }

    public void phase2A(ProtocolMessage protocolMessage) {
        System.out.println("Proposer("+getAgentId()+") começou a fase 2A");
        // nao precisa verificar aqui se é CFproposer, pois quem chama verifica
        Map<Constants, Object> messageMap = (Map<Constants, Object>) protocolMessage.getMessage();

        Object valMsgRecebida = messageMap.get(Constants.V_VAL);
        boolean condicao1 = protocolMessage.getProtocolMessageType() == ProtocolMessageType.MESSAGE_PROPOSE && valMsgRecebida != null;
        boolean condicao2 = protocolMessage.getProtocolMessageType() == ProtocolMessageType.MESSAGE_2A && Quoruns.isCFProposer((Integer)messageMap.get(Constants.AGENT_ID));

        if (currentRound == protocolMessage.getRound()
                && currentValue.get(currentRound) == null
                && (condicao1 || condicao2)) {

            currentValue.put(currentRound, valMsgRecebida);

            HashMap<Constants, Object> map = new HashMap<>();
            map.put(Constants.V_RND, currentRound);
            map.put(Constants.AGENT_TYPE, this.getClass());
            map.put(Constants.V_VAL, valMsgRecebida);

            ProtocolMessage responseMsg = new ProtocolMessage(ProtocolMessageType.MESSAGE_2A, protocolMessage.getRound(), map);
            QuorumMessage quorumMessage = new QuorumMessage(MessageType.QUORUM_REQUEST, responseMsg, getQuorumSender().getProcessId());

            // V != null (verificar como ele vem, ainda nao fiz isso)
            if (valMsgRecebida != null) {
                getQuorumSender().sendTo(Quoruns.idAcceptorsAndCFProposers(), quorumMessage);
            } else {
                getQuorumSender().sendTo(Quoruns.idLeaners(), quorumMessage);
            }
        }
    }

    public void phase2Start(ProtocolMessage protocolMessage) {
        // TODO após concluir parte 1
    }

    public void phase2Prepare(int round, Map<String, Object> vMapCoordinator) {
        // TODO após concluir parte 1
    }

    public void propose(ClientMessage clientMessage) {
        HashMap<Constants, Object> map = new HashMap<>();
        map.put(Constants.V_RND, currentRound);
        map.put(Constants.V_VAL, clientMessage);
        ProtocolMessage protocolMessage = new ProtocolMessage(ProtocolMessageType.MESSAGE_PROPOSE, currentRound, map);
        QuorumMessage quorumMessage = new QuorumMessage(MessageType.QUORUM_REQUEST, protocolMessage, getQuorumSender().getProcessId());
        getQuorumSender().sendTo(Quoruns.idCFProposers(), quorumMessage);

        System.out.println("Proposer ("+getAgentId()+") enviou uma proposta para os CFProposers");
    }

    public boolean isCoordinator() {
        return getAgentId() == 1;
    }

    public Boolean isColisionFastProposer() {
        return isColisionFastProposer;
    }
}
