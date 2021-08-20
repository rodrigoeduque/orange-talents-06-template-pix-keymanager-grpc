package br.com.zup.rodrigoeduque.keymanager

import br.com.zup.rodrigoeduque.TipoChavePix
import br.com.zup.rodrigoeduque.TipoConta
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Entity
class Chave(
    @field:NotBlank @field:Column(nullable = false) val idCliente: String,
    @field:NotNull @field:Column(nullable = false) @Enumerated(EnumType.STRING) val tipoChave: TipoChavePix?,
    @field:NotBlank @field:Size(max = 77) @field:Column(nullable = false) val chave: String,
    @field:NotNull @field:Column(nullable = false) @Enumerated(EnumType.STRING) val tipoConta: TipoConta,
    @Embedded val conta: Conta
) {
    @Id
    @GeneratedValue
    val id: UUID? = null

    val dhCriacao: LocalDateTime = LocalDateTime.now()

    override fun toString(): String {
        return "Chave(idCliente='$idCliente', tipoChave=$tipoChave, chave='$chave', tipoConta=$tipoConta, id=$id, dhCriacao=$dhCriacao)"
    }
}
