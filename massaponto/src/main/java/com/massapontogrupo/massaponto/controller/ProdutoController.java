package com.massapontogrupo.massaponto.controller;

import com.massapontogrupo.massaponto.model.Produto;
import com.massapontogrupo.massaponto.repository.ProdutoDAO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ProdutoController {

    private final ProdutoDAO produtoDAO;

    public ProdutoController(ProdutoDAO produtoDAO) {
        this.produtoDAO = produtoDAO;
    }

    @GetMapping("/produto/{id}")
    public String detalhe(@PathVariable Long id, Model model) {
        Produto produto = produtoDAO.buscarPorId(id);
        if (produto == null || !produto.getAtivo()) {
            return "redirect:/";
        }
        model.addAttribute("produto", produto);
        return "produto";
    }

}