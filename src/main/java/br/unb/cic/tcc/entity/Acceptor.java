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
        AcceptorReplica acceptorReplica = new AcceptorReplica(id, host, port, this,  acceptorSender);

        setAgentId(id);
        setQuorumSender(acceptorSender);
        setQuorumReplica(acceptorReplica);
    }

    public void phase1b(int round) {
        if(currentRound < round){
            currentRound = round;
            // envia 1B, round, valor round e
            HashMap<Constants, Object> map = new HashMap<>();
            map.put(Constants.AGENT_TYPE, this.getClass());
            map.put(Constants.AGENT_ID, this.getAgentId());
            map.put(Constants.V_RND, currentRound);
            map.put(Constants.V_VAL, getvMap());
            ProtocolMessage protocolMessage = new ProtocolMessage(ProtocolMessageType.MESSAGE_1B, round, map);
            QuorumMessage quorumMessage = new QuorumMessage(MessageType.QUORUM_REQUEST, protocolMessage, getQuorumSender().getProcessId());
            getQuorumSender().sendTo(Quoruns.idCoordinators(), quorumMessage);
        }
    }

    public void phase2b(ProtocolMessage protocolMessage) {
        System.out.println("Acceptor("+getAgentId()+") começou a fase 2b");
        Set<ClientMessage> vMapLastRound = getvMap().get(roundAceitouUltimaVez);
        if( vMapLastRound == null){
            vMapLastRound = new HashSet<>();
            getvMap().put(roundAceitouUltimaVez, vMapLastRound);
        }
        boolean condicao1 = vMapLastRound.isEmpty(); //vval[a] == none
        boolean condicao2 = false;

        Map<Constants, Object> message = (Map<Constants, Object>) protocolMessage.getMessage();
        if(protocolMessage.getProtocolMessageType() == ProtocolMessageType.MESSAGE_2S){
            condicao1 = (message.get(Constants.V_VAL) != null && roundAceitouUltimaVez < (Integer)message.get(Constants.V_RND)) || condicao1;
        } else if(protocolMessage.getProtocolMessageType() == ProtocolMessageType.MESSAGE_2A){
            condicao2 = message.get(Constants.V_VAL) != null;
        }

        int round = protocolMessage.getRound();
        if (currentRound <= round && (condicao1 || condicao2 )) {
            roundAceitouUltimaVez = round;
            currentRound = round;

            if (condicao1) {
//                ValorAprendido valorAprendido = new ValorAprendido((Integer) message.get(Constants.AGENT_ID), (ClientMessage) message.get(Constants.V_VAL));
                vMapLastRound.add((ClientMessage) message.get(Constants.V_VAL));
            } else if (condicao2 && (roundAceitouUltimaVez < round || vMapLastRound.isEmpty())) {
                // TODO colocar os valores dos outros CFProposers como nulos
            } else {
//                ValorAprendido valorAprendido = new ValorAprendido((Integer) message.get(Constants.AGENT_ID), (ClientMessage) message.get(Constants.V_VAL));
                vMapLastRound.add((ClientMessage) message.get(Constants.V_VAL));
            }

            Map<Constants, Object> msgToLeaner = new HashMap<>(); // TODO populate msg
            msgToLeaner.put(Constants.V_VAL, getSetFromVMap(roundAceitouUltimaVez));
            msgToLeaner.put(Constants.V_RND, round);
            msgToLeaner.put(Constants.AGENT_TYPE, this.getClass());
            msgToLeaner.put(Constants.AGENT_ID, this.getAgentId());

            ProtocolMessage protocolSendMsg = new ProtocolMessage(ProtocolMessageType.MESSAGE_2B, round, msgToLeaner);
            QuorumMessage quorumMessage = new QuorumMessage(MessageType.QUORUM_REQUEST, protocolSendMsg, getQuorumSender().getProcessId());
            getQuorumSender().sendTo(Quoruns.idLeaners(), quorumMessage);
        }
    }
}
