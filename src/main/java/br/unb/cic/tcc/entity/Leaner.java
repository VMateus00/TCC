package br.unb.cic.tcc.entity;

import quorum.communication.QuorumMessage;
import quorum.core.QuorumReplica;
import quorum.core.QuorumSender;

import java.util.HashSet;
import java.util.Set;

public class Leaner extends Agent {

    private Set<QuorumMessage> deliveredMessages = new HashSet<>();

    public Leaner(Integer agentId, QuorumReplica quorumReplica, QuorumSender quorumSender) {
        super(agentId, quorumReplica, quorumSender);
    }
}
