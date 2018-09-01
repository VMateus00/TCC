package br.unb.cic.tcc.client;

import br.unb.cic.tcc.messages.ClientMessage;
import br.unb.cic.tcc.quorum.Quoruns;

public class Client implements Runnable {

    @Override
    public void run() {
        // Modelo 1: aprender 1 coisa
        ClientMessage clientMessage = new ClientMessage("Aprende alguma coisa");

        Quoruns.receiveClientMessage(clientMessage);

//        sleep(10);
//
//        System.out.println();
//
//        Quoruns.getCoordinators().get(0).phase1A();
//
//        clientMessage = new ClientMessage("Aprende algo novo");
//        sleep(5);
//        Quoruns.receiveClientMessage(clientMessage);
//
//        sleep(25);
//
//        System.out.println();
//
//        Quoruns.getLearners().forEach(learner->{
//            System.out.println(learner.getvMap());
//        });
    }

    private void sleep(long seconds) {
        try {
            Thread.sleep(1000*seconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
