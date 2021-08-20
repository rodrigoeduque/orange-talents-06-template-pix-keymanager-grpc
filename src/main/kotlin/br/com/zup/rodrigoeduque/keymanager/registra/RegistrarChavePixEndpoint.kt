package br.com.zup.rodrigoeduque.keymanager.registra

import br.com.zup.rodrigoeduque.KeymanagerServiceGrpc
import br.com.zup.rodrigoeduque.RegistrarChavePixRequest
import br.com.zup.rodrigoeduque.RegistrarChavePixResponse
import io.grpc.stub.StreamObserver
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class RegistrarChavePixEndpoint(@Inject val service: ChaveService) :
    KeymanagerServiceGrpc.KeymanagerServiceImplBase() {
    override fun registra(
        request: RegistrarChavePixRequest,
        responseObserver: StreamObserver<RegistrarChavePixResponse>
    ) {
        val chaveDto = request.toModel()
//        val chavePixCriada = chaveDto.toEntity() -> O código se tornou ilegivel sem usar o service

        val chavePixCriada = service.registrar(chaveDto)





        responseObserver.onNext(
            RegistrarChavePixResponse.newBuilder()
                .setIdCliente(chavePixCriada.idCliente)
                .setIdPix(chavePixCriada.chave)
                .build()
        )
        println(chaveDto)
        println(chavePixCriada)
        responseObserver.onCompleted()
    }
}

