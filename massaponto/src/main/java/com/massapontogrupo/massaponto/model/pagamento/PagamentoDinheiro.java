package com.massapontogrupo.massaponto.model.pagamento;

import com.massapontogrupo.massaponto.model.FormaPagamento;

// HERANÇA: PagamentoDinheiro estende Pagamento
public class PagamentoDinheiro extends Pagamento {

    @Override // SOBRESCRITA
    public FormaPagamento getForma() {
        return FormaPagamento.DINHEIRO;
    }

    @Override // SOBRESCRITA
    public String processar(int totalCentavos) {
        return "Pagamento na entrega (dinheiro)\n\nValor a ter em mãos: " + formatarValor(totalCentavos) + "\n\nO troco será providenciado pelo entregador.";
    }
}
