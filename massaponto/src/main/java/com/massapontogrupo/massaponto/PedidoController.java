package com.massapontogrupo.massaponto;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@Controller
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    // Confirmação do pedido
    @GetMapping("/pedido/{id}")
    public String confirmacao(@PathVariable Long id, Model model) {
        Optional<Pedido> pedido = pedidoService.buscarComItens(id);
        if (pedido.isEmpty()) {
            return "redirect:/";
        }
        model.addAttribute("pedido", pedido.get());
        return "pedido";
    }

    // Histórico de pedidos
    @GetMapping("/pedidos")
    public String historico(Model model) {
        model.addAttribute("pedidos", pedidoService.listarHistorico());
        return "pedidos";
    }
}