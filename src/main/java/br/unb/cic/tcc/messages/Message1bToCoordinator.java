package br.unb.cic.tcc.messages;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Message1bToCoordinator implements Serializable {
    private int round;
    private int acceptorId;
    private int roundAcceptorAcceptedLastedValue;
    private Map<String, Object> vValueAcceptor;

    public Message1bToCoordinator(int round, int acceptorId, int roundAcceptorAcceptedLastedValue, Map<String, Object> vValueAcceptor) {
        this.round = round;
        this.acceptorId = acceptorId;
        this.roundAcceptorAcceptedLastedValue = roundAcceptorAcceptedLastedValue;
        this.vValueAcceptor = vValueAcceptor;
    }

    public int getRound() {
        return round;
    }

    public int getAcceptorId() {
        return acceptorId;
    }

    public int getRoundAcceptorAcceptedLastedValue() {
        return roundAcceptorAcceptedLastedValue;
    }

    public Map getvValueAcceptor() {
        return vValueAcceptor;
    }
}
