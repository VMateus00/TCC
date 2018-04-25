package br.unb.cic.tcc.main;

import br.unb.cic.tcc.entity.Leaner;
import br.unb.cic.tcc.entity.Proposer;
import br.unb.cic.tcc.quorum.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

public class Initializer {
    public static final String PROPOSERS = "#Proposers";
    public static final String LEANERS = "#Leaners";
    public static final String ACCEPTORS = "#Acceptors";
    private static int AGENT_ID = 1;

    private static Quoruns quoruns = Quoruns.getSingleton();

    public static void initializeQuoruns(){
        createQuorunsReadingFile();
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
        String actualLine;
        while (!(actualLine = bufferedReader.readLine()).startsWith("#")){
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
                        break;
                    default:
                        break;
                }
            }
        }
        return actualLine;
    }

    private static void createProposer(int id, String host, int port){
        ProposerSender proposerSender = new ProposerSender(AGENT_ID);
        ProposerReplica proposerReplica = new ProposerReplica(id, host, port);
        quoruns.getProposers().add(new Proposer(AGENT_ID++, proposerReplica, proposerSender));
    }

    private static void createLeaner(int id, String host, int port){
        LeanerSender leanerSender = new LeanerSender(AGENT_ID);
        LeanerReplica leanerReplica = new LeanerReplica(id, host, port);
        quoruns.getLeaners().add(new Leaner(AGENT_ID++, leanerReplica, leanerSender));
    }

//    private static void createAcceptor(int id, String host, int port){
//        LeanerSender leanerSender = new LeanerSender(AGENT_ID);
//        LeanerReplica leanerReplica = new LeanerReplica(id, host, port);
//        leaners.add(new Leaner(AGENT_ID++, leanerReplica, leanerSender));
//    }
}
