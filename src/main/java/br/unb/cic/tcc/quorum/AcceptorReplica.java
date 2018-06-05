package br.unb.cic.tcc.quorum;

import br.unb.cic.tcc.entity.Acceptor;
import br.unb.cic.tcc.messages.ClientMessage;
import br.unb.cic.tcc.messages.ProposerToAcceptorMessage;
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
        Object message = quorumMessage.getMsg();
        if(message instanceof ClientMessage){
            return null;
        }

        if(message instanceof ProposerToAcceptorMessage){
            ProposerToAcceptorMessage proposerToAcceptorMessage = (ProposerToAcceptorMessage) message;
//            quorumMessage.setMsg(acceptor.phase1b(proposerToAcceptorMessage.getRound()));
            return quorumMessage;
        }
        // TODO
        return null;
    }

    @Override
    public QuorumMessage executeReconfigurationMessage(QuorumMessage quorumMessage) {
        // TODO
        return null;
    }
}
