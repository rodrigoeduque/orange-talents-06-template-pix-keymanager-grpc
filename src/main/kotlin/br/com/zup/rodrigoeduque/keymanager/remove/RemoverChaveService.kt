package br.com.zup.rodrigoeduque.keymanager.remove

import br.com.zup.rodrigoeduque.keymanager.exceptions.ChaveClienteInexistenteException
import br.com.zup.rodrigoeduque.keymanager.registra.ChaveRepository
import io.micronaut.validation.Validated
import jakarta.inject.Inject
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory
import java.util.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Singleton
@Validated
class RemoverChaveService(@Inject val repository: ChaveRepository) {

    val log = LoggerFactory.getLogger(this::class.java)

    fun remove(@NotNull idPix: UUID, @NotBlank idCliente: String) {
        log.info("Realizando Verificação => ChavePix : $idPix || Cliente Id : $idCliente")
        val existe = repository.findByIdAndIdCliente(idPix, idCliente)
            .orElseThrow { ChaveClienteInexistenteException("Chave não encontrada ou não pertencente ao cliente") }
        log.info("Iniciando processo de remoção")
        repository.delete(existe)
        log.info("Chave Removida do Banco de Dados")
    }


}
