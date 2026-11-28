-- Dados iniciais do cardápio (protegidos contra duplicação com ON CONFLICT DO NOTHING)
-- 8 pizzas salgadas, 2 pizzas doces, 2 refrigerantes (350 ml)
-- Preços em centavos

INSERT INTO produto (categoria, nome, descricao, imagem_url, preco_centavos) VALUES
('PIZZA_SALGADA', 'Margherita', 'Molho de tomate, mussarela de búfala, manjericão fresco e azeite', 'https://via.placeholder.com/400x300?text=Margherita', 4500),
('PIZZA_SALGADA', 'Calabresa', 'Molho de tomate, mussarela, calabresa fatiada, cebola roxa e orégano', 'https://via.placeholder.com/400x300?text=Calabresa', 4800),
('PIZZA_SALGADA', 'Pepperoni', 'Molho de tomate, mussarela, pepperoni artesanal e orégano', 'https://via.placeholder.com/400x300?text=Pepperoni', 5200),
('PIZZA_SALGADA', 'Quatro Queijos', 'Molho de tomate, mussarela, provolone, gorgonzola e parmesão', 'https://via.placeholder.com/400x300?text=Quatro+Queijos', 5500),
('PIZZA_SALGADA', 'Frango com Catupiry', 'Molho de tomate, mussarela, frango desfiado, catupiry, milho e azeitona', 'https://via.placeholder.com/400x300?text=Frango+Catupiry', 5000),
('PIZZA_SALGADA', 'Portuguesa', 'Molho de tomate, mussarela, presunto, ovos, cebola, azeitona, ervilha e tomate', 'https://via.placeholder.com/400x300?text=Portuguesa', 5300),
('PIZZA_SALGADA', 'Vegetariana', 'Molho de tomate, mussarela, abobrinha, berinjela, pimentão, tomate seco e rúcula', 'https://via.placeholder.com/400x300?text=Vegetariana', 4900),
('PIZZA_SALGADA', 'Bacon com Cheddar', 'Molho de tomate, mussarela, bacon crocante, cheddar cremoso e cebola caramelizada', 'https://via.placeholder.com/400x300?text=Bacon+Cheddar', 5400),
('PIZZA_DOCE', 'Romeu e Julieta', 'Mussarela, goiabada cascão derretida e canela', 'https://via.placeholder.com/400x300?text=Romeu+e+Julieta', 4200),
('PIZZA_DOCE', 'Nutella com Morango', 'Creme de avelã artesanal, morangos frescos e açúcar de confeiteiro', 'https://via.placeholder.com/400x300?text=Nutella+Morango', 4800),
('BEBIDA', 'Coca-Cola 350ml', 'Refrigerante lata 350 ml', 'https://via.placeholder.com/400x300?text=Coca-Cola', 600),
('BEBIDA', 'Guaraná Antarctica 350ml', 'Refrigerante lata 350 ml', 'https://via.placeholder.com/400x300?text=Guaraná', 550)
ON CONFLICT (nome) DO NOTHING;