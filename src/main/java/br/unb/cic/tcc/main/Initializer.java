package br.unb.cic.tcc.main;

import br.unb.cic.tcc.entity.Acceptor;
import br.unb.cic.tcc.entity.Learner;
import br.unb.cic.tcc.entity.Proposer;
import br.unb.cic.tcc.quorum.Quoruns;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

public class Initializer {
    public static final String PROPOSERS = "#Proposers";
    public static final String LEANERS = "#Leaners";
    public static final String ACCEPTORS = "#Acceptors";

    public static void initializeQuoruns(){
        createQuorunsReadingFile();
        initializeProtocol();
    }

    private static void initializeProtocol() {
        Proposer coordinator = Quoruns.getCoordinators().get(0);
        coordinator.phase1A();
    }

    private static void createQuorunsReadingFile() {
        String filePath = "src/config"+System.getProperty("file.separator")+"hosts.config";

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath))){
            String actualLine = bufferedReader.readLine();

            while (actualLine != null){
                if(actualLine.contains(PROPOSERS)){
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
    }

    private static String insertOnQuorum(BufferedReader bufferedReader, String quorumName) throws IOException {
        String actualLine = bufferedReader.readLine();
        while (actualLine != null && !actualLine.startsWith("#")){
            StringTokenizer str = new StringTokenizer(actualLine," ");
            if(str.countTokens() > 2){
                int id = Integer.valueOf(str.nextToken());
                String host = str.nextToken();
                int port = Integer.valueOf(str.nextToken());

                switch (quorumName){
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

    private static void createProposer(int id, String host, int port){
        Quoruns.getProposers().add(proposerToAdd(id, host, port));
    }

    private static void createLeaner(int id, String host, int port){
        Quoruns.getLearners().add(learnerToAdd(id, host, port));
    }

    private static void createAcceptor(int id, String host, int port){
        Quoruns.getAcceptors().add(acceptorToAdd(id, host, port));
    }

    protected static Proposer proposerToAdd(int id, String host, int port){
        return new Proposer(id, host, port);
    }

    protected static Acceptor acceptorToAdd(int id, String host, int port){
        return new Acceptor(id, host, port);
    }

    protected static Learner learnerToAdd(int id, String host, int port){
        return new Learner(id, host, port);
    }

}
