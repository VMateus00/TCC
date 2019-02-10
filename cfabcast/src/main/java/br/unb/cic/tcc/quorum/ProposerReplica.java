package br.unb.cic.tcc.quorum;

import br.unb.cic.tcc.entity.Proposer;
import br.unb.cic.tcc.messages.ClientMessage;
import br.unb.cic.tcc.messages.ProtocolMessage;
import br.unb.cic.tcc.messages.ProtocolMessageType;
import quorum.communication.QuorumMessage;
import quorum.core.QuorumReplica;

import java.io.IOException;

public class ProposerReplica extends QuorumReplica {

    private Proposer proposer;

    public ProposerReplica(int id, String host, int port, Proposer proposer) {
        super(id, "", host, port);
        this.proposer = proposer;
    }

    @Override
    public QuorumMessage executeRequest(QuorumMessage quorumMessage) {
        try {
            if (quorumMessage.getMsg() instanceof ClientMessage) {
                    proposer.propose((ClientMessage) quorumMessage.getMsg());
            } else {
                ProtocolMessage protocolMessage = (ProtocolMessage) quorumMessage.getMsg();
                if (protocolMessage.getProtocolMessageType() == ProtocolMessageType.MESSAGE_PROPOSE) {
    //                System.out.println("Colision fast proposer foi chamado");
                    proposer.phase2A(protocolMessage);
                } else if (protocolMessage.getProtocolMessageType() == ProtocolMessageType.MESSAGE_1B) {
    //                System.out.println("Coordinator recebeu o 1B");
                    proposer.phase2Start(protocolMessage);
                } else if (protocolMessage.getProtocolMessageType() == ProtocolMessageType.MESSAGE_2S) {
    //                System.out.println("Fase 2Prepare foi chamada");
                    proposer.phase2Prepare(protocolMessage);
                } else if (protocolMessage.getProtocolMessageType() == ProtocolMessageType.MESSAGE_2A) {
                    System.out.println("Colision fast proposer foi chamado");
                    proposer.phase2A(protocolMessage);
                }
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
}
