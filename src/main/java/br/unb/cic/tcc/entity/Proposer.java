package br.unb.cic.tcc.entity;

import br.unb.cic.tcc.messages.ClientMessage;
import br.unb.cic.tcc.messages.Message1bToCoordinator;
import br.unb.cic.tcc.quorum.ProposerReplica;
import br.unb.cic.tcc.quorum.ProposerSender;
import br.unb.cic.tcc.quorum.Quoruns;
import quorum.communication.QuorumMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Proposer extends Agent<ProposerReplica, ProposerSender> {
    private int currentRound = 0;
    private Object currentValue;
    private Boolean isColisionFastProposer = true; // TODO deixar aleatorio

    // TODO revisar uma melhor solucao
    public Proposer(int id, String host, int port) {
        int agentId = nextId();
        ProposerSender proposerSender = new ProposerSender(agentId);
        ProposerReplica proposerReplica = new ProposerReplica(id, host, port, this, proposerSender);

        setAgentId(agentId);
        setQuorumReplica(proposerReplica);
        setQuorumSender(proposerSender);
    }

    // Phase 1A só é executada por coordinator
    public void phase1A(int round) {
        if (currentRound < round) {
            currentRound = round; // crnd[c] = <- r
            // current round proposer
            setvMap(new HashMap<>()); // cval[c] <- none
//            send msg to ACCEPTOR

            List<Acceptor> acceptors = Quoruns.getAcceptors();
            ArrayList<Message1bToCoordinator> resultMessages = new ArrayList<>();
            //PASSO DE COMUNICACAO
            // envia sinal 1A para todos os acceptors
            acceptors.forEach(acceptor ->
                    resultMessages.add(acceptor.recebe1AFromCoordinator(round)));
        } else {
            // error
        }
    }

    public void phase2A(int round) {
        // TODO
        if (isColisionFastProposer && currentRound == round && currentValue == null) {

        }
    }

    public void phase2Start(int round) {
        if (isCoordinator() && currentRound == round
                && getvMap().isEmpty()) {
            // Recebe a resposta dos acceptors
            List<QuorumMessage> msgsRecebidas = null;

            Object k = null;
            Object s = null; // depende de k

            if(s == null){
                currentValue = null;
//                send '2S', round, currentValue to Proposers Quorum
            } else {
                // setar valor de currentValue
//              send '2S', round, currentValue to Proposers Quorum
            }
        }
    }

    public void phase2Prepare(int round, Map<String, Object> vMapCoordinator) {
        // isProposer == default,
        // Recebeu 2S
        if (currentRound < round) {
            currentRound = round;

            if (vMapCoordinator == null) {
                currentValue = null;
            } else {
                currentValue = vMapCoordinator; // ??? pVal[p] = v(p) alinhar com o professor
            }
        }
    }

    public boolean isCoordinator() {
        return getAgentId() == 1;
    }

//    public void propose(ClientMessage clientMessage) {
////        TODO começar fase 1A
//        int round = 0; // TODO verificar qual o valor inicial do round
//        phase1A(round); // enviar isso para todos os proposers
//    }

    public void propose(ClientMessage clientMessage) {
        // chama o coordinator para iniciar o round
        List<Proposer> coordinators = Quoruns.getCoordinators();
        int coordinatorPosition = Quoruns.RANDOM.nextInt(coordinators.size());
        Proposer coordinator = coordinators.get(coordinatorPosition == 0 ? coordinatorPosition : coordinatorPosition - 1);

        int round = 1;
        coordinator.phase1A(round);
    }
}
