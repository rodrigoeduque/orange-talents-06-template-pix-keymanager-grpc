package br.com.zup.rodrigoeduque.keymanager.remove

import br.com.zup.rodrigoeduque.*
import br.com.zup.rodrigoeduque.keymanager.Chave
import br.com.zup.rodrigoeduque.keymanager.Conta
import br.com.zup.rodrigoeduque.keymanager.integracao.bcb.BcbClient
import br.com.zup.rodrigoeduque.keymanager.integracao.bcb.DeletePixKeyRequest
import br.com.zup.rodrigoeduque.keymanager.integracao.bcb.DeletePixKeyResponse
import br.com.zup.rodrigoeduque.keymanager.registra.ChaveRepository
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.http.HttpResponse
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import java.time.LocalDateTime
import java.util.*

@MicronautTest(transactional = false)
internal class RemoverChavePixEndPointTest(
    val grpcClientRemove: RemoverChavePixServiceGrpc.RemoverChavePixServiceBlockingStub,
    val repository: ChaveRepository
) {

    @Inject
    lateinit var bcbClient: BcbClient

    lateinit var NOVA_CHAVE: Chave

    companion object {
        val CLIENTE_ID = "c56dfef4-7901-44fb-84e2-a2cefb157890"
        val CLIENTE_KEY_CPF = "07355566611"
    }

    @MockBean(BcbClient::class)
    fun bcbClient(): BcbClient {
        return Mockito.mock(BcbClient::class.java)
    }

    @BeforeEach
    fun setup() {
        NOVA_CHAVE = repository.save(
            Chave(
                idCliente = CLIENTE_ID,
                tipoChave = TipoChavePix.CPF,
                chave = "07355566611",
                tipoConta = TipoConta.CONTA_CORRENTE,
                conta = Conta(
                    instituicao = "ITAÚ UNIBANCO S.A.",
                    nome = "Tiago de Freitas",
                    cpf = "07355566611",
                    agencia = "0001",
                    numero = "889976"
                )
            )
        )
    }

    @AfterEach
    fun cleanUp() {
        repository.deleteAll()
    }

    @Test
    fun `deve remover uma chave pix`() {

        `when`(
            bcbClient.remover(
                DeletePixKeyRequest("07355566611", participant = "60701190"), "07355566611"
            )
        )
            .thenReturn(HttpResponse.ok(obterResponseRemoverChavePixBCB()))

        val response = grpcClientRemove.remove(
            RemoverChavePixRequest.newBuilder()
                .setIdCliente(CLIENTE_ID.toString())
                .setIdPix(NOVA_CHAVE.id.toString())
                .build()
        )

        assertEquals(NOVA_CHAVE.id.toString(), response.idPix)
        assertEquals(NOVA_CHAVE.idCliente.toString(), response.idCliente)
    }

    @Test
    fun `nao deve remover chave pix se chave nao existe`() {
        val pixIdNaoExistente = UUID.randomUUID().toString()

        val thrown = assertThrows<StatusRuntimeException> {
            grpcClientRemove.remove(
                RemoverChavePixRequest.newBuilder()
                    .setIdPix(pixIdNaoExistente)
                    .setIdCliente(NOVA_CHAVE.idCliente.toString())
                    .build()
            )
        }
        with(thrown) {
            assertEquals(Status.NOT_FOUND.code, status.code)
        }
    }

    @Test
    fun `nao deve remover chave existente caso pertencer a outro cliente`() {
        val outroClienteId = UUID.randomUUID().toString()

        val thrown = assertThrows<StatusRuntimeException> {
            grpcClientRemove.remove(
                RemoverChavePixRequest.newBuilder()
                    .setIdPix(NOVA_CHAVE.id.toString())
                    .setIdCliente(outroClienteId)
                    .build()
            )
        }
        with(thrown) {
            assertEquals(Status.NOT_FOUND.code, status.code)
        }
    }

    @Test
    fun `nao deve remover chave pix existente caso haja erro na integracao BCB`() {
        `when`(bcbClient.remover(DeletePixKeyRequest("07355566611", participant = "60701190"), "07355566611"))
            .thenReturn(HttpResponse.unprocessableEntity())

        val thrown = assertThrows<StatusRuntimeException> {
            grpcClientRemove.remove(
                RemoverChavePixRequest.newBuilder()
                    .setIdPix(NOVA_CHAVE.id.toString())
                    .setIdCliente((NOVA_CHAVE.idCliente.toString()))
                    .build()
            )
        }

        with(thrown) {
            assertEquals((Status.NOT_FOUND).code, status.code)
        }
    }

    fun obterResponseRemoverChavePixBCB(): DeletePixKeyResponse {
        return DeletePixKeyResponse(key = "07355566611", participant = "60701190", deletedAt = LocalDateTime.now())
    }

    @Factory
    class Clients {
        @Bean
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): RemoverChavePixServiceGrpc.RemoverChavePixServiceBlockingStub {
            return RemoverChavePixServiceGrpc.newBlockingStub(channel)
        }
    }

}

