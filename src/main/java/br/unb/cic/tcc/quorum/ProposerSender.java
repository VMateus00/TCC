package br.unb.cic.tcc.quorum;

import quorum.communication.QuorumMessage;
import quorum.core.QuorumSender;

public class ProposerSender extends QuorumSender {
    public ProposerSender(int processId) {
        super(processId);
    }

    @Override
    public void replyReceived(QuorumMessage quorumMessage) {

    }

}
