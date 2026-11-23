CREATE TABLE IF NOT EXISTS produto (
    id SERIAL PRIMARY KEY,
    categoria VARCHAR(20) NOT NULL
        CHECK (categoria IN ('PIZZA_SALGADA', 'PIZZA_DOCE', 'BEBIDA')),
    nome VARCHAR(100) NOT NULL UNIQUE,
    descricao TEXT NOT NULL,
    imagem_url TEXT,
    preco_centavos INTEGER NOT NULL,
    vegetariana BOOLEAN NOT NULL DEFAULT FALSE,
    picante BOOLEAN NOT NULL DEFAULT FALSE,
    mais_pedida BOOLEAN NOT NULL DEFAULT FALSE,
    ativo BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS pedido (
    id SERIAL PRIMARY KEY,
    nome_cliente VARCHAR(100) NOT NULL,
    telefone VARCHAR(20) NOT NULL,
    endereco TEXT NOT NULL,
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    total_centavos INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS item_pedido (
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