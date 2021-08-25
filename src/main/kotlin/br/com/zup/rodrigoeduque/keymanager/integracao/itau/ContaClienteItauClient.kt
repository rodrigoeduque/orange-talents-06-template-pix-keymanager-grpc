package br.com.zup.rodrigoeduque.keymanager.integracao.itau

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.QueryValue
import io.micronaut.http.client.annotation.Client

@Client(value = "\${integracao.itau.url}")
interface ContaClienteItauClient {

    @Get(value = "/api/v1/clientes/{idcliente}/contas{?tipo}")
    fun buscaContaPorTipo(
        @PathVariable idcliente: String,
        @QueryValue tipo: String
    ): HttpResponse<DadosContaResponse>
}