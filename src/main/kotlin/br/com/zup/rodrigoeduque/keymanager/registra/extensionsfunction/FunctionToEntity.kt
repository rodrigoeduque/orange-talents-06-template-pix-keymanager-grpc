package br.com.zup.rodrigoeduque.keymanager.registra.extensionsfunction

import br.com.zup.rodrigoeduque.TipoChavePix
import br.com.zup.rodrigoeduque.keymanager.Chave
import br.com.zup.rodrigoeduque.keymanager.Conta
import br.com.zup.rodrigoeduque.keymanager.registra.NovaChaveRequest
import java.util.*

fun NovaChaveRequest.toEntity(conta: Conta): Chave {
    return Chave(
        idCliente = idCliente,
        tipoChave = this.tipoChavePix,
        chave = when (this.tipoChavePix) {
            TipoChavePix.CHAVE_ALEATORIA -> UUID.randomUUID().toString()
            else -> this.chave
        },
        tipoConta = this.tipoConta,
        conta = conta
    )


}