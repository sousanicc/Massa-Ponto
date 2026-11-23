package com.massapontogrupo.massaponto;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("item_pedido")
public class ItemPedido {

    @Id
    private Long id;

    @Column("pedido_id")
    private Long pedidoId;

    @Column("produto_id")
    private Long produtoId;

    @Column("quantidade")
    private Integer quantidade;

    @Column("observacao")
    private String observacao;

    @Column("preco_unitario_centavos")
    private Integer precoUnitarioCentavos;

    @Transient
    private Produto produto;

    public ItemPedido() {
        this.quantidade = 1;
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