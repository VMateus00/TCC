package br.unb.cic.tcc.messages;

import java.io.Serializable;

public class ProtocolMessage implements Serializable {
    private ProtocolMessageType protocolMessageType;
    private int round;
    private Object message;

    public ProtocolMessage(ProtocolMessageType protocolMessageType, int round, Object message) {
        this.protocolMessageType = protocolMessageType;
        this.round = round;
        this.message = message;
    }

    public ProtocolMessageType getProtocolMessageType() {
        return protocolMessageType;
    }

    public void setProtocolMessageType(ProtocolMessageType protocolMessageType) {
        this.protocolMessageType = protocolMessageType;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public Object getMessage() {
        return message;
    }

    public void setMessage(Object message) {
        this.message = message;
    }
}
