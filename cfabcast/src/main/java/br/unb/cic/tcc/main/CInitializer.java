package br.unb.cic.tcc.main;

import br.unb.cic.tcc.entity.Acceptor;
import br.unb.cic.tcc.entity.Coordinator;
import br.unb.cic.tcc.entity.Learner;
import br.unb.cic.tcc.entity.Proposer;

import java.util.Map;
import java.util.Set;

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
    Coordinator coordinatorToAdd(int id, String host, int port, Map<String, Set<Integer>> agentsMap) {
        return new Coordinator(id, host, port, agentsMap);
    }

    Proposer proposerToAdd(int id, String host, int port, Map<String, Set<Integer>> agentsMap) {
        return new Proposer(id, host, port, agentsMap);
    }

    Acceptor acceptorToAdd(int id, String host, int port, Map<String, Set<Integer>> agentsMap) {
        return new Acceptor(id, host, port, agentsMap);
    }

    Learner learnerToAdd(int id, String host, int port, Map<String, Set<Integer>> agentsMap) {
        return new Learner(id, host, port, agentsMap);
    }
}
