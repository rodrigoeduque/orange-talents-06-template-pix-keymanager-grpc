package br.com.zup.rodrigoeduque.keymanager.consulta

import br.com.zup.rodrigoeduque.Contas
import br.com.zup.rodrigoeduque.keymanager.Conta
import br.com.zup.rodrigoeduque.keymanager.exceptions.ChaveClienteInexistenteException
import br.com.zup.rodrigoeduque.keymanager.integracao.bcb.BcbClient
import br.com.zup.rodrigoeduque.keymanager.integracao.itau.ContaClienteItauClient
import br.com.zup.rodrigoeduque.keymanager.integracao.itau.DadosContaResponse
import br.com.zup.rodrigoeduque.keymanager.integracao.itau.InstituicaoResponse
import br.com.zup.rodrigoeduque.keymanager.integracao.itau.TitularResponse
import br.com.zup.rodrigoeduque.keymanager.registra.ChaveRepository
import jakarta.inject.Inject
import jakarta.inject.Singleton
import java.util.*

@Singleton
class ConsultaChaveService(
    @Inject val repository: ChaveRepository,
    @Inject val bcbClient: BcbClient,
    @Inject val itauClient: ContaClienteItauClient
) {

    fun buscaLocalmente(idCliente: String, idPix: UUID): ConsultaResponse {
        val existeLocalmente = repository.findByIdAndIdCliente(idPix, idCliente)
            .orElseThrow { ChaveClienteInexistenteException("Chave não encontrada ou não pertencente ao cliente") }

        val buscaRemotamente = bcbClient.buscarChave(idPix.toString())




        return ConsultaResponse(
            tipoChave = existeLocalmente.tipoChave,
            chave = existeLocalmente.chave,
            dadosConta = DadosContaResponse(
                tipo = existeLocalmente.tipoConta.name,
                instituicao = InstituicaoResponse(
                    existeLocalmente.conta.instituicao,
                    Conta.CODIGO_ISPB
                ),
                agencia = existeLocalmente.conta.agencia,
                numero = existeLocalmente.conta.numero,
                titular = TitularResponse(
                    id = UUID.fromString(existeLocalmente.idCliente),
                    nome = existeLocalmente.conta.nome,
                    cpf = existeLocalmente.conta.cpf
                ),
            ), tipoConta = existeLocalmente.tipoConta
        )
    }

}
