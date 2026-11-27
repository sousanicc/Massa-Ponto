package com.massapontogrupo.massaponto;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProdutoRepository extends CrudRepository<Produto, Long> {

    // Lista produtos ativos de uma categoria em ordem alfabética
    List<Produto> findByCategoriaAndAtivoTrueOrderByNomeAsc(Categoria categoria);

    // Busca produtos ativos por nome ou descrição (usado na busca da Home)
    @Query("SELECT * FROM produto WHERE ativo = true AND (nome ILIKE '%' || :termo || '%' OR descricao ILIKE '%' || :termo || '%') ORDER BY nome ASC")
    List<Produto> buscarPorTermo(@Param("termo") String termo);
}