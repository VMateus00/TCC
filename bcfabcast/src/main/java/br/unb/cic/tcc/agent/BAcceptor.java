package br.unb.cic.tcc.agent;

import br.unb.cic.tcc.entity.Acceptor;
import br.unb.cic.tcc.messages.BProtocolMessage;
import br.unb.cic.tcc.messages.ClientMessage;
import br.unb.cic.tcc.messages.Message1B;
import br.unb.cic.tcc.messages.ProposerClientMessage;
import br.unb.cic.tcc.messages.ProtocolMessage;
import br.unb.cic.tcc.messages.ProtocolMessageType;
import br.unb.cic.tcc.quorum.Quoruns;
import quorum.communication.MessageType;
import quorum.communication.QuorumMessage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class BAcceptor extends Acceptor {
    public BAcceptor(int id, String host, int port) {
        super(id, host, port);
    }

    @Override
    public void phase1b(ProtocolMessage protocolMessage) {// Igual superClasse, menos assinatura da msg
        if(currentRound < protocolMessage.getRound() && protocolMessage.getProtocolMessageType() == ProtocolMessageType.MESSAGE_1A){
            currentRound = protocolMessage.getRound();

            Message1B message1B = new Message1B(roundAceitouUltimaVez, getAgentId(), getVmapLastRound());

            BProtocolMessage protocolMessageToSend = new BProtocolMessage(ProtocolMessageType.MESSAGE_1B, currentRound, getAgentId(), message1B);
            QuorumMessage quorumMessage = new QuorumMessage(MessageType.QUORUM_REQUEST, protocolMessageToSend, getQuorumSender().getProcessId());
            getQuorumSender().sendTo(Quoruns.idCoordinators(currentRound), quorumMessage);
        }
    }

    @Override
    public void phase2b(ProtocolMessage protocolMessage) {
        System.out.println("Acceptor(" + getAgentId() + ") começou a fase 2b");
        Map<Integer, Set<ClientMessage>> vMapLastRound = getVmapLastRound();

        int round = protocolMessage.getRound();

        ProposerClientMessage clientMessage = null;
        if(protocolMessage.getMessage() instanceof ProposerClientMessage){
            clientMessage = (ProposerClientMessage) protocolMessage.getMessage();
        }

        boolean goodRoundValueResult = goodRoundValue(((BProtocolMessage) protocolMessage).getProofs(), protocolMessage.getRound());
        boolean condicao1 = protocolMessage.getProtocolMessageType() == ProtocolMessageType.MESSAGE_2S &&
                (vMapLastRound.isEmpty() ||
                (goodRoundValueResult && (protocolMessage.getMessage() != null && roundAceitouUltimaVez < round)));

        boolean condicao2 = protocolMessage.getProtocolMessageType() == ProtocolMessageType.MESSAGE_2A
                && goodRoundValueResult
                && (clientMessage).getClientMessage() != null;

        if (currentRound <= round && (condicao1 || condicao2)) {
            vMapLastRound = getVmapLastRound();

            Integer agentId = protocolMessage.getAgentSend();
            if (condicao1) {
                ((HashMap<Integer, Set<ClientMessage>>)protocolMessage.getMessage())
                        .forEach((k,v)->getVmapLastRound().put(k,v));
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
            roundAceitouUltimaVez = round;
            currentRound = round;

            BProtocolMessage protocolSendMsg = new BProtocolMessage(ProtocolMessageType.MESSAGE_2B, round, getAgentId(), vMapLastRound);
            QuorumMessage quorumMessage = new QuorumMessage(MessageType.QUORUM_REQUEST, protocolSendMsg, getQuorumSender().getProcessId());
            getQuorumSender().sendTo(Quoruns.idLearners(), quorumMessage);
        }
    }

    private boolean goodRoundValue(Set<ProtocolMessage> protocolMessages, Integer round) {
        int kMax = protocolMessages.stream()
                .map(p -> (Message1B) p.getMessage())
                .mapToInt(Message1B::getRoundAceitouUltimaVez)
                .max().getAsInt();

        List<Map<Integer, Set<ClientMessage>>> s = protocolMessages.stream()
                .map(p -> (Message1B) p.getMessage())
                .filter(p -> p.getRoundAceitouUltimaVez().equals(kMax))
                .map(Message1B::getvMapLastRound)
                .collect(Collectors.toList());

        return s.size() >= Quoruns.QTD_QUORUM_ACCEPTORS_BIZANTINO;
    }
}
