package br.unb.cic.tcc.quorum;

import br.unb.cic.tcc.entity.Acceptor;
import quorum.communication.QuorumMessage;
import quorum.core.QuorumReplica;

public class AcceptorReplica extends QuorumReplica {

    private Acceptor acceptor;
    private AcceptorSender acceptorSender;

    public AcceptorReplica(int id, String host, int port, Acceptor acceptor, AcceptorSender acceptorSender) {
        super(id, "", host, port);

        this.acceptor = acceptor;
        this.acceptorSender = acceptorSender;
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
