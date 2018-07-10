package br.unb.cic.tcc.definitions;

public enum Constants {

    AGENT_ID("agentId"),
    V_VAL("vval"),
    V_RND("vrnd");

    private String descricao;

    Constants(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
