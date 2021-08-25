package br.com.zup.rodrigoeduque.keymanager

import javax.persistence.Embeddable

@Embeddable
class Conta(
    val instituicao: String,
    val nome: String,
    val cpf: String,
    val agencia: String,
    val numero: String
) {
    override fun toString(): String {
        return "Conta(instituicao='$instituicao', nome='$nome', cpf='$cpf', agencia='$agencia', numero='$numero')"
    }

    companion object{
        public val CODIGO_ISPB: String = "60701190"
    }
}
