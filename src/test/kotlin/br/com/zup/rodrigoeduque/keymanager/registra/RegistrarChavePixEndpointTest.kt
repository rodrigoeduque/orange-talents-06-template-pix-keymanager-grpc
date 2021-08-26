package br.com.zup.rodrigoeduque.keymanager.registra

import br.com.zup.rodrigoeduque.KeymanagerServiceGrpc
import br.com.zup.rodrigoeduque.RegistrarChavePixRequest
import br.com.zup.rodrigoeduque.TipoChavePix
import br.com.zup.rodrigoeduque.TipoConta
import br.com.zup.rodrigoeduque.keymanager.integracao.bcb.*
import br.com.zup.rodrigoeduque.keymanager.integracao.itau.ContaClienteItauClient
import br.com.zup.rodrigoeduque.keymanager.integracao.itau.DadosContaResponse
import br.com.zup.rodrigoeduque.keymanager.integracao.itau.InstituicaoResponse
import br.com.zup.rodrigoeduque.keymanager.integracao.itau.TitularResponse
import io.grpc.ManagedChannel
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.http.HttpResponse
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import jakarta.inject.Singleton
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import java.time.LocalDateTime
import java.util.*

@MicronautTest(transactional = false)
internal class RegistrarChavePixEndpointTest(
    @Inject val repository: ChaveRepository,
    val grpc: KeymanagerServiceGrpc.KeymanagerServiceBlockingStub
) {
    companion object {
        val IDCLIENTE = "c56dfef4-7901-44fb-84e2-a2cefb157890"
    }

    @Inject
    lateinit var itauClient: ContaClienteItauClient

    @Inject
    lateinit var bcbClient: BcbClient

    @BeforeEach
    fun setUp() {
        repository.deleteAll()
    }

    @Test
    fun `deve registrar nova chave pix`() {

        `when`(
            itauClient.buscaContaPorTipo(
                idcliente = IDCLIENTE,
                tipo = "CONTA_CORRENTE"
            )
        )
            .thenReturn(HttpResponse.ok(contaResponse()))
        `when`(bcbClient.criar(novoCreatePixKeyRequest()))
            .thenReturn(HttpResponse.created(novoCreatePixKeyResponse()))
        val response = grpc.registra(
            RegistrarChavePixRequest.newBuilder()
                .setIdCliente(IDCLIENTE)
                .setTipoChavePix(TipoChavePix.EMAIL)
                .setChave("rafael@email.com")
                .setTipoConta(TipoConta.CONTA_CORRENTE)
                .build()
        )

        with(response) {
            assertEquals(IDCLIENTE, idCliente)
            assertNotNull(idPix)
        }
    }


    @MockBean(ContaClienteItauClient::class)
    fun itauClient(): ContaClienteItauClient? {
        return Mockito.mock(ContaClienteItauClient::class.java)
    }

    @MockBean(BcbClient::class)
    fun bcbPixClient(): BcbClient? {
        return Mockito.mock(BcbClient::class.java)
    }

    @Factory
    class Clients {
        @Singleton
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): KeymanagerServiceGrpc.KeymanagerServiceBlockingStub {
            return KeymanagerServiceGrpc.newBlockingStub(channel)
        }
    }

    private fun contaResponse(): DadosContaResponse {
        return DadosContaResponse(
            agencia = "0001",
            numero = "291900",
            titular = TitularResponse(id = UUID.fromString(IDCLIENTE), nome = "Rafael M C Ponte", cpf = "02467781054"),
            tipo = "CONTA_CORRENTE",
            instituicao = InstituicaoResponse(nome = "ITAÚ UNIBANCO S.A.", ispb = "60701190")
        )
    }

    private fun novoCreatePixKeyRequest(): CreatePixKeyRequest {
        val bankAccount = BankAccount(
            participant = "60701190",
            branch = "0001",
            accountNumber = "291900",
            accountType = AccountType.CACC
        )

        val owner = Owner(
            type = Type.NATURAL_PERSON,
            name = "Rafael M C Ponte",
            taxIdNumber = IDCLIENTE
        )
        return CreatePixKeyRequest(
            keyType = KeyType.EMAIL,
            key = "rafael@email.com",
            bankAccount = bankAccount,
            owner = owner
        )
    }

    private fun novoCreatePixKeyResponse(): CreatePixKeyResponse {
        return CreatePixKeyResponse(
            keyType = KeyType.EMAIL.name,
            key = "rafael@email.com",
            bankAccount = BankAccount(
                participant = "60701190",
                branch = "0001",
                accountNumber = "291900",
                accountType = AccountType.CACC
            ), owner = Owner(
                type = Type.NATURAL_PERSON,
                name = "Rafael M C Ponte",
                taxIdNumber = IDCLIENTE
            ), createdAt = LocalDateTime.now()
        )
    }
}