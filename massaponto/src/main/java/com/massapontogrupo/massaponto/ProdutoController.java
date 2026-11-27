package com.massapontogrupo.massaponto;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@Controller
public class ProdutoController {

    private final ProdutoRepository produtoRepository;

    public ProdutoController(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    @GetMapping("/produto/{id}")
    public String detalhe(@PathVariable Long id, Model model) {
        Optional<Produto> produto = produtoRepository.findById(id);
        if (produto.isEmpty() || !produto.get().getAtivo()) {
            return "redirect:/";
        }
        model.addAttribute("produto", produto.get());
        return "produto";
    }

}