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

    // Lista produtos de uma categoria em ordem alfabética
    public List<Produto> listarPorCategoria(Categoria categoria) {
        String sql = "SELECT * FROM produto WHERE categoria = ? ORDER BY nome ASC";
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

    // Busca produtos por nome ou descrição (usado na busca da Home)
    public List<Produto> buscarPorTermo(String termo) {
        String sql = "SELECT * FROM produto WHERE (nome ILIKE '%' || ? || '%' OR descricao ILIKE '%' || ? || '%') ORDER BY nome ASC";
        List<Map<String, Object>> listaRegistros = jdbc.queryForList(sql, termo, termo);
        List<Produto> produtos = new ArrayList<>();
        for (Map<String, Object> registro : listaRegistros) {
            produtos.add(Produto.converterRegistros(registro));
        }
        return produtos;
    }

    // Lista todos os produtos em ordem alfabética
    public List<Produto> listarTodosAtivos() {
        String sql = "SELECT * FROM produto ORDER BY nome ASC";
        List<Map<String, Object>> listaRegistros = jdbc.queryForList(sql);
        List<Produto> produtos = new ArrayList<>();
        for (Map<String, Object> registro : listaRegistros) {
            produtos.add(Produto.converterRegistros(registro));
        }
        return produtos;
    }

    // INSERT de um produto
    public void inserirProduto(Produto produto) {
        String sql = "INSERT INTO produto(categoria, nome, descricao, imagem_url, preco_centavos) VALUES (?, ?, ?, ?, ?)";
        Object[] obj = new Object[5];
        obj[0] = produto.getCategoria().name();
        obj[1] = produto.getNome();
        obj[2] = produto.getDescricao();
        obj[3] = produto.getImagemUrl();
        obj[4] = produto.getPrecoCentavos();
        jdbc.update(sql, obj);
    }

    // UPDATE de um produto
    public void atualizarProduto(Produto produto) {
        String sql = "UPDATE produto SET categoria = ?, nome = ?, descricao = ?, imagem_url = ?, preco_centavos = ? WHERE id = ?";
        Object[] obj = new Object[6];
        obj[0] = produto.getCategoria().name();
        obj[1] = produto.getNome();
        obj[2] = produto.getDescricao();
        obj[3] = produto.getImagemUrl();
        obj[4] = produto.getPrecoCentavos();
        obj[5] = produto.getId();
        jdbc.update(sql, obj);
    }

    // Deleta apenas o produto (a cascade é feita no Service)
    public void deletarProduto(Long id) {
        String sql = "DELETE FROM produto WHERE id = ?";
        jdbc.update(sql, id);
    }

    // Expor JdbcTemplate para uso em transações no Service
    public JdbcTemplate getJdbcTemplate() {
        return jdbc;
    }
}