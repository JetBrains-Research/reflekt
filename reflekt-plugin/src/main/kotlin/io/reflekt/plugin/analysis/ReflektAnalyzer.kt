package io.reflekt.plugin.analysis

import io.reflekt.plugin.analysis.psi.isSubtypeOf
import io.reflekt.plugin.analysis.psi.visitClass
import io.reflekt.plugin.analysis.psi.visitObject
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.resolve.BindingContext

class ReflektAnalyzer(private val ktFiles: Set<KtFile>, private val binding: BindingContext) {
    fun objects(vararg subtypes: String): Set<KtObjectDeclaration> {
        val objects = HashSet<KtObjectDeclaration>()
        for (it in ktFiles) {
            it.visitObject {
                if (it.isSubtypeOf(subtypes.toSet(), binding)) {
                    objects.add(it)
                }
            }
        }
        return objects
    }

    fun classes(vararg subtypes: String): Set<KtClass> {
        val classes = HashSet<KtClass>()
        for (it in ktFiles) {
            it.visitClass {
                if (it.isSubtypeOf(subtypes.toSet(), binding)) {
                    classes.add(it)
                }
            }
        }
        return classes
    }
}
