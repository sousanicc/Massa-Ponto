package com.massapontogrupo.massaponto.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Table("pedido")
public class Pedido {

    @Id
    private Long id;

    @Column("nome_cliente")
    private String nomeCliente;

    @Column("telefone")
    private String telefone;

    @Column("endereco")
    private String endereco;

    @Column("criado_em")
    private LocalDateTime criadoEm;

    @Column("total_centavos")
    private Integer totalCentavos;

    @Column("forma_pagamento")
    private FormaPagamento formaPagamento;

    @Transient
    private List<ItemPedido> itens = new ArrayList<>();

    public Pedido() {
        this.criadoEm = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomeCliente() {
        return nomeCliente;
    }

    public void setNomeCliente(String nomeCliente) {
        this.nomeCliente = nomeCliente;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }

    public Integer getTotalCentavos() {
        return totalCentavos;
    }

    public void setTotalCentavos(Integer totalCentavos) {
        this.totalCentavos = totalCentavos;
    }

    public FormaPagamento getFormaPagamento() {
        return formaPagamento;
    }

    public void setFormaPagamento(FormaPagamento formaPagamento) {
        this.formaPagamento = formaPagamento;
    }

    public List<ItemPedido> getItens() {
        return itens;
    }

    public void setItens(List<ItemPedido> itens) {
        this.itens = itens;
    }

    public void adicionarItem(ItemPedido item) {
        this.itens.add(item);
    }

    public String getTotalFormatado() {
        if (totalCentavos == null) {
            return "R$ 0,00";
        }
        long reais = totalCentavos / 100;
        long centavos = totalCentavos % 100;
        return String.format("R$ %d,%02d", reais, centavos);
    }

    public String getCriadoEmFormatado() {
        if (criadoEm == null) {
            return "";
        }
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return criadoEm.format(fmt);
    }
}