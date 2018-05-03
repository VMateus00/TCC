package br.unb.cic.tcc.quorum;

import br.unb.cic.tcc.entity.Leaner;
import quorum.communication.QuorumMessage;
import quorum.core.QuorumReplica;

public class LeanerReplica extends QuorumReplica {

    private Leaner leaner;
    private LeanerSender leanerSender;

    public LeanerReplica(int id, String host, int port, Leaner leaner, LeanerSender leanerSender) {
        super(id, "", host, port);

        this.leaner = leaner;
        this.leanerSender = leanerSender;
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
