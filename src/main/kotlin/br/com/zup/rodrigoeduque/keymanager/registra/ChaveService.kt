package br.com.zup.rodrigoeduque.keymanager.registra

import br.com.zup.rodrigoeduque.keymanager.Chave
import br.com.zup.rodrigoeduque.keymanager.Conta
import br.com.zup.rodrigoeduque.keymanager.exceptions.ChaveExistenteException
import br.com.zup.rodrigoeduque.keymanager.exceptions.ClienteNaoIdentificadoException
import br.com.zup.rodrigoeduque.keymanager.integracao.bcb.BcbClient
import br.com.zup.rodrigoeduque.keymanager.integracao.bcb.CreatePixKeyRequest
import br.com.zup.rodrigoeduque.keymanager.integracao.itau.ContaClienteItauClient
import br.com.zup.rodrigoeduque.keymanager.registra.extensionsfunction.toEntity
import io.micronaut.http.HttpStatus
import io.micronaut.validation.Validated
import jakarta.inject.Inject
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory
import javax.transaction.Transactional

@Singleton
@Validated
class ChaveService(
    @Inject val repository: ChaveRepository,
    @Inject val itauClient: ContaClienteItauClient,
    @Inject val bcBClient: BcbClient
) {

    private val LOG = LoggerFactory.getLogger(this::class.java)

    @Transactional
    fun registrar(chaveDto: NovaChaveRequest): Chave {

        LOG.info("Verificando existencia de Chave no sistema")

        if (repository.existsByChave(chaveDto.chave)) {
            throw ChaveExistenteException("Chave já está sendo utilizada")
        }
        val conta: Conta


        try {
            LOG.info("Iniciano Integração |ITAU|")
            val response = itauClient.buscaContaPorTipo(chaveDto.idCliente, chaveDto.tipoConta.name)
            conta =
                response.body()?.toModel() ?: throw ClienteNaoIdentificadoException("Cliente não encontrado no Itau")
            LOG.info("Finalizando Integração |ITAU|")
        } catch (e: NullPointerException) {

            throw IllegalStateException("Cliente não encontrado no Itau")

        }

        LOG.info("Persistencia | Banco De Dados |")

        val chaveCriada = chaveDto.toEntity(conta)
        repository.save(chaveCriada)

        LOG.info("Iniciando Integração |BCB|")
        val bcbRequest =
            CreatePixKeyRequest.of(chaveCriada).also { LOG.info("Registrando Chave Pix no Banco Central |BCB|: $it") }
        LOG.warn("bcbRequest -> ${bcbRequest}")

        val responseBcb = bcBClient.criar(bcbRequest)
        LOG.warn("STATUS -> ${responseBcb.status}")

        if (responseBcb.status == HttpStatus.UNPROCESSABLE_ENTITY) {
            throw IllegalStateException("Chave PIX já cadastrada anteriormente no Banco Central do Brasil")
        }
        if (responseBcb.status != HttpStatus.CREATED) {
            throw IllegalStateException("Erro ao Registrar chave junto ao Banco Central ")
        }

        chaveCriada.atualiza(responseBcb.body().key)
        LOG.info("Finalizando Integração |BCB|")

        return chaveCriada
    }

}
