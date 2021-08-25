package br.com.zup.rodrigoeduque.keymanager.integracao.itau

import br.com.zup.rodrigoeduque.keymanager.Conta

data class DadosContaResponse(
    val tipo: String,
    val instituicao: InstituicaoResponse,
    val agencia: String,
    val numero: String,
    val titular: TitularResponse
) {
    fun toModel(): Conta {
        return Conta(
            instituicao = instituicao.nome,
            nome = titular.nome,
            cpf = titular.cpf,
            agencia = agencia,
            numero = numero
        )
    }
}