package br.unb.cic.tcc.entity;

import br.unb.cic.tcc.messages.AcceptorToProposerMessage;
import br.unb.cic.tcc.messages.CFABCastMessageType;
import br.unb.cic.tcc.quorum.AcceptorReplica;
import br.unb.cic.tcc.quorum.AcceptorSender;

public class Acceptor extends Agent<AcceptorReplica, AcceptorSender> {
    private int currentRound = 0;

    public Acceptor(int id, String host, int port) {
        int agentId = nextId();

        AcceptorSender acceptorSender = new AcceptorSender(agentId);
        AcceptorReplica acceptorReplica = new AcceptorReplica(id, host, port, this,  acceptorSender);

        setAgentId(agentId);
        setQuorumSender(acceptorSender);
        setQuorumReplica(acceptorReplica);
    }

    public AcceptorToProposerMessage phase1b(int round){
        if(currentRound < round){ // rnd[a] < r
            currentRound = round;
            return new AcceptorToProposerMessage(this, round, CFABCastMessageType.PHASE_1B);
        }
        return null;
    }

    public void recebe1AFromCoordinator(int round){
        if(currentRound < round){
            currentRound = round;
        }
    }
}
