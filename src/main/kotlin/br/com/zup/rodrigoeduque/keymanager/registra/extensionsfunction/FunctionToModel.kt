package br.com.zup.rodrigoeduque.keymanager.registra

import br.com.zup.rodrigoeduque.RegistrarChavePixRequest

fun RegistrarChavePixRequest.toModel(): NovaChaveRequest {
    return NovaChaveRequest(
        idCliente = this.idCliente,
        tipoChavePix = this.tipoChavePix,
        chave = this.chave,
        tipoConta = this.tipoConta,
    )
}