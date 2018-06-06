package br.unb.cic.tcc.messages;

import br.unb.cic.tcc.entity.Acceptor;

public class AcceptorToProposerMessage {
    private Acceptor acceptor;
    private int round;
    private ProtocolMessageType protocolMessageType;

    public AcceptorToProposerMessage(Acceptor acceptor, int round, ProtocolMessageType protocolMessageType) {
        this.acceptor = acceptor;
        this.round = round;
        this.protocolMessageType = protocolMessageType;
    }

    public Acceptor getAcceptor() {
        return acceptor;
    }

    public int getRound() {
        return round;
    }

    public ProtocolMessageType getProtocolMessageType() {
        return protocolMessageType;
    }
}
