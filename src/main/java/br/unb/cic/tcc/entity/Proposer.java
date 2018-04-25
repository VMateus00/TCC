package br.unb.cic.tcc.entity;

import quorum.core.QuorumReplica;
import quorum.core.QuorumSender;

public class Proposer extends Agent {

    public Proposer(Integer agentId, QuorumReplica quorumReplica, QuorumSender quorumSender) {
        super(agentId, quorumReplica, quorumSender);
    }

    public void phase1A(){

    }

    public void phase2A(){

    }

    public void phase2Start(){

    }

    public void phase2Prepare(){

    }

    public void propose(){

        phase1A();
    }

//    public boolean isCoordinator(){
//        return getAgentId() == 1;
//    }
}
