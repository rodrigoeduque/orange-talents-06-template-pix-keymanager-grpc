package br.com.zup.rodrigoeduque.keymanager.registra

import br.com.zup.rodrigoeduque.keymanager.Conta
import java.util.*

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

data class InstituicaoResponse(
    val nome: String,
    val ispb: String

)

data class TitularResponse(
    val id: UUID,
    val nome: String,
    val cpf: String
)

