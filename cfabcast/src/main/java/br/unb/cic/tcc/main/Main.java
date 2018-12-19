package br.unb.cic.tcc.main;

import br.unb.cic.tcc.client.Client;
import br.unb.cic.tcc.entity.Acceptor;
import br.unb.cic.tcc.entity.Learner;
import br.unb.cic.tcc.entity.Proposer;

import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import static br.unb.cic.tcc.main.Initializer.ACCEPTORS;
import static br.unb.cic.tcc.main.Initializer.CLIENTS;
import static br.unb.cic.tcc.main.Initializer.LEANERS;
import static br.unb.cic.tcc.main.Initializer.PROPOSERS;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        if(args.length != 2){
            System.out.println("Devem ser passados dois argumentos");
        }

        Map<String, Set<Integer>> agentsMap;

        try {
            agentsMap = AgentMapUtil.createAgentsMap();
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
            case ACCEPTORS:
                new Acceptor(id, host, port, agentsMap);
                break;
            case LEANERS:
                new Learner(id, host, port, agentsMap);
                break;
            case CLIENTS:
                new Client(id, host, port, agentsMap);
                break;
        }
    }

}


