package br.com.zup.rodrigoeduque.keymanager.integracao.bcb

import br.com.zup.rodrigoeduque.keymanager.Chave
import br.com.zup.rodrigoeduque.keymanager.Conta

data class CreatePixKeyRequest(
    val keyType: KeyType?,
    val key: String,
    val bankAccount: BankAccount,
    val owner: Owner
) {
    companion object {
        fun of(chave: Chave): CreatePixKeyRequest {
            return CreatePixKeyRequest(
                keyType = KeyType.by(chave.tipoChave!!),
                key = chave.chave,
                bankAccount = BankAccount(
                    participant = Conta.CODIGO_ISPB,
                    branch = chave.conta.agencia,
                    accountNumber = chave.conta.numero,
                    accountType = AccountType.by(chave.tipoConta)
                ),
                owner = Owner(
                    type = Type.NATURAL_PERSON,
                    name = chave.conta.nome,
                    taxIdNumber = chave.conta.cpf
                )
            )
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CreatePixKeyRequest

        if (keyType != other.keyType) return false

        return true
    }

    override fun hashCode(): Int {
        return keyType?.hashCode() ?: 0
    }


}