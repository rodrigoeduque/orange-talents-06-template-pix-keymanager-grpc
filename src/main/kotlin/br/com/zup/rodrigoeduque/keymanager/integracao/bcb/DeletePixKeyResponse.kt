package br.com.zup.rodrigoeduque.keymanager.integracao.bcb

import java.time.LocalDateTime

data class DeletePixKeyResponse(
    val key: String,
    val participant: String,
    val deletedAt: LocalDateTime
) {

}
