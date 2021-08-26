package br.com.zup.rodrigoeduque.keymanager

import br.com.zup.rodrigoeduque.TipoChavePix
import br.com.zup.rodrigoeduque.TipoConta
import br.com.zup.rodrigoeduque.keymanager.utils.ValidaChavePix
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Entity
@ValidaChavePix
class Chave(
    @field:NotBlank @field:Column(nullable = false) val idCliente: String,
    @field:NotNull @field:Column(nullable = false) @Enumerated(EnumType.STRING) val tipoChave: TipoChavePix?,
    @field:NotBlank @field:Size(max = 77) @field:Column(nullable = false) var chave: String,
    @field:NotNull @field:Column(nullable = false) @Enumerated(EnumType.STRING) val tipoConta: TipoConta,
    @Embedded val conta: Conta
) {
    fun atualiza(key: String) {
        chave = key
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Chave

        if (idCliente != other.idCliente) return false
        if (tipoChave != other.tipoChave) return false
        if (tipoConta != other.tipoConta) return false
        if (conta != other.conta) return false

        return true
    }

    override fun hashCode(): Int {
        var result = idCliente.hashCode()
        result = 31 * result + (tipoChave?.hashCode() ?: 0)
        result = 31 * result + tipoConta.hashCode()
        result = 31 * result + conta.hashCode()
        return result
    }

    @Id
    @GeneratedValue
    val id: UUID? = null

    val dhCriacao: LocalDateTime = LocalDateTime.now()


}
