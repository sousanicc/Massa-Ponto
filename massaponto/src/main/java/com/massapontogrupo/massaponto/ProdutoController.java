package com.massapontogrupo.massaponto;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ProdutoController {

    private final ProdutoRepository produtoRepository;

    public ProdutoController(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    @GetMapping("/produto/{id}")
    public String detalhe(@PathVariable Long id, Model model) {
        return produtoRepository.findById(id)
                .filter(Produto::getAtivo)
                .map(produto -> {
                    model.addAttribute("produto", produto);
                    return "produto";
                })
                .orElse("redirect:/");
    }

}