package br.com.zup.rodrigoeduque.keymanager.utils

import br.com.zup.rodrigoeduque.TipoChavePix
import br.com.zup.rodrigoeduque.keymanager.Chave
import io.micronaut.core.annotation.AnnotationValue
import io.micronaut.validation.validator.constraints.ConstraintValidator
import io.micronaut.validation.validator.constraints.ConstraintValidatorContext
import jakarta.inject.Singleton
import javax.validation.Constraint
import javax.validation.Payload
import kotlin.reflect.KClass


@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FIELD)
@Constraint(validatedBy = [PixValidator::class])


annotation class ValidaChavePix(
    val message: String = "Chave Pix invalida",
    val groups: Array<KClass<Any>> = [],
    val payload: Array<KClass<Payload>> = []
)


@Singleton
class PixValidator : ConstraintValidator<ValidaChavePix, Chave> {
    override fun isValid(
        value: Chave?,
        annotationMetadata: AnnotationValue<ValidaChavePix>,
        context: ConstraintValidatorContext
    ): Boolean {
        return when {
            value?.tipoChave == TipoChavePix.CPF -> {
                value.chave.matches("^[0-9]{11}\$".toRegex())
            }
            value?.tipoChave == TipoChavePix.EMAIL -> {
                value.chave.matches("^[a-z0-9.]+@[a-z0-9]+\\.[a-z]+(\\.[a-z]+)?\$".toRegex())
            }
            value?.tipoChave == TipoChavePix.CELULAR -> {
                value.chave.matches("^\\+[1-9][0-9]\\d{1,14}\$".toRegex())
            }
//            value?.chave.isNullOrEmpty() -> {
//                return true
//            }
            else -> {
                true
            }
        }
    }


}
