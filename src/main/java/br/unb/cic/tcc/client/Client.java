package br.unb.cic.tcc.client;

import br.unb.cic.tcc.messages.ClientMessage;
import br.unb.cic.tcc.quorum.Quoruns;

public class Client implements Runnable {

    @Override
    public void run() {
        // Modelo 1: aprender 1 coisa
        ClientMessage clientMessage = new ClientMessage("Aprende alguma coisa");

        Quoruns.receiveClientMessage(clientMessage);
    }
}
