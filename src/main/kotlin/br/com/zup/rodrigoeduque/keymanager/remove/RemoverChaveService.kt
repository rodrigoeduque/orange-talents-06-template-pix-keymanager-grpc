package br.com.zup.rodrigoeduque.keymanager.remove

import br.com.zup.rodrigoeduque.keymanager.exceptions.ChaveClienteInexistenteException
import br.com.zup.rodrigoeduque.keymanager.integracao.bcb.BcbClient
import br.com.zup.rodrigoeduque.keymanager.integracao.bcb.DeletePixKeyRequest
import br.com.zup.rodrigoeduque.keymanager.registra.ChaveRepository
import io.micronaut.http.HttpStatus
import io.micronaut.validation.Validated
import jakarta.inject.Inject
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory
import java.util.*
import javax.transaction.Transactional
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Singleton
@Validated
class RemoverChaveService(@Inject val repository: ChaveRepository, @Inject val bcBClient: BcbClient) {
    private val LOG = LoggerFactory.getLogger(this::class.java)

    @Transactional
    fun remove(@NotNull idPix: UUID, @NotBlank idCliente: String) {
        LOG.info("Realizando Verificação se ID Cliente e Chave Pix existem no banco")

        val existe = repository.findByIdAndIdCliente(idPix, idCliente)
            .orElseThrow { ChaveClienteInexistenteException("Chave não encontrada ou não pertencente ao cliente") }
        LOG.info("Iniciando processo de remoção")

        repository.delete(existe)

        LOG.info("Iniciando Integração |BCB|")
        val request = DeletePixKeyRequest(existe.chave)
        val responseBcb = bcBClient.remover(request, existe.chave)

        if (responseBcb.status != HttpStatus.OK) {
            throw IllegalStateException("Erro ao tentra remover chave Pix |Service BCB| ")
        }
        LOG.info("Finalizando Integração |BCB|")
    }


}
