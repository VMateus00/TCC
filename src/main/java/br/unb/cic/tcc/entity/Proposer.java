package br.unb.cic.tcc.entity;

import br.unb.cic.tcc.messages.ClientMessage;
import br.unb.cic.tcc.messages.ProtocolMessage;
import br.unb.cic.tcc.messages.ProtocolMessageType;
import br.unb.cic.tcc.quorum.ProposerReplica;
import br.unb.cic.tcc.quorum.ProposerSender;
import br.unb.cic.tcc.quorum.Quoruns;
import quorum.communication.MessageType;
import quorum.communication.QuorumMessage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Proposer extends Agent<ProposerReplica, ProposerSender> {
    private int currentRound = 0;
    private Object currentValue;
    private Boolean isColisionFastProposer = true; // TODO deixar aleatorio (verificar quantos sao necessarios ter)
    List<ProtocolMessage> msgsRecebidas = null;

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
            setvMap(new HashMap<>()); // cval[c] <- none

            ProtocolMessage protocolMessage = new ProtocolMessage(ProtocolMessageType.MESSAGE_1A, round, null);
            QuorumMessage quorumMessage = new QuorumMessage(MessageType.QUORUM_REQUEST, protocolMessage, getQuorumSender().getProcessId());
            getQuorumSender().sendTo(Quoruns.idAcceptors(), quorumMessage);
        }
    }

    public void phase2A(ProtocolMessage protocolMessage) {
        // TODO
        if (isColisionFastProposer
                && currentRound == protocolMessage.getRound()
                && currentValue == null) {

        }
    }

    public void phase2Start(ProtocolMessage protocolMessage) {
        msgsRecebidas.add(protocolMessage); // TODO verificar com o Alchieri se é concorrente

        if (msgsRecebidas.size() == Quoruns.getAcceptors().size()
                && isCoordinator() && currentRound == protocolMessage.getRound()
                && getvMap().isEmpty()) {
            // Recebe a resposta dos acceptors

            Object k = null;
            Object s = null; // depende de k

            if(s == null){
                currentValue = null;
//                send '2S', round, currentValue to Proposers Quorum
            } else {
                // setar valor de currentValue
//              send '2S', round, currentValue to Proposers Quorum
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
        ProtocolMessage protocolMessage = new ProtocolMessage(ProtocolMessageType.MESSAGE_PROPOSE, null, clientMessage);
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
