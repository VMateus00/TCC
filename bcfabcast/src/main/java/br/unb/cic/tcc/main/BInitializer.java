package br.unb.cic.tcc.main;

import br.unb.cic.tcc.agent.BAcceptor;
import br.unb.cic.tcc.agent.BLearner;
import br.unb.cic.tcc.agent.BProposer;
import br.unb.cic.tcc.entity.Acceptor;
import br.unb.cic.tcc.entity.Learner;
import br.unb.cic.tcc.entity.Proposer;

public class BInitializer extends Initializer {

    private static BInitializer singletonInstance = null;

    private BInitializer() {
        // EMPTY METHOD
    }

    public static BInitializer getSingletonInstance(){
        if (singletonInstance == null){
            singletonInstance = new BInitializer();
        }
        return singletonInstance;
    }

    @Override
    protected Proposer proposerToAdd(int id, String host, int port){
        return new BProposer(id, host, port);
    }

    @Override
    protected Acceptor acceptorToAdd(int id, String host, int port){
        return new BAcceptor(id, host, port);
    }

    @Override
    Learner learnerToAdd(int id, String host, int port) {
        return new BLearner(id, host, port);
    }

}
