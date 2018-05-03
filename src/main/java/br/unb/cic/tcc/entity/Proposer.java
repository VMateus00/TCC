package br.unb.cic.tcc.entity;

import br.unb.cic.tcc.quorum.ProposerReplica;
import br.unb.cic.tcc.quorum.ProposerSender;

public class Proposer extends Agent {

    // TODO revisar uma melhor solucao
    public Proposer(int id, String host, int port) {
        int agentId = nextId();
        ProposerSender proposerSender = new ProposerSender(agentId);
        ProposerReplica proposerReplica = new ProposerReplica(id, host, port, this, proposerSender);

        setAgentId(agentId);
        setQuorumReplica(proposerReplica);
        setQuorumSender(proposerSender);
    }

    public void phase1A(){
        // IF
//        c = C(r)
//        crnd[c] < r
        if(isCoordinator()){ // verificar se é mesmo essa a condição
            // current round proposer
            // TODO
            // current round cordinator recebe r?  (crd[c] <- r)
            //cval recebe vazio (cval[c] <- none)

//            send msg to ACCEPTOR
        }
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

    public boolean isCoordinator(){
        return getAgentId() == 1;
    }
}
