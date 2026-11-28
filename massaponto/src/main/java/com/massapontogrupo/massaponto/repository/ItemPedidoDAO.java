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
}
