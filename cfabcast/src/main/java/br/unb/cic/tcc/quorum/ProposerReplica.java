package br.unb.cic.tcc.quorum;

import br.unb.cic.tcc.entity.Proposer;
import br.unb.cic.tcc.messages.ClientMessage;
import br.unb.cic.tcc.messages.ProtocolMessage;
import br.unb.cic.tcc.messages.ProtocolMessageType;
import quorum.communication.QuorumMessage;
import quorum.core.QuorumReplica;

public class ProposerReplica extends QuorumReplica {

    private Proposer proposer;

    public ProposerReplica(int id, String host, int port, Proposer proposer) {
        super(id, "", host, port);
        this.proposer = proposer;
    }

    @Override
    public QuorumMessage executeRequest(QuorumMessage quorumMessage) {
        if(quorumMessage.getMsg() instanceof ClientMessage){
            proposer.propose((ClientMessage) quorumMessage.getMsg());
        } else {
            ProtocolMessage protocolMessage = (ProtocolMessage) quorumMessage.getMsg();
            if (protocolMessage.getProtocolMessageType() == ProtocolMessageType.MESSAGE_PROPOSE) {
                System.out.println("Colision fast proposer foi chamado");
                proposer.phase2A(protocolMessage);

            } else if (protocolMessage.getProtocolMessageType() == ProtocolMessageType.MESSAGE_2S) {
                System.out.println("Fase 2Prepare foi chamada");
                proposer.phase2Prepare(protocolMessage);

            } else if (protocolMessage.getProtocolMessageType() == ProtocolMessageType.MESSAGE_2A) {
                System.out.println("Colision fast proposer foi chamado");
                proposer.phase2A(protocolMessage);
            }
        }

        return null;
    }

    @Override
    public QuorumMessage executeReconfigurationMessage(QuorumMessage quorumMessage) {
        throw new UnsupportedOperationException("Not utilized in this protocol");
    }
}
