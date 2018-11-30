package br.unb.cic.tcc.messages;

import java.io.Serializable;
import java.util.concurrent.Semaphore;

public class ClientMessage implements Serializable, Comparable {

    private final String instructionToExecute;
    private final Integer idClient;
    private final Integer idMsg;

    private static Integer contadorId = 0;

    private static Semaphore semaphore = new Semaphore(1);

    public ClientMessage(String instructionToExecute, Integer idClient) {
        this.instructionToExecute = instructionToExecute;
        this.idClient = idClient;

        try {
            semaphore.acquire();
            ClientMessage.contadorId++;
            idMsg = ClientMessage.contadorId;
            semaphore.release();
        } catch (InterruptedException e) {
           throw new RuntimeException("Ocorreu erro ao criar a msg");
        }
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
