package br.unb.cic.tcc.entity;

import br.unb.cic.tcc.messages.AcceptorToProposerMessage;
import br.unb.cic.tcc.messages.CFABCastMessageType;
import br.unb.cic.tcc.messages.ClientMessage;
import br.unb.cic.tcc.quorum.ProposerReplica;
import br.unb.cic.tcc.quorum.ProposerSender;
import br.unb.cic.tcc.quorum.Quoruns;
import quorum.communication.QuorumMessage;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class Proposer extends Agent<ProposerReplica, ProposerSender> {
    private int currentRound = 0;
    private Object currentValue;

    public static CyclicBarrier barreiraPhase1A;

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
    public void phase1A(int round){
        if(currentRound < round){
            currentRound = round; // crnd[c] = <- r
            // current round proposer
            setvMap(new HashMap<>()); // cval[c] <- none
//            send msg to ACCEPTOR

//            getQuorumSender().send1AToAcceptor(round);
            List<Acceptor> acceptors = Quoruns.getAcceptors();
            barreiraPhase1A = new CyclicBarrier(acceptors.size());
            try {
                acceptors.forEach(acceptor->{
                    // envia sinal 1A para todos os acceptors
                    
                });
                barreiraPhase1A.await();


            } catch (Exception e) {
                System.out.println("Erro na barreira");
                e.printStackTrace();
            }


        }
    }

    public void phase2A(){
    }

    public void phase2Start(int round, QuorumMessage quorumMessage){
        if( quorumMessage.getMsg() instanceof AcceptorToProposerMessage &&
                ((AcceptorToProposerMessage)quorumMessage.getMsg()).getCfabCastMessageType() == CFABCastMessageType.PHASE_1B){

            if(isCoordinator() && currentRound == round
                    && getvMap().isEmpty()){


                List<QuorumMessage> msgsRecebidas = null;

                if(msgsRecebidas.isEmpty()){
                    // chama
                }else{

                }
            }
        }
    }

    public void phase2Prepare(){

    }

    public boolean isCoordinator(){
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
        int coordinatorId = Quoruns.RANDOM.nextInt(coordinators.size());
        Proposer coordinator = coordinators.get(coordinatorId - 1);

        int round = 1;
        coordinator.phase1A(round);
    }
}
