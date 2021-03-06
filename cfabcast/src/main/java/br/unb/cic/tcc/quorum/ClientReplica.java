package br.unb.cic.tcc.quorum;

import br.unb.cic.tcc.client.Client;
import br.unb.cic.tcc.entity.Acceptor;
import br.unb.cic.tcc.messages.ProtocolMessage;
import quorum.communication.QuorumMessage;
import quorum.core.QuorumReplica;

public class ClientReplica extends QuorumReplica {

    private Client client;

    public ClientReplica(int id, String host, int port, Client client) {
        super(id, "", host, port);
        this.client = client;
    }

    @Override
    public QuorumMessage executeRequest(QuorumMessage quorumMessage) {
        ProtocolMessage message = (ProtocolMessage) quorumMessage.getMsg();
        client.mostraRecebeuMensagem(message);

        return null;
    }

    @Override
    public QuorumMessage executeReconfigurationMessage(QuorumMessage quorumMessage) {
        throw new UnsupportedOperationException("Not utilized in this protocol");
    }
}
