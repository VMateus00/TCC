package br.unb.cic.tcc.main;

import br.unb.cic.tcc.agent.UsigBAcceptor;
import br.unb.cic.tcc.agent.UsigBCoordinator;
import br.unb.cic.tcc.agent.UsigBLearner;
import br.unb.cic.tcc.agent.UsigBProposer;
import br.unb.cic.tcc.entity.Acceptor;
import br.unb.cic.tcc.entity.Agent;
import br.unb.cic.tcc.entity.Learner;
import br.unb.cic.tcc.entity.Proposer;
import br.unb.cic.tcc.quorum.Quoruns;

import java.util.Map;
import java.util.Set;

public class UsigBInitializer extends Initializer {

    private static UsigBInitializer singletonInstance = null;

    private UsigBInitializer() {
        // EMPTY METHOD
    }

    public static UsigBInitializer getSingletonInstance(){
        if (singletonInstance == null){
            singletonInstance = new UsigBInitializer();
        }
        return singletonInstance;
    }

    protected void verificaTamanhoQuorumAcceptors() {
        if(Quoruns.getAcceptors().size() < Agent.QTD_QUORUM_ACCEPTORS_USIG){
            System.out.println("Não foi atingido o tamanho minimo do quorum de acceptors");
            System.exit(1);
        }
    }

//    @Override
//    Coordinator coordinatorToAdd(int id, String host, int port, Map<String, Set<Integer>> agentsMap) {
//        return new UsigBCoordinator(id, host, port, getQtdAgents(), agentsMap);
//    }

    @Override
    protected Proposer proposerToAdd(int id, String host, int port, Map<String, Set<Integer>> agentsMap){
        return new UsigBProposer(id, host, port, getQtdAgents(), agentsMap);
    }

    @Override
    protected Acceptor acceptorToAdd(int id, String host, int port, Map<String, Set<Integer>> agentsMap){
        return new UsigBAcceptor(id, host, port, getQtdAgents(), agentsMap);
    }

    @Override
    Learner learnerToAdd(int id, String host, int port, Map<String, Set<Integer>> agentsMap) {
        return new UsigBLearner(id, host, port, getQtdAgents(), agentsMap);
    }

    private Integer getQtdAgents(){

        Integer[] qtdAgents = new Integer[1];
        qtdAgents[0] = 0;

        agentsMap.forEach((k,v)->
            qtdAgents[0] = qtdAgents[0]+v.size());
        return qtdAgents[0];
    }
}
