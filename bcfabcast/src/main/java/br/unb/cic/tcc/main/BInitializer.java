package br.unb.cic.tcc.main;

import br.unb.cic.tcc.agent.BAcceptor;
import br.unb.cic.tcc.agent.BCoordinator;
import br.unb.cic.tcc.agent.BLearner;
import br.unb.cic.tcc.agent.BProposer;
import br.unb.cic.tcc.entity.Acceptor;
import br.unb.cic.tcc.entity.Coordinator;
import br.unb.cic.tcc.entity.Learner;
import br.unb.cic.tcc.entity.Proposer;
import br.unb.cic.tcc.quorum.Quoruns;

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

    protected void verificaTamanhoQuorumAcceptors() {
        if(Quoruns.getAcceptors().size() < Quoruns.QTD_QUORUM_ACCEPTORS_BIZANTINO){
            System.out.println("Não foi atingido o tamanho minimo do quorum de acceptors");
            System.exit(1);
        }
    }

    @Override
    Coordinator coordinatorToAdd(int id, String host, int port) {
        return new BCoordinator(id, host, port);
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
