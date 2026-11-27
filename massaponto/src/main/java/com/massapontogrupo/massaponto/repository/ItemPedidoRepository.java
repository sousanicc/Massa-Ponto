package com.massapontogrupo.massaponto.repository;

import com.massapontogrupo.massaponto.model.ItemPedido;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemPedidoRepository extends CrudRepository<ItemPedido, Long> {

    // Lista os itens pertencentes a um pedido
    List<ItemPedido> findByPedidoId(Long pedidoId);
}