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
                bProtocolMessage.getRound(), bProtocolMessage.getAgentSend(), bProtocolMessage.getInstanciaExecucao(),
                bProtocolMessage.getAssinatura(), bProtocolMessage.getPublicKey(),
                bProtocolMessage.getMessage(), bProtocolMessage.getProofs(), addNovoContador());

        msgs.put(usigBProtocolMessage.getAssinaturaUsig(), usigBProtocolMessage);
        return usigBProtocolMessage;
    }

    @Override
    public boolean verifyUI(UsigBProtocolMessage protocolMessage) {
        UsigBProtocolMessage usigBProtocolMessage = msgs.get(protocolMessage.getAssinaturaUsig());
        msgs.put(protocolMessage.getAssinaturaUsig(), protocolMessage);
        return usigBProtocolMessage == null && protocolMessage.equals(msgs.get(protocolMessage.getAssinaturaUsig()));
    }

    private int addNovoContador(){
        return ++contadorUsig;
    }
}
