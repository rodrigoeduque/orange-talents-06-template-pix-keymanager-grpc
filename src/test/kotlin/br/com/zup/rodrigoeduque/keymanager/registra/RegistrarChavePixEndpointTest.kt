package br.com.zup.rodrigoeduque.keymanager.registra

import br.com.zup.rodrigoeduque.KeymanagerServiceGrpc
import br.com.zup.rodrigoeduque.RegistrarChavePixRequest
import br.com.zup.rodrigoeduque.TipoChavePix
import br.com.zup.rodrigoeduque.TipoConta
import br.com.zup.rodrigoeduque.keymanager.integracao.itau.DadosContaResponse
import br.com.zup.rodrigoeduque.keymanager.integracao.itau.InstituicaoResponse
import br.com.zup.rodrigoeduque.keymanager.integracao.itau.TitularResponse
import io.grpc.ManagedChannel
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Singleton
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

@MicronautTest
internal class RegistrarChavePixEndpointTest(
    val grpcClient: KeymanagerServiceGrpc.KeymanagerServiceBlockingStub,
    val repository: ChaveRepository
) {

    private val dadosContaResponse = DadosContaResponse(
        "CONTA_CORRENTE",
        InstituicaoResponse("ITAÚ UNIBANCO S.A.", "60701190"),
        "0001",
        "212233",
        TitularResponse(UUID.randomUUID(), "Alberto Tavares", "39109890041")
    )

    private val idClienteTest: String = "0d1bb194-3c52-4e67-8c35-a93c0af9284f"
    private val tipoContaTest = TipoConta.CONTA_CORRENTE
    private val tipoChavePixTest = TipoChavePix.CPF
    private val chaveTest = "06628726061"
    private val idClienteInvalidoTest = "Ad1bb194-3c52-4e67-8c35-a93c0af9284f"


    @BeforeEach
    internal fun setUp() {
        repository.deleteAll()
    }


    @Test
    internal fun `deve registrar uma nova chave Pix com o tipo CPF`() {
        //cenário

        // executar ação
        val response = grpcClient.registra(
            RegistrarChavePixRequest
                .newBuilder()
                .setIdCliente(idClienteTest)
                .setTipoConta(tipoContaTest)
                .setTipoChavePix(tipoChavePixTest)
                .setChave(chaveTest)
                .build()
        )

        //validar
        assertNotNull(response.idPix)
        assertNotNull(response.idCliente)
        assertTrue(repository.existsByChave(chaveTest))
    }

    @Test
    fun `deve registrar uma nova chave Pix com o tipo EMAIL`() {


        val response = grpcClient.registra(
            RegistrarChavePixRequest.newBuilder()
                .setIdCliente(idClienteTest)
                .setTipoChavePix(TipoChavePix.EMAIL)
                .setChave("test@test.com.br")
                .setTipoConta(TipoConta.CONTA_CORRENTE)
                .build()
        )

        with(response) {
            assertNotNull(idPix)
            assertTrue(repository.existsByChave("test@test.com.br"))
        }
    }

    @Test
    fun `deve registrar nova chave PIX com o tipo CHAVE ALEATORIA`() {


        val response = grpcClient.registra(
            RegistrarChavePixRequest.newBuilder()
                .setIdCliente(idClienteTest)
                .setTipoChavePix(TipoChavePix.CHAVE_ALEATORIA)
                .setTipoConta(TipoConta.CONTA_CORRENTE)
                .build()
        )

        with(response) {
            assertNotNull(idPix)
        }
    }

    @Factory
    class Clients {
        @Singleton
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): KeymanagerServiceGrpc.KeymanagerServiceBlockingStub? {
            return KeymanagerServiceGrpc.newBlockingStub(channel)
        }
    }
}