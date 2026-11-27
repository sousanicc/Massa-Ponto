package com.massapontogrupo.massaponto;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("produto")
public class Produto {

    @Id
    private Long id;

    @Column("categoria")
    private Categoria categoria;

    @Column("nome")
    private String nome;

    @Column("descricao")
    private String descricao;

    @Column("imagem_url")
    private String imagemUrl;

    @Column("preco_centavos")
    private Integer precoCentavos;

    @Column("ativo")
    private Boolean ativo;

    public Produto() {
        this.ativo = true;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getImagemUrl() {
        return imagemUrl;
    }

    public void setImagemUrl(String imagemUrl) {
        this.imagemUrl = imagemUrl;
    }

    public Integer getPrecoCentavos() {
        return precoCentavos;
    }

    public void setPrecoCentavos(Integer precoCentavos) {
        this.precoCentavos = precoCentavos;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public String getPrecoFormatado() {
        if (precoCentavos == null) {
            return "R$ 0,00";
        }
        long reais = precoCentavos / 100;
        long centavos = precoCentavos % 100;
        return String.format("R$ %d,%02d", reais, centavos);
    }
}