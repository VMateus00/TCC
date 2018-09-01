package br.unb.cic.tcc.quorum;

import br.unb.cic.tcc.entity.Coordinator;
import br.unb.cic.tcc.messages.ProtocolMessage;
import br.unb.cic.tcc.messages.ProtocolMessageType;
import quorum.communication.QuorumMessage;
import quorum.core.QuorumReplica;

public class CoordinatorReplica extends QuorumReplica {

    private Coordinator coordinator;

    public CoordinatorReplica(int id, String host, int port, Coordinator coordinator) {
        super(id, "", host, port);
        this.coordinator = coordinator;
    }

    @Override
    public QuorumMessage executeRequest(QuorumMessage quorumMessage) {
        ProtocolMessage protocolMessage = (ProtocolMessage) quorumMessage.getMsg();
        if (protocolMessage.getProtocolMessageType() == ProtocolMessageType.MESSAGE_1B) {
            System.out.println("Coordinator recebeu o 1B");
            coordinator.phase2Start(protocolMessage);
        }
        return null;
    }

    @Override
    public QuorumMessage executeReconfigurationMessage(QuorumMessage quorumMessage) {
        throw new UnsupportedOperationException("Not utilized in this protocol");
    }
}
