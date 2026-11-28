package com.massapontogrupo.massaponto.controller;

import com.massapontogrupo.massaponto.model.Categoria;
import com.massapontogrupo.massaponto.model.Produto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(HttpStatus.PAYLOAD_TOO_LARGE)
    public ModelAndView handleMaxUploadSizeExceeded(MaxUploadSizeExceededException ex) {
        ModelAndView mav = new ModelAndView("admin-produto-form");
        mav.addObject("erro", "A imagem deve ter no máximo 2 MB.");
        mav.addObject("categorias", Categoria.values());
        mav.addObject("produto", new Produto());
        return mav;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ModelAndView handleGenericError(Exception ex) {
        ModelAndView mav = new ModelAndView("admin-produto-form");
        mav.addObject("erro", "Erro interno: " + ex.getMessage());
        mav.addObject("categorias", Categoria.values());
        mav.addObject("produto", new Produto());
        return mav;
    }
}