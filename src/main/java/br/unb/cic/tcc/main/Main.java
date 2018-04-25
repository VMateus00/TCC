package br.unb.cic.tcc.main;

import br.unb.cic.tcc.client.Client;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Initializer.initializeQuoruns();

        new Client().run();
    }
}


