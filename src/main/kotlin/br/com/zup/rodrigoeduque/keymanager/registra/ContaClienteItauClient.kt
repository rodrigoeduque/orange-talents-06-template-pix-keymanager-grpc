package br.com.zup.rodrigoeduque.keymanager.registra

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.QueryValue
import io.micronaut.http.client.annotation.Client

@Client(value = "http://localhost:9091") //Preciso ajustar para variavel, não consegui pois estava retornando erro : Could not resolve placeholder ToDo : Ler Documentação https://docs.micronaut.io/1.3.0.M1/guide/index.html
interface ContaClienteItauClient {

    @Get(value = "/api/v1/clientes/{idcliente}/contas{?tipo}")
    fun buscaContaPorTipo(
        @PathVariable idcliente: String,
        @QueryValue tipo: String
    ): HttpResponse<DadosContaResponse>
}