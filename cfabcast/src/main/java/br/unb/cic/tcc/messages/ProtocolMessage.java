package br.unb.cic.tcc.messages;

import java.io.Serializable;
import java.util.Objects;

public class ProtocolMessage implements Serializable, Comparable {
    private ProtocolMessageType protocolMessageType;
    private Integer agentSend;
    private Integer round;
    private Integer instanciaExecucao; //TODO pensar em um nome melhor
    private Object message;

//    public ProtocolMessage(ProtocolMessageType protocolMessageType, Integer round, Integer agentSend, Object message) {
//        this.protocolMessageType = protocolMessageType;
//        this.round = round;
//        this.agentSend = agentSend;
//        this.message = message;
//    }

    public ProtocolMessage(ProtocolMessageType protocolMessageType, Integer round, Integer agentSend, Integer instanciaExecucao, Object message) {
        this.protocolMessageType = protocolMessageType;
        this.round = round;
        this.agentSend = agentSend;
        this.instanciaExecucao = instanciaExecucao;
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

    public Integer getAgentSend() {
        return agentSend;
    }

    public void setAgentSend(Integer agentSend) {
        this.agentSend = agentSend;
    }

    public Integer getInstanciaExecucao() {
        return instanciaExecucao;
    }

    public void setInstanciaExecucao(Integer instanciaExecucao) {
        this.instanciaExecucao = instanciaExecucao;
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

    @Override
    public String toString() {
        return "ProtocolMessage{" +
                "protocolMessageType=" + protocolMessageType +
                ", agentSend=" + agentSend +
                ", round=" + round +
                ", message=" + message +
                '}';
    }
}
