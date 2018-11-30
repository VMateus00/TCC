package br.unb.cic.tcc.definitions;

import br.unb.cic.tcc.messages.ClientMessage;
import br.unb.cic.tcc.messages.ProtocolMessage;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class CurrentInstanceProposer implements Serializable {
    private final Integer instanciaAtual;
    private Integer round;
    protected Map<Integer, Object> proposedValuesOnRound; // round, valorProposto

    private Map<Integer, Map<Integer, Set<ClientMessage>>> vMap;
    protected Map<Integer, Set<ProtocolMessage>> msgsRecebidas = new ConcurrentHashMap<>(); // round /msgs from acceptors (só o coordinator usa)

    public CurrentInstanceProposer(Integer instanciaAtual) {
        this.instanciaAtual = instanciaAtual;
        round = 1;
        proposedValuesOnRound = new HashMap<>();
        vMap = new HashMap<>();
    }

    public Integer getInstanciaAtual() {
        return instanciaAtual;
    }

    public Integer getRound() {
        return round;
    }

    public void setRound(Integer round) {
        this.round = round;
    }

    public Object getProposedValueOnRound(Integer round){
        return proposedValuesOnRound.get(round);
    }

    public void saveProposedValueOnRound(Integer round, Object value){
        proposedValuesOnRound.put(round, value);
    }

    public Map<Integer, Object> getProposedValuesOnRound() {
        return proposedValuesOnRound;
    }

    public Map<Integer, Map<Integer, Set<ClientMessage>>> getvMap() {
        return vMap;
    }

    public Map<Integer, Set<ClientMessage>> getVmapCriadoOnRound(Integer round){
        Map<Integer, Set<ClientMessage>> integerSetMap = vMap.get(round);
        if(integerSetMap == null){
            integerSetMap = new HashMap<>();
            vMap.put(round, integerSetMap);
        }
        return integerSetMap;
    }

    public void setvMap(Map<Integer, Map<Integer, Set<ClientMessage>>> vMap) {
        this.vMap = vMap;
    }

    public Map<Integer, Set<ProtocolMessage>> getMsgsRecebidas() {
        return msgsRecebidas;
    }

    public Set<ProtocolMessage> getMsgsRecebidasOnRound(Integer round){
        Set<ProtocolMessage> protocolMessages = msgsRecebidas.get(round);
        if(protocolMessages == null){
            protocolMessages = new HashSet<>();
            msgsRecebidas.put(round, protocolMessages);
        }
        return protocolMessages;
    }

    public void setMsgsRecebidas(Map<Integer, Set<ProtocolMessage>> msgsRecebidas) {
        this.msgsRecebidas = msgsRecebidas;
    }
}
