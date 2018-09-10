package br.unb.cic.tcc.main;

import br.unb.cic.tcc.agent.UsigBAcceptor;
import br.unb.cic.tcc.agent.UsigBCoordinator;
import br.unb.cic.tcc.agent.UsigBLearner;
import br.unb.cic.tcc.agent.UsigBProposer;
import br.unb.cic.tcc.entity.Acceptor;
import br.unb.cic.tcc.entity.Coordinator;
import br.unb.cic.tcc.entity.Learner;
import br.unb.cic.tcc.entity.Proposer;
import br.unb.cic.tcc.quorum.Quoruns;

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
        if(Quoruns.getAcceptors().size() < Quoruns.QTD_QUORUM_ACCEPTORS_BIZANTINO){
            System.out.println("Não foi atingido o tamanho minimo do quorum de acceptors");
            System.exit(1);
        }
    }

    @Override
    Coordinator coordinatorToAdd(int id, String host, int port) {
        return new UsigBCoordinator(id, host, port);
    }

    @Override
    protected Proposer proposerToAdd(int id, String host, int port){
        return new UsigBProposer(id, host, port);
    }

    @Override
    protected Acceptor acceptorToAdd(int id, String host, int port){
        return new UsigBAcceptor(id, host, port);
    }

    @Override
    Learner learnerToAdd(int id, String host, int port) {
        return new UsigBLearner(id, host, port);
    }

}
