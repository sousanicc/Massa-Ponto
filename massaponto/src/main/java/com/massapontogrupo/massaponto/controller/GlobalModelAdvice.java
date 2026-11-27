package com.massapontogrupo.massaponto.controller;

import com.massapontogrupo.massaponto.model.Sacola;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAdvice {

    private final Sacola sacola;

    public GlobalModelAdvice(Sacola sacola) {
        this.sacola = sacola;
    }

    @ModelAttribute("sacola")
    public Sacola sacola() {
        return sacola;
    }
}