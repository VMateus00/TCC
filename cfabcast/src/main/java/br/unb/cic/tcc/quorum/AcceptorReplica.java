package br.unb.cic.tcc.quorum;

import br.unb.cic.tcc.entity.Acceptor;
import br.unb.cic.tcc.messages.ProtocolMessage;
import br.unb.cic.tcc.messages.ProtocolMessageType;
import quorum.communication.QuorumMessage;
import quorum.core.QuorumReplica;

import java.io.IOException;

public class AcceptorReplica extends QuorumReplica {

    private Acceptor acceptor;

    public AcceptorReplica(int id, String host, int port, Acceptor acceptor) {
        super(id, "", host, port);

        this.acceptor = acceptor;
    }

    @Override
    public QuorumMessage executeRequest(QuorumMessage quorumMessage) {
        ProtocolMessage message = (ProtocolMessage) quorumMessage.getMsg();

        try {
            if (message.getProtocolMessageType() == ProtocolMessageType.MESSAGE_1A) {
                acceptor.phase1b(message);
//            System.out.println("Acceptor: "+ acceptor.getAgentId() + "chamou a phase1b");

            } else if (message.getProtocolMessageType() == ProtocolMessageType.MESSAGE_2A
                    || message.getProtocolMessageType() == ProtocolMessageType.MESSAGE_2S) {
                acceptor.phase2b(message);
//            System.out.println("Acceptor: "+ acceptor.getAgentId() + "chamou a phase2b");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public QuorumMessage executeReconfigurationMessage(QuorumMessage quorumMessage) {
        throw new UnsupportedOperationException("Not utilized in this protocol");
    }

    public Acceptor getAcceptor() {
        return acceptor;
    }
}
