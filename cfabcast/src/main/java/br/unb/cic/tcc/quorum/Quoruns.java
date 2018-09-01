package br.unb.cic.tcc.quorum;

import br.unb.cic.tcc.entity.Acceptor;
import br.unb.cic.tcc.entity.Agent;
import br.unb.cic.tcc.entity.Coordinator;
import br.unb.cic.tcc.entity.Learner;
import br.unb.cic.tcc.entity.Proposer;
import br.unb.cic.tcc.messages.ClientMessage;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

public class Quoruns {
    private static final Integer QTD_ERROS_BIZANTINOS_MAXIMOS_ESPERADOS = 1;
    public static final Integer TAMANHO_MINIMO_QUORUM_ACCEPTORS_BIZANTINO = 5*QTD_ERROS_BIZANTINOS_MAXIMOS_ESPERADOS+1;
    public static final Integer TAMANHO_MINIMO_QUORUM_ACCEPTORS_CRASH = 2*QTD_ERROS_BIZANTINOS_MAXIMOS_ESPERADOS+1;

    private static final Random RANDOM = new Random();

    private static Integer roundAtual = 1;

    private static boolean podeAtualizarRound = false;

    private static List<Proposer> proposers = new ArrayList<>();
    private static List<Coordinator> coordinators = new ArrayList<>();
    private static List<Learner> learners = new ArrayList<>();
    private static List<Acceptor> acceptors = new ArrayList<>();


    private static Map<Integer, Set<Integer>> listaDeRoundsConcluidos = new HashMap<>(); // <round, learners>

    private Quoruns() {
    }

    public static void receiveClientMessage(ClientMessage clientMessage) {
        if(clientMessage != null){
            // escolhe um proposer aleatoriamente do quorum:
            int size = Quoruns.getProposers().size();
            int positionProposerEscolhido = RANDOM.nextInt(size) - 1;
            positionProposerEscolhido = positionProposerEscolhido > 0 ? positionProposerEscolhido : 0;
            Proposer proposer = Quoruns.getProposers().get(positionProposerEscolhido);

            // inicia o protocolo
            proposer.propose(clientMessage);
        }
    }

    public static List<Proposer> getProposers() {
        return proposers;
    }

    public static List<Learner> getLearners() {
        return learners;
    }

    public static List<Acceptor> getAcceptors() {
        return acceptors;
    }

    public static List<Coordinator> getCoordinators() {
        return coordinators;
    }

    public static  List<Proposer> getCFProposersOnRound(int round){
        return proposers.stream().filter(Proposer::isColisionFastProposer).collect(Collectors.toList());
    }

    public static int[] idLearners(){
         return learners.stream().mapToInt(Learner::getAgentId).toArray();
    }

    public static int[] idProposers(){
         return proposers.stream().mapToInt(Agent::getAgentId).toArray();
    }

    public static int[] idNCFProposers(int round){ // NOT COLISION FAST PROPOSERS
         return proposers.stream().filter(p-> !p.isColisionFastProposer()).mapToInt(Proposer::getAgentId).toArray();
    }

    public static int[] idAcceptors(){
         return acceptors.stream().mapToInt(Acceptor::getAgentId).toArray();
    }

    public static int[] idCoordinators(int currentRound) {
        return coordinators.stream().mapToInt(Coordinator::getAgentId).toArray();
    }

    public static int[] idCFProposers(int round) {
        return getCFProposersOnRound(round).stream().filter(Proposer::isColisionFastProposer).mapToInt(Proposer::getAgentId).toArray();
    }

    // false se nao pertence aos CFProposers ou se nao for
    public static boolean isCFProposerOnRound(int agendId, int round){
        return getCFProposersOnRound(round).stream().filter(Proposer::isColisionFastProposer).anyMatch(p->p.getAgentId().equals(agendId));
    }

    public static int[] idAcceptorsAndCFProposers(int round) {
        return ArrayUtils.addAll(idAcceptors(), idCFProposers(round));
    }

    public static int[] idAcceptorsAndProposers() {
        return ArrayUtils.addAll(idAcceptors(), idProposers());
    }

    public static Integer getRoundAtual(){
        return roundAtual;
    }

    public static synchronized void atualizaRound(){
        if(podeAtualizarRound){
            Quoruns.roundAtual++;
        }
        podeAtualizarRound = false;
    }

    public static synchronized void liberaAtualizacaoRound(Integer learnerId, Integer round){
        Set<Integer> roundsExecutados = listaDeRoundsConcluidos.putIfAbsent(round, new HashSet<>());
        if(roundsExecutados == null){
            roundsExecutados = listaDeRoundsConcluidos.put(round, new HashSet<>());
        }
        roundsExecutados.add(learnerId);

        if(roundsExecutados.size() == Quoruns.getLearners().size()){
            Quoruns.podeAtualizarRound = true;
        }
    }
}
