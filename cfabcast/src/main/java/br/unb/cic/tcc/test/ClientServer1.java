package br.unb.cic.tcc.test;

import quorum.communication.QuorumMessage;
import quorum.core.QuorumReplica;
import quorum.core.QuorumSender;

public class ClientServer1 {
    private QuorumReplica quorumReplica;
    private QuorumSender quorumSender;

    public ClientServer1() {
        this.quorumSender = new QuorumSender(1) { // criar um processo? processo atual?
            @Override
            public void replyReceived(QuorumMessage quorumMessage) {
                System.out.println("a msg foi recebida");
            }
        };

        this.quorumReplica = new QuorumReplica(1, "", "127.0.0.1", 11000) {
            @Override
            public QuorumMessage executeRequest(QuorumMessage quorumMessage) {
                System.out.println("Executed request: "+ quorumMessage.getMsg());
                return quorumMessage;
            }

            @Override
            public QuorumMessage executeReconfigurationMessage(QuorumMessage quorumMessage) {
                System.out.println("Executed configuration message");
                return quorumMessage;
            }
        };

        new QuorumReplica(2, "", "127.0.0.1", 11010) {
            @Override
            public QuorumMessage executeRequest(QuorumMessage quorumMessage) {
                System.out.println("Executed request");
                return quorumMessage;
            }

            @Override
            public QuorumMessage executeReconfigurationMessage(QuorumMessage quorumMessage) {
                System.out.println("Executed configuration message");
                return quorumMessage;
            }
        };

        new QuorumReplica(3, "", "127.0.0.1", 11020) {
            @Override
            public QuorumMessage executeRequest(QuorumMessage quorumMessage) {
                System.out.println("Executed request");
                return quorumMessage;
            }

            @Override
            public QuorumMessage executeReconfigurationMessage(QuorumMessage quorumMessage) {
                System.out.println("Executed configuration message");
                return quorumMessage;
            }
        };
    }

    public QuorumReplica getQuorumReplica() {
        return quorumReplica;
    }

    public void setQuorumReplica(QuorumReplica quorumReplica) {
        this.quorumReplica = quorumReplica;
    }

    public QuorumSender getQuorumSender() {
        return quorumSender;
    }

    public void setQuorumSender(QuorumSender quorumSender) {
        this.quorumSender = quorumSender;
    }
}
