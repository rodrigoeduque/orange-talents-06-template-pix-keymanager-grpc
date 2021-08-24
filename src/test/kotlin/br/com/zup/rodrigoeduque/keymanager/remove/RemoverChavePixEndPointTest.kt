package br.com.zup.rodrigoeduque.keymanager.remove

import br.com.zup.rodrigoeduque.*
import br.com.zup.rodrigoeduque.keymanager.Chave
import br.com.zup.rodrigoeduque.keymanager.Conta
import br.com.zup.rodrigoeduque.keymanager.registra.ChaveRepository
import io.grpc.ManagedChannel
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Singleton
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

@MicronautTest(transactional = false)
internal class RemoverChavePixEndPointTest(
    val grpcClientRemove: RemoverChavePixServiceGrpc.RemoverChavePixServiceBlockingStub,
    val repository: ChaveRepository
) {

    val chaveCadastrada = Chave(
        idCliente = "0d1bb194-3c52-4e67-8c35-a93c0af9284f",
        tipoChave = TipoChavePix.CPF,
        chave = "06628726061",
        tipoConta = TipoConta.CONTA_CORRENTE,
        Conta(
            instituicao = "ITAÚ UNIBANCO S.A.",
            nome = "Alberto Tavares",
            cpf = "06628726061",
            agencia = "0001",
            numero = "212233"
        )
    )

    @BeforeEach
    internal fun setUp() {
        repository.deleteAll()
    }

    @AfterEach
    internal fun tearDown() {
        repository.save(chaveCadastrada)
    }

//    @Test
//    internal fun `deve remover chave pix sem erros`() {
//        //cenário
//
//        //acao
//        val response = grpcClientRemove.remove(
//            RemoverChavePixRequest.newBuilder()
//                .setIdCliente(chaveCadastrada.idCliente)
//                .setIdPix(UUID.fromString(chaveCadastrada.id))
//                .build()
//        )
//
//        //validação
//
//
//    }

    @Factory
    class Clients {
        @Singleton
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): RemoverChavePixServiceGrpc.RemoverChavePixServiceBlockingStub? {
            return RemoverChavePixServiceGrpc.newBlockingStub(channel)
        }
    }
}

