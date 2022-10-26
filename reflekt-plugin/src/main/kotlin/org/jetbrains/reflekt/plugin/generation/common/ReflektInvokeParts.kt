package org.jetbrains.reflekt.plugin.generation.common

import org.jetbrains.reflekt.Reflekt
import org.jetbrains.reflekt.SmartReflekt
import org.jetbrains.reflekt.plugin.analysis.common.*
import org.jetbrains.reflekt.plugin.utils.enumToRegexOptions
import org.jetbrains.reflekt.plugin.utils.toEnum

/**
 * Stores Reflekt query parts.
 *
 * @property entityType a type of entities that should be retrieved (e.g. classes, objects, or functions).
 *  In other words, a type of function from the DSL that is called by the user (e.g. classes(), objects(), functions(), etc)
 */
sealed class BaseReflektInvokeParts(
    open val entityType: ReflektEntity,
)

// TODO: can we move the common functionality?

/**
 * Reflekt invoke expression has the following structure:
 * [1]Reflekt.[2]Classes/Objects/Functions.[3]WithSupertypes/WithAnnotations.[4]toList/toSet/etc
 * [entityType] stores part [2], [nestedClass] - part [3], [terminalFunction] - part [4].
 *
 * @property entityType
 * @property nestedClass
 * @property terminalFunction
 */
data class ReflektInvokeParts(
    override val entityType: ReflektEntity,
    val nestedClass: ReflektNestedClass,
    val terminalFunction: ReflektTerminalFunction,
) : BaseReflektInvokeParts(entityType) {
    companion object {
        /**
         * Builds a regular expression for a Reflekt query,
         * according to [ReflektEntity], [ReflektNestedClass], and [ReflektTerminalFunction].
         *
         * @return a regular expression to recognize the Reflekt query
         */
        private fun getReflektFullNameRegex(): Regex {
            val reflektFqName = Reflekt::class.qualifiedName!!
            val entityTypes = enumToRegexOptions(ReflektEntity::className)
            val nestedClasses = enumToRegexOptions(ReflektNestedClass::className)
            val terminalFunctions = enumToRegexOptions(ReflektTerminalFunction::functionName)
            return Regex("$reflektFqName\\.$entityTypes\\.$nestedClasses\\.$terminalFunctions")
        }

        /**
         * Parses the fully qualified name into [ReflektInvokeParts] by Reflekt regular expression.
         *
         * @param fqName
         * @return parsed query or null
         */
        fun parse(fqName: String): ReflektInvokeParts? {
            val matchResult = getReflektFullNameRegex().matchEntire(fqName) ?: return null
            val (_, klass, nestedClass, terminalFunction) = matchResult.groupValues
            return ReflektInvokeParts(
                klass.toEnum(ReflektEntity::className),
                nestedClass.toEnum(ReflektNestedClass::className),
                terminalFunction.toEnum(ReflektTerminalFunction::functionName),
            )
        }
    }
}

/**
 * SmartReflekt invoke expression has the following structure:
 * [1]SmartReflekt.[2]ClassCompileTimeExpression/ObjectCompileTimeExpression/FunctionCompileTimeExpression.[3]toList/toSet/etc
 * [entityType] stores part [2], [terminalFunction] - part [3]
 * @property entityType
 * @property terminalFunction
 */
data class SmartReflektInvokeParts(
    override val entityType: ReflektEntity,
    val terminalFunction: SmartReflektTerminalFunction,
) : BaseReflektInvokeParts(entityType) {
    companion object {
        /**
         * Build a regular expression for the Reflekt query
         * according to [ReflektEntity], and [SmartReflektTerminalFunction]
         *
         * @return a regular expression to recognize the SmartReflekt query
         */
        private fun getSmartReflektFullNameRegex(): Regex {
            val smartReflektFqName = SmartReflekt::class.qualifiedName!!
            val entityClasses = enumToRegexOptions(ReflektEntity::smartClassName)
            val terminalFunctions = enumToRegexOptions(SmartReflektTerminalFunction::functionName)
            return Regex("$smartReflektFqName\\.$entityClasses\\.$terminalFunctions")
        }

        /**
         * Parse the fully qualified name into [SmartReflektInvokeParts] by the Reflekt regular expression
         *
         * @param fqName
         * @return parsed query or null
         */
        fun parse(fqName: String): SmartReflektInvokeParts? {
            val matchResult = getSmartReflektFullNameRegex().matchEntire(fqName) ?: return null
            val (_, entityClass, terminalFunction) = matchResult.groupValues
            return SmartReflektInvokeParts(
                entityClass.toEnum(ReflektEntity::smartClassName),
                terminalFunction.toEnum(SmartReflektTerminalFunction::functionName),
            )
        }
    }
}
