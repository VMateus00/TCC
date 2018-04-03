package br.unb.cic.tcc.messages;

import quorum.communication.MessageType;
import quorum.communication.QuorumMessage;

public class ClientMessage extends QuorumMessage {
    public ClientMessage(int sender, MessageType type) {
        super(sender, type);
    }
}
