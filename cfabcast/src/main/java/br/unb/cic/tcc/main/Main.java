package br.unb.cic.tcc.main;

import br.unb.cic.tcc.client.Client;
import br.unb.cic.tcc.entity.Acceptor;
import br.unb.cic.tcc.entity.Coordinator;
import br.unb.cic.tcc.entity.Learner;
import br.unb.cic.tcc.entity.Proposer;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        CInitializer.getSingletonInstance().initializeQuoruns();

        Thread.sleep(5*1000);
        System.out.println("Propor msg ---------------------------------------------------------------------------");
        new Client().run();

//        System.out.println(args);
//        if (args.length < 2 | args.length > 2) {
//
//            System.out.println("Quantidade de argumentos invalida");
//            System.out.println("Arg 1 -> Tipo de agente");
//            System.out.println("Arg 2 -> Dados para inicializar o agente");
//            System.exit(1);
//        } else {
//            String[] split = args[1].split(" ");
//
//            Integer id = Integer.valueOf(split[0]);
//            String host = split[1];
//            Integer port = Integer.valueOf(split[2]);
//
//            switch (args[0]){
//                case "Coordinator":
//                    new Coordinator(id, host, port);
//                    System.out.println("Passou aqui");
//                    break;
//                case "Proposer":
//                    new Proposer(id, host, port);
//                    break;
//                case "Acceptor":
//                    new Acceptor(id, host, port);
//                    break;
//                case "Learner":
//                    new Learner(id, host, port);
//                    break;
//                default:
//                    System.out.println("Valor invalido");
//                    System.exit(0);
//            }
//        }
    }
}


