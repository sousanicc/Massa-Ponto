package com.massapontogrupo.massaponto.model;

import java.util.Map;

public class ItemPedido {

    private Long id;
    private Long pedidoId;
    private Long produtoId;
    private Integer quantidade;
    private String observacao;
    private Integer precoUnitarioCentavos;
    private Produto produto;

    public ItemPedido() {
        this.quantidade = 1;
    }

    // Converte um registro do JdbcTemplate em um objeto ItemPedido
    public static ItemPedido converterRegistros(Map<String, Object> registro) {
        ItemPedido item = new ItemPedido();
        item.setId(((Number) registro.get("id")).longValue());
        item.setPedidoId(((Number) registro.get("pedido_id")).longValue());
        item.setProdutoId(((Number) registro.get("produto_id")).longValue());
        item.setQuantidade(((Number) registro.get("quantidade")).intValue());
        item.setObservacao((String) registro.get("observacao"));
        item.setPrecoUnitarioCentavos(((Number) registro.get("preco_unitario_centavos")).intValue());
        return item;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPedidoId() {
        return pedidoId;
    }

    public void setPedidoId(Long pedidoId) {
        this.pedidoId = pedidoId;
    }

    public Long getProdutoId() {
        return produtoId;
    }

    public void setProdutoId(Long produtoId) {
        this.produtoId = produtoId;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public Integer getPrecoUnitarioCentavos() {
        return precoUnitarioCentavos;
    }

    public void setPrecoUnitarioCentavos(Integer precoUnitarioCentavos) {
        this.precoUnitarioCentavos = precoUnitarioCentavos;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public Integer getSubtotalCentavos() {
        if (precoUnitarioCentavos == null || quantidade == null) {
            return 0;
        }
        return precoUnitarioCentavos * quantidade;
    }

    public String getSubtotalFormatado() {
        int subtotal = getSubtotalCentavos();
        long reais = subtotal / 100;
        long centavos = subtotal % 100;
        return String.format("R$ %d,%02d", reais, centavos);
    }

    public String getPrecoUnitarioFormatado() {
        if (precoUnitarioCentavos == null) {
            return "R$ 0,00";
        }
        long reais = precoUnitarioCentavos / 100;
        long centavos = precoUnitarioCentavos % 100;
        return String.format("R$ %d,%02d", reais, centavos);
    }
}