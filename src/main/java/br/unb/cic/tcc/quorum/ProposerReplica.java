package br.unb.cic.tcc.quorum;

import br.unb.cic.tcc.entity.Proposer;
import quorum.communication.QuorumMessage;
import quorum.core.QuorumReplica;

public class ProposerReplica extends QuorumReplica {

    private Proposer proposer;
    private ProposerSender proposerSender;

    public ProposerReplica(int id, String host, int port, Proposer proposer, ProposerSender proposerSender) {
        super(id, "", host, port);

        this.proposer = proposer;
        this.proposerSender = proposerSender;
    }

    @Override
    public QuorumMessage executeRequest(QuorumMessage quorumMessage) {
        return null;
    }

    @Override
    public QuorumMessage executeReconfigurationMessage(QuorumMessage quorumMessage) {
        return null;
    }
}
