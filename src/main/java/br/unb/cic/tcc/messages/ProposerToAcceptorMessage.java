package br.unb.cic.tcc.messages;

public class ProposerToAcceptorMessage {

    private Integer round;
    private CFABCastMessageType CFABCastMessageType;

    public ProposerToAcceptorMessage(int round, CFABCastMessageType cfabCastMessageType) {
        this.round = round;
        this.CFABCastMessageType = cfabCastMessageType;
    }
}
