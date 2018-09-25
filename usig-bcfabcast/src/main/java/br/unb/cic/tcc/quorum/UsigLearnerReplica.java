package br.unb.cic.tcc.quorum;

import br.unb.cic.tcc.agent.UsigBLearner;
import br.unb.cic.tcc.entity.Learner;
import br.unb.cic.tcc.messages.ProtocolMessage;
import br.unb.cic.tcc.messages.ProtocolMessageType;
import br.unb.cic.tcc.messages.UsigBProtocolMessage;
import quorum.communication.QuorumMessage;

public class UsigLearnerReplica extends LearnerReplica {
    public UsigLearnerReplica(int id, String host, int port, UsigBLearner learner) {
        super(id, host, port, learner);
    }

    @Override
    public QuorumMessage executeRequest(QuorumMessage quorumMessage) {
        ProtocolMessage protocolMessage = (ProtocolMessage) quorumMessage.getMsg();
        if (ProtocolMessageType.MESSAGE_2B == protocolMessage.getProtocolMessageType()
                || ProtocolMessageType.MESSAGE_2A == protocolMessage.getProtocolMessageType()) {
            learner.learn(protocolMessage);
        } else if (protocolMessage.getProtocolMessageType() == ProtocolMessageType.MESSAGE_1B) {
            ((UsigBLearner)learner).updateCnt((UsigBProtocolMessage) protocolMessage);
        }

        return null;
    }
}
