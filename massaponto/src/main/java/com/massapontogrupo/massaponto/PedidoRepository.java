package com.massapontogrupo.massaponto;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PedidoRepository extends CrudRepository<Pedido, Long> {

    List<Pedido> findAllByOrderByCriadoEmDesc();

    @Query("SELECT * FROM pedido ORDER BY criado_em DESC LIMIT :limite")
    List<Pedido> findRecentOrders(@Param("limite") int limite);
}