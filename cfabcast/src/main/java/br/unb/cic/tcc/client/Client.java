package br.unb.cic.tcc.client;

import br.unb.cic.tcc.entity.Agent;
import br.unb.cic.tcc.main.Initializer;
import br.unb.cic.tcc.messages.ClientMessage;
import br.unb.cic.tcc.messages.ProtocolMessage;
import br.unb.cic.tcc.quorum.AgentSender;
import br.unb.cic.tcc.quorum.ClientReplica;
import quorum.communication.MessageType;
import quorum.communication.QuorumMessage;
import quorum.core.QuorumSender;

import java.util.Comparator;
import java.util.Date;
import java.util.Map;
import java.util.Set;

public class Client extends Agent<ClientReplica, QuorumSender> implements Runnable {

    public Client(int id, String host, int port, Map<String, Set<Integer>> agentsMap) {
        AgentSender leanerSender = new AgentSender(id);
        ClientReplica learnerReplica = new ClientReplica(id, host, port, this);

        setAgentId(id);
        idAgentes = agentsMap;
        setQuorumSender(leanerSender);
        setQuorumReplica(learnerReplica);
    }

    @Override
    public void run() {
        for (int i = 0; i < 1; i++) {
            ClientMessage clientMessage = new ClientMessage("Instrução " + i, getAgentId());
            enviaMsgFromClientToProposer(clientMessage);
            System.out.println("Client ("+getAgentId()+") enviou msg com id:" + clientMessage.getIdMsg() + " às "+ new Date());
        }
    }

    private void enviaMsgFromClientToProposer(ClientMessage clientMessage){
        // By default there will be only two CF-propers, with this, we will only send proposes to these two.

        Integer[] idProposers = idAgentes.get(Initializer.PROPOSERS).stream()
                .sorted(Comparator.naturalOrder())
                .toArray(Integer[]::new);

        if(idProposers.length > 1){
            int cfProposer[];
            if(clientMessage.getIdMsg()%2 == 0){
                int[] temp = {idProposers[0]};
                cfProposer = temp;
            }else{
                int[] temp = {idProposers[1]};
                cfProposer = temp;
            }
            getQuorumSender().sendTo(cfProposer
                    ,new QuorumMessage(MessageType.QUORUM_REQUEST, clientMessage, getAgentId()));
        } else {
            // send to 1
            getQuorumSender().sendTo(new int[idProposers[0]], new QuorumMessage(MessageType.QUORUM_REQUEST, clientMessage, getAgentId()));
        }
    }

    public void mostraRecebeuMensagem(ProtocolMessage protocolMessage){
        ClientMessage msg = (ClientMessage) protocolMessage.getMessage();
        System.out.println("Client ("+getAgentId()+") recebeu que a instrução: "+msg.getIdMsg()+" foi concluída e confirmada às " + new Date());
    }
}
