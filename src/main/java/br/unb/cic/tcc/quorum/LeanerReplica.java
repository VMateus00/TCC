package br.unb.cic.tcc.quorum;

import quorum.communication.QuorumMessage;
import quorum.core.QuorumReplica;

public class LeanerReplica extends QuorumReplica {
    public LeanerReplica(int id, String host, int port) {
        super(id, "", host, port);
    }

    @Override
    public QuorumMessage executeRequest(QuorumMessage quorumMessage) {
        // TODO
        return null;
    }

    @Override
    public QuorumMessage executeReconfigurationMessage(QuorumMessage quorumMessage) {
        // TODO
        return null;
    }
}
