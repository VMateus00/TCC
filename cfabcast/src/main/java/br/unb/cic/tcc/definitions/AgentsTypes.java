package br.unb.cic.tcc.definitions;

public enum AgentsTypes {
    COORDINATOR("Coordinator"),
    PROPOSER("Proposer"),
    ACCEPTOR("Acceptor"),
    LEARNER("Learner");

    private String descricao;

    AgentsTypes(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
