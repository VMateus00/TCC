package br.unb.cic.tcc.agent;

import br.unb.cic.tcc.component.IUsig;
import br.unb.cic.tcc.component.UsigComponent;
import br.unb.cic.tcc.messages.BProtocolMessage;
import br.unb.cic.tcc.messages.ClientMessage;
import br.unb.cic.tcc.messages.Message1B;
import br.unb.cic.tcc.messages.ProposerClientMessage;
import br.unb.cic.tcc.messages.ProtocolMessage;
import br.unb.cic.tcc.messages.ProtocolMessageType;
import br.unb.cic.tcc.messages.UsigBProtocolMessage;
import quorum.communication.MessageType;
import quorum.communication.QuorumMessage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class UsigBAcceptor extends BAcceptor {

    private final IUsig usigComponenet = new UsigComponent(); // vetor com uma quantidade maior(vamos ignora a posicao zero)
    private final Integer[] contadorRespostasAgentes;

    public UsigBAcceptor(int id, String host, int port, Integer qtdAgentes, Map<String, Set<Integer>> agentsMap) {
        super(id, host, port, agentsMap);
        contadorRespostasAgentes = new Integer[qtdAgentes];

        for (int i=0; i<contadorRespostasAgentes.length;i++){
            contadorRespostasAgentes[i] = 1;
        }
    }

    @Override
    public void phase1b(ProtocolMessage protocolMessage) {
        super.phase1b(protocolMessage);// Igual superClasse
    }

    @Override
    public void phase2b(ProtocolMessage protocolMessage) {
        UsigBProtocolMessage usigBProtocolMessage = (UsigBProtocolMessage) protocolMessage;
        System.out.println("Acceptor(" + getAgentId() + ") começou a fase 2b");
        Map<Integer, Set<ClientMessage>> vMapLastRound = getVmapLastRound();

        int round = usigBProtocolMessage.getRound();

        ProposerClientMessage clientMessage = null;
        if(usigBProtocolMessage.getMessage() instanceof ProposerClientMessage){
            clientMessage = (ProposerClientMessage) usigBProtocolMessage.getMessage();
        }

        boolean condicao1 = usigBProtocolMessage.getProtocolMessageType() == ProtocolMessageType.MESSAGE_2S &&
                (vMapLastRound.isEmpty() ||
                (goodRoundValue(usigBProtocolMessage.getProofs(), usigBProtocolMessage.getRound())
                        && usigComponenet.verifyUI(usigBProtocolMessage)
                        && verifyCnt(usigBProtocolMessage.getAssinaturaUsig(), usigBProtocolMessage.getAgentSend()-1)
                        && (usigBProtocolMessage.getMessage() != null && roundAceitouUltimaVez < round)));

        boolean condicao2 = usigBProtocolMessage.getProtocolMessageType() == ProtocolMessageType.MESSAGE_2A
                && verifyCnt(usigBProtocolMessage.getAssinaturaUsig(), usigBProtocolMessage.getAgentSend()-1)
                && usigComponenet.verifyUI(usigBProtocolMessage)
                && (clientMessage).getClientMessage() != null;

        if (currentRound <= round && (condicao1 || condicao2)) {
            vMapLastRound = getVmapLastRound();

            Integer agentId = protocolMessage.getAgentSend();
            if (condicao1) {
                ((HashMap<Integer, Set<ClientMessage>>)usigBProtocolMessage.getMessage())
                        .forEach((k,v)->getVmapLastRound().put(k,v));
            } else if (condicao2 && (roundAceitouUltimaVez < round || vMapLastRound.isEmpty())) {
                vMapLastRound = getVmapLastRound();
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

            ProtocolMessageType msgType = ProtocolMessageType.MESSAGE_2B;
            UsigBProtocolMessage protocolSendMsg = usigComponenet.createUI(new BProtocolMessage(
                    msgType, round, getAgentId(), encrypt(msgType, keyPair.getPrivate()), keyPair.getPublic(), vMapLastRound));

            QuorumMessage quorumMessage = new QuorumMessage(MessageType.QUORUM_REQUEST, protocolSendMsg, getQuorumSender().getProcessId());
            getQuorumSender().sendTo(idLearners(), quorumMessage);
        }
    }

    private boolean verifyCnt(Integer valorRecebido, Integer agentId){
        if(valorRecebido.equals(contadorRespostasAgentes[agentId])){
            contadorRespostasAgentes[agentId] = contadorRespostasAgentes[agentId]++;
            return true;
        }
        return false;
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

        return s.size() >= QTD_MINIMA_RESPOSTAS_QUORUM_ACCEPTORS_USIG;
    }
}
