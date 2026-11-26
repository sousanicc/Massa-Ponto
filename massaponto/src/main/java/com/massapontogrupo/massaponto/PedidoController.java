package com.massapontogrupo.massaponto;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    // Confirmação do pedido
    @GetMapping("/pedido/{id}")
    public String confirmacao(@PathVariable Long id, Model model) {
        return pedidoService.buscarComItens(id)
                .map(pedido -> {
                    model.addAttribute("pedido", pedido);
                    return "pedido";
                })
                .orElse("redirect:/");
    }

    // Histórico de pedidos
    @GetMapping("/pedidos")
    public String historico(Model model) {
        model.addAttribute("pedidos", pedidoService.listarHistorico());
        return "pedidos";
    }
}