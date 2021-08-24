package br.com.zup.rodrigoeduque.keymanager.utils

import javax.validation.Constraint
import javax.validation.ReportAsSingleViolation
import javax.validation.constraints.Pattern
import kotlin.annotation.AnnotationTarget.*
import kotlin.reflect.KClass

@ReportAsSingleViolation
@Constraint(validatedBy = [])
@Pattern(
    regexp = "^[0-9a-f]{8}-[0-9a-f]{4}-[0-5][0-9a-f]{3}-[089ab][0-9a-f]{3}-[0-9a-f]{12}\$",
    flags = [Pattern.Flag.CASE_INSENSITIVE]
)
@Retention(AnnotationRetention.RUNTIME)
@Target(
    CONSTRUCTOR,
    FIELD,
    PROPERTY,
    VALUE_PARAMETER
)
annotation class ValidaUUID(
    val message: String = "Formato Inválido para UUID",
    val groups: Array<KClass<Any>> = [],
    val payload: Array<KClass<Any>> = []
)
