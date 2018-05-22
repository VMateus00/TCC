package br.unb.cic.tcc.quorum;

import br.unb.cic.tcc.entity.Acceptor;
import br.unb.cic.tcc.entity.Leaner;
import br.unb.cic.tcc.entity.Proposer;
import br.unb.cic.tcc.messages.ClientMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Quoruns {
    public static final Random RANDOM = new Random();
    private static Quoruns quorum;

    private static List<Proposer> proposers = new ArrayList<>();
    private static List<Proposer> coordinators = new ArrayList<>();
    private static List<Leaner> leaners = new ArrayList<>();
    private static List<Acceptor> acceptors = new ArrayList<>();

    private Quoruns() {
    }

    public static void receiveClientMessage(ClientMessage clientMessage) {
        if(clientMessage != null){
            // escolhe um proposer aleatoriamente do quorum:
            int size = quorum.getProposers().size();
            Proposer proposer = quorum.getProposers().get(RANDOM.nextInt(size) - 1);

            // inicia o protocolo
            proposer.propose(clientMessage);
        }
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

    public static List<Proposer> getCoordinators() {
        if(coordinators == null){
            coordinators = getProposers().stream()
                    .filter(p->p.isCoordinator()).collect(Collectors.toList());
        }
        return coordinators;
    }
}
