package br.unb.cic.tcc.messages;

import java.io.Serializable;

public class ProposerClientMessage implements Serializable {
    private Integer agentId;
    private ClientMessage clientMessage;

    public ProposerClientMessage(Integer agentId, ClientMessage clientMessage) {
        this.agentId = agentId;
        this.clientMessage = clientMessage;
    }

    public Integer getAgentId() {
        return agentId;
    }

    public void setAgentId(Integer agentId) {
        this.agentId = agentId;
    }

    public ClientMessage getClientMessage() {
        return clientMessage;
    }

    public void setClientMessage(ClientMessage clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public String toString() {
        return "ProposerClientMessage{" +
                "agentId=" + agentId +
                ", clientMessage=" + clientMessage +
                '}';
    }
}
