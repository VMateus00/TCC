package br.unb.cic.tcc.messages;

import java.io.Serializable;

public class ClientMessage implements Serializable, Comparable {

    private final String instructionToExecute;
    private final Integer idClient;
    private final Integer idMsg;

    private static Integer contadorId = 0;

    public ClientMessage(String instructionToExecute, Integer idClient) {
        this.instructionToExecute = instructionToExecute;
        this.idClient = idClient;

        ClientMessage.contadorId++;
        idMsg = ClientMessage.contadorId;
    }

    public String getInstructionToExecute() {
        return instructionToExecute;
    }

    public Integer getIdClient() {
        return idClient;
    }

    public Integer getIdMsg() {
        return idMsg;
    }

    public static Integer getContadorId() {
        return contadorId;
    }

    public static void setContadorId(Integer contadorId) {
        ClientMessage.contadorId = contadorId;
    }

    @Override
    public String toString() {
        return "ClientMessage{" +
                "instructionToExecute='" + instructionToExecute + '\'' +
                ", idClient=" + idClient +
                '}';
    }

    @Override
    public int compareTo(Object o) {
        if(!(o instanceof ClientMessage)){
            return -1;
        }
        return ((ClientMessage)o).getInstructionToExecute().compareTo(this.getInstructionToExecute());
    }
}
