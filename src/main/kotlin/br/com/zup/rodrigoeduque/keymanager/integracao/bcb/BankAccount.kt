package br.com.zup.rodrigoeduque.keymanager.integracao.bcb

data class BankAccount(
    val participant: String,
    val branch: String,
    val accountNumber: String,
    val accountType: AccountType
){

}