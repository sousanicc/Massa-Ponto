package com.massapontogrupo.massaponto.controller;

import com.massapontogrupo.massaponto.model.FormaPagamento;
import com.massapontogrupo.massaponto.model.Pedido;
import com.massapontogrupo.massaponto.model.Sacola;
import com.massapontogrupo.massaponto.model.pagamento.Pagamento;
import com.massapontogrupo.massaponto.service.PedidoService;
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
        model.addAttribute("formaPagamento", FormaPagamento.PIX);
        model.addAttribute("sacola", sacola);
        return "checkout";
    }

    @PostMapping("/checkout")
    public String checkoutSubmit(@RequestParam String nomeCliente,
                                 @RequestParam String telefone,
                                 @RequestParam String endereco,
                                 @RequestParam FormaPagamento formaPagamento,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {

        // Validação simples sem DTO
        String erro = null;
        if (nomeCliente == null || nomeCliente.isBlank()) erro = "Nome é obrigatório";
        else if (telefone == null || telefone.isBlank()) erro = "Telefone é obrigatório";
        else if (endereco == null || endereco.isBlank()) erro = "Endereço é obrigatório";

        if (erro != null) {
            // Retorna a view diretamente com os dados preenchidos
            model.addAttribute("erro", erro);
            model.addAttribute("nomeCliente", nomeCliente);
            model.addAttribute("telefone", telefone);
            model.addAttribute("endereco", endereco);
            model.addAttribute("formaPagamento", formaPagamento);
            model.addAttribute("sacola", sacola);
            return "checkout";
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

            return "redirect:/pedido/" + pedido.getId();

        } catch (IllegalStateException | IllegalArgumentException e) {
            // Em caso de erro de negócio, retorna view com erro e dados
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("nomeCliente", nomeCliente);
            model.addAttribute("telefone", telefone);
            model.addAttribute("endereco", endereco);
            model.addAttribute("formaPagamento", formaPagamento);
            model.addAttribute("sacola", sacola);
            return "checkout";
        }
    }
}