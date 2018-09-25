package br.unb.cic.tcc.quorum;

import br.unb.cic.tcc.entity.Learner;
import br.unb.cic.tcc.messages.ProtocolMessage;
import br.unb.cic.tcc.messages.ProtocolMessageType;
import quorum.communication.QuorumMessage;
import quorum.core.QuorumReplica;

public class LearnerReplica extends QuorumReplica {

    protected Learner learner;

    public LearnerReplica(int id, String host, int port, Learner learner) {
        super(id, "", host, port);

        this.learner = learner;
    }

    @Override
    public QuorumMessage executeRequest(QuorumMessage quorumMessage) {
        ProtocolMessage protocolMessage = (ProtocolMessage) quorumMessage.getMsg();
        if(ProtocolMessageType.MESSAGE_2B == protocolMessage.getProtocolMessageType()
                || ProtocolMessageType.MESSAGE_2A == protocolMessage.getProtocolMessageType()){
            learner.learn(protocolMessage);
        }
        return null;
    }

    @Override
    public QuorumMessage executeReconfigurationMessage(QuorumMessage quorumMessage) {
        throw new UnsupportedOperationException("Not utilized in this protocol");
    }
}
