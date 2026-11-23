package com.massapontogrupo.massaponto;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProdutoRepository extends CrudRepository<Produto, Long> {

    List<Produto> findByAtivoTrueOrderByCategoriaAscNomeAsc();

    List<Produto> findByCategoriaAndAtivoTrueOrderByNomeAsc(Categoria categoria);

    Optional<Produto> findByNomeAndAtivoTrue(String nome);

    @Query("SELECT * FROM produto WHERE ativo = true AND (nome ILIKE %:termo% OR descricao ILIKE %:termo%) ORDER BY nome ASC")
    List<Produto> buscarPorTermo(@Param("termo") String termo);

    List<Produto> findByMaisPedidaTrueAndAtivoTrue();
}