package br.unb.cic.tcc.messages;

import java.io.Serializable;

public class ClientMessage implements Serializable, Comparable {
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

    @Override
    public String toString() {
        return "ClientMessage{" +
                "msg='" + msg + '\'' +
                '}';
    }

    @Override
    public int compareTo(Object o) {
        if(!(o instanceof ClientMessage)){
            return -1;
        }
        return ((ClientMessage)o).getMsg().compareTo(this.getMsg());
    }
}
