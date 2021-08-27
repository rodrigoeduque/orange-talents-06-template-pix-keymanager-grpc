package br.com.zup.rodrigoeduque.keymanager.registra

import br.com.zup.rodrigoeduque.KeymanagerServiceGrpc
import br.com.zup.rodrigoeduque.RegistrarChavePixRequest
import br.com.zup.rodrigoeduque.RegistrarChavePixResponse
import br.com.zup.rodrigoeduque.keymanager.Chave
import br.com.zup.rodrigoeduque.keymanager.exceptions.ChaveExistenteException
import br.com.zup.rodrigoeduque.keymanager.exceptions.ClienteNaoIdentificadoException
import io.grpc.Status
import io.grpc.stub.StreamObserver
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.validation.Validated
import jakarta.inject.Inject
import jakarta.inject.Singleton
import javax.validation.ConstraintViolationException

@Validated
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

            responseObserver.onNext(
                RegistrarChavePixResponse.newBuilder()
                    .setIdCliente(chavePixCriada.idCliente)
                    .setIdPix(chavePixCriada.id.toString())
                    .build()
            )
            responseObserver.onCompleted()


        } catch (e: ClienteNaoIdentificadoException) {
            e.stackTrace
            val error = Status.NOT_FOUND
                .withDescription(e.message)
                .asRuntimeException()
            responseObserver.onError(error)
            return
        } catch (e: ChaveExistenteException) {
            e.stackTrace
            val error = Status.ALREADY_EXISTS
                .withDescription(e.message)
                .asRuntimeException()
            responseObserver.onError(error)
            return
        } catch (e: IllegalArgumentException) {
            e.stackTrace
            val error = Status.INVALID_ARGUMENT
                .withDescription(e.message)
                .asRuntimeException()
            responseObserver.onError(error)
            return
        } catch (e: ConstraintViolationException) {
            e.stackTrace
            val error = Status.INVALID_ARGUMENT
                .withDescription(e.message)
                .withCause(e.cause)
                .asRuntimeException()
            responseObserver.onError(error)
            return
        } catch (e: HttpClientResponseException) {
            e.stackTrace
            val error = Status.NOT_FOUND
                .withDescription(e.message)
                .withCause(e.cause)
                .asRuntimeException()
            responseObserver.onError(error)
            return
        }

    }
}

