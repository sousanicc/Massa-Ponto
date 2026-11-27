package com.massapontogrupo.massaponto.model;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class Sacola {

    private static final int QUANTIDADE_MAXIMA = 99;

    private final List<ItemPedido> itens = new ArrayList<>();

    // SOBRECARGA 1: adicionar com apenas produto (quantidade = 1, sem observação)
    public void adicionar(Produto produto) {
        adicionar(produto, null, 1);
    }

    // SOBRECARGA 2: adicionar com produto e observação (quantidade = 1)
    public void adicionar(Produto produto, String observacao) {
        adicionar(produto, observacao, 1);
    }

    // SOBRECARGA 3: método completo (chamado pelas duas anteriores)
    public void adicionar(Produto produto, String observacao, int quantidade) {
        if (produto == null || !produto.getAtivo()) {
            return;
        }
        int qtdNormalizada = normalizarQuantidade(quantidade);
        String obsNormalizada = normalizarObservacao(observacao);

        // Procura item existente com mesmo produto e mesma observação
        for (ItemPedido item : itens) {
            if (Objects.equals(item.getProdutoId(), produto.getId())
                    && Objects.equals(normalizarObservacao(item.getObservacao()), obsNormalizada)) {
                item.setQuantidade(normalizarQuantidade(item.getQuantidade() + qtdNormalizada));
                return;
            }
        }

        // Cria novo item
        ItemPedido novo = new ItemPedido();
        novo.setProdutoId(produto.getId());
        novo.setProduto(produto); // @Transient, só para exibição
        novo.setQuantidade(qtdNormalizada);
        novo.setObservacao(obsNormalizada);
        novo.setPrecoUnitarioCentavos(produto.getPrecoCentavos()); // preço do banco, nunca do formulário
        itens.add(novo);
    }

    // Atualiza observação e quantidade de um item existente; mescla se ficar igual a outro
    public void atualizar(int indice, String observacao, int quantidade) {
        if (indice < 0 || indice >= itens.size()) {
            return;
        }

        int novaQuantidade = normalizarQuantidade(quantidade);
        String obsNormalizada = normalizarObservacao(observacao);
        ItemPedido itemEditado = itens.get(indice);

        for (int i = 0; i < itens.size(); i++) {
            if (i == indice) {
                continue;
            }
            ItemPedido outro = itens.get(i);
            if (Objects.equals(outro.getProdutoId(), itemEditado.getProdutoId())
                    && Objects.equals(normalizarObservacao(outro.getObservacao()), obsNormalizada)) {
                outro.setQuantidade(normalizarQuantidade(outro.getQuantidade() + novaQuantidade));
                itens.remove(indice);
                return;
            }
        }

        itemEditado.setObservacao(obsNormalizada);
        itemEditado.setQuantidade(novaQuantidade);
    }

    private String normalizarObservacao(String observacao) {
        return (observacao == null || observacao.isBlank()) ? null : observacao.trim();
    }

    private int normalizarQuantidade(int quantidade) {
        return Math.max(1, Math.min(QUANTIDADE_MAXIMA, quantidade));
    }

    // Remove item por índice na lista (mais simples para o template)
    public void remover(int indice) {
        if (indice >= 0 && indice < itens.size()) {
            itens.remove(indice);
        }
    }

    public void limpar() {
        itens.clear();
    }

    public List<ItemPedido> getItens() {
        return itens;
    }

    public int getQuantidadeTotal() {
        return itens.stream().mapToInt(ItemPedido::getQuantidade).sum();
    }

    public Integer getTotalCentavos() {
        return itens.stream()
                .map(ItemPedido::getSubtotalCentavos)
                .reduce(0, Integer::sum);
    }

    public String getTotalFormatado() {
        Integer total = getTotalCentavos();
        long reais = total / 100;
        long centavos = total % 100;
        return String.format("R$ %d,%02d", reais, centavos);
    }

    public boolean isVazia() {
        return itens.isEmpty();
    }
}