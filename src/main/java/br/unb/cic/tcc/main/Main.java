package br.unb.cic.tcc.main;

import br.unb.cic.tcc.client.Client;
import br.unb.cic.tcc.test.ClientServer1;
import quorum.communication.MessageType;
import quorum.communication.QuorumMessage;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Initializer.initializeQuoruns();
        
        new Client().run();

//        ClientServer1 clientServer1 = new ClientServer1();
//
//        Thread.sleep(5000);
//
//        QuorumMessage sm = new QuorumMessage(clientServer1.getQuorumSender().getProcessId(), MessageType.QUORUM_REQUEST);
//        sm.setMsg("Teste");
//
//        clientServer1.getQuorumSender().multicast(sm);
    }
}


