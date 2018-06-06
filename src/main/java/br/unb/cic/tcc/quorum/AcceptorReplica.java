package br.unb.cic.tcc.quorum;

import br.unb.cic.tcc.entity.Acceptor;
import br.unb.cic.tcc.messages.ProtocolMessage;
import br.unb.cic.tcc.messages.ProtocolMessageType;
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
        ProtocolMessage message = (ProtocolMessage) quorumMessage.getMsg();

        if(message.getProtocolMessageType() == ProtocolMessageType.MESSAGE_1A){
            acceptor.phase1b(message.getRound());
            System.out.println("Acceptor: "+ acceptor.getAgentId() + "chamou a phase1b");
        }
        return null;
    }

    @Override
    public QuorumMessage executeReconfigurationMessage(QuorumMessage quorumMessage) {
        // TODO
        return null;
    }
}
