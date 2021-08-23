package br.com.zup.rodrigoeduque.keymanager.registra

import br.com.zup.rodrigoeduque.ErrorDetails
import br.com.zup.rodrigoeduque.KeymanagerServiceGrpc
import br.com.zup.rodrigoeduque.RegistrarChavePixRequest
import br.com.zup.rodrigoeduque.RegistrarChavePixResponse
import br.com.zup.rodrigoeduque.keymanager.exceptions.ChaveExistenteException
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
        try {
            val chaveDto = request.toModel()
//        val chavePixCriada = chaveDto.toEntity() -> O código se tornou ilegivel sem usar o service

            val chavePixCriada = service.registrar(chaveDto)
            responseObserver.onNext(
                RegistrarChavePixResponse.newBuilder()
                    .setIdCliente(chavePixCriada.idCliente)
                    .setIdPix(chavePixCriada.chave)
                    .build()
            )
            responseObserver.onCompleted()

        } catch (e: InfoInvalidaException) {
            e.stackTrace
            responseObserver.onError(
                Status.INVALID_ARGUMENT
                    .withDescription(e.message)
                    .withCause(e.cause)
                    .asRuntimeException()
            )
        } catch (e: HttpClientResponseException) {
            e.stackTrace
            responseObserver.onError(
                Status.NOT_FOUND
                    .withDescription("${e.message} Identificador cliente Itau Inválido")
                    .withCause(e.cause)
                    .asRuntimeException()
            )
        } catch (e: ChaveExistenteException) {
            e.stackTrace
            responseObserver.onError(
                Status.ALREADY_EXISTS
                    .withDescription(e.message)
                    .withCause(e.cause)
                    .asRuntimeException()
            )
            return
        } catch (e: Throwable) {
            responseObserver.onError(
                Status.INTERNAL
                    .withDescription(e.message)
                    .withCause(e.cause)
                    .asRuntimeException()
            )
        }

    }
}

