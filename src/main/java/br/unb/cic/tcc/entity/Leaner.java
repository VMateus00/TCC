package br.unb.cic.tcc.entity;

import br.unb.cic.tcc.definitions.Constants;
import br.unb.cic.tcc.messages.ClientMessage;
import br.unb.cic.tcc.messages.ProtocolMessage;
import br.unb.cic.tcc.messages.ProtocolMessageType;
import br.unb.cic.tcc.quorum.LeanerReplica;
import br.unb.cic.tcc.quorum.LeanerSender;
import br.unb.cic.tcc.quorum.Quoruns;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Leaner extends Agent<LeanerReplica, LeanerSender> {

//    private Set<ClientMessage> learned = new HashSet<>();

    private Map<Integer, List<ProtocolMessage>> messagesFromAcceptors = new HashMap<>();
    private Map<Integer, List<ProtocolMessage>> messagesFromProposers = new HashMap<>();

    public Leaner(int id, String host, int port) {
        LeanerSender leanerSender = new LeanerSender(id);
        LeanerReplica leanerReplica = new LeanerReplica(id, host, port, this, leanerSender);

        setAgentId(id);
        setQuorumSender(leanerSender);
        setQuorumReplica(leanerReplica);
    }

    public void learn(ProtocolMessage protocolMessage) {

        List<ProtocolMessage> protocolMessagesFromAcceptors = messagesFromAcceptors.get(protocolMessage.getRound());
        List<ProtocolMessage> protocolMessagesFromProposers = messagesFromProposers.get(protocolMessage.getRound()); // só quem envia sao os CF

        if (protocolMessage.getProtocolMessageType() == ProtocolMessageType.MESSAGE_2A) {
            if (protocolMessagesFromProposers == null){
                messagesFromProposers.put(protocolMessage.getRound(), new ArrayList<>());
                protocolMessagesFromProposers = messagesFromProposers.get(protocolMessage.getRound());
            }
            protocolMessagesFromProposers.add(protocolMessage);
            System.out.println("Proposer chamou a fase leaner");

        } else if (protocolMessage.getProtocolMessageType() == ProtocolMessageType.MESSAGE_2B) {
            if (protocolMessagesFromAcceptors == null) {
                messagesFromAcceptors.put(protocolMessage.getRound(), new ArrayList<>());
                protocolMessagesFromAcceptors = messagesFromAcceptors.get(protocolMessage.getRound());
            }
            protocolMessagesFromAcceptors.add(protocolMessage);
        }

        if (protocolMessagesFromAcceptors.size() == Quoruns.getAcceptors().size()) {
            // ACTIONS:

            if(protocolMessagesFromProposers == null){
                protocolMessagesFromProposers = Collections.emptyList();
            }

            List<ProtocolMessage> msgWithNilValue = protocolMessagesFromProposers.stream()
                    .filter(p -> ((Map<Constants, Object>) p.getMessage()).get(Constants.V_VAL) == null)
                    .collect(Collectors.toList());

            Map<Integer, Set<ClientMessage>> mapToLearn = new HashMap<>();

//            List<Map<Constants, Object>> collect = protocolMessagesFromAcceptors.stream().map(v -> (Map<Constants, Object>) v.getMessage()).collect(Collectors.toList());

            Map<Integer, Set<ClientMessage>> q2bVals = new HashMap<>();
            protocolMessagesFromAcceptors.forEach(protocolMsgAcceptor->{
                Map<Constants, Object> message = (Map<Constants, Object>) protocolMsgAcceptor.getMessage();
                ((Map<Integer, Set<ClientMessage>>)message.get(Constants.V_VAL)).forEach((k,v)-> q2bVals.putIfAbsent(k, v));
            });

            Map<Integer, Set<ClientMessage>> w = new HashMap<>();
            msgWithNilValue.forEach(p-> {
                Map<Constants, Object> message = (Map<Constants, Object>) p.getMessage();
                w.put((Integer) message.get(Constants.AGENT_ID), null);
            });

            Map<Integer, Set<ClientMessage>> learnedThisRound = getLearnedThisRound(protocolMessage.getRound());

            q2bVals.forEach((k,v)-> learnedThisRound.putIfAbsent(k,v));
            w.forEach((k,v)-> learnedThisRound.putIfAbsent(k,v));

        System.out.println("Leaner (" + getAgentId()+") - aprendeu: "+learnedThisRound);
        }
    }

    private Map<Integer, Set<ClientMessage>> getLearnedThisRound(Integer currentRound){
        Map<Integer, Set<ClientMessage>> integerSetMap = getvMap().get(currentRound);
        if(integerSetMap == null){
            integerSetMap = new HashMap<>();
            getvMap().put(currentRound, integerSetMap);
        }
        return integerSetMap;
    }
}
