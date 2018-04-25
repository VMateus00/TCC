package br.unb.cic.tcc.quorum;

import br.unb.cic.tcc.entity.Acceptor;
import br.unb.cic.tcc.entity.Leaner;
import br.unb.cic.tcc.entity.Proposer;

import java.util.ArrayList;
import java.util.List;

public class Quoruns {
    private static Quoruns quorum;

    private List<Proposer> proposers = new ArrayList<>();
    private List<Leaner> leaners = new ArrayList<>();
    private List<Acceptor> acceptors = new ArrayList<>();

    private Quoruns() {
    }

    public static Quoruns getSingleton(){
        if(quorum == null)
            quorum = new Quoruns();
        return quorum;
    }

    public List<Proposer> getProposers() {
        return proposers;
    }

    public List<Leaner> getLeaners() {
        return leaners;
    }

    public List<Acceptor> getAcceptors() {
        return acceptors;
    }
}
