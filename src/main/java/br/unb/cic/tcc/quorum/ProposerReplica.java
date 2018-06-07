package br.unb.cic.tcc.quorum;

import br.unb.cic.tcc.entity.Proposer;
import br.unb.cic.tcc.messages.ProtocolMessage;
import br.unb.cic.tcc.messages.ProtocolMessageType;
import quorum.communication.QuorumMessage;
import quorum.core.QuorumReplica;

import java.util.List;

public class ProposerReplica extends QuorumReplica {

    private Proposer proposer;
    private ProposerSender proposerSender;

    public ProposerReplica(int id, String host, int port, Proposer proposer, ProposerSender proposerSender) {
        super(id, "", host, port);

        this.proposer = proposer;
        this.proposerSender = proposerSender;
    }

    @Override
    public QuorumMessage executeRequest(QuorumMessage quorumMessage) {
        ProtocolMessage protocolMessage = (ProtocolMessage) quorumMessage.getMsg();
        if(protocolMessage.getProtocolMessageType() == ProtocolMessageType.MESSAGE_1B){
            System.out.println("Coordinator recebeu o 1B");
            proposer.phase2Start(protocolMessage);
        }

        if(protocolMessage.getProtocolMessageType() == ProtocolMessageType.MESSAGE_PROPOSE){
            System.out.println("Colision fast proposer foi chamado");
            proposer.phase2A(protocolMessage);
        }

        return null;
    }

    @Override
    public QuorumMessage executeReconfigurationMessage(QuorumMessage quorumMessage) {
        return null;
    }

    private Proposer getCoordinator(){
        List<Proposer> coordinators = Quoruns.getCoordinators();
        int coordinatorPosition = Quoruns.RANDOM.nextInt(coordinators.size());

        return coordinators.get(coordinatorPosition == 0 ? coordinatorPosition : coordinatorPosition - 1);
    }
}
