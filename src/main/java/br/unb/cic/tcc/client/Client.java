package br.unb.cic.tcc.client;

import br.unb.cic.tcc.messages.ClientMessage;
import quorum.communication.MessageType;
import quorum.communication.QuorumMessage;
import quorum.core.QuorumSender;

public class Client implements Runnable {
    public static final int CLIENT_ID = 1;

    private QuorumSender quorumSender;

    public Client() {
        // TODO a primeira msg nao é necessariamente feita a partir de msg
        this.quorumSender = new QuorumSender(CLIENT_ID) {
            @Override
            public void replyReceived(QuorumMessage quorumMessage) {
                System.out.println("O valor foi reconhecido pelo sistema.");
            }
        };
    }

    @Override
    public void run() {
        // Modelo 1: aprender 1 coisa
        ClientMessage quorumMessage = new ClientMessage(CLIENT_ID, MessageType.QUORUM_REQUEST);
        quorumMessage.setMsg("Aprende alguma coisa");
        quorumSender.multicast(quorumMessage);
    }
}
