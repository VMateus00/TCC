package br.unb.cic.tcc.main;

import br.unb.cic.tcc.client.Client;
import br.unb.cic.tcc.quorum.Quoruns;


public class UsigBMain {
    public static void main(String[] args) throws InterruptedException {
//        if(args.length != 2){
//            System.out.println("Devem ser passados dois argumentos");
//        }
//
//        Map<String, Set<Integer>> agentsMap;
//        Integer[] qtdAgents = new Integer[1];
//
//        try {
//            agentsMap = AgentMapUtil.createAgentsMap();
//            qtdAgents[0] = 0;
//            agentsMap.forEach((k,v)->qtdAgents[0] = qtdAgents[0]+v.size());
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
//                new UsigBProposer(id, host, port, qtdAgents[0], agentsMap);
//                break;
//            case COORDINATOR:
//                new UsigBCoordinator(id, host, port, qtdAgents[0], agentsMap);
//                break;
//            case ACCEPTORS:
//                new UsigBAcceptor(id, host, port, qtdAgents[0], agentsMap);
//                break;
//            case LEANERS:
//                new UsigBLearner(id, host, port, qtdAgents[0], agentsMap);
//                break;
//        }

        UsigBInitializer.getSingletonInstance().initializeQuoruns();

        Thread.sleep(5*1000);
        System.out.println("Propor msg ---------------------------------------------------------------------------");
        Quoruns.getClients().stream().forEach(Client::run);
    }
}
