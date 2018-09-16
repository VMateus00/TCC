package br.unb.cic.tcc.messages;

import java.security.PublicKey;
import java.util.Set;

public class UsigBProtocolMessage extends BProtocolMessage {

    private final Integer assinaturaUsig;

    public UsigBProtocolMessage(ProtocolMessageType protocolMessageType, Integer round, Integer agentSend, byte[] assinatura, PublicKey publicKey, Object message, Set<ProtocolMessage> proofs, Integer assinaturaUsig) {
        super(protocolMessageType, round, agentSend, assinatura, publicKey, message, proofs);

        this.assinaturaUsig = assinaturaUsig;
    }

    public Integer getAssinaturaUsig() {
        return assinaturaUsig;
    }
}
