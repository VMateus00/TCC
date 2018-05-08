package br.unb.cic.tcc.messages;

import br.unb.cic.tcc.entity.Acceptor;

public class AcceptorToProposerMessage {
    private Acceptor acceptor;
    private int round;
    private CFABCastMessageType cfabCastMessageType;

    public AcceptorToProposerMessage(Acceptor acceptor, int round, CFABCastMessageType cfabCastMessageType) {
        this.acceptor = acceptor;
        this.round = round;
        this.cfabCastMessageType = cfabCastMessageType;
    }

    public Acceptor getAcceptor() {
        return acceptor;
    }

    public int getRound() {
        return round;
    }

    public CFABCastMessageType getCfabCastMessageType() {
        return cfabCastMessageType;
    }
}
