package com.massapontogrupo.massaponto;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ItemPedidoRepository itemPedidoRepository;

    public PedidoService(PedidoRepository pedidoRepository, ItemPedidoRepository itemPedidoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.itemPedidoRepository = itemPedidoRepository;
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
}