package br.unb.cic.tcc.component;

import br.unb.cic.tcc.messages.ProtocolMessage;
import br.unb.cic.tcc.messages.ProtocolMessageType;
import br.unb.cic.tcc.messages.UsigBProtocolMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class UsigComponent implements IUsig {

    private static int contadorUsig = 0;

    private Map<Integer, UsigBProtocolMessage> msgs = new HashMap<>();

    private static UsigComponent thisComponent = new UsigComponent();

    public static UsigComponent singleton(){
        return thisComponent;
    }

    private UsigComponent() {
    }

    @Override
    public UsigBProtocolMessage createUI(ProtocolMessageType protocolMessageType, Integer round, Integer assinatura, Object message) {
        return new UsigBProtocolMessage(protocolMessageType, round, assinatura, message, addNovoContador());
    }

    @Override
    public UsigBProtocolMessage createUI(ProtocolMessageType protocolMessageType, Integer round, Integer agentSend, Integer assinatura, Object message, Set<ProtocolMessage> proofs) {
        return new UsigBProtocolMessage(protocolMessageType, round, agentSend, assinatura, message, proofs, addNovoContador());
    }

    @Override
    public boolean verifyUI(UsigBProtocolMessage protocolMessage) {
        UsigBProtocolMessage usigBProtocolMessage = msgs.get(protocolMessage.getAssinaturaUsig());
        return usigBProtocolMessage != null && usigBProtocolMessage.equals(protocolMessage);
    }

    private int addNovoContador(){
        return ++contadorUsig;
    }
}
