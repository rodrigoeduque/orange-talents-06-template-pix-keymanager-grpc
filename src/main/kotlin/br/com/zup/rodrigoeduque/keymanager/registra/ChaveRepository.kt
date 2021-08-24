package br.com.zup.rodrigoeduque.keymanager.registra

import br.com.zup.rodrigoeduque.keymanager.Chave
import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository
import java.util.*

@Repository
interface ChaveRepository : JpaRepository<Chave, UUID> {
    abstract fun existsByChave(chave: String): Boolean
    abstract fun findByIdAndIdCliente(idPix: UUID, idcliente: String): Optional<Chave>

}
