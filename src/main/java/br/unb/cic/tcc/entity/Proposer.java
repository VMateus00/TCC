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
    private int currentRound = 0;
    private Object currentValue;
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
        if (currentRound < round) {
            currentRound = round; // crnd[c] = <- r
            getvMap().put(round, new HashSet<>());// cval[c] <- none

            ProtocolMessage protocolMessage = new ProtocolMessage(ProtocolMessageType.MESSAGE_1A, round, null);
            QuorumMessage quorumMessage = new QuorumMessage(MessageType.QUORUM_REQUEST, protocolMessage, getQuorumSender().getProcessId());
            getQuorumSender().sendTo(Quoruns.idAcceptors(), quorumMessage);
        }
    }

    public void phase2A(ProtocolMessage protocolMessage) { // O que é o V?
        // nao precisa verificar aqui se é CFproposer, pois quem chama verifica
        Map<Constants, Object> messageMap = (Map<Constants, Object>) protocolMessage.getMessage();
        if (isColisionFastProposer
                && currentRound == protocolMessage.getRound()
                && currentValue == null
                && ((protocolMessage.getProtocolMessageType() == ProtocolMessageType.MESSAGE_PROPOSE && messageMap.get(Constants.V_VAL) != null)
                    || (protocolMessage.getProtocolMessageType() == ProtocolMessageType.MESSAGE_2A
                        && (Quoruns.isCFProposer((Integer)messageMap.get(Constants.AGENT_ID)))
                        &&  messageMap.get(Constants.V_VAL) == null))) {

            currentValue = messageMap.get(Constants.V_VAL);

            HashMap<Constants, Object> map = new HashMap<>();
            map.put(Constants.V_RND, currentRound);
            map.put(Constants.AGENT_TYPE, this.getClass());
            map.put(Constants.V_VAL, currentValue);

            ProtocolMessage responseMsg = new ProtocolMessage(ProtocolMessageType.MESSAGE_2A, protocolMessage.getRound(), map);
            QuorumMessage quorumMessage = new QuorumMessage(MessageType.QUORUM_REQUEST, responseMsg, getQuorumSender().getProcessId());

            // V != null (verificar como ele vem, ainda nao fiz isso)
            if (protocolMessage.getMessage() != null) {
                // TODO verificar PQ FAZER ISSO?
                getQuorumSender().sendTo(Quoruns.idAcceptorsAndCFProposers(), quorumMessage);
            } else {
                getQuorumSender().sendTo(Quoruns.idLeaners(), quorumMessage);
            }
        }
    }

    public void phase2Start(ProtocolMessage protocolMessage) {
        msgsRecebidas.add(protocolMessage); // TODO verificar com o Alchieri se é concorrente

        if (msgsRecebidas.size() == Quoruns.getAcceptors().size()
                && isCoordinator()
                && currentRound == protocolMessage.getRound()
                && getvMap().isEmpty()) {
            // Recebe a resposta dos acceptors

            Integer k =  msgsRecebidas.stream()
                    .map(p-> (Map<Constants, Object>)p.getMessage())
                    .map(p->(Integer)p.get(Constants.V_RND))
                    .max(Comparator.comparing(Integer::valueOf)).get();

            List<Map<Constants, Object>> s = msgsRecebidas.stream()
                    .map(p -> (Map<Constants, Object>) p.getMessage())
                    .filter(p -> p.get(Constants.V_RND).equals(k) && p.get(Constants.V_VAL) != null)
                    .collect(Collectors.toList());

            HashMap<Object, Object> argumentos = new HashMap<>();// TODO popular
            ProtocolMessage messageToSend = new ProtocolMessage(ProtocolMessageType.MESSAGE_2S, protocolMessage.getRound(), argumentos);
            QuorumMessage quorumMessage = new QuorumMessage(MessageType.QUORUM_REQUEST, messageToSend, getQuorumSender().getProcessId());
            if (s.isEmpty()) {
                currentValue = null;
                getQuorumSender().sendTo(Quoruns.idProposers(), quorumMessage);
            } else {
                // TODO setar valor de currentValue
                getQuorumSender().sendTo(Quoruns.idAcceptorsAndProposers(), quorumMessage);
            }
        }
    }

    public void phase2Prepare(int round, Map<String, Object> vMapCoordinator) {
        // isProposer == default,
        // Recebeu 2S
        if (currentRound < round) {
            currentRound = round;

            if (vMapCoordinator == null) {
                currentValue = null;
            } else {
                currentValue = vMapCoordinator; // ??? pVal[p] = v(p) alinhar com o professor
            }
        }
    }

    public void propose(ClientMessage clientMessage) {
        HashMap<Constants, Object> map = new HashMap<>();
        map.put(Constants.V_RND, currentRound);
        map.put(Constants.V_VAL, clientMessage);
        ProtocolMessage protocolMessage = new ProtocolMessage(ProtocolMessageType.MESSAGE_PROPOSE, currentRound, map);
        QuorumMessage quorumMessage = new QuorumMessage(MessageType.QUORUM_REQUEST, protocolMessage, getQuorumSender().getProcessId());
        getQuorumSender().sendTo(Quoruns.idCFProposers(), quorumMessage);
    }

    public boolean isCoordinator() {
        return getAgentId() == 1;
    }

    public Boolean isColisionFastProposer() {
        return isColisionFastProposer;
    }
}
