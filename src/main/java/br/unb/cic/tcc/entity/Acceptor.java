package br.unb.cic.tcc.entity;

import br.unb.cic.tcc.messages.Message1bToCoordinator;
import br.unb.cic.tcc.quorum.AcceptorReplica;
import br.unb.cic.tcc.quorum.AcceptorSender;

public class Acceptor extends Agent<AcceptorReplica, AcceptorSender> {
    private int currentRound = 0;
    private int roundAceitouUltimaVez = -1; // começa nunca tendo aceitado nada, por isso -1

    public Acceptor(int id, String host, int port) {
        AcceptorSender acceptorSender = new AcceptorSender(id);
        AcceptorReplica acceptorReplica = new AcceptorReplica(id, host, port, this,  acceptorSender);

        setAgentId(id);
        setQuorumSender(acceptorSender);
        setQuorumReplica(acceptorReplica);
    }

//    public AcceptorToProposerMessage phase1b(int round){
//        if(currentRound < round){ // rnd[a] < r
//            currentRound = round;
//            return new AcceptorToProposerMessage(this, round, ProtocolMessageType.MESSAGE_1B);
//        }
//        return null;
//    }

    public Message1bToCoordinator recebe1AFromCoordinator(int round){
        return phase1b(round);
    }

    private Message1bToCoordinator phase1b(int round) {
        if(currentRound < round){
            currentRound = round;
            // envia 1B, round, valor round e
            // PASSO DE COMUNICACAO
            return new Message1bToCoordinator(round, this.getAgentId().intValue(), roundAceitouUltimaVez, this.getvMap());
        }
        return null;
    }

    public void phase2b(int round) {
        // CONDICAO_1: (received ("2S", r, v), v != null e roundAceitouUltimaVez < r ) ou getvMap() == null ou vazio
        // CONDICAO_2: receveid ("2A", r, (p, V)), V != null

        boolean condicao1 = true;
        boolean condicao2 = true;

        if (currentRound <= round) {  // && condicao_1 ou condicao_2
            roundAceitouUltimaVez = round;
            currentRound = round;

            if (condicao1) {

            } else if (condicao2 && (roundAceitouUltimaVez < round || getvMap().isEmpty())) {

            } else {

            }

            // send 2b, a, r, vval[a] to L
        }
    }
}
