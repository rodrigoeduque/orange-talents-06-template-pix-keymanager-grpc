package br.com.zup.rodrigoeduque.keymanager.remove

import br.com.zup.rodrigoeduque.*
import br.com.zup.rodrigoeduque.keymanager.Chave
import br.com.zup.rodrigoeduque.keymanager.Conta
import br.com.zup.rodrigoeduque.keymanager.registra.ChaveRepository
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Singleton
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

@MicronautTest(transactional = false)
internal class RemoverChavePixEndPointTest(
    val grpcClientRemove: RemoverChavePixServiceGrpc.RemoverChavePixServiceBlockingStub,
    val repository: ChaveRepository
) {

    lateinit var CHAVE_CADASTRADA: Chave


    @BeforeEach
    internal fun setUp() {
        CHAVE_CADASTRADA = Chave(
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
        repository.save(CHAVE_CADASTRADA)
    }

    @AfterEach
    internal fun limpar() {
        repository.deleteAll()
    }

    @Test
    internal fun `deve remover chave pix sem erros`() {
        //cenário

        //acao
        val response = grpcClientRemove.remove(
            RemoverChavePixRequest.newBuilder()
                .setIdCliente(CHAVE_CADASTRADA.idCliente)
                .setIdPix(CHAVE_CADASTRADA.id.toString())
                .build()
        )

        //validação
        assertEquals(CHAVE_CADASTRADA.id.toString(), response.idPix.toString())
        assertEquals(CHAVE_CADASTRADA.idCliente, response.idCliente)
    }

    @Test
    internal fun `nao deve remover chave quando nao existir`() {
        //cenario
        val idInexistente = UUID.randomUUID().toString()
        //acao
        val error = assertThrows<StatusRuntimeException> {
            grpcClientRemove.remove(
                RemoverChavePixRequest.newBuilder()
                    .setIdCliente(CHAVE_CADASTRADA.idCliente)
                    .setIdPix(idInexistente)
                    .build()
            )
        }

        //validacao

        assertEquals(Status.NOT_FOUND.code, error.status.code)
        assertEquals("Chave não encontrada ou não pertencente ao cliente",error.status.description)
    }

    @Test
    internal fun `nao deve remover chave quando a mesma pertence a outro cliente`() {
        //cenario
        val idClienteIncorreto = "5260263c-a3c1-4727-ae32-3bdb2538841b"

        //acao
        val error = assertThrows<StatusRuntimeException> { grpcClientRemove.remove(
            RemoverChavePixRequest.newBuilder()
                .setIdCliente(idClienteIncorreto)
                .setIdPix(CHAVE_CADASTRADA.id.toString())
                .build()
        )}
        //validacao
        assertEquals(Status.NOT_FOUND.code, error.status.code)
        assertEquals("Chave não encontrada ou não pertencente ao cliente",error.status.description)

    }

    @Factory
    class Clients {
        @Singleton
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): RemoverChavePixServiceGrpc.RemoverChavePixServiceBlockingStub? {
            return RemoverChavePixServiceGrpc.newBlockingStub(channel)
        }
    }

}

