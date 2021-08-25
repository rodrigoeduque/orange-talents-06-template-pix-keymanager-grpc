package br.com.zup.rodrigoeduque.keymanager.integracao.bcb

import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Post
import io.micronaut.http.client.annotation.Client

@Client(value = "\${bcb.pix.url}")
interface BcbClient {
    @Post(value = "/api/v1/pix/keys", produces = [MediaType.APPLICATION_XML], consumes = [MediaType.APPLICATION_XML])
    fun criar(@Body request: CreatePixKeyRequest): HttpResponse<CreatePixKeyResponse>
}