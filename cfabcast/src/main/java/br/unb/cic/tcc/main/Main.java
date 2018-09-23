package br.unb.cic.tcc.main;

import br.unb.cic.tcc.client.Client;
import br.unb.cic.tcc.entity.Acceptor;
import br.unb.cic.tcc.entity.Coordinator;
import br.unb.cic.tcc.entity.Learner;
import br.unb.cic.tcc.entity.Proposer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import static br.unb.cic.tcc.main.Initializer.ACCEPTORS;
import static br.unb.cic.tcc.main.Initializer.COORDINATOR;
import static br.unb.cic.tcc.main.Initializer.LEANERS;
import static br.unb.cic.tcc.main.Initializer.PROPOSERS;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        if(args.length != 2){
            System.out.println("Devem ser passados dois argumentos");
        }

        Map<String, Set<Integer>> agentsMap;

        String filePath = "src/config"+System.getProperty("file.separator")+"hosts.config";
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath))){
            agentsMap = criaMapComEnderecos(bufferedReader);
        }catch (Exception e){
            throw new RuntimeException("Erro ao criar map com os agentes", e);
        }

        StringTokenizer str = new StringTokenizer(args[1]," ");
        String agentType = args[0];
        int id = Integer.valueOf(str.nextToken());
        String host = str.nextToken();
        int port = Integer.valueOf(str.nextToken());

        switch (agentType){
            case PROPOSERS:
                new Proposer(id, host, port, agentsMap);
                break;
            case COORDINATOR:
                new Coordinator(id, host, port, agentsMap);
                break;
            case ACCEPTORS:
                new Acceptor(id, host, port, agentsMap);
                break;
            case LEANERS:
                new Learner(id, host, port, agentsMap);
                break;

        }
//        CInitializer.getSingletonInstance().initializeQuoruns();
//
//        Thread.sleep(5*1000);
//        System.out.println("Propor msg ---------------------------------------------------------------------------");
//        new Client().run();
    }

    private static Map<String, Set<Integer>> criaMapComEnderecos(BufferedReader bufferedReader) throws IOException {
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

    private static String addAgentId(BufferedReader bufferedReader, HashMap<String, Set<Integer>> agentsMap, String agentType) throws IOException {
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
}


