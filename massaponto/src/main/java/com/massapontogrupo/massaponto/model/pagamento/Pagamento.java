package com.massapontogrupo.massaponto.model.pagamento;

import com.massapontogrupo.massaponto.model.FormaPagamento;

public abstract class Pagamento {

    // ABSTRAÇÃO: define o contrato comum
    public abstract FormaPagamento getForma();
    public abstract String processar(int totalCentavos);

    // Método concreto herdado
    public String getNome() {
        return getForma().getNomeExibicao();
    }

    // Fábrica estática (polimorfismo na criação)
    public static Pagamento de(FormaPagamento forma) {
        return switch (forma) {
            case PIX -> new PagamentoPix();
            case CARTAO -> new PagamentoCartao();
            case DINHEIRO -> new PagamentoDinheiro();
        };
    }

    // Formatação comum
    protected String formatarValor(int centavos) {
        long reais = centavos / 100;
        long cent = centavos % 100;
        return String.format("R$ %d,%02d", reais, cent);
    }
}
