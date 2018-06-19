package br.unb.cic.tcc.quorum;

import br.unb.cic.tcc.entity.Acceptor;
import br.unb.cic.tcc.entity.Agent;
import br.unb.cic.tcc.entity.Leaner;
import br.unb.cic.tcc.entity.Proposer;
import br.unb.cic.tcc.messages.ClientMessage;
import org.apache.commons.lang3.ArrayUtils;

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
            int positionProposerEscolhido = RANDOM.nextInt(size) - 1;
            positionProposerEscolhido = positionProposerEscolhido > 0 ? positionProposerEscolhido : 0;
            Proposer proposer = quorum.getProposers().get(positionProposerEscolhido);

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
        if(coordinators.isEmpty()){
            coordinators = getProposers().stream()
                    .filter(Proposer::isCoordinator).collect(Collectors.toList());
        }
        return coordinators;
    }

    public static  List<Proposer> getCFProposers(){
        return proposers.stream().filter(Proposer::isColisionFastProposer).collect(Collectors.toList());
    }

    public static int[] idLeaners(){
         return leaners.stream().mapToInt(Leaner::getAgentId).toArray();
    }

    public static int[] idProposers(){
         return proposers.stream().mapToInt(Agent::getAgentId).toArray();
    }

    public static int[] idAcceptors(){
         return acceptors.stream().mapToInt(Acceptor::getAgentId).toArray();
    }

    public static int[] idCoordinators() {
        return coordinators.stream().mapToInt(Proposer::getAgentId).toArray();
    }

    public static int[] idCFProposers() {
        return getCFProposers().stream().filter(Proposer::isColisionFastProposer).mapToInt(Proposer::getAgentId).toArray();
    }

    // false se nao pertence aos CFProposers ou se nao for
    public static boolean isCFProposer(int id){
        return getCFProposers().stream().filter(Proposer::isColisionFastProposer).anyMatch(p->p.getAgentId().equals(id));
    }

    public static int[] idAcceptorsAndCFProposers() {
        return ArrayUtils.addAll(idAcceptors(), idCFProposers());
    }

    public static int[] idAcceptorsAndProposers() {
        return ArrayUtils.addAll(idAcceptors(), idProposers());
    }
}
