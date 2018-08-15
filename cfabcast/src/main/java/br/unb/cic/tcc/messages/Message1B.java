package br.unb.cic.tcc.messages;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

public class Message1B implements Serializable {

    private Integer roundAceitouUltimaVez;
    private Integer idAcceptor;
    private Map<Integer, Set<ClientMessage>> vMapLastRound;

    public Message1B(Integer roundAceitouUltimaVez, Integer idAcceptor, Map<Integer, Set<ClientMessage>> vMapLastRound) {
        this.roundAceitouUltimaVez = roundAceitouUltimaVez;
        this.idAcceptor = idAcceptor;
        this.vMapLastRound = vMapLastRound;
    }

    public Integer getRoundAceitouUltimaVez() {
        return roundAceitouUltimaVez;
    }

    public void setRoundAceitouUltimaVez(Integer roundAceitouUltimaVez) {
        this.roundAceitouUltimaVez = roundAceitouUltimaVez;
    }

    public Integer getIdAcceptor() {
        return idAcceptor;
    }

    public void setIdAcceptor(Integer idAcceptor) {
        this.idAcceptor = idAcceptor;
    }

    public Map<Integer, Set<ClientMessage>> getvMapLastRound() {
        return vMapLastRound;
    }

    public void setvMapLastRound(Map<Integer, Set<ClientMessage>> vMapLastRound) {
        this.vMapLastRound = vMapLastRound;
    }
}
