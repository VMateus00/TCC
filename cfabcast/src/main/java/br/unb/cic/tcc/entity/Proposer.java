package br.unb.cic.tcc.entity;

import br.unb.cic.tcc.definitions.CurrentInstanceProposer;
import br.unb.cic.tcc.messages.ClientMessage;
import br.unb.cic.tcc.messages.Message1B;
import br.unb.cic.tcc.messages.ProposerClientMessage;
import br.unb.cic.tcc.messages.ProtocolMessage;
import br.unb.cic.tcc.messages.ProtocolMessageType;
import br.unb.cic.tcc.quorum.AgentSender;
import br.unb.cic.tcc.quorum.ProposerReplica;
import quorum.communication.MessageType;
import quorum.communication.QuorumMessage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class Proposer extends Agent<ProposerReplica, AgentSender> {

    protected int currentRound = 1;
    protected Map<Integer, Object> currentValue = new HashMap<>(); // round, valorProposto
    protected HashSet<CurrentInstanceProposer> instancias = new HashSet();
    private final Boolean isColisionFastProposer;

    protected static Integer instanciaExecucao = 0;

    public Proposer(int id, String host, int port, Map<String, Set<Integer>> agentsMap) {
        AgentSender proposerSender = new AgentSender(id);
        ProposerReplica proposerReplica = new ProposerReplica(id, host, port, this);

        setAgentId(id);
        idAgentes = agentsMap;
        setQuorumReplica(proposerReplica);
        setQuorumSender(proposerSender);

        isColisionFastProposer = isColisionFastProposer(id);
    }

    public void phase1A(ProtocolMessage protocolMessage) {
        CurrentInstanceProposer currentInstance;
        boolean enviaMsg = false;
        if(protocolMessage == null){
             currentInstance = defineTypeInstance(++instanciaExecucao);
             enviaMsg = true;
        } else {
            currentInstance = getInstanciaAtual(protocolMessage.getInstanciaExecucao());
            if (currentInstance.getRound() < protocolMessage.getRound()) {
                currentInstance.setRound(protocolMessage.getRound());
                enviaMsg = true;
            }
        }
        if(enviaMsg){
            currentInstance.getvMap().put(currentInstance.getRound(), new HashMap<>());
            ProtocolMessage msgToSend = msgFromPhase1A(currentInstance);
            QuorumMessage quorumMessage = new QuorumMessage(MessageType.QUORUM_REQUEST, msgToSend, getQuorumSender().getProcessId());
            getQuorumSender().sendTo(idAcceptors(), quorumMessage);
        }
    }

    protected ProtocolMessage msgFromPhase1A(CurrentInstanceProposer currentInstance){
        return new ProtocolMessage(ProtocolMessageType.MESSAGE_1A, currentInstance.getRound(), getAgentId(),
                currentInstance.getInstanciaAtual(), null);
    }

    public synchronized void phase2Start(ProtocolMessage protocolMessage) {
        CurrentInstanceProposer instanciaAtual = getInstanciaAtual(protocolMessage.getInstanciaExecucao());
//        System.out.println("Coordinator começou a fase 2Start");

        Set<ProtocolMessage> protocolMessages = instanciaAtual.getMsgsRecebidasOnRound(protocolMessage.getRound());
        protocolMessages.add(protocolMessage);

        if (instanciaAtual.getRound().equals(protocolMessage.getRound())
                && instanciaAtual.getVmapCriadoOnRound(instanciaAtual.getRound()).isEmpty()
                && protocolMessages.size() == QTD_MINIMA_RESPOSTAS_QUORUM_ACCEPTORS_CRASH) {

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
                getvMap().put(protocolMessage.getRound(), null); // deixa vazio nesse caso
                agentsToSendMsg = idProposers();
            } else {
                s.forEach((map) ->
                        map.forEach((k, v) -> instanciaAtual.getVmapCriadoOnRound(instanciaAtual.getRound()).put(k, v))); // TODO verificar caso onde isso entra de novo

                for (int idProposer: idProposers()){
                    instanciaAtual.getVmapCriadoOnRound(instanciaAtual.getRound()).putIfAbsent(idProposer, null);
                }
                agentsToSendMsg = idAcceptorsAndProposers();
            }
            ProtocolMessage msgToSend = new ProtocolMessage(ProtocolMessageType.MESSAGE_2S, instanciaAtual.getRound(), getAgentId(), protocolMessage.getInstanciaExecucao(), instanciaAtual.getVmapCriadoOnRound(instanciaAtual.getRound()));
            QuorumMessage quorumMessage = new QuorumMessage(MessageType.QUORUM_REQUEST, msgToSend, getQuorumSender().getProcessId());
            getQuorumSender().sendTo(agentsToSendMsg, quorumMessage);
        }
    }

    public void phase2A(ProtocolMessage protocolMessage) {
//        System.out.println("Proposer(" + getAgentId() + ") começou a fase 2A");
        CurrentInstanceProposer instanciaAtual = getInstanciaAtual(protocolMessage.getInstanciaExecucao());
        boolean condicao1 = protocolMessage.getProtocolMessageType() == ProtocolMessageType.MESSAGE_PROPOSE && protocolMessage.getMessage() != null;
        boolean condicao2 = protocolMessage.getProtocolMessageType() == ProtocolMessageType.MESSAGE_2A && isColisionFastProposer(protocolMessage.getAgentSend());

        if (protocolMessage.getRound().equals(instanciaAtual.getRound()) // verifica se o round da instancia atual é o mesmo
                && (instanciaAtual.getProposedValueOnRound(protocolMessage.getRound()) == null)
                && (condicao1 || condicao2)) {

            instanciaAtual.getProposedValuesOnRound().put(instanciaAtual.getRound(), protocolMessage.getMessage());

            ProposerClientMessage valResponseMsg;
            if (protocolMessage.getMessage() instanceof ClientMessage) {
                valResponseMsg = new ProposerClientMessage(getAgentId(), (ClientMessage) protocolMessage.getMessage());
            } else if (protocolMessage.getMessage() instanceof ProposerClientMessage) {
                valResponseMsg = (ProposerClientMessage) protocolMessage.getMessage();
            } else if(protocolMessage.getMessage() == null) {
                valResponseMsg = null;
            } else {
                System.out.println("O CF-proposer "+getAgentId()+" não conseguiu reconhecer a mensagem recebida. E portanto nao executou a phase2 corretamente.");
                return;
            }
            ProtocolMessage responseMsg = new ProtocolMessage(ProtocolMessageType.MESSAGE_2A, protocolMessage.getRound(), getAgentId(),
                    protocolMessage.getInstanciaExecucao(), valResponseMsg);
            QuorumMessage quorumMessage = new QuorumMessage(MessageType.QUORUM_REQUEST, responseMsg, getQuorumSender().getProcessId());

            instanciaAtual.saveProposedValueOnRound(protocolMessage.getRound(), valResponseMsg);
            if (protocolMessage.getMessage() != null) {
//                System.out.println("Proposer (" + getAgentId() + ") enviou msg to acceptors and cfProposers");
                getQuorumSender().sendTo(idAcceptorsAndCFProposers(getAgentId()), quorumMessage);
            } else {
//                System.out.println("Proposer (" + getAgentId() + ") enviou msg to leaners");
                getQuorumSender().sendTo(idLearners(), quorumMessage);
            }
        }
    }

    public void phase2Prepare(ProtocolMessage protocolMessage) {
//        System.out.println("Proposer(" + getAgentId() + ") começou a fase 2Prepare");
        CurrentInstanceProposer currentInstance = getInstanciaAtual(protocolMessage.getInstanciaExecucao());

        if (currentInstance.getRound() < protocolMessage.getRound()) {
            currentInstance.setRound(protocolMessage.getRound());

            Map<Integer, Set<ClientMessage>> msgVal = (Map<Integer, Set<ClientMessage>>) protocolMessage.getMessage();
            if (msgVal != null && !msgVal.isEmpty()) {
                currentValue.put(protocolMessage.getRound(), msgVal.get(getAgentId()));
                currentInstance.saveProposedValueOnRound(protocolMessage.getRound(), msgVal.get(getAgentId()));
            } else {
                currentInstance.saveProposedValueOnRound(protocolMessage.getRound(), null);
            }
        }
    }

    protected CurrentInstanceProposer getInstanciaAtual(Integer instanciaExecucao) {
        Optional<CurrentInstanceProposer> instanciaEncontrada = instancias.stream()
                .filter(p -> p.getInstanciaAtual().equals(instanciaExecucao))
                .findFirst();
        if(instanciaEncontrada.isPresent()){
            return instanciaEncontrada.get();
        } else {
            CurrentInstanceProposer currentInstance = defineTypeInstance(instanciaExecucao);
            instancias.add(currentInstance);
            return currentInstance;
        }
    }

    public synchronized void propose(ClientMessage clientMessage) {
        ProtocolMessage protocolMessage = getMessageToPropose(clientMessage);
        QuorumMessage quorumMessage = new QuorumMessage(MessageType.QUORUM_REQUEST, protocolMessage, getQuorumSender().getProcessId());

        System.out.println("Proposer ("+getAgentId()+") propos o valor: "+clientMessage + " na instancia: "+protocolMessage.getInstanciaExecucao());
        if(isColisionFastProposer){
            phase2A(protocolMessage);
        }else{
            getQuorumSender().sendTo(idCFProposers(getAgentId()), quorumMessage);
        }
    }

    protected ProtocolMessage getMessageToPropose(ClientMessage clientMessage){
        CurrentInstanceProposer currentInstance = defineTypeInstance(++instanciaExecucao);
        instancias.add(currentInstance);

        return new ProtocolMessage(ProtocolMessageType.MESSAGE_PROPOSE,
                1, getAgentId(), currentInstance.getInstanciaAtual(), clientMessage);
    }

    protected CurrentInstanceProposer defineTypeInstance(Integer instanciaExecucao){
        return new CurrentInstanceProposer(instanciaExecucao);
    }

    public boolean isCoordinator() {
        return getAgentId() == 1;
    }

}
