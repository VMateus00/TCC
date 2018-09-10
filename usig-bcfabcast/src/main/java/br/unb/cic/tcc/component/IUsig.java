package br.unb.cic.tcc.component;

import br.unb.cic.tcc.messages.ProtocolMessage;
import br.unb.cic.tcc.messages.ProtocolMessageType;
import br.unb.cic.tcc.messages.UsigBProtocolMessage;

import java.util.Set;

public interface IUsig {
    UsigBProtocolMessage createUI(ProtocolMessageType protocolMessageType, Integer round, Integer agentSend, Integer assinatura, Object message, Set<ProtocolMessage> proofs);
    UsigBProtocolMessage createUI(ProtocolMessageType protocolMessageType, Integer round, Integer assinatura, Object message);

    boolean verifyUI(UsigBProtocolMessage protocolMessage);
}
