package br.com.zup.rodrigoeduque.keymanager.integracao.bcb

import br.com.zup.rodrigoeduque.TipoConta

enum class AccountType() {
    /**
     * Tipo de conta (CACC=Conta Corrente; SVGS=Conta Poupança)
     **/
    CACC,
    SVGS;

    companion object {
        fun by(tipoContaDominio: TipoConta): AccountType {
            return when (tipoContaDominio) {
                TipoConta.CONTA_CORRENTE -> CACC
                TipoConta.CONTA_POUPANCA -> SVGS
                else -> throw IllegalStateException("Tipo de Conta não identificada")
            }
        }
    }
}