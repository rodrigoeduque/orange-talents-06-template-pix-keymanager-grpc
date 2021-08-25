package br.com.zup.rodrigoeduque.keymanager.integracao.bcb

import br.com.zup.rodrigoeduque.keymanager.Conta

data class DeletePixKeyRequest(
    val key: String,
    val participant: String = Conta.CODIGO_ISPB
)