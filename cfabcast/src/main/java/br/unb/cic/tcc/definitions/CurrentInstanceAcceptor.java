package br.unb.cic.tcc.definitions;

import br.unb.cic.tcc.messages.ClientMessage;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CurrentInstanceAcceptor implements Serializable {
    private final Integer instanciaAtual;
    private Integer round;
    private Integer roundAceitouUltimaVez;

    // VMAP<ROUND, MAP<PROPOSER_ID, MSG>>
    private Map<Integer, Map<Integer, Set<ClientMessage>>> vMap;

    public CurrentInstanceAcceptor(Integer instanciaAtual) {
        this.instanciaAtual = instanciaAtual;
        round = 0;
        roundAceitouUltimaVez = 0;
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

    public Integer getRoundAceitouUltimaVez() {
        return roundAceitouUltimaVez;
    }

    public void setRoundAceitouUltimaVez(Integer roundAceitouUltimaVez) {
        this.roundAceitouUltimaVez = roundAceitouUltimaVez;
    }

    public Map<Integer, Map<Integer, Set<ClientMessage>>> getvMap() {
        return vMap;
    }

    public void setvMap(Map<Integer, Map<Integer, Set<ClientMessage>>> vMap) {
        this.vMap = vMap;
    }

    public Map<Integer, Set<ClientMessage>> getVmapLastRound() {
        getvMap().putIfAbsent(roundAceitouUltimaVez, new HashMap<>());
        return getvMap().get(roundAceitouUltimaVez);
    }
}
