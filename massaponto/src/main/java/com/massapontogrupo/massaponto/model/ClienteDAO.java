package com.massapontogrupo.massaponto.model;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import jakarta.annotation.PostConstruct;

public class ClienteDAO {

    @Repository
    public class clienteDAO {
        @Autowired
        DataSource dataSource;

        JdbcTemplate jdbc;

        @PostConstruct
        private void initialize() {
            jdbc = new JdbcTemplate(dataSource);
        }

        public void inserirCliente(Cliente c) {

            String sql = "INSERT INTO Cliente(nome,cpf)" + "VALUES(?,?);";

            Object[] campos = new Object[2];
            campos[0] = (String) c.getNome();
            campos[0] = (String) c.getCPF();

            jdbc.update(sql, campos);

        }

    }

}
