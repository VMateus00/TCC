package br.unb.cic.tcc.entity;

import br.unb.cic.tcc.definitions.Constants;
import br.unb.cic.tcc.messages.ProtocolMessage;
import br.unb.cic.tcc.messages.ProtocolMessageType;
import br.unb.cic.tcc.quorum.AcceptorReplica;
import br.unb.cic.tcc.quorum.AcceptorSender;
import br.unb.cic.tcc.quorum.Quoruns;
import quorum.communication.MessageType;
import quorum.communication.QuorumMessage;

import java.util.HashMap;

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
        // CONDICAO_1: (received ("2S", r, v), v != null e roundAceitouUltimaVez < r ) ou getvMap() == null ou vazio
        // CONDICAO_2: receveid ("2A", r, (p, V)), V != null

        boolean condicao1 = true;
        boolean condicao2 = true;

        int round = protocolMessage.getRound();
        if (currentRound <= round) {  // && condicao_1 ou condicao_2
            roundAceitouUltimaVez = round;
            currentRound = round;

            if (condicao1) {

            } else if (condicao2 && (roundAceitouUltimaVez < round || getvMap().isEmpty())) {

            } else {

            }

            // send 2b, a, r, vval[a] to L
        }
    }
}
