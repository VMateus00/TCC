package br.unb.cic.tcc.quorum;

import quorum.communication.QuorumMessage;
import quorum.core.QuorumSender;

public class AgentSender extends QuorumSender {
    public AgentSender(int processId) {
        super(processId);
    }

    @Override
    public void replyReceived(QuorumMessage reply) {
        return; // NOT USED IN THIS PROTOCOL
    }
}
