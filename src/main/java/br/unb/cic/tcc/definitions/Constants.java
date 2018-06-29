package br.unb.cic.tcc.definitions;

public enum Constants {

    AGENT_ID("agentId"),
    V_VAL("vval");

    private String descricao;

    Constants(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
