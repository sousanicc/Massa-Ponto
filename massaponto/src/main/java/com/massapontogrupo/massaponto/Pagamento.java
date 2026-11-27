package com.massapontogrupo.massaponto;

import java.util.UUID;

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

// HERANÇA: PagamentoPix estende Pagamento
class PagamentoPix extends Pagamento {

    @Override // SOBRESCRITA
    public FormaPagamento getForma() {
        return FormaPagamento.PIX;
    }

    @Override // SOBRESCRITA
    public String processar(int totalCentavos) {
        String codigo = "00020126580014br.gov.bcb.pix0136" + UUID.randomUUID().toString().replace("-", "").substring(0, 32) + "520400005303986540" + String.format("%013d", totalCentavos) + "5802BR5913Massa & Ponto6009Sao Paulo62070503***6304ABCD";
        return "Pagamento via Pix\n\nCódigo copia e cola:\n" + codigo + "\n\nValor: " + formatarValor(totalCentavos);
    }
}

// HERANÇA: PagamentoCartao estende Pagamento
class PagamentoCartao extends Pagamento {

    @Override // SOBRESCRITA
    public FormaPagamento getForma() {
        return FormaPagamento.CARTAO;
    }

    @Override // SOBRESCRITA
    public String processar(int totalCentavos) {
        return "Pagamento aprovado no cartão\n\nValor: " + formatarValor(totalCentavos) + "\n\nComprovante: " + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}

// HERANÇA: PagamentoDinheiro estende Pagamento
class PagamentoDinheiro extends Pagamento {

    @Override // SOBRESCRITA
    public FormaPagamento getForma() {
        return FormaPagamento.DINHEIRO;
    }

    @Override // SOBRESCRITA
    public String processar(int totalCentavos) {
        return "Pagamento na entrega (dinheiro)\n\nValor a ter em mãos: " + formatarValor(totalCentavos) + "\n\nO troco será providenciado pelo entregador.";
    }
}