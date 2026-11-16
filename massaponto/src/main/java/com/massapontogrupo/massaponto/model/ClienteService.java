package com.massapontogrupo.massaponto.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.massapontogrupo.massaponto.model.ClienteDAO.clienteDAO;

@Service
public class ClienteService {

   @Autowired
   clienteDAO cdao;

   public void inserirCliente(Cliente c) {
      cdao.inserirCliente(c);
   }

}