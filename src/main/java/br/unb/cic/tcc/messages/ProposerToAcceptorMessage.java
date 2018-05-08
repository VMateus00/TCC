package br.unb.cic.tcc.messages;

import java.io.Serializable;

public class ProposerToAcceptorMessage implements Serializable {

    private Integer round;
    private CFABCastMessageType CFABCastMessageType;

    public ProposerToAcceptorMessage(int round, CFABCastMessageType cfabCastMessageType) {
        this.round = round;
        this.CFABCastMessageType = cfabCastMessageType;
    }

    public Integer getRound() {
        return round;
    }
}
