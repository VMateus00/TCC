package br.unb.cic.tcc.main;

import br.unb.cic.tcc.client.Client;
import br.unb.cic.tcc.quorum.Quoruns;

public class Main {
    public static void main(String[] args) throws InterruptedException {
//        if(args.length != 2){
//            System.out.println("Devem ser passados dois argumentos");
//        }
//
//        Map<String, Set<Integer>> agentsMap;
//
//        try {
//            agentsMap = AgentMapUtil.createAgentsMap();
//        }catch (Exception e){
//            throw new RuntimeException("Erro ao criar map com os agentes", e);
//        }
//
//        StringTokenizer str = new StringTokenizer(args[1]," ");
//        String agentType = args[0];
//        int id = Integer.valueOf(str.nextToken());
//        String host = str.nextToken();
//        int port = Integer.valueOf(str.nextToken());
//
//        switch (agentType){
//            case PROPOSERS:
//                new Proposer(id, host, port, agentsMap);
//                break;
//            case ACCEPTORS:
//                new Acceptor(id, host, port, agentsMap);
//                break;
//            case LEANERS:
//                new Learner(id, host, port, agentsMap);
//                break;
//        }
        CInitializer.getSingletonInstance().initializeQuoruns();

        Thread.sleep(5*1000);
        System.out.println("Propor msg ---------------------------------------------------------------------------");
//        new Client().run();

        Quoruns.getClients().forEach(Client::run);
    }

}


