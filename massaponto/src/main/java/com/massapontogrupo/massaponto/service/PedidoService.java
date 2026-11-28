package com.massapontogrupo.massaponto.service;

import com.massapontogrupo.massaponto.model.FormaPagamento;
import com.massapontogrupo.massaponto.model.ItemPedido;
import com.massapontogrupo.massaponto.model.Pedido;
import com.massapontogrupo.massaponto.model.Produto;
import com.massapontogrupo.massaponto.model.Sacola;
import com.massapontogrupo.massaponto.repository.ItemPedidoDAO;
import com.massapontogrupo.massaponto.repository.PedidoDAO;
import com.massapontogrupo.massaponto.repository.ProdutoDAO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PedidoService {

    private final PedidoDAO pedidoDAO;
    private final ItemPedidoDAO itemPedidoDAO;
    private final ProdutoDAO produtoDAO;

    public PedidoService(PedidoDAO pedidoDAO, ItemPedidoDAO itemPedidoDAO, ProdutoDAO produtoDAO) {
        this.pedidoDAO = pedidoDAO;
        this.itemPedidoDAO = itemPedidoDAO;
        this.produtoDAO = produtoDAO;
    }

    // POLIMORFISMO: service não conhece subclasse de pagamento
    @Transactional
    public Pedido finalizar(String nomeCliente, String telefone, String endereco,
                            FormaPagamento formaPagamento, Sacola sacola) {

        // Validações básicas
        if (nomeCliente == null || nomeCliente.isBlank()
                || telefone == null || telefone.isBlank()
                || endereco == null || endereco.isBlank()
                || formaPagamento == null) {
            throw new IllegalArgumentException("Dados incompletos");
        }
        if (sacola.isVazia()) {
            throw new IllegalStateException("Sacola vazia");
        }

        // Cria e salva o pedido
        Pedido pedido = new Pedido();
        pedido.setNomeCliente(nomeCliente.trim());
        pedido.setTelefone(telefone.trim());
        pedido.setEndereco(endereco.trim());
        pedido.setFormaPagamento(formaPagamento);
        pedido.setTotalCentavos(sacola.getTotalCentavos());
        pedido.setCriadoEm(LocalDateTime.now());
        pedido = pedidoDAO.salvar(pedido);

        // Salva cada item como novo objeto (desacoplado da sacola)
        for (ItemPedido itemSacola : sacola.getItens()) {
            ItemPedido item = new ItemPedido();
            item.setPedidoId(pedido.getId());
            item.setProdutoId(itemSacola.getProdutoId());
            item.setQuantidade(itemSacola.getQuantidade());
            item.setObservacao(itemSacola.getObservacao());
            item.setPrecoUnitarioCentavos(itemSacola.getPrecoUnitarioCentavos());
            itemPedidoDAO.salvar(item);
        }

        return pedido;
    }

    // Busca pedido por ID e preenche o produto de cada item (inclui inativos para histórico)
    public Pedido buscarComItens(Long id) {
        Pedido pedido = pedidoDAO.buscarPorId(id);
        if (pedido != null) {
            carregarItens(pedido);
        }
        return pedido;
    }

    // Lista histórico de pedidos com itens e produtos preenchidos
    public List<Pedido> listarHistorico() {
        List<Pedido> pedidos = pedidoDAO.listarTodosOrdenadoPorData();
        for (Pedido pedido : pedidos) {
            carregarItens(pedido);
        }
        return pedidos;
    }

    private void carregarItens(Pedido pedido) {
        List<ItemPedido> itens = itemPedidoDAO.listarPorPedidoId(pedido.getId());
        for (ItemPedido item : itens) {
            // Busca o produto mesmo se inativo (para histórico)
            Produto produto = produtoDAO.buscarPorId(item.getProdutoId());
            if (produto != null) {
                item.setProduto(produto);
            }
        }
        pedido.setItens(itens);
    }

    // Exclusão física em cascata de produto (segue modelo da monitora):
    // Ordem correta (FKs): primeiro apaga filhos (item_pedido), depois pais (pedido), por último produto
    // 1. Deleta TODOS os itens dos pedidos que contenham este produto (para liberar FK item_pedido -> pedido)
    // 2. Deleta os pedidos que continham este produto
    // 3. Deleta o produto
    // Tudo em uma transação: se qualquer etapa falhar, tudo é desfeito
    @Transactional
    public void excluirProdutoEmCascata(Long produtoId) {
        // 1. Primeiro: pega os IDs dos pedidos que têm este produto
        List<Long> pedidoIds = itemPedidoDAO.buscarPedidoIdsPorProdutoId(produtoId);
        
        if (!pedidoIds.isEmpty()) {
            // 2. Deleta TODOS os itens desses pedidos (filhos de pedido)
            itemPedidoDAO.deletarItensPorPedidoIds(pedidoIds);
            
            // 3. Deleta os pedidos (pais dos itens)
            pedidoDAO.deletarPedidosPorIds(pedidoIds);
        } else {
            // Se nenhum pedido tem este produto, só deleta os itens órfãos do produto
            itemPedidoDAO.deletarItensPorProdutoId(produtoId);
        }
        
        // 4. Por último: deleta o produto
        produtoDAO.deletarProduto(produtoId);
    }
}