package com.massapontogrupo.massaponto.controller;

import com.massapontogrupo.massaponto.model.Categoria;
import com.massapontogrupo.massaponto.repository.ProdutoDAO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MenuController {

    private final ProdutoDAO produtoDAO;

    public MenuController(ProdutoDAO produtoDAO) {
        this.produtoDAO = produtoDAO;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("pizzasSalgadas", produtoDAO.listarPorCategoria(Categoria.PIZZA_SALGADA));
        model.addAttribute("pizzasDoces", produtoDAO.listarPorCategoria(Categoria.PIZZA_DOCE));
        model.addAttribute("bebidas", produtoDAO.listarPorCategoria(Categoria.BEBIDA));
        return "index";
    }

}