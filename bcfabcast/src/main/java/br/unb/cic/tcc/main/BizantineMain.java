package br.unb.cic.tcc.main;

import br.unb.cic.tcc.agent.BAcceptor;
import br.unb.cic.tcc.agent.BCoordinator;
import br.unb.cic.tcc.agent.BLearner;
import br.unb.cic.tcc.agent.BProposer;

import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import static br.unb.cic.tcc.main.Initializer.ACCEPTORS;
import static br.unb.cic.tcc.main.Initializer.COORDINATOR;
import static br.unb.cic.tcc.main.Initializer.LEANERS;
import static br.unb.cic.tcc.main.Initializer.PROPOSERS;

public class BizantineMain {
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
                new BProposer(id, host, port, agentsMap);
                break;
            case COORDINATOR:
                new BCoordinator(id, host, port, agentsMap);
                break;
            case ACCEPTORS:
                new BAcceptor(id, host, port, agentsMap);
                break;
            case LEANERS:
                new BLearner(id, host, port, agentsMap);
                break;
        }
//        BInitializer.getSingletonInstance().initializeQuoruns();
//
//        Thread.sleep(5*1000);
//        System.out.println("Propor msg ---------------------------------------------------------------------------");
//        new Client().run();
    }
}
