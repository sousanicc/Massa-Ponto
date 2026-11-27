package com.massapontogrupo.massaponto.model.pagamento;

import com.massapontogrupo.massaponto.model.FormaPagamento;

import java.util.UUID;

// HERANÇA: PagamentoPix estende Pagamento
public class PagamentoPix extends Pagamento {

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
