package br.unb.cic.tcc.entity;

import br.unb.cic.tcc.definitions.Constants;
import br.unb.cic.tcc.messages.ClientMessage;
import br.unb.cic.tcc.messages.Message1B;
import br.unb.cic.tcc.messages.ProposerClientMessage;
import br.unb.cic.tcc.messages.ProtocolMessage;
import br.unb.cic.tcc.messages.ProtocolMessageType;
import br.unb.cic.tcc.quorum.AgentSender;
import br.unb.cic.tcc.quorum.ProposerReplica;
import br.unb.cic.tcc.quorum.Quoruns;
import quorum.communication.MessageType;
import quorum.communication.QuorumMessage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;

public class Proposer extends Agent<ProposerReplica, AgentSender> {

    protected int currentRound = 0;
    protected Map<Integer, Object> currentValue = new HashMap<>(); // round, valorProposto
    private final Boolean isColisionFastProposer;
    protected Map<Integer, Set<ProtocolMessage>> msgsRecebidas = new ConcurrentHashMap<>(); // round /msgs from acceptors (só o coordinator usa)

    public Proposer(int id, String host, int port) {
        AgentSender proposerSender = new AgentSender(id);
        ProposerReplica proposerReplica = new ProposerReplica(id, host, port, this);

        setAgentId(id);
        setQuorumReplica(proposerReplica);
        setQuorumSender(proposerSender);

        isColisionFastProposer = getAgentId() <= 3 && getAgentId() != 1;
    }

    // Phase 1A só é executada por coordinator
    public void phase1A() {
        Quoruns.atualizaRound(); // Metodo criado para testar varios rounds
        if (currentRound < Quoruns.getRoundAtual()) {
            currentRound = Quoruns.getRoundAtual();
            getvMap().put(currentRound, new HashMap<>());

            ProtocolMessage protocolMessage = new ProtocolMessage(ProtocolMessageType.MESSAGE_1A, currentRound, getAgentId(), null);
            QuorumMessage quorumMessage = new QuorumMessage(MessageType.QUORUM_REQUEST, protocolMessage, getQuorumSender().getProcessId());
            getQuorumSender().sendTo(Quoruns.idAcceptors(), quorumMessage);
        }
    }

    public void phase2A(ProtocolMessage protocolMessage) {
        System.out.println("Proposer(" + getAgentId() + ") começou a fase 2A");
        // nao precisa verificar aqui se é CFproposer, pois quem chama verifica

        boolean condicao1 = protocolMessage.getProtocolMessageType() == ProtocolMessageType.MESSAGE_PROPOSE && protocolMessage.getMessage() != null;
        boolean condicao2 = protocolMessage.getProtocolMessageType() == ProtocolMessageType.MESSAGE_2A && Quoruns.isCFProposerOnRound(protocolMessage.getAgentSend(), currentRound);

        if (currentRound == protocolMessage.getRound()
                && (currentValue.get(currentRound) == null)
                && (condicao1 || condicao2)) {

            currentValue.put(currentRound, protocolMessage.getMessage());

            ProposerClientMessage valResponseMsg = null;
            if (protocolMessage.getMessage() instanceof ClientMessage) {
                valResponseMsg = new ProposerClientMessage(getAgentId(), (ClientMessage) protocolMessage.getMessage());
            } else if (protocolMessage.getMessage() instanceof ProposerClientMessage) {
                valResponseMsg = (ProposerClientMessage) protocolMessage.getMessage();
            } else {
                System.out.println("Erro");
//                throw new Exception("Nao pode entrar como ProposerClientMessage nesse ponto");
            }
            ProtocolMessage responseMsg = new ProtocolMessage(ProtocolMessageType.MESSAGE_2A, protocolMessage.getRound(), getAgentId(), valResponseMsg);
            QuorumMessage quorumMessage = new QuorumMessage(MessageType.QUORUM_REQUEST, responseMsg, getQuorumSender().getProcessId());

            if (protocolMessage.getMessage() != null) {
                System.out.println("Proposer (" + getAgentId() + ") enviou msg to acceptors and cfProposers");
                getQuorumSender().sendTo(Quoruns.idAcceptorsAndCFProposers(currentRound), quorumMessage);
            } else {
                System.out.println("Proposer (" + getAgentId() + ") enviou msg to leaners");
                getQuorumSender().sendTo(Quoruns.idLearners(), quorumMessage);
            }
        }
    }

