package br.com.zup.rodrigoeduque.keymanager.integracao.itau

import java.util.*

data class TitularResponse(
    val id: UUID,
    val nome: String,
    val cpf: String
)