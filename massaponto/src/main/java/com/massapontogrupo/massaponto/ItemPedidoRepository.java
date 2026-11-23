package com.massapontogrupo.massaponto;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemPedidoRepository extends CrudRepository<ItemPedido, Long> {

    List<ItemPedido> findByPedidoId(Long pedidoId);

    @Query("DELETE FROM item_pedido WHERE pedido_id = :pedidoId")
    void deleteByPedidoId(@Param("pedidoId") Long pedidoId);
}