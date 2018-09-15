package br.unb.cic.tcc.client;

import br.unb.cic.tcc.messages.ClientMessage;
import br.unb.cic.tcc.quorum.Quoruns;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Semaphore;

public class Client implements Runnable {

    private static Semaphore semaphore = new Semaphore(0);

    private static Map<Integer, Map<Integer, Set<ClientMessage>>> resultado = new HashMap<>();

    @Override
    public void run() {
        ClientMessage msg = new ClientMessage("aprende: " + 0);
        Quoruns.startInstancia(msg);

        try {
            semaphore.acquire();
            System.out.println(resultado);
            System.out.println("------------------------------------------");
//            Thread.sleep(1000*10);
//            Quoruns.startInstancia(new ClientMessage("aprende 1"));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static Semaphore getSemaphore() {
        return semaphore;
    }

    public static Map<Integer, Map<Integer, Set<ClientMessage>>> getResultado() {
        return resultado;
    }

    public static void setResultado(Map<Integer, Map<Integer, Set<ClientMessage>>> resultado) {
        Client.resultado = resultado;
    }
}
