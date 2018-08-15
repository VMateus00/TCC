package br.unb.cic.tcc.agent;

import br.unb.cic.tcc.entity.Acceptor;
import br.unb.cic.tcc.messages.ProtocolMessage;

public class BAcceptor extends Acceptor {
    public BAcceptor(int id, String host, int port) {
        super(id, host, port);
    }

    @Override
    public void phase1b(ProtocolMessage protocolMessage) {
        super.phase1b(protocolMessage); // Igual superclasse
    }

//    @Override
//    public void phase2b(ProtocolMessage protocolMessage) {
//        //TODO
//    }
}
