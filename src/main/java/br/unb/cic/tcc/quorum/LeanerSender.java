package br.unb.cic.tcc.quorum;

import quorum.communication.QuorumMessage;
import quorum.core.QuorumSender;

public class LeanerSender extends QuorumSender {
    public LeanerSender(Integer processId) {
        super(processId);
    }

    @Override
    public void replyReceived(QuorumMessage quorumMessage) {
        // TODO
    }
}
