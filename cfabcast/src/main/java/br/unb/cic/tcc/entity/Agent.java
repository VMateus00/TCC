package br.unb.cic.tcc.entity;

import br.unb.cic.tcc.main.Initializer;
import br.unb.cic.tcc.messages.ClientMessage;
import org.apache.commons.lang3.ArrayUtils;
import quorum.core.QuorumReplica;
import quorum.core.QuorumSender;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public abstract class Agent<QR extends QuorumReplica, QS extends QuorumSender> {
    private static final Integer QTD_FALHAS_ESPERADAS = 1;
    public static final Integer QTD_QUORUM_ACCEPTORS_CRASH = 2* QTD_FALHAS_ESPERADAS +1;
    public static final Integer QTD_QUORUM_ACCEPTORS_BIZANTINO = 5* QTD_FALHAS_ESPERADAS +1;
    public static final Integer QTD_QUORUM_ACCEPTORS_USIG = 2* QTD_FALHAS_ESPERADAS +1;

    protected static final Integer QTD_MINIMA_RESPOSTAS_QUORUM_ACCEPTORS_CRASH = QTD_FALHAS_ESPERADAS +1;
    protected static final Integer QTD_MINIMA_RESPOSTAS_QUORUM_ACCEPTORS_BIZANTINO = 4* QTD_FALHAS_ESPERADAS +1;
    protected static final Integer QTD_MINIMA_RESPOSTAS_QUORUM_ACCEPTORS_USIG = QTD_FALHAS_ESPERADAS +1;

    private Integer agentId;
    private QR quorumReplica;
    private QS quorumSender;
    protected Map<String, Set<Integer>> idAgentes;

    protected File agentControllerFile;
    protected FileWriter fileWriter;
//    protected PrintWriter printWriter;
    protected BufferedWriter printWriter;

    private static Integer roundAtual = 1;

    // VMAP<ROUND, MAP<PROPOSER_ID, MSG>>
    private Map<Integer, Map<Integer, Set<ClientMessage>>> vMap = new HashMap<>();

    public Integer getAgentId() {
        return agentId;
    }

    public void setAgentId(Integer agentId) {
        this.agentId = agentId;
    }

    public QR getQuorumReplica() {
        return quorumReplica;
    }

    public void setQuorumReplica(QR quorumReplica) {
        this.quorumReplica = quorumReplica;
    }

    public QS getQuorumSender() {
        return quorumSender;
    }

    public void setQuorumSender(QS quorumSender) {
        this.quorumSender = quorumSender;
    }

    public Map<Integer, Map<Integer, Set<ClientMessage>>> getvMap() {
        return vMap;
    }

    public void setvMap(Map<Integer, Map<Integer, Set<ClientMessage>>> vMap) {
        this.vMap = vMap;
    }

    public Map<String, Set<Integer>> getIdAgentes() {
        return idAgentes;
    }

    protected Map<Integer, Set<ClientMessage>> getMapFromRound(Integer round) {
        Map<Integer, Set<ClientMessage>> mapOfRound = getvMap().get(round);
        if (mapOfRound == null) {
            mapOfRound = new ConcurrentHashMap<>();
            getvMap().put(round, mapOfRound);
        }
        return mapOfRound;
    }

    public static Integer getRoundAtual() {
        return roundAtual;
    }

    public int[] idLearners(){
        return idAgentes.get(Initializer.LEANERS).stream().mapToInt(p->p).toArray();
    }

    public int[] idAcceptors(){
        return idAgentes.get(Initializer.ACCEPTORS).stream().mapToInt(p->p).toArray();
    }

    public int[] idProposers(){
        return idAgentes.get(Initializer.PROPOSERS).stream().mapToInt(p->p).toArray();
    }

    public int[] idCoordinator(){
        return idAgentes.get(Initializer.PROPOSERS).stream().filter(p->p == 1).mapToInt(p->p).toArray();
    }

    public int[] idCoordinatorAndLearners(){
        return ArrayUtils.addAll(idCoordinator(), idLearners());
    }

    public boolean isColisionFastProposer(Integer idProposer){ // Esse metodo só deve ser chamado pelos proposers
        return idProposer < 4; // REGRA ARBITRARIA, pode ser alterada dps
    }

    public int[] idCFProposers(int currentProposer){
        return idAgentes.get(Initializer.PROPOSERS).stream()
                .filter(p->!p.equals(currentProposer))
                .filter(this::isColisionFastProposer).mapToInt(p->p).toArray();
    }

    public int[] idNCFProposers(){
        return idAgentes.get(Initializer.PROPOSERS).stream()
                .filter(p->!this.isColisionFastProposer(p)).mapToInt(p->p).toArray();
    }

    public int[] idAcceptorsAndProposers(){
        return ArrayUtils.addAll(idAcceptors(), idProposers());
    }

    public int[] idAcceptorsAndCFProposers(int currentProposer){
        return ArrayUtils.addAll(idAcceptors(), idCFProposers(currentProposer));
    }

    public int[] idAcceptorsAndLearnersAndCFProposers(int currentProposer){
        return ArrayUtils.addAll(idLearners(), idAcceptorsAndCFProposers(currentProposer));
    }

    protected abstract String fileName();

    protected void createFile(){
        agentControllerFile = new File(fileName());
        try {
            if(agentControllerFile.createNewFile()){
                System.out.println("File sucessifully created");
            } else {
                System.out.println("some problemm ocurred while creating file");
            }
            fileWriter = new FileWriter(agentControllerFile);
//            printWriter = new PrintWriter(fileWriter);
            printWriter = new BufferedWriter(fileWriter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
