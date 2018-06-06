package br.unb.cic.tcc.entity;

import quorum.core.QuorumReplica;
import quorum.core.QuorumSender;

import java.util.HashMap;
import java.util.Map;

public abstract class Agent<QR extends QuorumReplica, QS extends QuorumSender> {
    private Integer agentId;
    private QR quorumReplica;
    private QS quorumSender;

    private Map<String, Object> vMap = null;

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

    public Map<String, Object> getvMap() {
        return vMap;
    }

    public void setvMap(Map<String, Object> vMap) {
        this.vMap = vMap;
    }

}
