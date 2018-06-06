package br.unb.cic.tcc.quorum;

import br.unb.cic.tcc.messages.AcceptorToProposerMessage;
import br.unb.cic.tcc.messages.ProtocolMessageType;
import br.unb.cic.tcc.messages.ProposerToAcceptorMessage;
import quorum.communication.MessageType;
import quorum.communication.QuorumMessage;
import quorum.core.QuorumSender;

public class ProposerSender extends QuorumSender {
    public ProposerSender(int processId) {
        super(processId);
    }

    @Override
    public void replyReceived(QuorumMessage quorumMessage) {
        if(quorumMessage.getMsg() instanceof AcceptorToProposerMessage){
            AcceptorToProposerMessage testMessage = (AcceptorToProposerMessage) quorumMessage.getMsg();
            if(testMessage.getProtocolMessageType() == ProtocolMessageType.MESSAGE_1B){
                System.out.println("Terminou fase 1B \n\n\n");
            }
        }
    }

    public void send1AToAcceptor(int round) {
        ProposerToAcceptorMessage proposerToAcceptorMessage = new ProposerToAcceptorMessage(round, ProtocolMessageType.MESSAGE_1A);
        System.out.println("Proposer terminou fase 1A");
        // TODO evoluir biblioteca para enviar para apenas um quorum ou apenas um tipo de quorum
        this.multicast(new QuorumMessage(MessageType.QUORUM_REQUEST, proposerToAcceptorMessage, getProcessId()));
    }
}
