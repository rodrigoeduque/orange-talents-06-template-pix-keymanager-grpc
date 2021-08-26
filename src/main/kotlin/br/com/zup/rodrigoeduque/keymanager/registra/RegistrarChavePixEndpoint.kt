package br.com.zup.rodrigoeduque.keymanager.registra

import br.com.zup.rodrigoeduque.ErrorDetails
import br.com.zup.rodrigoeduque.KeymanagerServiceGrpc
import br.com.zup.rodrigoeduque.RegistrarChavePixRequest
import br.com.zup.rodrigoeduque.RegistrarChavePixResponse
import br.com.zup.rodrigoeduque.keymanager.Chave
import br.com.zup.rodrigoeduque.keymanager.exceptions.ChaveExistenteException
import br.com.zup.rodrigoeduque.keymanager.exceptions.ClienteNaoIdentificadoException
import br.com.zup.rodrigoeduque.keymanager.exceptions.InfoInvalidaException
import com.google.protobuf.Any
import com.google.rpc.ErrorDetailsProto
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.grpc.protobuf.StatusProto.*
import io.grpc.stub.StreamObserver
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.http.exceptions.HttpStatusException
import jakarta.inject.Inject
import jakarta.inject.Singleton
import javax.validation.UnexpectedTypeException

@Singleton
class RegistrarChavePixEndpoint(@Inject val service: ChaveService) : KeymanagerServiceGrpc.KeymanagerServiceImplBase() {
    override fun registra(
        request: RegistrarChavePixRequest,
        responseObserver: StreamObserver<RegistrarChavePixResponse>
    ) {
        val chaveDto = request.toModel()
        val chavePixCriada: Chave

        try {
            chavePixCriada = service.registrar(chaveDto)
        } catch (e: IllegalStateException) {
            e.stackTrace
            val error = Status.FAILED_PRECONDITION
                .withDescription(e.message)
                .asRuntimeException()
            responseObserver.onError(error)
            return
        }

        responseObserver.onNext(
            RegistrarChavePixResponse.newBuilder()
                .setIdCliente(chavePixCriada.idCliente)
                .setIdPix(chavePixCriada.id.toString())
                .build()
        )
        responseObserver.onCompleted()

    }
}

