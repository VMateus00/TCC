package br.unb.cic.tcc.quorum;

import quorum.communication.QuorumMessage;
import quorum.core.QuorumReplica;

public class ProposerReplica extends QuorumReplica {
    public ProposerReplica(int id, String configHome, String host, int port) {
        super(id, configHome, host, port);
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