    public synchronized void phase2Start(ProtocolMessage protocolMessage) {
        System.out.println("Coordinator começou a fase 2Start");
        Set<ProtocolMessage> protocolMessages = msgsRecebidas.get(protocolMessage.getRound());
        if (protocolMessages == null) {
            protocolMessages = new ConcurrentSkipListSet<>();
            msgsRecebidas.put(protocolMessage.getRound(), protocolMessages);
        }
        protocolMessages.add(protocolMessage);

        if (currentRound == protocolMessage.getRound()
                && getMapFromRound(currentRound).isEmpty()
                && protocolMessages.size() == Quoruns.getAcceptors().size()) {

            int max = protocolMessages.stream()
                    .map(p -> (Message1B) p.getMessage())
                    .mapToInt(Message1B::getRoundAceitouUltimaVez)
                    .max().getAsInt();

            List<Map<Integer, Set<ClientMessage>>> s = protocolMessages.stream()
                    .map(p -> (Message1B) p.getMessage())
                    .filter(p -> p.getRoundAceitouUltimaVez().equals(max))
                    .map(Message1B::getvMapLastRound)
                    .collect(Collectors.toList());

            int[] agentsToSendMsg;
            if (s.isEmpty()) {
                getvMap().put(currentRound, null); // deixa vazio nesse caso
                agentsToSendMsg = Quoruns.idProposers();
            } else {
                s.forEach((map) ->
                        map.forEach((k, v) ->
                                getMapFromRound(currentRound).put(k, v))); // TODO verificar caso onde isso entra de novo

                Quoruns.getProposers().forEach(proposer ->
                        getMapFromRound(currentRound).putIfAbsent(proposer.getAgentId(), null));
                agentsToSendMsg = Quoruns.idAcceptorsAndProposers();
            }
            ProtocolMessage msgToSend = new ProtocolMessage(ProtocolMessageType.MESSAGE_2S, currentRound, getAgentId(), getMapFromRound(currentRound));
            QuorumMessage quorumMessage = new QuorumMessage(MessageType.QUORUM_REQUEST, msgToSend, getQuorumSender().getProcessId());
            getQuorumSender().sendTo(agentsToSendMsg, quorumMessage);
        }
    }

    public void phase2Prepare(ProtocolMessage protocolMessage) {
        System.out.println("Proposer(" + getAgentId() + ") começou a fase 2Prepare");
        if (currentRound < protocolMessage.getRound()) {
            currentRound = protocolMessage.getRound();

            Map<Integer, Set<ClientMessage>> msgVal = (Map<Integer, Set<ClientMessage>>) protocolMessage.getMessage();
            if (msgVal != null && !msgVal.isEmpty()) {
                currentValue.put(currentRound, msgVal.get(getAgentId()));
            } else {
                currentValue.put(currentRound, null);
            }
        }
    }

    public void propose(ClientMessage clientMessage) {
        ProtocolMessage protocolMessage = new ProtocolMessage(ProtocolMessageType.MESSAGE_PROPOSE, currentRound, getAgentId(), clientMessage);
        QuorumMessage quorumMessage = new QuorumMessage(MessageType.QUORUM_REQUEST, protocolMessage, getQuorumSender().getProcessId());
        getQuorumSender().sendTo(Quoruns.idCFProposers(currentRound), quorumMessage);

        System.out.println("Proposer (" + getAgentId() + ") enviou uma proposta para os CFProposers");
    }

    public boolean isCoordinator() {
        return getAgentId() == 1;
    }

    public Boolean isColisionFastProposer() {
        return isColisionFastProposer;
    }

    protected Map<Integer, Set<ClientMessage>> getMapFromRound(Integer round) {
        Map<Integer, Set<ClientMessage>> mapOfRound = getvMap().get(round);
        if (mapOfRound == null) {
            mapOfRound = new ConcurrentHashMap<>();
            getvMap().put(round, mapOfRound);
        }
        return mapOfRound;
    }
}
