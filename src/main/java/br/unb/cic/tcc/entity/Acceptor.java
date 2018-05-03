package br.unb.cic.tcc.entity;

import br.unb.cic.tcc.quorum.AcceptorReplica;
import br.unb.cic.tcc.quorum.AcceptorSender;

public class Acceptor extends Agent {
    public Acceptor(int id, String host, int port) {
        int agentId = nextId();

        AcceptorSender acceptorSender = new AcceptorSender(agentId);
        AcceptorReplica acceptorReplica = new AcceptorReplica(id, host, port, this,  acceptorSender);

        setAgentId(agentId);
        setQuorumSender(acceptorSender);
        setQuorumReplica(acceptorReplica);
    }

    public void phase1b(){
        // Pre condições
        // 'a' pertence a 'A' > vdd por default
        // rnd[a] < r, a = this, r = round
        // foi chamado pelo coordenador

        /*
            Acoes:

            rnd[a] recebe r

            envia sinal para coordenador começar fase 2

         */

    }
}
