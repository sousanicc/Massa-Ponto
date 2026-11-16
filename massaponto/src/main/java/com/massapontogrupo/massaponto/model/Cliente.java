package com.massapontogrupo.massaponto.model;

public class Cliente {

    private int id;
    private String nome;
    private String CPF;

    public Cliente() {
    }

    public Cliente(String nome, String CPF) {
        this.nome = nome;
        this.CPF = CPF;
    }

    public Cliente(String nome, String CPF, int id) {
        this.nome = nome;
        this.CPF = CPF;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCPF() {
        return CPF;
    }

    public void setCPF(String cPF) {
        CPF = cPF;
    }

}