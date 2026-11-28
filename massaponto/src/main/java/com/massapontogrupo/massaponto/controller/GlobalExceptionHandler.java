package com.massapontogrupo.massaponto.controller;

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
        mav.addObject("categorias", com.massapontogrupo.massaponto.model.Categoria.values());
        mav.addObject("produto", new com.massapontogrupo.massaponto.model.Produto());
        return mav;
    }
}