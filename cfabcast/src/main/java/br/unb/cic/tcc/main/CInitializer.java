package br.unb.cic.tcc.main;

import br.unb.cic.tcc.entity.Acceptor;
import br.unb.cic.tcc.entity.Coordinator;
import br.unb.cic.tcc.entity.Learner;
import br.unb.cic.tcc.entity.Proposer;

public class CInitializer extends Initializer {

    private static CInitializer singletonInstance = null;

    private CInitializer() {
        // EMPTY METHOD
    }

    public static CInitializer getSingletonInstance(){
        if (singletonInstance == null){
            singletonInstance = new CInitializer();
        }
        return singletonInstance;
    }

    @Override
    Coordinator coordinatorToAdd(int id, String host, int port) {
        return new Coordinator(id, host, port);
    }

    Proposer proposerToAdd(int id, String host, int port) {
        return new Proposer(id, host, port);
    }

    Acceptor acceptorToAdd(int id, String host, int port) {
        return new Acceptor(id, host, port);
    }

    Learner learnerToAdd(int id, String host, int port) {
        return new Learner(id, host, port);
    }
}
