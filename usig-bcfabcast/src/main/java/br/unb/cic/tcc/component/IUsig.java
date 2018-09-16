package br.unb.cic.tcc.component;

import br.unb.cic.tcc.messages.BProtocolMessage;
import br.unb.cic.tcc.messages.UsigBProtocolMessage;

public interface IUsig {
    UsigBProtocolMessage createUI(BProtocolMessage bProtocolMessage);

    boolean verifyUI(UsigBProtocolMessage protocolMessage);
}
