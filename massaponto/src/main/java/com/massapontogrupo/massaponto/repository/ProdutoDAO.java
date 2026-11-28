package com.massapontogrupo.massaponto.repository;

import com.massapontogrupo.massaponto.model.Categoria;
import com.massapontogrupo.massaponto.model.Produto;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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

    // Exclusão física em cascata (segue modelo da monitora):
    // 1. Deleta itens de pedido do produto
    // 2. Deleta pedidos que contenham itens desse produto
    // 3. Deleta o produto
    // Tudo em uma transação: se qualquer etapa falhar, tudo é desfeito
    @Transactional
    public void excluir(Long id) {
        // 1. Deleta itens do produto (item_pedido)
        jdbc.update("DELETE FROM item_pedido WHERE produto_id = ?", id);

        // 2. Deleta pedidos que têm itens desse produto
        // (subquery: seleciona IDs de pedidos que contêm o produto)
        jdbc.update("""
            DELETE FROM pedido
            WHERE id IN (
                SELECT DISTINCT pedido_id FROM item_pedido WHERE produto_id = ?
            )
            """, id);

        // 3. Deleta o produto
        jdbc.update("DELETE FROM produto WHERE id = ?", id);
    }
}