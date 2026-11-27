package com.massapontogrupo.massaponto;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoRepository extends CrudRepository<Pedido, Long> {

    // Lista todos os pedidos do mais recente para o mais antigo
    List<Pedido> findAllByOrderByCriadoEmDesc();
}