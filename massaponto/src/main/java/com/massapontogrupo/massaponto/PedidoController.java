package com.massapontogrupo.massaponto;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class PedidoController {

    private final PedidoRepository pedidoRepository;
    private final ItemPedidoRepository itemPedidoRepository;

    public PedidoController(PedidoRepository pedidoRepository, ItemPedidoRepository itemPedidoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.itemPedidoRepository = itemPedidoRepository;
    }

    // Confirmação do pedido
    @GetMapping("/pedido/{id}")
    public String confirmacao(@PathVariable Long id, Model model) {
        var pedidoOpt = pedidoRepository.findById(id);
        if (pedidoOpt.isEmpty()) {
            return "redirect:/";
        }
        Pedido pedido = pedidoOpt.get();

        // Busca itens e preenche o produto para exibir nome
        List<ItemPedido> itens = itemPedidoRepository.findByPedidoId(id);
        pedido.setItens(itens);

        model.addAttribute("pedido", pedido);
        return "pedido";
    }

    // Histórico de pedidos
    @GetMapping("/pedidos")
    public String historico(Model model) {
        List<Pedido> pedidos = pedidoRepository.findAllByOrderByCriadoEmDesc();
        // Para cada pedido, busca os itens
        for (Pedido pedido : pedidos) {
            pedido.setItens(itemPedidoRepository.findByPedidoId(pedido.getId()));
        }
        model.addAttribute("pedidos", pedidos);
        return "pedidos";
    }
}