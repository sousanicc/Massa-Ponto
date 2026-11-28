package com.massapontogrupo.massaponto.repository;

import com.massapontogrupo.massaponto.model.Pedido;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class PedidoDAO {

    @Autowired
    DataSource dataSource;

    JdbcTemplate jdbc;

    @PostConstruct
    private void initialize() {
        jdbc = new JdbcTemplate(dataSource);
    }

    // Salva um novo pedido no banco e preenche o ID gerado
    public Pedido salvar(Pedido pedido) {
        String sql = "INSERT INTO pedido (nome_cliente, telefone, endereco, criado_em, total_centavos, forma_pagamento) " +
                     "VALUES (?, ?, ?, ?, ?, ?) RETURNING id";
        Long idGerado = jdbc.queryForObject(
                sql,
                Long.class,
                pedido.getNomeCliente(),
                pedido.getTelefone(),
                pedido.getEndereco(),
                Timestamp.valueOf(pedido.getCriadoEm()),
                pedido.getTotalCentavos(),
                pedido.getFormaPagamento().name()
        );
        pedido.setId(idGerado);
        return pedido;
    }

    // Busca um pedido pelo ID (retorna null se não existir)
    public Pedido buscarPorId(Long id) {
        String sql = "SELECT * FROM pedido WHERE id = ?";
        List<Map<String, Object>> listaRegistros = jdbc.queryForList(sql, id);
        if (listaRegistros.isEmpty()) {
            return null;
        }
        return Pedido.converterRegistros(listaRegistros.get(0));
    }

    // Lista todos os pedidos do mais recente para o mais antigo
    public List<Pedido> listarTodosOrdenadoPorData() {
        String sql = "SELECT * FROM pedido ORDER BY criado_em DESC";
        List<Map<String, Object>> listaRegistros = jdbc.queryForList(sql);
        List<Pedido> pedidos = new ArrayList<>();
        for (Map<String, Object> registro : listaRegistros) {
            pedidos.add(Pedido.converterRegistros(registro));
        }
        return pedidos;
    }

    // Deleta pedidos por uma lista de IDs (usado na cascade delete)
    public void deletarPedidosPorIds(List<Long> pedidoIds) {
        if (pedidoIds == null || pedidoIds.isEmpty()) {
            return;
        }
        // Monta placeholders (?, ?, ...) para o IN
        String placeholders = String.join(",", pedidoIds.stream().map(i -> "?").toList());
        String sql = "DELETE FROM pedido WHERE id IN (" + placeholders + ")";
        jdbc.update(sql, pedidoIds.toArray());
    }
}
