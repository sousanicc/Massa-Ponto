package com.massapontogrupo.massaponto.repository;

import com.massapontogrupo.massaponto.model.Categoria;
import com.massapontogrupo.massaponto.model.Produto;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class ProdutoDAO {

    @Autowired
    DataSource dataSource;

    JdbcTemplate jdbc;

    @PostConstruct
    private void initialize() {
        jdbc = new JdbcTemplate(dataSource);
    }

    // Lista produtos ativos de uma categoria em ordem alfabética
    public List<Produto> listarPorCategoria(Categoria categoria) {
        String sql = "SELECT * FROM produto WHERE ativo = true AND categoria = ? ORDER BY nome ASC";
        List<Map<String, Object>> listaRegistros = jdbc.queryForList(sql, categoria.name());
        List<Produto> produtos = new ArrayList<>();
        for (Map<String, Object> registro : listaRegistros) {
            produtos.add(Produto.converterRegistros(registro));
        }
        return produtos;
    }

    // Busca um produto pelo ID (retorna null se não existir)
    public Produto buscarPorId(Long id) {
        String sql = "SELECT * FROM produto WHERE id = ?";
        List<Map<String, Object>> listaRegistros = jdbc.queryForList(sql, id);
        if (listaRegistros.isEmpty()) {
            return null;
        }
        return Produto.converterRegistros(listaRegistros.get(0));
    }

    // Busca produtos ativos por nome ou descrição (usado na busca da Home)
    public List<Produto> buscarPorTermo(String termo) {
        String sql = "SELECT * FROM produto WHERE ativo = true AND (nome ILIKE '%' || ? || '%' OR descricao ILIKE '%' || ? || '%') ORDER BY nome ASC";
        List<Map<String, Object>> listaRegistros = jdbc.queryForList(sql, termo, termo);
        List<Produto> produtos = new ArrayList<>();
        for (Map<String, Object> registro : listaRegistros) {
            produtos.add(Produto.converterRegistros(registro));
        }
        return produtos;
    }
}
