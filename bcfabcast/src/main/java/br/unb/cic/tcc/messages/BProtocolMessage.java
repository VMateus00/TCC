package br.unb.cic.tcc.messages;

public class BProtocolMessage extends ProtocolMessage {

    private Object proof; // TODO nao faco ideia de como isso funciona

    public BProtocolMessage(ProtocolMessageType protocolMessageType, Integer round, Integer agentSend, Object message, Object proof) {
        super(protocolMessageType, round, agentSend, message);
        this.proof = proof;
    }

    public Object getProof() {
        return proof;
    }

    public void setProof(Object proof) {
        this.proof = proof;
    }
}
