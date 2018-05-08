package br.unb.cic.tcc.messages;

import java.io.Serializable;

public class ClientMessage implements Serializable {
    private String msg;

    public ClientMessage(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
