package br.unb.cic.tcc.messages;

import java.security.PublicKey;
import java.util.Arrays;
import java.util.Set;

public class BProtocolMessage extends ProtocolMessage {

    private Set<ProtocolMessage> proofs;
    private final byte[] assinatura;
    private final PublicKey publicKey;

    public BProtocolMessage(ProtocolMessage protocolMessage,  byte[] assinaturaHash, Set<ProtocolMessage> proofs, PublicKey publicKey){
        super(protocolMessage.getProtocolMessageType(), protocolMessage.getRound(), protocolMessage.getAgentSend(), protocolMessage.getMessage());
        this.assinatura = assinaturaHash;
        this.publicKey = publicKey;
        this.proofs = proofs;
    }

    public BProtocolMessage(ProtocolMessageType protocolMessageType, Integer round, Integer agentSend,
                            Object message, Set<ProtocolMessage> proofs, byte[] assinatura, PublicKey publicKey) {
        super(protocolMessageType, round, agentSend, message);
        this.proofs = proofs;
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

    @Override
    public String toString() {
        return "BProtocolMessage{" +
                "proofs=" + proofs +
                ", assinatura=" + Arrays.toString(assinatura) +
                ", publicKey=" + publicKey +
                '}';
    }
}
