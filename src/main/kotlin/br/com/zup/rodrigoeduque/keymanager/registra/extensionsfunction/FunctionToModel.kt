package br.com.zup.rodrigoeduque.keymanager.registra

import br.com.zup.rodrigoeduque.RegistrarChavePixRequest
import br.com.zup.rodrigoeduque.TipoChavePix
import br.com.zup.rodrigoeduque.keymanager.registra.extensionsfunction.toEntity
import java.util.*

fun RegistrarChavePixRequest.toModel(): NovaChaveRequest {
    return NovaChaveRequest(
        idCliente = this.idCliente,
        tipoChavePix = this.tipoChavePix,
        chave = when (this.tipoChavePix) {
            TipoChavePix.CHAVE_ALEATORIA -> UUID.randomUUID().toString()
            else -> this.chave
        },
        tipoConta = this.tipoConta,
    )
}