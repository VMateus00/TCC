package br.unb.cic.tcc.messages;

import java.util.Set;

public class BProtocolMessage extends ProtocolMessage {

    private Set<ProtocolMessage> proofs;
    private final Integer assinatura;

    public BProtocolMessage(ProtocolMessageType protocolMessageType, Integer round, Integer agentSend, Integer assinatura, Object message, Set<ProtocolMessage> proofs) {
        super(protocolMessageType, round, agentSend, message);

        this.assinatura = assinatura;
        this.proofs = proofs;
    }

    public BProtocolMessage(ProtocolMessageType protocolMessageType, Integer round, Integer assinatura, Object message) {
        super(protocolMessageType, round, assinatura, message);

        this.assinatura = assinatura;
    }

    public Set<ProtocolMessage> getProofs() {
        return proofs;
    }

    public void setProofs(Set<ProtocolMessage> proofs) {
        this.proofs = proofs;
    }

    public Integer getAssinatura() {
        return assinatura;
    }
}
