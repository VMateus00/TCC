package br.unb.cic.tcc.defintions;

import br.unb.cic.tcc.definitions.CurrentInstanceProposer;
import br.unb.cic.tcc.messages.ProtocolMessage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CurrentInstanceBProposer extends CurrentInstanceProposer {

    protected Map<Integer, Set<ProtocolMessage>> proofs = new HashMap<>(); // round - provas

    public CurrentInstanceBProposer(Integer instanciaAtual) {
        super(instanciaAtual);
    }

    public Map<Integer, Set<ProtocolMessage>> getProofs() {
        return proofs;
    }

    public Set<ProtocolMessage> getProofsFromRound(){
        Set<ProtocolMessage> protocolMessages = proofs.get(getRound());
        if(protocolMessages == null) {
            protocolMessages = new HashSet<>();
            proofs.put(getRound(), protocolMessages);
        }
        return protocolMessages;
    }
}
