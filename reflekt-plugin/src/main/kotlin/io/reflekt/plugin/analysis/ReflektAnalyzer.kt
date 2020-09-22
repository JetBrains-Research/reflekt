package io.reflekt.plugin.analysis

import io.reflekt.plugin.analysis.psi.isSubtypeOf
import io.reflekt.plugin.analysis.psi.visitClass
import io.reflekt.plugin.analysis.psi.visitKtDotQualifiedExpression
import io.reflekt.plugin.analysis.psi.visitObject
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.resolve.BindingContext

class ReflektAnalyzer(private val ktFiles: Set<KtFile>, private val binding: BindingContext) {
    fun objects(vararg subtypes: String): Set<KtObjectDeclaration> {
        val objects = HashSet<KtObjectDeclaration>()
        val subtypesSet = subtypes.toSet()
        val test = invokes()
        for (it in ktFiles) {
            it.visitObject {
                if (it.isSubtypeOf(subtypesSet, binding)) {
                    objects.add(it)
                }
            }
        }
        return objects
    }

    fun classes(vararg subtypes: String): Set<KtClass> {
        val classes = HashSet<KtClass>()
        val subtypesSet = subtypes.toSet()
        for (it in ktFiles) {
            it.visitClass {
                if (it.isSubtypeOf(subtypesSet, binding)) {
                    classes.add(it)
                }
            }
        }
        return classes
    }

    fun invokes(): Set<String> {
        val invokes = HashSet<String>()
        for (it in ktFiles) {
            println(it.name)
            it.visitKtDotQualifiedExpression(binding)
        }
        return invokes
    }
}
