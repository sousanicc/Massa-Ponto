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
}