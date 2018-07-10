package br.unb.cic.tcc.quorum;

import br.unb.cic.tcc.entity.Leaner;
import br.unb.cic.tcc.messages.ProtocolMessage;
import br.unb.cic.tcc.messages.ProtocolMessageType;
import quorum.communication.QuorumMessage;
import quorum.core.QuorumReplica;

public class LeanerReplica extends QuorumReplica {

    private Leaner leaner;

    public LeanerReplica(int id, String host, int port, Leaner leaner) {
        super(id, "", host, port);

        this.leaner = leaner;
    }

    @Override
    public QuorumMessage executeRequest(QuorumMessage quorumMessage) {
        ProtocolMessage protocolMessage = (ProtocolMessage) quorumMessage.getMsg();
        if(ProtocolMessageType.MESSAGE_2B == protocolMessage.getProtocolMessageType()
                || ProtocolMessageType.MESSAGE_2A == protocolMessage.getProtocolMessageType()){
            leaner.learn(protocolMessage);
        }
        return null;
    }

    @Override
    public QuorumMessage executeReconfigurationMessage(QuorumMessage quorumMessage) {
        throw new UnsupportedOperationException("Not utilized in this protocol");
    }
}
