package br.unb.cic.tcc.component;

import br.unb.cic.tcc.messages.BProtocolMessage;
import br.unb.cic.tcc.messages.UsigBProtocolMessage;

import java.util.HashMap;
import java.util.Map;

public class UsigComponent implements IUsig {

    private int contadorUsig = 0;

    private Map<Integer, UsigBProtocolMessage> msgs = new HashMap<>();

    @Override
    public UsigBProtocolMessage createUI(BProtocolMessage bProtocolMessage) {
        UsigBProtocolMessage usigBProtocolMessage = new UsigBProtocolMessage(bProtocolMessage.getProtocolMessageType(),
                bProtocolMessage.getRound(), bProtocolMessage.getAgentSend(),
                bProtocolMessage.getAssinatura(), bProtocolMessage.getPublicKey(),
                bProtocolMessage.getMessage(), bProtocolMessage.getProofs(), addNovoContador());

        msgs.put(usigBProtocolMessage.getAssinaturaUsig(), usigBProtocolMessage);
        return usigBProtocolMessage;
    }

    @Override
    public boolean verifyUI(UsigBProtocolMessage protocolMessage) { // TODO alinhar
//        UsigBProtocolMessage usigBProtocolMessage = msgs.get(protocolMessage.getAssinaturaUsig());
//        return usigBProtocolMessage != null && usigBProtocolMessage.equals(protocolMessage);
        return true;
    }

    private int addNovoContador(){
        return ++contadorUsig;
    }
}
