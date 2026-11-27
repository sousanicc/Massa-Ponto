package com.massapontogrupo.massaponto.model.pagamento;

import com.massapontogrupo.massaponto.model.FormaPagamento;

import java.util.UUID;

// HERANÇA: PagamentoCartao estende Pagamento
public class PagamentoCartao extends Pagamento {

    @Override // SOBRESCRITA
    public FormaPagamento getForma() {
        return FormaPagamento.CARTAO;
    }

    @Override // SOBRESCRITA
    public String processar(int totalCentavos) {
        return "Pagamento aprovado no cartão\n\nValor: " + formatarValor(totalCentavos) + "\n\nComprovante: " + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
