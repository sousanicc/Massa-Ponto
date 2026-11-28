package com.massapontogrupo.massaponto.model;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Pedido {

    private Long id;
    private String nomeCliente;
    private String telefone;
    private String endereco;
    private LocalDateTime criadoEm;
    private Integer totalCentavos;
    private FormaPagamento formaPagamento;
    private List<ItemPedido> itens = new ArrayList<>();

    public Pedido() {
        this.criadoEm = LocalDateTime.now();
    }

    // Converte um registro do JdbcTemplate em um objeto Pedido
    public static Pedido converterRegistros(Map<String, Object> registro) {
        Pedido pedido = new Pedido();
        pedido.setId(((Number) registro.get("id")).longValue());
        pedido.setNomeCliente((String) registro.get("nome_cliente"));
        pedido.setTelefone((String) registro.get("telefone"));
        pedido.setEndereco((String) registro.get("endereco"));
        Object criadoEmObj = registro.get("criado_em");
        if (criadoEmObj instanceof Timestamp ts) {
            pedido.setCriadoEm(ts.toLocalDateTime());
        } else if (criadoEmObj instanceof LocalDateTime ldt) {
            pedido.setCriadoEm(ldt);
        }
        pedido.setTotalCentavos(((Number) registro.get("total_centavos")).intValue());
        pedido.setFormaPagamento(FormaPagamento.valueOf((String) registro.get("forma_pagamento")));
        return pedido;
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