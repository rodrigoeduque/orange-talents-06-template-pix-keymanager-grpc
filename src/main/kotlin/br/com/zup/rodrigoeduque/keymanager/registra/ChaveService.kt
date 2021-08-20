package br.com.zup.rodrigoeduque.keymanager.registra

import br.com.zup.rodrigoeduque.keymanager.Chave
import br.com.zup.rodrigoeduque.keymanager.exceptions.ChaveExistenteException
import br.com.zup.rodrigoeduque.keymanager.registra.extensionsfunction.toEntity
import io.micronaut.validation.Validated
import jakarta.inject.Inject
import jakarta.inject.Singleton
import javax.transaction.Transactional

@Singleton
@Validated
class ChaveService(@Inject val repository: ChaveRepository, @Inject val client: ContaClienteItauClient) {

    @Transactional
    fun registrar(chaveDto: NovaChaveRequest): Chave {

        if (repository.existsByChave(chaveDto.chave)) {
            throw ChaveExistenteException("Chave já está sendo utilizada > ${chaveDto.chave}")
        }

        val response = client.buscaContaPorTipo(chaveDto.idCliente, chaveDto.tipoConta.name)

        // Havia implementado dessa forma, no entanto achei mais interessante a forma como o Rafael abordou usando o Elvis Operator
//        if (response.status == HttpStatus.INTERNAL_SERVER_ERROR) {
//            throw IllegalStateException("Identificador do Cliente/Tipo de Conta com Formato Inválido")
//        }

        val conta = response.body().toModel() ?: throw IllegalStateException("Cliente não Encontrado")

        val chaveCriada = chaveDto.toEntity(conta)

        repository.save(chaveCriada)

        return chaveCriada
    }

}
