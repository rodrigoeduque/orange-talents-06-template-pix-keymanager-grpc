package br.com.zup.rodrigoeduque.keymanager.integracao.bcb

import java.time.LocalDateTime

data class CreatePixKeyResponse(
    val keyType: String,
    val key: String,
    val bankAccount: BankAccount,
    val owner: Owner,
    val createdAt: LocalDateTime
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CreatePixKeyResponse

        if (bankAccount != other.bankAccount) return false

        return true
    }

    override fun hashCode(): Int {
        return bankAccount.hashCode()
    }
}