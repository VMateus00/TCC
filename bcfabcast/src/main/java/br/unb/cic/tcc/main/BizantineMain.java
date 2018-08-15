package br.unb.cic.tcc.main;

import br.unb.cic.tcc.client.Client;

public class BizantineMain {
    public static void main(String[] args) throws InterruptedException {
        BInitializer.getSingletonInstance().initializeQuoruns();

        Thread.sleep(5*1000);
        System.out.println("Propor msg ---------------------------------------------------------------------------");
        new Client().run();
    }
}
