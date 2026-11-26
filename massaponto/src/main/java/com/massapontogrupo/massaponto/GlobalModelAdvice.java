package com.massapontogrupo.massaponto;

import org.springframework.ui.Model;
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