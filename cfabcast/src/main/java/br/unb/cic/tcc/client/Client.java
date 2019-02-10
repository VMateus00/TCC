package br.unb.cic.tcc.client;

import br.unb.cic.tcc.entity.Agent;
import br.unb.cic.tcc.main.Initializer;
import br.unb.cic.tcc.messages.ClientMessage;
import br.unb.cic.tcc.messages.ProtocolMessage;
import br.unb.cic.tcc.quorum.AgentSender;
import br.unb.cic.tcc.quorum.ClientReplica;
import quorum.communication.MessageType;
import quorum.communication.QuorumMessage;
import quorum.core.QuorumSender;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Client extends Agent<ClientReplica, QuorumSender> implements Runnable {

    public static final int QTD_INSTRUCOES = 10;
    public static final int QTD_TESTES = 5;

    private List<MsgInfo> msgs = new ArrayList<>();
    private Integer qtdRespostasRecebidas = 0;
    private Integer qtdTestesRealizados = 0;

    public Client(int id, String host, int port, Map<String, Set<Integer>> agentsMap) {
        AgentSender leanerSender = new AgentSender(id);
        ClientReplica learnerReplica = new ClientReplica(id, host, port, this);

        setAgentId(id);
        idAgentes = agentsMap;
        setQuorumSender(leanerSender);
        setQuorumReplica(learnerReplica);

        createFile();

        try {
            Thread.sleep(5000);
            this.run();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        System.out.println(idAgentes);
        for (int i = 0; i < QTD_INSTRUCOES; i++) {
            try {
                enviaMsgTeste();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private synchronized void enviaMsgTeste() throws IOException {
        ClientMessage clientMessage = new ClientMessage(getAgentId());
        enviaMsgFromClientToProposer(clientMessage);
        msgs.add(new MsgInfo(clientMessage.getIdMsg(), System.currentTimeMillis()));
        System.out.println("Client (" + getAgentId() + ") enviou msg com id:" + clientMessage.getIdMsg() + " as " + new Date());
        printWriter.write("Client (" + getAgentId() + ") enviou msg com id:" + clientMessage.getIdMsg() + " as " + new Date()+"\n");
        printWriter.flush();

        qtdTestesRealizados++;
    }

    private void enviaMsgFromClientToProposer(ClientMessage clientMessage) {
        // By default there will be only two CF-propers, with this, we will only send proposes to these two.

        Integer[] idProposers = idAgentes.get(Initializer.PROPOSERS).stream()
                .sorted(Comparator.naturalOrder())
                .toArray(Integer[]::new);

        int cfProposer[];
        if (idProposers.length > 1) {
            if (clientMessage.getIdMsg() % 2 == 0) {
                int[] temp = {idProposers[0]};
                cfProposer = temp;
            } else {
                int[] temp = {idProposers[1]};
                cfProposer = temp;
            }
        } else {
            // send to 1
            int[] temp = {idProposers[0]};
            cfProposer = temp;
        }
        getQuorumSender().sendTo(cfProposer, new QuorumMessage(MessageType.QUORUM_REQUEST, clientMessage, getAgentId()));
    }

    public void mostraRecebeuMensagem(ProtocolMessage protocolMessage) throws IOException {
        ClientMessage msg = (ClientMessage) protocolMessage.getMessage();
        long instantReceivedConfirmation = System.currentTimeMillis();
        MsgInfo msgInfoFromMsg = getMsgInfoFromMsg(msg.getIdMsg());
        msgInfoFromMsg.setInstantEnd(instantReceivedConfirmation);

        System.out.println("Client (" + getAgentId() + ") recebeu que a instrução com id: " + msg.getIdMsg()
                + " foi concluída com latencia de "+ msgInfoFromMsg.latenciaEnvioMsgAteReceberConfirmacao()+" ms");

//        printWriter.println("Client (" + getAgentId() + ") recebeu que a instrução com id: " + msg.getIdMsg()
        printWriter.write("Client (" + getAgentId() + ") recebeu que a instrução com id: " + msg.getIdMsg()
                + " foi concluída com latencia de "+ msgInfoFromMsg.latenciaEnvioMsgAteReceberConfirmacao()+" ms\n");
        printWriter.flush();

        System.out.println("Quantidade de testes realizados: "+qtdTestesRealizados);
        if (qtdTestesRealizados < QTD_INSTRUCOES * QTD_TESTES) {
            enviaMsgTeste();
        }

        if (qtdRespostasRecebidas == QTD_INSTRUCOES * QTD_TESTES) {
            insertInfoOnFile(printWriter);
            System.out.println("Arquivo com respostas criado");
        }
    }

    private void insertInfoOnFile(BufferedWriter printWriter) throws IOException {
        printWriter.write("----------------------\n");
        msgs.forEach(msg -> {
            try {
                printWriter.write("Msg com id: " + msg.getMsgId() + " - demorou " + msg.latenciaEnvioMsgAteReceberConfirmacao() + " ms para ser confirmada - latencia\n");
                printWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Msg com id: " + msg.getMsgId() + " - demorou " + msg.latenciaEnvioMsgAteReceberConfirmacao() + " ms para ser confirmada - latencia\n");
        });
    }

    private synchronized MsgInfo getMsgInfoFromMsg(Integer idMsg) {
        MsgInfo msgInfo = msgs.stream().filter(p -> p.getMsgId().equals(idMsg))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Não foi encontrada a msg associada"));

        qtdRespostasRecebidas++;
        return msgInfo;
    }

    @Override
    protected String fileName() {
        return "respostas_client_" + getAgentId() + ".txt";
    }
}
