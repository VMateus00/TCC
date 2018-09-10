package br.unb.cic.tcc.messages;

import java.util.Set;

public class UsigBProtocolMessage extends BProtocolMessage {

    private final Integer assinaturaUsig;

    public UsigBProtocolMessage(ProtocolMessageType protocolMessageType, Integer round, Integer agentSend, Integer assinatura, Object message, Set<ProtocolMessage> proofs, Integer assinaturaUsig) {
        super(protocolMessageType, round, agentSend, assinatura, message, proofs);

        this.assinaturaUsig = assinaturaUsig;
    }

    public UsigBProtocolMessage(ProtocolMessageType protocolMessageType, Integer round, Integer assinatura, Object message, Integer assinaturaUsig) {
        super(protocolMessageType, round, assinatura, message);
        this.assinaturaUsig = assinaturaUsig;
    }

    public Integer getAssinaturaUsig() {
        return assinaturaUsig;
    }
}
