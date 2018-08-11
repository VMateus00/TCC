package br.unb.cic.tcc.main;

import br.unb.cic.tcc.client.Client;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        CInitializer.getSingletonInstance().initializeQuoruns();

        Thread.sleep(5*1000);
        System.out.println("Propor msg ---------------------------------------------------------------------------");
        new Client().run();
    }
}


