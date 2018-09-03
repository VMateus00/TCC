package br.unb.cic.tcc.client;

import br.unb.cic.tcc.messages.ClientMessage;
import br.unb.cic.tcc.quorum.Quoruns;

public class Client implements Runnable {

    @Override
    public void run() {
        ClientMessage msg = new ClientMessage("aprende: " + 0);
        Quoruns.receiveClientMessage(msg);
    }
}
