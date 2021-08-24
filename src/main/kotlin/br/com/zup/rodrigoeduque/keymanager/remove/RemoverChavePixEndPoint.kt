package br.com.zup.rodrigoeduque.keymanager.remove

import br.com.zup.rodrigoeduque.RemoverChavePixRequest
import br.com.zup.rodrigoeduque.RemoverChavePixResponse
import br.com.zup.rodrigoeduque.RemoverChavePixServiceGrpc
import br.com.zup.rodrigoeduque.keymanager.exceptions.ChaveClienteInexistenteException
import io.grpc.Status
import io.grpc.stub.StreamObserver
import jakarta.inject.Inject
import jakarta.inject.Singleton
import java.util.*

@Singleton
class RemoverChavePixEndPoint(@Inject val removerChaveService: RemoverChaveService) :
    RemoverChavePixServiceGrpc.RemoverChavePixServiceImplBase() {
    override fun remove(request: RemoverChavePixRequest, responseObserver: StreamObserver<RemoverChavePixResponse>) {
        try {
            removerChaveService.remove(UUID.fromString(request.idPix), request.idCliente)
            responseObserver.onNext(
                RemoverChavePixResponse.newBuilder()
                    .setIdCliente(request.idCliente)
                    .setIdPix(request.idPix.toString())
                    .build()
            )
            responseObserver.onCompleted()
        } catch (e: ChaveClienteInexistenteException) {
            e.stackTrace
            responseObserver.onError(
                Status.NOT_FOUND.withDescription(e.message).withCause(e.cause).asRuntimeException()
            )
        }


    }
}

