package br.com.zup.rodrigoeduque.keymanager.registra

import br.com.zup.rodrigoeduque.TipoChavePix
import br.com.zup.rodrigoeduque.TipoConta
import io.micronaut.core.annotation.Introspected
import javax.persistence.Enumerated
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Introspected
data class NovaChaveRequest(
    @field:NotBlank
    val idCliente: String,
    @field:Enumerated
    @field:NotNull
    val tipoChavePix: TipoChavePix,
    @field:NotBlank
    @field:Size(max = 77)
    val chave: String,
    @field:NotNull
    @field:NotBlank
    val tipoConta: TipoConta
)

