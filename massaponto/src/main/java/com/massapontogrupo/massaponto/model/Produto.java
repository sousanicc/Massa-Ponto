package com.massapontogrupo.massaponto.model;

import java.util.Map;

public class Produto {

    private Long id;
    private Categoria categoria;
    private String nome;
    private String descricao;
    private String imagemUrl;
    private Integer precoCentavos;
    private Boolean ativo;

    public Produto() {
        this.ativo = true;
    }

    // Converte um registro do JdbcTemplate em um objeto Produto
    public static Produto converterRegistros(Map<String, Object> registro) {
        Produto produto = new Produto();
        produto.setId(((Number) registro.get("id")).longValue());
        produto.setCategoria(Categoria.valueOf((String) registro.get("categoria")));
        produto.setNome((String) registro.get("nome"));
        produto.setDescricao((String) registro.get("descricao"));
        produto.setImagemUrl((String) registro.get("imagem_url"));
        produto.setPrecoCentavos(((Number) registro.get("preco_centavos")).intValue());
        produto.setAtivo((Boolean) registro.get("ativo"));
        return produto;
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

    /**
     * Retorna o caminho correto para a imagem do produto.
     * - Se imagemUrl for nula ou vazia: retorna null (template mostra "Sem imagem")
     * - Se já for URL absoluta (http://, https://) ou caminho absoluto (/...): retorna como está
     * - Caso contrário: assume que é apenas o nome do arquivo e prefixa com "/uploads/"
     */
    public String getImagemSrc() {
        if (imagemUrl == null || imagemUrl.isBlank()) {
            return null;
        }
        String url = imagemUrl.trim();
        if (url.startsWith("http://") || url.startsWith("https://") || url.startsWith("/")) {
            return url;
        }
        return "/uploads/" + url;
    }
}