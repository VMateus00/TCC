package br.unb.cic.tcc.entity;

import br.unb.cic.tcc.quorum.LeanerReplica;
import br.unb.cic.tcc.quorum.LeanerSender;
import quorum.communication.QuorumMessage;

import java.util.HashSet;
import java.util.Set;

public class Leaner extends Agent<LeanerReplica, LeanerSender> {

    private Set<QuorumMessage> deliveredMessages = new HashSet<>();

    public Leaner(int id, String host, int port) {
        int agentId = nextId();

        LeanerSender leanerSender = new LeanerSender(agentId);
        LeanerReplica leanerReplica = new LeanerReplica(id, host, port, this, leanerSender);

        setAgentId(agentId);
        setQuorumSender(leanerSender);
        setQuorumReplica(leanerReplica);
    }

    public void learn(){

    }
}
