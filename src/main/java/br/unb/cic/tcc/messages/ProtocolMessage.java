package br.unb.cic.tcc.messages;

import java.io.Serializable;

public class ProtocolMessage implements Serializable {
    private ProtocolMessageType protocolMessageType;
    private Integer round;
    private Object message;

    public ProtocolMessage(ProtocolMessageType protocolMessageType, Integer round, Object message) {
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

    public Integer getRound() {
        return round;
    }

    public void setRound(Integer round) {
        this.round = round;
    }

    public Object getMessage() {
        return message;
    }

    public void setMessage(Object message) {
        this.message = message;
    }
}
