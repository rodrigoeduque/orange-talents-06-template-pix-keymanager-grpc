package br.com.zup.rodrigoeduque.keymanager.registra

import br.com.zup.rodrigoeduque.TipoChavePix
import br.com.zup.rodrigoeduque.TipoConta
import br.com.zup.rodrigoeduque.keymanager.utils.ValidaChavePix
import br.com.zup.rodrigoeduque.keymanager.utils.ValidaUUID
import io.micronaut.core.annotation.Introspected
import javax.persistence.Enumerated
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Introspected
@ValidaChavePix
data class NovaChaveRequest(
    @field:NotBlank
    @field:ValidaUUID
    val idCliente: String,
    @field:Enumerated
    @field:NotNull
    val tipoChavePix: TipoChavePix,
    @field:NotBlank
    @field:Size(max = 77)
    @ValidaChavePix
    val chave: String,
    @field:NotNull
    @field:NotBlank
    val tipoConta: TipoConta
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NovaChaveRequest

        if (tipoChavePix != other.tipoChavePix) return false

        return true
    }

    override fun hashCode(): Int {
        return tipoChavePix.hashCode()
    }
}

