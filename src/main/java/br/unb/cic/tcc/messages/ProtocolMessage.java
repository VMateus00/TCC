package br.unb.cic.tcc.messages;

import java.io.Serializable;
import java.util.Objects;

public class ProtocolMessage implements Serializable, Comparable {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProtocolMessage that = (ProtocolMessage) o;
        return protocolMessageType == that.protocolMessageType &&
                Objects.equals(round, that.round) &&
                Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(protocolMessageType, round, message);
    }

    @Override
    public int compareTo(Object object) {
        if (object instanceof ProtocolMessage) {
            if(object.equals(this)){
                return 0;
            }else{
                return 1;
            }
        } else {
            return -1;
        }
    }
}
