package br.unb.cic.tcc.definitions;

import br.unb.cic.tcc.messages.ClientMessage;
import br.unb.cic.tcc.messages.ProtocolMessage;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

public class CurrentInstanceLearner implements Serializable {
    private final Integer instanciaAtual;
    private Boolean enviouResultado = false;

    protected Map<Integer, Set<ProtocolMessage>> messagesFromAcceptors = new ConcurrentHashMap<>();
    protected Map<Integer, Set<ProtocolMessage>> messagesFromProposers = new ConcurrentHashMap<>();
    // VMAP<ROUND, MAP<PROPOSER_ID, MSG>>
    private Map<Integer, Map<Integer, Set<ClientMessage>>> vMap = new HashMap<>();

    public CurrentInstanceLearner(Integer instanciaAtual) {
        this.instanciaAtual = instanciaAtual;
    }
    
    public void atualizaMessagensRecebidasBaseadoNoRound(Integer round){
        Set<ProtocolMessage> protocolMessagesFromAcceptors = messagesFromAcceptors.get(round);
        Set<ProtocolMessage> protocolMessagesFromProposers = messagesFromProposers.get(round); // só quem envia sao os CF

        if (protocolMessagesFromAcceptors == null) {
            messagesFromAcceptors.put(round, new ConcurrentSkipListSet<>());
        }
        if (protocolMessagesFromProposers == null) {
            messagesFromProposers.put(round, new ConcurrentSkipListSet<>());
        }
    }

    public Integer getInstanciaAtual() {
        return instanciaAtual;
    }

    public Map<Integer, Set<ProtocolMessage>> getMessagesFromAcceptors() {
        return messagesFromAcceptors;
    }

    public Set<ProtocolMessage> messagesFromAcceptorsOnRound(Integer round){
        return messagesFromAcceptors.get(round);
    }

    public Set<ProtocolMessage> messagesFromProposersOnRound(Integer round){
        return messagesFromProposers.get(round);
    }

    public void setMessagesFromAcceptors(Map<Integer, Set<ProtocolMessage>> messagesFromAcceptors) {
        this.messagesFromAcceptors = messagesFromAcceptors;
    }

    public Map<Integer, Set<ProtocolMessage>> getMessagesFromProposers() {
        return messagesFromProposers;
    }

    public void setMessagesFromProposers(Map<Integer, Set<ProtocolMessage>> messagesFromProposers) {
        this.messagesFromProposers = messagesFromProposers;
    }

    public Boolean getEnviouResultado() {
        return enviouResultado;
    }

    public void setEnviouResultado(Boolean enviouResultado) {
        this.enviouResultado = enviouResultado;
    }

    public Map<Integer, Map<Integer, Set<ClientMessage>>> getvMap() {
        return vMap;
    }
}
