package br.unb.cic.tcc.client;

import java.io.Serializable;

public class MsgInfo implements Serializable {
    private Integer msgId;
    private Long instantStart;
    private Long instantEnd;

    public MsgInfo(Integer msgId, Long instantStart) {
        this.msgId = msgId;
        this.instantStart = instantStart;
    }

    public Integer getMsgId() {
        return msgId;
    }

    public void setMsgId(Integer msgId) {
        this.msgId = msgId;
    }

    public Long getInstantStart() {
        return instantStart;
    }

    public void setInstantStart(Long instantStart) {
        this.instantStart = instantStart;
    }

    public Long getInstantEnd() {
        return instantEnd;
    }

    public void setInstantEnd(Long instantEnd) {
        this.instantEnd = instantEnd;
    }

    // retorno em milisegundos
    public long latenciaEnvioMsgAteReceberConfirmacao(){
        return instantEnd - instantStart;
    }
}
