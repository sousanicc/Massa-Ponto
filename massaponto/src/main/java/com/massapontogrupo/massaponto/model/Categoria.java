package com.massapontogrupo.massaponto.model;

public enum Categoria {
    PIZZA_SALGADA("Pizza Salgada"),
    PIZZA_DOCE("Pizza Doce"),
    BEBIDA("Bebida");

    private final String nomeExibicao;

    Categoria(String nomeExibicao) {
        this.nomeExibicao = nomeExibicao;
    }

    public String getNomeExibicao() {
        return nomeExibicao;
    }
}