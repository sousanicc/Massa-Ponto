package com.massapontogrupo.massaponto;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class CheckoutController {

    private final PedidoService pedidoService;
    private final Sacola sacola;

    public CheckoutController(PedidoService pedidoService, Sacola sacola) {
        this.pedidoService = pedidoService;
        this.sacola = sacola;
    }

    @GetMapping("/checkout")
    public String checkoutForm(Model model) {
        if (sacola.isVazia()) {
            return "redirect:/sacola";
        }
        model.addAttribute("sacola", sacola);
        return "checkout";
    }

    @PostMapping("/checkout")
    public String checkoutSubmit(@RequestParam String nomeCliente,
                                 @RequestParam String telefone,
                                 @RequestParam String endereco,
                                 @RequestParam FormaPagamento formaPagamento,
                                 RedirectAttributes redirectAttributes) {

        // Validação simples sem DTO
        String erro = null;
        if (nomeCliente == null || nomeCliente.isBlank()) erro = "Nome é obrigatório";
        else if (telefone == null || telefone.isBlank()) erro = "Telefone é obrigatório";
        else if (endereco == null || endereco.isBlank()) erro = "Endereço é obrigatório";

        if (erro != null) {
            return "redirect:/checkout?erro=" + erro;
        }

        try {
            Pedido pedido = pedidoService.finalizar(
                    nomeCliente.trim(), telefone.trim(), endereco.trim(),
                    formaPagamento, sacola
            );

            // POLIMORFISMO: controller usa Pagamento sem saber subclasse
            Pagamento pagamento = Pagamento.de(pedido.getFormaPagamento());
            String comprovante = pagamento.processar(pedido.getTotalCentavos());

            sacola.limpar();

            redirectAttributes.addFlashAttribute("comprovante", comprovante);
            redirectAttributes.addFlashAttribute("pedido", pedido);

            return "redirect:/pedido/" + pedido.getId();

        } catch (IllegalStateException | IllegalArgumentException e) {
            return "redirect:/checkout?erro=" + e.getMessage();
        }
    }
}