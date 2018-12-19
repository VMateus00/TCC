package br.unb.cic.tcc.quorum;

import br.unb.cic.tcc.agent.UsigBAcceptor;
import br.unb.cic.tcc.entity.Acceptor;
import br.unb.cic.tcc.messages.ProtocolMessage;
import br.unb.cic.tcc.messages.ProtocolMessageType;
import br.unb.cic.tcc.messages.UsigBProtocolMessage;
import quorum.communication.QuorumMessage;

public class UsigAcceptorReplica extends AcceptorReplica {
    public UsigAcceptorReplica(int id, String host, int port, Acceptor acceptor) {
        super(id, host, port, acceptor);
    }

    @Override
    public QuorumMessage executeRequest(QuorumMessage quorumMessage) {
        ProtocolMessage message = (ProtocolMessage) quorumMessage.getMsg();

        if(message.getProtocolMessageType() == ProtocolMessageType.MESSAGE_1A){
            getAcceptor().phase1b(message);
//            System.out.println("Acceptor: "+ acceptor.getAgentId() + "chamou a phase1b");

        } else if(message.getProtocolMessageType() == ProtocolMessageType.MESSAGE_2A
                ||message.getProtocolMessageType() == ProtocolMessageType.MESSAGE_2S){
            getAcceptor().phase2b(message);
//            System.out.println("Acceptor: "+ acceptor.getAgentId() + "chamou a phase2b");
        } else {
            ((UsigBAcceptor)getAcceptor()).updateCnt((UsigBProtocolMessage) message);
        }


        return null;
    }
}
