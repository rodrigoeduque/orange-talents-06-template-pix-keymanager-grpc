package br.com.zup.rodrigoeduque.keymanager.consulta

import br.com.zup.rodrigoeduque.*
import io.grpc.stub.StreamObserver
import jakarta.inject.Inject
import jakarta.inject.Singleton
import java.util.*

@Singleton
class ConsultaChaveDetailsEndpoint(@Inject val service: ConsultaChaveService) :
    ConsultarChavePixPorChaveGrpc.ConsultarChavePixPorChaveImplBase() {
    override fun consulta(
        request: ConsultarChavePixRequest,
        responseObserver: StreamObserver<ConsultarChavePixResponse>
    ) {

        val informacoesLocal = service.buscaLocalmente(request.idCliente, UUID.fromString(request.idPix))



        responseObserver.onNext(
            ConsultarChavePixResponse.newBuilder()
                .setIdPix(request.idPix)
                .setIdCliente(request.idCliente)
                .setTipoChavePix(informacoesLocal.tipoChave)
                .setChave(informacoesLocal.chave)
                .setNome(informacoesLocal.dadosConta.titular.nome)
                .setCpf(informacoesLocal.dadosConta.titular.cpf)
                .setConta(
                    Contas.newBuilder()
                        .setInstituicao(informacoesLocal.dadosConta.instituicao.nome)
                        .setAgencia(informacoesLocal.dadosConta.agencia)
                        .setNumero(informacoesLocal.dadosConta.numero)
                        .setTipoConta(informacoesLocal.tipoConta)
                        .build()
                )
                .build()
        )
        responseObserver.onCompleted()


    }

}
