package br.unb.cic.tcc.main;

import br.unb.cic.tcc.entity.Acceptor;
import br.unb.cic.tcc.entity.Leaner;
import br.unb.cic.tcc.entity.Proposer;
import br.unb.cic.tcc.quorum.ProposerReplica;
import br.unb.cic.tcc.quorum.ProposerSender;

import java.util.ArrayList;
import java.util.List;

public class Initializer {

    private static List<Proposer> proposers;
    private static List<Leaner> leaners;
    private static List<Acceptor> acceptors;

    public static void initializeQuoruns(){
        proposers = new ArrayList<>();
        proposers.add(createInitialProposer());

        leaners = new ArrayList<>();

        acceptors = new ArrayList<>();
    }

    private static Proposer createInitialProposer(){
        ProposerSender proposerSender = new ProposerSender(1);
        ProposerReplica proposerReplica = new ProposerReplica(1, "", "127.0.0.1", 11000);
        return new Proposer(1, proposerReplica, proposerSender);
    }

    private static Leaner createInitialLeaner(){
        return new Leaner();
    }

    private static Acceptor createInitialAcceptor(){
        return new Acceptor();
    }

    public static List<Proposer> getProposers() {
        return proposers;
    }

    public static List<Leaner> getLeaners() {
        return leaners;
    }

    public static List<Acceptor> getAcceptors() {
        return acceptors;
    }
}
