package br.com.zup.rodrigoeduque.keymanager.registra

import br.com.zup.rodrigoeduque.KeymanagerServiceGrpc
import br.com.zup.rodrigoeduque.RegistrarChavePixRequest
import br.com.zup.rodrigoeduque.TipoChavePix
import br.com.zup.rodrigoeduque.TipoConta
import br.com.zup.rodrigoeduque.keymanager.Chave
import br.com.zup.rodrigoeduque.keymanager.Conta
import br.com.zup.rodrigoeduque.keymanager.integracao.bcb.*
import br.com.zup.rodrigoeduque.keymanager.integracao.itau.ContaClienteItauClient
import br.com.zup.rodrigoeduque.keymanager.integracao.itau.DadosContaResponse
import br.com.zup.rodrigoeduque.keymanager.integracao.itau.InstituicaoResponse
import br.com.zup.rodrigoeduque.keymanager.integracao.itau.TitularResponse
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
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
import org.junit.jupiter.api.assertThrows
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
        val IDCLIENTE = "0d1bb194-3c52-4e67-8c35-a93c0af9284f"
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
    fun `deve registrar nova chave pix do tipo email`() {

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
                .setChave("test@test.com.br")
                .setTipoConta(TipoConta.CONTA_CORRENTE)
                .build()
        )

        with(response) {
            assertEquals(IDCLIENTE, idCliente)
            assertNotNull(idPix)
        }
    }

    @Test
    internal fun `nao deve registrar chave quando email for invalido`() {

        `when`(
            itauClient.buscaContaPorTipo(
                idcliente = IDCLIENTE,
                tipo = "CONTA_CORRENTE"
            )
        )
            .thenReturn(HttpResponse.ok(contaResponse()))
        `when`(bcbClient.criar(novoCreatePixKeyRequest()))
            .thenReturn(HttpResponse.created(novoCreatePixKeyResponse()))
        val statusGerado = assertThrows<StatusRuntimeException> {
            grpc.registra(
                RegistrarChavePixRequest.newBuilder()
                    .setIdCliente(IDCLIENTE)
                    .setTipoChavePix(TipoChavePix.EMAIL)
                    .setChave("testtest.com.br")
                    .setTipoConta(TipoConta.CONTA_CORRENTE)
                    .build()
            )
        }

        with(statusGerado) {
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
        }
    }

    @Test
    internal fun `nao deve gerar chave quando identificador do cliente nao existir no itau`() {
        `when`(
            itauClient.buscaContaPorTipo(
                idcliente = IDCLIENTE,
                tipo = "CONTA_CORRENTE"
            )
        )
            .thenReturn(HttpResponse.notFound())
        `when`(bcbClient.criar(novoCreatePixKeyRequest()))
            .thenReturn(HttpResponse.created(novoCreatePixKeyResponse()))

        val statusGerado = assertThrows<StatusRuntimeException> {
            grpc.registra(
                cadastroNovaChave(TipoChavePix.CPF, "07441173650", TipoConta.CONTA_CORRENTE)
            )
        }

        with(statusGerado) {
            assertEquals(io.grpc.Status.NOT_FOUND.code, status.code)
        }

    }

    @Test
    internal fun `nao deve registrar chave caso a mesma já exista`() {
        //cenario
        repository.save(
            Chave(
                idCliente = IDCLIENTE,
                tipoChave = TipoChavePix.CPF,
                chave = "07441173650",
                tipoConta = TipoConta.CONTA_CORRENTE,
                conta = Conta(
                    instituicao = "ITAÚ UNIBANCO S.A.",
                    nome = "Tiago de Freitas",
                    cpf = "07441173650",
                    agencia = "0001",
                    numero = "889976"
                )
            )
        )

        //acao
        val statusGerado = assertThrows<StatusRuntimeException> {
            grpc.registra(
                cadastroNovaChave(TipoChavePix.CPF, "07441173650", TipoConta.CONTA_CORRENTE)
            )
        }

        with(statusGerado) {
            assertEquals(io.grpc.Status.ALREADY_EXISTS.code, status.code)
        }
    }

    @Test
    internal fun `nao deve registrar caso o formato dos dados esteja invalido`() {
        //cenario
        `when`(
            itauClient.buscaContaPorTipo(
                idcliente = IDCLIENTE,
                tipo = "CONTA_CORRENTE"
            )
        )
            .thenReturn(HttpResponse.notAllowed())
        `when`(bcbClient.criar(novoCreatePixKeyRequest()))
            .thenReturn(HttpResponse.created(novoCreatePixKeyResponse()))

        //acao
        val statusGerado = assertThrows<StatusRuntimeException> {
            grpc.registra(
                cadastroNovaChave(TipoChavePix.CPF, "074a41173650", TipoConta.CONTA_CORRENTE)
            )
        }

        with(statusGerado) {
            assertEquals(io.grpc.Status.NOT_FOUND.code, status.code)
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
            numero = "212233",
            titular = TitularResponse(id = UUID.fromString(IDCLIENTE), nome = "Alberto Tavares", cpf = "06628726061"),
            tipo = "CONTA_CORRENTE",
            instituicao = InstituicaoResponse(nome = "ITAÚ UNIBANCO S.A.", ispb = "60701190")
        )
    }

    private fun novoCreatePixKeyRequest(): CreatePixKeyRequest {
        val bankAccount = BankAccount(
            participant = "60701190",
            branch = "0001",
            accountNumber = "212233",
            accountType = AccountType.CACC
        )

        val owner = Owner(
            type = Type.NATURAL_PERSON,
            name = "Alberto Tavares",
            taxIdNumber = "06628726061"
        )
        return CreatePixKeyRequest(
            keyType = KeyType.EMAIL,
            key = "test@test.com.br",
            bankAccount = bankAccount,
            owner = owner
        )
    }

    private fun novoCreatePixKeyResponse(): CreatePixKeyResponse {
        return CreatePixKeyResponse(
            keyType = KeyType.EMAIL.name,
            key = "test@test.com.br",
            bankAccount = BankAccount(
                participant = "60701190",
                branch = "0001",
                accountNumber = "212233",
                accountType = AccountType.CACC
            ), owner = Owner(
                type = Type.NATURAL_PERSON,
                name = "Alberto Tavares",
                taxIdNumber = "06628726061"
            ), createdAt = LocalDateTime.now()
        )
    }

    fun cadastroNovaChave(
        tipoChavePix: TipoChavePix,
        chavePix: String,
        tipoConta: TipoConta
    ): RegistrarChavePixRequest {
        return RegistrarChavePixRequest.newBuilder()
            .setIdCliente(IDCLIENTE)
            .setTipoChavePix(tipoChavePix)
            .setChave(chavePix)
            .setTipoConta(tipoConta)
            .build()
    }
}