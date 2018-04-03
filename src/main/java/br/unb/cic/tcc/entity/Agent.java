package br.unb.cic.tcc.entity;

import quorum.core.QuorumReplica;
import quorum.core.QuorumSender;

public abstract class Agent {

    private Integer agentId;
    private QuorumReplica quorumReplica;
    private QuorumSender quorumSender;

    public Agent(Integer agentId, QuorumReplica quorumReplica, QuorumSender quorumSender) {
        this.agentId = agentId;
        this.quorumReplica = quorumReplica;
        this.quorumSender = quorumSender;
    }

    public Integer getAgentId() {
        return agentId;
    }

    public QuorumReplica getQuorumReplica() {
        return quorumReplica;
    }

    public QuorumSender getQuorumSender() {
        return quorumSender;
    }
}
