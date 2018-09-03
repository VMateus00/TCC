package br.unb.cic.tcc.entity;

import br.unb.cic.tcc.messages.ClientMessage;
import br.unb.cic.tcc.messages.Message1B;
import br.unb.cic.tcc.messages.ProposerClientMessage;
import br.unb.cic.tcc.messages.ProtocolMessage;
import br.unb.cic.tcc.messages.ProtocolMessageType;
import br.unb.cic.tcc.quorum.AcceptorReplica;
import br.unb.cic.tcc.quorum.AgentSender;
import br.unb.cic.tcc.quorum.Quoruns;
import quorum.communication.MessageType;
import quorum.communication.QuorumMessage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Acceptor extends Agent<AcceptorReplica, AgentSender> {
    protected int currentRound = 0;
    protected int roundAceitouUltimaVez = 0; // começa nunca tendo aceitado nada, por isso 0

    public Acceptor(int id, String host, int port) {
        AgentSender acceptorSender = new AgentSender(id);
        AcceptorReplica acceptorReplica = new AcceptorReplica(id, host, port, this);

        setAgentId(id);
        setQuorumSender(acceptorSender);
        setQuorumReplica(acceptorReplica);
    }

    public void phase1b(ProtocolMessage protocolMessage) {
        if(currentRound < protocolMessage.getRound() && protocolMessage.getProtocolMessageType() == ProtocolMessageType.MESSAGE_1A){
            currentRound = protocolMessage.getRound();

            Message1B message1B = new Message1B(roundAceitouUltimaVez, getAgentId(), getVmapLastRound());

            ProtocolMessage protocolMessageToSend = new ProtocolMessage(ProtocolMessageType.MESSAGE_1B, currentRound, getAgentId(), message1B);
            QuorumMessage quorumMessage = new QuorumMessage(MessageType.QUORUM_REQUEST, protocolMessageToSend, getQuorumSender().getProcessId());
            getQuorumSender().sendTo(Quoruns.idCoordinators(currentRound), quorumMessage);
        }
    }

    public void phase2b(ProtocolMessage protocolMessage) {
        System.out.println("Acceptor(" + getAgentId() + ") começou a fase 2b");
        Map<Integer, Set<ClientMessage>> vMapLastRound = getVmapLastRound();

        int round = protocolMessage.getRound();

        ProposerClientMessage clientMessage = null;
        if(protocolMessage.getMessage() instanceof ProposerClientMessage){
            clientMessage = (ProposerClientMessage) protocolMessage.getMessage();
        }

        boolean condicao1 = protocolMessage.getProtocolMessageType() == ProtocolMessageType.MESSAGE_2S &&
                    ((protocolMessage.getMessage() != null && roundAceitouUltimaVez < round) || vMapLastRound.isEmpty());  //vval[a] == none

        boolean condicao2 = protocolMessage.getProtocolMessageType() == ProtocolMessageType.MESSAGE_2A
                && (clientMessage).getClientMessage() != null;

        if (currentRound <= round && (condicao1 || condicao2)) {
            roundAceitouUltimaVez = round;
            currentRound = round;
            vMapLastRound = getVmapLastRound();

            Integer agentId = protocolMessage.getAgentSend();
            if (condicao1) {
                ((HashMap<Integer, Set<ClientMessage>>)protocolMessage.getMessage())
                        .forEach((k,v)->getVmapLastRound().put(k,v));
                // TODO atualizar o valor no mapa
            } else if (condicao2 && (roundAceitouUltimaVez < round || vMapLastRound.isEmpty())) {
                vMapLastRound = getVmapLastRound();

                // TODO verificar se é para zerar o valor do vMapLastRound
                vMapLastRound.putIfAbsent(agentId, new HashSet<>());

                for (Integer proposerId : Quoruns.idNCFProposers(currentRound)) {
                    Set<ClientMessage> proposedValues = vMapLastRound.get(proposerId);
                    if(proposedValues == null){
                        vMapLastRound.put(proposerId, new HashSet<>());
                        proposedValues = vMapLastRound.get(proposerId);
                    }
                    proposedValues .add(null);
                }
                vMapLastRound.get(agentId).add(clientMessage.getClientMessage());
            } else {
                vMapLastRound.putIfAbsent(agentId, new HashSet<>());
                vMapLastRound.get(agentId).add(clientMessage.getClientMessage());
            }

            ProtocolMessage protocolSendMsg = new ProtocolMessage(ProtocolMessageType.MESSAGE_2B, round, getAgentId(), vMapLastRound);
            QuorumMessage quorumMessage = new QuorumMessage(MessageType.QUORUM_REQUEST, protocolSendMsg, getQuorumSender().getProcessId());
            getQuorumSender().sendTo(Quoruns.idLearners(), quorumMessage);
        }
    }

    protected Map<Integer, Set<ClientMessage>> getVmapLastRound() {
        getvMap().putIfAbsent(roundAceitouUltimaVez, new HashMap<>());
        return getvMap().get(roundAceitouUltimaVez);
    }

    @Override
    public void limpaDadosExecucao() {
        currentRound = 0;
        roundAceitouUltimaVez = 0;
        setvMap(new HashMap<>());
    }
}
