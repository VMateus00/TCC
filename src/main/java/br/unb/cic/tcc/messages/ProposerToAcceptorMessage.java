package br.unb.cic.tcc.messages;

import java.io.Serializable;

public class ProposerToAcceptorMessage implements Serializable {

    private Integer round;
    private ProtocolMessageType ProtocolMessageType;

    public ProposerToAcceptorMessage(int round, ProtocolMessageType protocolMessageType) {
        this.round = round;
        this.ProtocolMessageType = protocolMessageType;
    }

    public Integer getRound() {
        return round;
    }
}
