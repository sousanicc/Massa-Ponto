-- Schema do banco Massa & Ponto
-- Executa a cada restart (spring.sql.init.mode=always)
-- DROPA tabelas na ordem inversa das FKs

DROP TABLE IF EXISTS item_pedido CASCADE;
DROP TABLE IF EXISTS pedido CASCADE;
DROP TABLE IF EXISTS produto CASCADE;

-- Tabela de produtos
CREATE TABLE produto (
    id SERIAL PRIMARY KEY,
    categoria VARCHAR(20) NOT NULL
        CHECK (categoria IN ('PIZZA_SALGADA', 'PIZZA_DOCE', 'BEBIDA')),
    nome VARCHAR(100) NOT NULL UNIQUE,
    descricao TEXT NOT NULL,
    imagem_url TEXT,
    preco_centavos INTEGER NOT NULL
);

-- Tabela de pedidos
CREATE TABLE pedido (
    id SERIAL PRIMARY KEY,
    nome_cliente VARCHAR(100) NOT NULL,
    telefone VARCHAR(20) NOT NULL,
    endereco TEXT NOT NULL,
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    total_centavos INTEGER NOT NULL,
    forma_pagamento VARCHAR(20) NOT NULL CHECK (forma_pagamento IN ('PIX', 'CARTAO', 'DINHEIRO'))
);

-- Tabela de itens do pedido
CREATE TABLE item_pedido (
    id SERIAL PRIMARY KEY,
    pedido_id INTEGER NOT NULL,
    produto_id INTEGER NOT NULL,
    quantidade INTEGER NOT NULL,
    observacao TEXT,
    preco_unitario_centavos INTEGER NOT NULL,

    CONSTRAINT fk_item_pedido_pedido
        FOREIGN KEY (pedido_id) REFERENCES pedido(id),

    CONSTRAINT fk_item_pedido_produto
        FOREIGN KEY (produto_id) REFERENCES produto(id)
);