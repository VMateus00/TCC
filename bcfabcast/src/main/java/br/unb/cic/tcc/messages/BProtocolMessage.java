package br.unb.cic.tcc.messages;

import java.security.PublicKey;
import java.util.Set;

public class BProtocolMessage extends ProtocolMessage {

    private Set<ProtocolMessage> proofs;
    private final byte[] assinatura;
    private final PublicKey publicKey;

    public BProtocolMessage(ProtocolMessageType protocolMessageType, Integer round, Integer agentSend, byte[] assinatura, PublicKey publicKey, Object message, Set<ProtocolMessage> proofs) {
        super(protocolMessageType, round, agentSend, message);

        this.assinatura = assinatura;
        this.proofs = proofs;
        this.publicKey = publicKey;
    }

    public BProtocolMessage(ProtocolMessageType protocolMessageType, Integer round, Integer agentSend, byte[] assinatura, PublicKey publicKey, Object message) {
        super(protocolMessageType, round, agentSend, message);

        this.assinatura = assinatura;
        this.publicKey = publicKey;
    }

    public Set<ProtocolMessage> getProofs() {
        return proofs;
    }

    public void setProofs(Set<ProtocolMessage> proofs) {
        this.proofs = proofs;
    }

    public byte[] getAssinatura() {
        return assinatura;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }
}
