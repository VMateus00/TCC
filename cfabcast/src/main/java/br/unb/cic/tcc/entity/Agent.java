package br.unb.cic.tcc.entity;

import br.unb.cic.tcc.messages.ClientMessage;
import quorum.core.QuorumReplica;
import quorum.core.QuorumSender;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class Agent<QR extends QuorumReplica, QS extends QuorumSender> {
    private Integer agentId;
    private QR quorumReplica;
    private QS quorumSender;

    // VMAP<ROUND, MAP<PROPOSER_ID, MSG>>
    private Map<Integer, Map<Integer, Set<ClientMessage>>> vMap = new HashMap<>();

    public Integer getAgentId() {
        return agentId;
    }

    public void setAgentId(Integer agentId) {
        this.agentId = agentId;
    }

    public QR getQuorumReplica() {
        return quorumReplica;
    }

    public void setQuorumReplica(QR quorumReplica) {
        this.quorumReplica = quorumReplica;
    }

    public QS getQuorumSender() {
        return quorumSender;
    }

    public void setQuorumSender(QS quorumSender) {
        this.quorumSender = quorumSender;
    }

    public Map<Integer, Map<Integer, Set<ClientMessage>>> getvMap() {
        return vMap;
    }
}
