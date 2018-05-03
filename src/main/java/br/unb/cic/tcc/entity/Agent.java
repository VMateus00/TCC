package br.unb.cic.tcc.entity;

import quorum.core.QuorumReplica;
import quorum.core.QuorumSender;

import java.util.HashMap;
import java.util.Map;

public abstract class Agent {
    private static int AGENT_ID = 1;
    private Integer agentId;
    private QuorumReplica quorumReplica;
    private QuorumSender quorumSender;

    private Map<String, Object> vMap = new HashMap<>(); // TODO verifciar se é isso msm

    public Integer getAgentId() {
        return agentId;
    }

    public void setAgentId(Integer agentId) {
        this.agentId = agentId;
    }

    public QuorumReplica getQuorumReplica() {
        return quorumReplica;
    }

    public void setQuorumReplica(QuorumReplica quorumReplica) {
        this.quorumReplica = quorumReplica;
    }

    public QuorumSender getQuorumSender() {
        return quorumSender;
    }

    public void setQuorumSender(QuorumSender quorumSender) {
        this.quorumSender = quorumSender;
    }

    public Map<String, Object> getvMap() {
        return vMap;
    }

    public void setvMap(Map<String, Object> vMap) {
        this.vMap = vMap;
    }

    protected int nextId(){
        return AGENT_ID++;
    }

}
