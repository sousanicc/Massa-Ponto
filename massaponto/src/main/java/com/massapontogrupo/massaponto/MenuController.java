package com.massapontogrupo.massaponto;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MenuController {

    private final ProdutoRepository produtoRepository;

    public MenuController(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("pizzasSalgadas", produtoRepository.findByCategoriaAndAtivoTrueOrderByNomeAsc(Categoria.PIZZA_SALGADA));
        model.addAttribute("pizzasDoces", produtoRepository.findByCategoriaAndAtivoTrueOrderByNomeAsc(Categoria.PIZZA_DOCE));
        model.addAttribute("bebidas", produtoRepository.findByCategoriaAndAtivoTrueOrderByNomeAsc(Categoria.BEBIDA));
        model.addAttribute("maisPedidas", produtoRepository.findByMaisPedidaTrueAndAtivoTrue());
        return "index";
    }

}