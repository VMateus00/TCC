package br.unb.cic.tcc.main;

import br.unb.cic.tcc.entity.Acceptor;
import br.unb.cic.tcc.entity.Coordinator;
import br.unb.cic.tcc.entity.Learner;
import br.unb.cic.tcc.entity.Proposer;
import br.unb.cic.tcc.quorum.Quoruns;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

public abstract class Initializer{
    public static final String COORDINATOR = "#Coordinator";
    public static final String PROPOSERS = "#Proposers";
    public static final String LEANERS = "#Leaners";
    public static final String ACCEPTORS = "#Acceptors";

    private static boolean isAlreadyExecuted = false;

    protected HashMap<String, Set<Integer>> agentsMap;

    public void initializeQuoruns(){
        createQuorunsReadingFile();
        initializeProtocol();
    }

    private void initializeProtocol() {
        verificaTamanhoQuorumAcceptors();
        Coordinator coordinator = Quoruns.getCoordinators().get(0);
        coordinator.phase1A();
    }

    protected void verificaTamanhoQuorumAcceptors() {
        if(Quoruns.getAcceptors().size() < Quoruns.QTD_QUORUM_ACCEPTORS_CRASH){
            System.out.println("Não foi atingido o tamanho minimo do quorum de acceptors");
            System.exit(1);
        }
    }

    private void createQuorunsReadingFile() {
        if(!isAlreadyExecuted){
            String filePath = "src/config"+System.getProperty("file.separator")+"hosts.config";
            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath))){
                agentsMap = criaMapComEnderecos(bufferedReader);
            }catch (Exception e){
                throw new RuntimeException("Erro ao criar map com os agentes", e);
            }

            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath))){
                String actualLine = bufferedReader.readLine();

                while (actualLine != null){
                    if(actualLine.contains(COORDINATOR)){
                        actualLine = insertOnQuorum(bufferedReader, COORDINATOR, agentsMap);
                    }else if(actualLine.contains(PROPOSERS)){
                        actualLine = insertOnQuorum(bufferedReader, PROPOSERS, agentsMap);
                    } else if(actualLine.contains(LEANERS)){
                        actualLine = insertOnQuorum(bufferedReader, LEANERS, agentsMap);
                    } else if(actualLine.contains(ACCEPTORS)){
                        actualLine = insertOnQuorum(bufferedReader, ACCEPTORS, agentsMap);
                    } else {
                        actualLine = bufferedReader.readLine();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            isAlreadyExecuted = true;
        }
    }

    private HashMap<String, Set<Integer>> criaMapComEnderecos(BufferedReader bufferedReader) throws IOException {
        String actualLine = bufferedReader.readLine();
        HashMap<String, Set<Integer>> agentsMap = new HashMap<>();

        while (actualLine != null){
            if(actualLine.contains(COORDINATOR)){
                actualLine = addAgentId(bufferedReader, agentsMap, COORDINATOR);
            }else if(actualLine.contains(PROPOSERS)){
                actualLine = addAgentId(bufferedReader, agentsMap, PROPOSERS);
            }else if(actualLine.contains(ACCEPTORS)){
                actualLine = addAgentId(bufferedReader, agentsMap, ACCEPTORS);
            }else if (actualLine.contains(LEANERS)){
                actualLine = addAgentId(bufferedReader, agentsMap, LEANERS);
            }else {
                actualLine = bufferedReader.readLine();
            }
        }
        return agentsMap;
    }

    private String addAgentId(BufferedReader bufferedReader, HashMap<String, Set<Integer>> agentsMap, String agentType) throws IOException {
        String actualLine = bufferedReader.readLine();
        while (actualLine != null && !actualLine.startsWith("#")){
            agentsMap.putIfAbsent(agentType, new HashSet<>());
            Set<Integer> integers = agentsMap.get(agentType);
            StringTokenizer stringTokenizer = new StringTokenizer(actualLine, " ");
            integers.add(Integer.valueOf(stringTokenizer.nextToken()));
            actualLine = bufferedReader.readLine();
        }
        return actualLine;
    }

    private String insertOnQuorum(BufferedReader bufferedReader, String quorumName, Map<String, Set<Integer>> agentsMap) throws IOException {
        String actualLine = bufferedReader.readLine();
        while (actualLine != null && !actualLine.startsWith("#")){
            StringTokenizer str = new StringTokenizer(actualLine," ");
            if(str.countTokens() > 2){
                int id = Integer.valueOf(str.nextToken());
                String host = str.nextToken();
                int port = Integer.valueOf(str.nextToken());

                switch (quorumName){
                    case COORDINATOR:
                        createCoordinator(id, host, port, agentsMap);
                        break;
                    case PROPOSERS:
                        createProposer(id, host, port, agentsMap);
                        break;
                    case LEANERS:
                        createLeaner(id, host, port, agentsMap);
                        break;
                    case ACCEPTORS:
                        createAcceptor(id, host, port, agentsMap);
                        break;
                    default:
                        break;
                }
            }
            actualLine = bufferedReader.readLine();
        }
        return actualLine;
    }

    private void createCoordinator(int id, String host, int port, Map<String, Set<Integer>> agentsMap){
        Quoruns.getCoordinators().add(coordinatorToAdd(id, host, port, agentsMap));
    }

    private void createProposer(int id, String host, int port, Map<String, Set<Integer>> agentsMap){
        Quoruns.getProposers().add(proposerToAdd(id, host, port, agentsMap));
    }

    private void createLeaner(int id, String host, int port, Map<String, Set<Integer>> agentsMap){
        Quoruns.getLearners().add(learnerToAdd(id, host, port, agentsMap));
    }

    private void createAcceptor(int id, String host, int port, Map<String, Set<Integer>> agentsMap){
        Quoruns.getAcceptors().add(acceptorToAdd(id, host, port, agentsMap));
    }

    abstract Coordinator coordinatorToAdd(int id, String host, int port, Map<String, Set<Integer>> agentsMap);

    abstract Proposer proposerToAdd(int id, String host, int port, Map<String, Set<Integer>> agentsMap);

    abstract Acceptor acceptorToAdd(int id, String host, int port, Map<String, Set<Integer>> agentsMap);

    abstract Learner learnerToAdd(int id, String host, int port, Map<String, Set<Integer>> agentsMap);
}
