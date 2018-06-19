package br.unb.cic.tcc.entity;

import br.unb.cic.tcc.definitions.Constants;
import br.unb.cic.tcc.messages.ClientMessage;
import br.unb.cic.tcc.messages.ProtocolMessage;
import br.unb.cic.tcc.quorum.LeanerReplica;
import br.unb.cic.tcc.quorum.LeanerSender;
import quorum.communication.QuorumMessage;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Leaner extends Agent<LeanerReplica, LeanerSender> {

    private Set<ClientMessage> learned = new HashSet<>();

    public Leaner(int id, String host, int port) {
        LeanerSender leanerSender = new LeanerSender(id);
        LeanerReplica leanerReplica = new LeanerReplica(id, host, port, this, leanerSender);

        setAgentId(id);
        setQuorumSender(leanerSender);
        setQuorumReplica(leanerReplica);
    }

    public void learn(ProtocolMessage protocolMessage){
        Map<Constants, Object> message = (Map<Constants, Object>) protocolMessage.getMessage();

        Set<ClientMessage> msg = (Set<ClientMessage>) message.get(Constants.V_VAL);

        Integer agentId = (Integer) message.get(Constants.AGENT_ID);
        Set<ClientMessage> clientMessages = getvMap().get(agentId);
        if(clientMessages == null){
            clientMessages = new HashSet<>();
            getvMap().put(agentId, clientMessages);
        }
        clientMessages.addAll(msg);
        learned.addAll(msg);

        System.out.println("Leanerd (" + getAgentId()+") - aprendeu: "+msg);
    }
}
