package io.reflekt.plugin.generation.common

import io.reflekt.Reflekt
import io.reflekt.SmartReflekt
import io.reflekt.plugin.analysis.common.*
import io.reflekt.plugin.utils.enumToRegexOptions
import io.reflekt.plugin.utils.toEnum

sealed class BaseReflektInvokeParts(
    open val entityType: ReflektEntity
)

/*
 * Reflekt invoke expression has the following structure:
 * [1]Reflekt.[2]Classes/Objects/Functions.[3]WithSubTypes/WithAnnotations.[4]toList/toSet/etc
 * entityType stores part [2], nestedClass - part [3], terminalFunction - part [4]
 */
data class ReflektInvokeParts(
    override val entityType: ReflektEntity,
    val nestedClass: ReflektNestedClass,
    val terminalFunction: ReflektTerminalFunction
) : BaseReflektInvokeParts(entityType) {
    companion object {
        private fun getReflektFullNameRegex(): Regex {
            val reflektFqName = Reflekt::class.qualifiedName!!
            val entityTypes = enumToRegexOptions(ReflektEntity.values(), ReflektEntity::className)
            val nestedClasses = enumToRegexOptions(ReflektNestedClass.values(), ReflektNestedClass::className)
            val terminalFunctions = enumToRegexOptions(ReflektTerminalFunction.values(), ReflektTerminalFunction::functionName)
            return Regex("$reflektFqName\\.$entityTypes\\.$nestedClasses\\.$terminalFunctions")
        }

        fun parse(fqName: String): ReflektInvokeParts? {
            val matchResult = getReflektFullNameRegex().matchEntire(fqName) ?: return null
            val (_, klass, nestedClass, terminalFunction) = matchResult.groupValues
            return ReflektInvokeParts(
                klass.toEnum(ReflektEntity.values(), ReflektEntity::className),
                nestedClass.toEnum(ReflektNestedClass.values(), ReflektNestedClass::className),
                terminalFunction.toEnum(ReflektTerminalFunction.values(), ReflektTerminalFunction::functionName)
            )
        }
    }
}

/*
 * SmartReflekt invoke expression has the following structure:
 * [1]SmartReflekt.[2]ClassCompileTimeExpression/ObjectCompileTimeExpression/FunctionCompileTimeExpression.[3]toList/toSet/etc
 * entityType stores part [2], terminalFunction - part [3]
 */
data class SmartReflektInvokeParts(
    override val entityType: ReflektEntity,
    val terminalFunction: SmartReflektTerminalFunction
) : BaseReflektInvokeParts(entityType) {
    companion object {
        private fun getSmartReflektFullNameRegex(): Regex {
            val smartReflektFqName = SmartReflekt::class.qualifiedName!!
            val entityClasses = enumToRegexOptions(ReflektEntity.values(), ReflektEntity::smartClassName)
            val terminalFunctions = enumToRegexOptions(SmartReflektTerminalFunction.values(), SmartReflektTerminalFunction::functionName)
            return Regex("$smartReflektFqName\\.$entityClasses\\.$terminalFunctions")
        }

        fun parse(fqName: String): SmartReflektInvokeParts? {
            val matchResult = getSmartReflektFullNameRegex().matchEntire(fqName) ?: return null
            val (_, entityClass, terminalFunction) = matchResult.groupValues
            return SmartReflektInvokeParts(
                entityClass.toEnum(ReflektEntity.values(), ReflektEntity::smartClassName),
                terminalFunction.toEnum(SmartReflektTerminalFunction.values(), SmartReflektTerminalFunction::functionName)
            )
        }
    }
}
