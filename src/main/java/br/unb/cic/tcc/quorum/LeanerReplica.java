package br.unb.cic.tcc.quorum;

import br.unb.cic.tcc.entity.Leaner;
import br.unb.cic.tcc.messages.ClientMessage;
import br.unb.cic.tcc.messages.ProtocolMessage;
import br.unb.cic.tcc.messages.ProtocolMessageType;
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
        ProtocolMessage protocolMessage = (ProtocolMessage) quorumMessage.getMsg();
        if(ProtocolMessageType.MESSAGE_2B == protocolMessage.getProtocolMessageType()){
            leaner.learn(protocolMessage);
        }
        // TODO
        return null;
    }

    @Override
    public QuorumMessage executeReconfigurationMessage(QuorumMessage quorumMessage) {
        // NOT USED IN THIS PROTOCOL
        return null;
    }
}
