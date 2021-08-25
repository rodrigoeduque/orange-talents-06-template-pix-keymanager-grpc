package br.com.zup.rodrigoeduque.keymanager.integracao.bcb

import br.com.zup.rodrigoeduque.TipoChavePix

enum class KeyType() {
    CPF,
    CNPJ,
    PHONE,
    EMAIL,
    RANDOM;

    companion object {
        fun by(tipoChavePixDominio: TipoChavePix): KeyType? {
            return when (tipoChavePixDominio) {
                TipoChavePix.CPF -> CPF
                TipoChavePix.UNRECOGNIZED -> null
                TipoChavePix.CELULAR -> PHONE
                TipoChavePix.EMAIL -> EMAIL
                TipoChavePix.CHAVE_ALEATORIA -> RANDOM
                else -> throw IllegalStateException("Tipo de Chave não reconhecida")
            }
        }
    }
}