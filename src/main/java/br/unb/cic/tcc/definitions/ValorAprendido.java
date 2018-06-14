package br.unb.cic.tcc.definitions;

import br.unb.cic.tcc.messages.ClientMessage;

import java.io.Serializable;

public class ValorAprendido implements Serializable {
    private Integer agentId; // identificar quem enviou essa informacao (Nao deve ser utilizado para comparar se sao iguais)
    private ClientMessage clientMessage;

    public ValorAprendido(Integer agentId, ClientMessage clientMessage) {
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
}
