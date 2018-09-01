package br.unb.cic.tcc.main;

import br.unb.cic.tcc.entity.Acceptor;
import br.unb.cic.tcc.entity.Coordinator;
import br.unb.cic.tcc.entity.Learner;
import br.unb.cic.tcc.entity.Proposer;
import br.unb.cic.tcc.quorum.Quoruns;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

public abstract class Initializer{
    private static final String COORDINATOR = "#Coordinator";
    private static final String PROPOSERS = "#Proposers";
    private static final String LEANERS = "#Leaners";
    private static final String ACCEPTORS = "#Acceptors";

    private static boolean isAlreadyExecuted = false;

    public void initializeQuoruns(){
        createQuorunsReadingFile();
        initializeProtocol();
    }

    private void initializeProtocol() {
        Coordinator coordinator = Quoruns.getCoordinators().get(0);
        coordinator.phase1A();
    }

    private void createQuorunsReadingFile() {
        if(!isAlreadyExecuted){
            String filePath = "src/config"+System.getProperty("file.separator")+"hosts.config";

            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath))){
                String actualLine = bufferedReader.readLine();

                while (actualLine != null){
                    if(actualLine.contains(COORDINATOR)){
                        actualLine = insertOnQuorum(bufferedReader, COORDINATOR);
                    }else if(actualLine.contains(PROPOSERS)){
                        actualLine = insertOnQuorum(bufferedReader, PROPOSERS);
                    } else if(actualLine.contains(LEANERS)){
                        actualLine = insertOnQuorum(bufferedReader, LEANERS);
                    } else if(actualLine.contains(ACCEPTORS)){
                        actualLine = insertOnQuorum(bufferedReader, ACCEPTORS);
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

    private String insertOnQuorum(BufferedReader bufferedReader, String quorumName) throws IOException {
        String actualLine = bufferedReader.readLine();
        while (actualLine != null && !actualLine.startsWith("#")){
            StringTokenizer str = new StringTokenizer(actualLine," ");
            if(str.countTokens() > 2){
                int id = Integer.valueOf(str.nextToken());
                String host = str.nextToken();
                int port = Integer.valueOf(str.nextToken());

                switch (quorumName){
                    case COORDINATOR:
                        createCoordinator(id, host, port);
                        break;
                    case PROPOSERS:
                        createProposer(id, host, port);
                        break;
                    case LEANERS:
                        createLeaner(id, host, port);
                        break;
                    case ACCEPTORS:
                        createAcceptor(id, host, port);
                        break;
                    default:
                        break;
                }
            }
            actualLine = bufferedReader.readLine();
        }
        return actualLine;
    }

    private void createCoordinator(int id, String host, int port){
        Quoruns.getCoordinators().add(coordinatorToAdd(id, host, port));
    }

    private void createProposer(int id, String host, int port){
        Quoruns.getProposers().add(proposerToAdd(id, host, port));
    }

    private void createLeaner(int id, String host, int port){
        Quoruns.getLearners().add(learnerToAdd(id, host, port));
    }

    private void createAcceptor(int id, String host, int port){
        Quoruns.getAcceptors().add(acceptorToAdd(id, host, port));
    }

    abstract Coordinator coordinatorToAdd(int id, String host, int port);

    abstract Proposer proposerToAdd(int id, String host, int port);

    abstract Acceptor acceptorToAdd(int id, String host, int port);

    abstract Learner learnerToAdd(int id, String host, int port);
}
