package com.massapontogrupo.massaponto.controller;

import com.massapontogrupo.massaponto.model.Produto;
import com.massapontogrupo.massaponto.model.Sacola;
import com.massapontogrupo.massaponto.repository.ProdutoDAO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SacolaController {

    private final ProdutoDAO produtoDAO;
    private final Sacola sacola;

    public SacolaController(ProdutoDAO produtoDAO, Sacola sacola) {
        this.produtoDAO = produtoDAO;
        this.sacola = sacola;
    }

    // POST /sacola/adicionar - adiciona item vindo da página do produto
    @PostMapping("/sacola/adicionar")
    public String adicionar(@RequestParam Long produtoId,
                            @RequestParam(required = false) String observacao,
                            @RequestParam(defaultValue = "1") int quantidade) {
        Produto produto = produtoDAO.buscarPorId(produtoId);
        if (produto == null || !produto.getAtivo()) {
            return "redirect:/";
        }
        if (quantidade < 1) {
            quantidade = 1;
        }
        sacola.adicionar(produto, observacao, quantidade);
        return "redirect:/sacola";
    }

    // GET /sacola - mostra a página da sacola
    @GetMapping("/sacola")
    public String verSacola(Model model) {
        model.addAttribute("sacola", sacola);
        return "sacola";
    }

    // GET /sacola/editar/{indice} - abre formulário para editar observação e quantidade
    @GetMapping("/sacola/editar/{indice}")
    public String editarForm(@PathVariable int indice, Model model) {
        if (indice < 0 || indice >= sacola.getItens().size()) {
            return "redirect:/sacola";
        }
        model.addAttribute("indice", indice);
        model.addAttribute("item", sacola.getItens().get(indice));
        return "sacola-editar";
    }

    // POST /sacola/atualizar - salva alterações de observação e quantidade
    @PostMapping("/sacola/atualizar")
    public String atualizar(@RequestParam int indice,
                            @RequestParam(required = false) String observacao,
                            @RequestParam(defaultValue = "1") int quantidade) {
        sacola.atualizar(indice, observacao, quantidade);
        return "redirect:/sacola";
    }

    // POST /sacola/remover - remove item por índice
    @PostMapping("/sacola/remover")
    public String remover(@RequestParam int indice) {
        sacola.remover(indice);
        return "redirect:/sacola";
    }
}