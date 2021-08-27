package br.com.zup.rodrigoeduque.keymanager.consulta

import br.com.zup.rodrigoeduque.TipoChavePix
import br.com.zup.rodrigoeduque.TipoConta
import br.com.zup.rodrigoeduque.keymanager.integracao.itau.DadosContaResponse

data class ConsultaResponse(
    val tipoChave: TipoChavePix?,
    val chave: String,
    val dadosConta: DadosContaResponse,
    val tipoConta: TipoConta
)
