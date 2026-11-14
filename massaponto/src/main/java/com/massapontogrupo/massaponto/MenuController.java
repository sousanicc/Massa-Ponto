package com.massapontogrupo.massaponto;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MenuController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/p1")
    public String pagina1() {
        return "p1";
    }

}