package br.unb.cic.tcc.entity;

import br.unb.cic.tcc.messages.ClientMessage;
import br.unb.cic.tcc.quorum.ProposerReplica;
import br.unb.cic.tcc.quorum.ProposerSender;

import java.util.HashMap;

public class Proposer extends Agent<ProposerReplica, ProposerSender> {
    private int currentRound = 0;
    private Object currentValue;

    // TODO revisar uma melhor solucao
    public Proposer(int id, String host, int port) {
        int agentId = nextId();
        ProposerSender proposerSender = new ProposerSender(agentId);
        ProposerReplica proposerReplica = new ProposerReplica(id, host, port, this, proposerSender);

        setAgentId(agentId);
        setQuorumReplica(proposerReplica);
        setQuorumSender(proposerSender);
    }

    public void phase1A(int round){
        if(isCoordinator() && currentRound < round){ // verificar se é mesmo essa a condição
            currentRound = round; // crnd[c] = <- r
            // current round proposer
            setvMap(new HashMap<>()); //cval recebe vazio (cval[c] <- none)
//            send msg to ACCEPTOR
            getQuorumSender().send1AToAcceptor(round);
        }
    }

    public void phase2A(){

    }

    public void phase2Start(){

    }

    public void phase2Prepare(){

    }

    public boolean isCoordinator(){
        return getAgentId() == 1;
    }

    public void propose(ClientMessage clientMessage) {
//        TODO começar fase 1A
        int round = 1; // TODO verificar qual o valor inicial do round
        phase1A(round); // enviar isso para todos os proposers
    }
}
