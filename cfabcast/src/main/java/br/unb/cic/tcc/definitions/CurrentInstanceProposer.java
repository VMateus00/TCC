package br.unb.cic.tcc.definitions;

import br.unb.cic.tcc.messages.ClientMessage;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CurrentInstanceProposer implements Serializable {
    private final Integer instanciaAtual;
    private Integer round;
    protected Map<Integer, Object> proposedValuesOnRound; // round, valorProposto

    private Map<Integer, Map<Integer, Set<ClientMessage>>> vMap;

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

    //    public Integer getRoundAceitouUltimaVez() {
//        return roundAceitouUltimaVez;
//    }
//
//    public void setRoundAceitouUltimaVez(Integer roundAceitouUltimaVez) {
//        this.roundAceitouUltimaVez = roundAceitouUltimaVez;
//    }

    public Map<Integer, Map<Integer, Set<ClientMessage>>> getvMap() {
        return vMap;
    }

    public void setvMap(Map<Integer, Map<Integer, Set<ClientMessage>>> vMap) {
        this.vMap = vMap;
    }
}
