package br.unb.cic.tcc.entity;

import br.unb.cic.tcc.definitions.CurrentInstanceAcceptor;
import br.unb.cic.tcc.messages.ClientMessage;
import br.unb.cic.tcc.messages.Message1B;
import br.unb.cic.tcc.messages.ProposerClientMessage;
import br.unb.cic.tcc.messages.ProtocolMessage;
import br.unb.cic.tcc.messages.ProtocolMessageType;
import br.unb.cic.tcc.quorum.AcceptorReplica;
import br.unb.cic.tcc.quorum.AgentSender;
import quorum.communication.MessageType;
import quorum.communication.QuorumMessage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class Acceptor extends Agent<AcceptorReplica, AgentSender> {
//    protected int currentRound = 0;
//    protected int roundAceitouUltimaVez = 0; // começa nunca tendo aceitado nada, por isso 0
//    protected int instanciaAtual = 0;
//    protected Map<Integer, Map<Integer, Object>> valueByInstancia = new HashMap<>(); //instancia <round, valor>
    protected HashSet<CurrentInstanceAcceptor> instancias = new HashSet();

    public Acceptor(int id, String host, int port, Map<String, Set<Integer>> agentsMap) {
        AgentSender acceptorSender = new AgentSender(id);
        AcceptorReplica acceptorReplica = new AcceptorReplica(id, host, port, this);

        setAgentId(id);
        idAgentes = agentsMap;
        setQuorumSender(acceptorSender);
        setQuorumReplica(acceptorReplica);
    }

    public void phase1b(ProtocolMessage protocolMessage) {
        CurrentInstanceAcceptor instanciaAtual = getInstanciaAtual(protocolMessage.getInstanciaExecucao());

        if(instanciaAtual.getRound() < protocolMessage.getRound()
                && protocolMessage.getProtocolMessageType() == ProtocolMessageType.MESSAGE_1A){
            instanciaAtual.setRound(protocolMessage.getRound());

            Message1B message1B = new Message1B(instanciaAtual.getRoundAceitouUltimaVez(), getAgentId(), instanciaAtual.getVmapLastRound());

            ProtocolMessage protocolMessageToSend = new ProtocolMessage(ProtocolMessageType.MESSAGE_1B, instanciaAtual.getRound(), getAgentId(), instanciaAtual.getInstanciaAtual(), message1B);
            QuorumMessage quorumMessage = new QuorumMessage(MessageType.QUORUM_REQUEST, protocolMessageToSend, getQuorumSender().getProcessId());
            getQuorumSender().sendTo(idCoordinator(), quorumMessage);
        }
    }

    public void phase2b(ProtocolMessage protocolMessage) {
        CurrentInstanceAcceptor instanciaAtual = getInstanciaAtual(protocolMessage.getInstanciaExecucao());
        Integer roundAceitouUltimaVez = instanciaAtual.getRoundAceitouUltimaVez();
        System.out.println("Acceptor(" + getAgentId() + ") começou a fase 2b - instancia:" +instanciaAtual.getInstanciaAtual());

        Map<Integer, Set<ClientMessage>> vMapLastRound = instanciaAtual.getVmapLastRound();

        int round = protocolMessage.getRound();

        ProposerClientMessage clientMessage = null;
        if(protocolMessage.getMessage() instanceof ProposerClientMessage){
            clientMessage = (ProposerClientMessage) protocolMessage.getMessage();
        }

        boolean condicao1 = protocolMessage.getProtocolMessageType() == ProtocolMessageType.MESSAGE_2S &&
                    ((protocolMessage.getMessage() != null && roundAceitouUltimaVez < round) || vMapLastRound.isEmpty());  //vval[a] == none

        boolean condicao2 = protocolMessage.getProtocolMessageType() == ProtocolMessageType.MESSAGE_2A
                && (clientMessage).getClientMessage() != null;

        if (instanciaAtual.getRound() <= round && (condicao1 || condicao2)) {
            instanciaAtual.setRoundAceitouUltimaVez(round);
            instanciaAtual.setRound(round);
            vMapLastRound = instanciaAtual.getVmapLastRound();

            Integer agentId = protocolMessage.getAgentSend();
            if (condicao1) {
                ((HashMap<Integer, Set<ClientMessage>>)protocolMessage.getMessage())
                        .forEach((k,v)->instanciaAtual.getVmapLastRound().put(k,v));
            } else if (condicao2 && (roundAceitouUltimaVez < round || vMapLastRound.isEmpty())) {
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

            ProtocolMessage protocolSendMsg = new ProtocolMessage(ProtocolMessageType.MESSAGE_2B, round, getAgentId(), instanciaAtual.getInstanciaAtual(), vMapLastRound);
            QuorumMessage quorumMessage = new QuorumMessage(MessageType.QUORUM_REQUEST, protocolSendMsg, getQuorumSender().getProcessId());
            getQuorumSender().sendTo(idLearners(), quorumMessage);
        }
    }

    protected CurrentInstanceAcceptor getInstanciaAtual(Integer instancia){
        Optional<CurrentInstanceAcceptor> first = instancias.stream()
                .filter(p -> p.getInstanciaAtual().equals(instancia))
                .findFirst();

        if(first.isPresent()){
            return first.get();
        } else {
            CurrentInstanceAcceptor currentInstance = new CurrentInstanceAcceptor(instancia);
            instancias.add(currentInstance);
            return currentInstance;
        }
    }

    @Override
    public void limpaDadosExecucao() { // TODO remover esse metodo
//        currentRound = 1;
//        roundAceitouUltimaVez = 0;
//        setvMap(new HashMap<>());
    }
}
