package com.massapontogrupo.massaponto;

public enum FormaPagamento {
    PIX("Pix"),
    CARTAO("Cartão"),
    DINHEIRO("Dinheiro");

    private final String nomeExibicao;

    FormaPagamento(String nomeExibicao) {
        this.nomeExibicao = nomeExibicao;
    }

    public String getNomeExibicao() {
        return nomeExibicao;
    }
}