package com.massapontogrupo.massaponto.service;

import com.massapontogrupo.massaponto.model.FormaPagamento;
import com.massapontogrupo.massaponto.model.ItemPedido;
import com.massapontogrupo.massaponto.model.Pedido;
import com.massapontogrupo.massaponto.model.Sacola;
import com.massapontogrupo.massaponto.repository.ItemPedidoRepository;
import com.massapontogrupo.massaponto.repository.PedidoRepository;
import com.massapontogrupo.massaponto.repository.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ItemPedidoRepository itemPedidoRepository;
    private final ProdutoRepository produtoRepository;

    public PedidoService(PedidoRepository pedidoRepository, ItemPedidoRepository itemPedidoRepository, ProdutoRepository produtoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.itemPedidoRepository = itemPedidoRepository;
        this.produtoRepository = produtoRepository;
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
        pedido = pedidoRepository.save(pedido);

        // Salva cada item como novo objeto (desacoplado da sacola)
        for (ItemPedido itemSacola : sacola.getItens()) {
            ItemPedido item = new ItemPedido();
            item.setPedidoId(pedido.getId());
            item.setProdutoId(itemSacola.getProdutoId());
            item.setQuantidade(itemSacola.getQuantidade());
            item.setObservacao(itemSacola.getObservacao());
            item.setPrecoUnitarioCentavos(itemSacola.getPrecoUnitarioCentavos());
            itemPedidoRepository.save(item);
        }

        return pedido;
    }

    // Busca pedido por ID e preenche o produto de cada item (inclui inativos para histórico)
    public Optional<Pedido> buscarComItens(Long id) {
        Optional<Pedido> pedidoOpt = pedidoRepository.findById(id);
        pedidoOpt.ifPresent(this::carregarItens);
        return pedidoOpt;
    }

    // Lista histórico de pedidos com itens e produtos preenchidos
    public List<Pedido> listarHistorico() {
        List<Pedido> pedidos = pedidoRepository.findAllByOrderByCriadoEmDesc();
        for (Pedido pedido : pedidos) {
            carregarItens(pedido);
        }
        return pedidos;
    }

    private void carregarItens(Pedido pedido) {
        List<ItemPedido> itens = itemPedidoRepository.findByPedidoId(pedido.getId());
        for (ItemPedido item : itens) {
            // Busca o produto mesmo se inativo (para histórico)
            produtoRepository.findById(item.getProdutoId()).ifPresent(item::setProduto);
        }
        pedido.setItens(itens);
    }
}