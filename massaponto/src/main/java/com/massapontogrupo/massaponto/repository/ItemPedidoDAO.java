package com.massapontogrupo.massaponto.repository;

import com.massapontogrupo.massaponto.model.ItemPedido;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class ItemPedidoDAO {

    @Autowired
    DataSource dataSource;

    JdbcTemplate jdbc;

    @PostConstruct
    private void initialize() {
        jdbc = new JdbcTemplate(dataSource);
    }

    // Salva um item vinculado a um pedido
    public void salvar(ItemPedido item) {
        String sql = "INSERT INTO item_pedido (pedido_id, produto_id, quantidade, observacao, preco_unitario_centavos) " +
                     "VALUES (?, ?, ?, ?, ?)";
        jdbc.update(
                sql,
                item.getPedidoId(),
                item.getProdutoId(),
                item.getQuantidade(),
                item.getObservacao(),
                item.getPrecoUnitarioCentavos()
        );
    }

    // Lista os itens pertencentes a um pedido
    public List<ItemPedido> listarPorPedidoId(Long pedidoId) {
        String sql = "SELECT * FROM item_pedido WHERE pedido_id = ?";
        List<Map<String, Object>> listaRegistros = jdbc.queryForList(sql, pedidoId);
        List<ItemPedido> itens = new ArrayList<>();
        for (Map<String, Object> registro : listaRegistros) {
            itens.add(ItemPedido.converterRegistros(registro));
        }
        return itens;
    }

    // Busca IDs dos pedidos que contêm um determinado produto
    public List<Long> buscarPedidoIdsPorProdutoId(Long produtoId) {
        String sql = "SELECT DISTINCT pedido_id FROM item_pedido WHERE produto_id = ?";
        List<Map<String, Object>> listaRegistros = jdbc.queryForList(sql, produtoId);
        List<Long> ids = new ArrayList<>();
        for (Map<String, Object> registro : listaRegistros) {
            ids.add(((Number) registro.get("pedido_id")).longValue());
        }
        return ids;
    }

    // Deleta TODOS os itens de uma lista de pedidos (usado na cascade delete)
    public void deletarItensPorPedidoIds(List<Long> pedidoIds) {
        if (pedidoIds == null || pedidoIds.isEmpty()) {
            return;
        }
        // Monta placeholders (?, ?, ...) para o IN
        String placeholders = String.join(",", pedidoIds.stream().map(i -> "?").toList());
        String sql = "DELETE FROM item_pedido WHERE pedido_id IN (" + placeholders + ")";
        jdbc.update(sql, pedidoIds.toArray());
    }

    // Deleta itens de um determinado produto
    public void deletarItensPorProdutoId(Long produtoId) {
        String sql = "DELETE FROM item_pedido WHERE produto_id = ?";
        jdbc.update(sql, produtoId);
    }

    // Expor JdbcTemplate para uso em transações no Service
    public JdbcTemplate getJdbcTemplate() {
        return jdbc;
    }
}
