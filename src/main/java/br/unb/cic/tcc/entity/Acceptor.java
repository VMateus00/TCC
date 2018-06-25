package br.unb.cic.tcc.entity;

import br.unb.cic.tcc.definitions.Constants;
import br.unb.cic.tcc.messages.ClientMessage;
import br.unb.cic.tcc.messages.ProtocolMessage;
import br.unb.cic.tcc.messages.ProtocolMessageType;
import br.unb.cic.tcc.quorum.AcceptorReplica;
import br.unb.cic.tcc.quorum.AcceptorSender;
import br.unb.cic.tcc.quorum.Quoruns;
import quorum.communication.MessageType;
import quorum.communication.QuorumMessage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Acceptor extends Agent<AcceptorReplica, AcceptorSender> {
    private int currentRound = 0;
    private int roundAceitouUltimaVez = -1; // começa nunca tendo aceitado nada, por isso -1

    public Acceptor(int id, String host, int port) {
        AcceptorSender acceptorSender = new AcceptorSender(id);
        AcceptorReplica acceptorReplica = new AcceptorReplica(id, host, port, this, acceptorSender);

        setAgentId(id);
        setQuorumSender(acceptorSender);
        setQuorumReplica(acceptorReplica);
    }

    public void phase1b(int round) {
        // TODO após concluir parte 1
    }

    public void phase2b(ProtocolMessage protocolMessage) {
        System.out.println("Acceptor(" + getAgentId() + ") começou a fase 2b");
        Map<Integer, Set<ClientMessage>> vMapLastRound = getvMap().get(roundAceitouUltimaVez);
//        Set<ClientMessage> vMapLastRound = getvMap().get(roundAceitouUltimaVez);
        if (vMapLastRound == null) {
            vMapLastRound = new HashMap<>();
            getvMap().put(roundAceitouUltimaVez, vMapLastRound); // garantir que esse map está sendo referenciado
        }
        boolean condicao1 = vMapLastRound.isEmpty(); //vval[a] == none
        boolean condicao2 = false;

        Map<Constants, Object> message = (Map<Constants, Object>) protocolMessage.getMessage();
        if (protocolMessage.getProtocolMessageType() == ProtocolMessageType.MESSAGE_2S) {
            // TODO arrumar comparacao com o valor do vmap após fazer a reconstrucao do quorum
            condicao1 = (message.get(Constants.V_VAL) != null && roundAceitouUltimaVez < (Integer) message.get(Constants.V_RND)) || condicao1;
        } else if (protocolMessage.getProtocolMessageType() == ProtocolMessageType.MESSAGE_2A) {
            condicao2 = message.get(Constants.V_VAL) != null;
        }

        int round = protocolMessage.getRound();
        if (currentRound <= round && (condicao1 || condicao2)) {
            roundAceitouUltimaVez = round;
            currentRound = round;

            Integer agentId = (Integer) message.get(Constants.AGENT_ID);
            ClientMessage clientMessage = (ClientMessage) message.get(Constants.V_VAL);
            if (condicao1) {
                vMapLastRound = (Map<Integer, Set<ClientMessage>>) message.get(Constants.V_VAL); // veio do Coordinator
            } else if (condicao2 && (roundAceitouUltimaVez < round || vMapLastRound.isEmpty())) {
                vMapLastRound = new HashMap<>();
                for (Integer proposerId : Quoruns.idProposers()) {
                    HashSet<ClientMessage> hashSet = new HashSet<>();
                    if (proposerId.equals(agentId)) {
                        hashSet.add(clientMessage);
                    } else {
                        hashSet.add(null);
                    }
                    vMapLastRound.put(proposerId, hashSet);
                }
            } else {
                Set<ClientMessage> clientMessages = vMapLastRound.get(agentId);
                if (clientMessages == null) {
                    clientMessages = new HashSet<>();
                    vMapLastRound.put(agentId, clientMessages);
                }
                vMapLastRound.get(agentId).add(clientMessage);
            }

            Map<Constants, Object> msgToLeaner = new HashMap<>(); // TODO populate msg
            msgToLeaner.put(Constants.V_VAL, vMapLastRound);
            msgToLeaner.put(Constants.V_RND, round);
            msgToLeaner.put(Constants.AGENT_TYPE, this.getClass());
            msgToLeaner.put(Constants.AGENT_ID, this.getAgentId());

            ProtocolMessage protocolSendMsg = new ProtocolMessage(ProtocolMessageType.MESSAGE_2B, round, msgToLeaner);
            QuorumMessage quorumMessage = new QuorumMessage(MessageType.QUORUM_REQUEST, protocolSendMsg, getQuorumSender().getProcessId());
            getQuorumSender().sendTo(Quoruns.idLeaners(), quorumMessage);
        }
    }
}
