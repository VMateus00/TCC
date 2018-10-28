package br.unb.cic.tcc.agent;

import br.unb.cic.tcc.entity.Acceptor;
import br.unb.cic.tcc.messages.BProtocolMessage;
import br.unb.cic.tcc.messages.ClientMessage;
import br.unb.cic.tcc.messages.Message1B;
import br.unb.cic.tcc.messages.ProposerClientMessage;
import br.unb.cic.tcc.messages.ProtocolMessage;
import br.unb.cic.tcc.messages.ProtocolMessageType;
import br.unb.cic.tcc.util.RsaUtil;
import quorum.communication.MessageType;
import quorum.communication.QuorumMessage;

import java.security.KeyPair;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class BAcceptor extends Acceptor implements BAgent {
    protected final KeyPair keyPair;

    public BAcceptor(int id, String host, int port, Map<String, Set<Integer>> agentsMap) {
        super(id, host, port, agentsMap);
        keyPair = RsaUtil.generateKeyPair();
    }

    @Override
    public void phase1b(ProtocolMessage protocolMessage) {// Igual superClasse, menos assinatura da msg
        if(verifyMsg((BProtocolMessage) protocolMessage)
                && currentRound < protocolMessage.getRound()
                && protocolMessage.getProtocolMessageType() == ProtocolMessageType.MESSAGE_1A){
            currentRound = protocolMessage.getRound();

            Message1B message1B = new Message1B(roundAceitouUltimaVez, getAgentId(), getVmapLastRound());

            ProtocolMessageType messageType = ProtocolMessageType.MESSAGE_1B;
            BProtocolMessage protocolMessageToSend = createAssignedMessage(new ProtocolMessage(messageType,
                    currentRound, getAgentId(), message1B), null, keyPair);
            QuorumMessage quorumMessage = new QuorumMessage(MessageType.QUORUM_REQUEST, protocolMessageToSend, getQuorumSender().getProcessId());
            getQuorumSender().sendTo(idCoordinator(), quorumMessage);
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

        if (verifyMsg((BProtocolMessage) protocolMessage)
                && currentRound <= round && (condicao1 || condicao2)) {
            vMapLastRound = getVmapLastRound();

            Integer agentId = protocolMessage.getAgentSend();
            if (condicao1) {
                ((HashMap<Integer, Set<ClientMessage>>)protocolMessage.getMessage())
                        .forEach((k,v)->getVmapLastRound().put(k,v));
            } else if (condicao2 && (roundAceitouUltimaVez < round || vMapLastRound.isEmpty())) {
                vMapLastRound = getVmapLastRound();
                // TODO verificar se é para zerar o valor do vMapLastRound
                vMapLastRound.putIfAbsent(agentId, new HashSet<>());

                for (Integer proposerId : idNCFProposers()) {
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

            ProtocolMessageType messageType = ProtocolMessageType.MESSAGE_2B;
            BProtocolMessage protocolSendMsg = createAssignedMessage(new ProtocolMessage(messageType,
                    round, getAgentId(), vMapLastRound), null, keyPair);
            QuorumMessage quorumMessage = new QuorumMessage(MessageType.QUORUM_REQUEST, protocolSendMsg, getQuorumSender().getProcessId());
            getQuorumSender().sendTo(idLearners(), quorumMessage);
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

        return s.size() >= QTD_MINIMA_RESPOSTAS_QUORUM_ACCEPTORS_BIZANTINO;
    }
}
