package org.jetbrains.reflekt.plugin.scripting

import org.jetbrains.reflekt.plugin.analysis.models.Import
import org.reflections.ReflectionUtils
import org.reflections.Reflections
import org.reflections.scanners.SubTypesScanner
import org.reflections.util.ConfigurationBuilder
import java.io.File
import java.lang.reflect.*
import java.net.URLClassLoader

/**
 * Checks if specified imports can be found in classpath to use tme in the KotlinScript runner
 *
 * @property allNames fully qualified names of public packages, classes, functions and properties in classpath
 * */
@Suppress("ConvertSecondaryConstructorToPrimary", "KDOC_NO_CLASS_BODY_PROPERTIES_IN_HEADER", "KDOC_EXTRA_PROPERTY")
class ImportChecker {
    private val allNames = HashSet<String>()

    constructor(classpath: List<File>) {
        if (classpath.isEmpty()) {
            return
        }

        val urls = classpath.map { it.toURI().toURL() }
        val classLoader = URLClassLoader(urls.toTypedArray())
        // Scan each class in classpath
        val reflections = Reflections(
            ConfigurationBuilder()
                .addClassLoader(classLoader)
                .setUrls(urls)
                .setScanners(SubTypesScanner(false)),
        )

        /*
         * Reflection is used at the compile-time with the compileClasspath configuration imports
         * and we can catch some unexpected errors like SecurityException or NoClassDefFoundError
         * since some classes can not be loaded at the compile time for an unexpected reason.
         * This can happen if, for example, the included libraries use other libraries
         * that may not be available at compile time.
         */
        @Suppress("TooGenericExceptionCaught")
        // Get all classes (each class is subtype of java.lang.Object)
        reflections.getSubTypesOf(Object::class.java)
            // Only public classes can be imported
            .filter { Modifier.isPublic(it.modifiers) }
            .filter { it.hasCanonicalName() }
            .forEach { clazz ->
                // Full package may be imported
                allNames.add(clazz.packageName)
                // Class may be imported
                allNames.add(clazz.canonicalName)

                clazz.saveMethodsAndFields()
            }
    }

    /**
     * Save possible methods and fields that can be imported in the project
     *  (including top-level fields and functions and they from companion objects)
     */
    private fun <T> Class<out T>.saveMethodsAndFields() {
        val methods = this.publicMethods()
        val fields = this.publicFields()

        // Save method and field names with specified prefix
        val addMembers: (prefix: String) -> Unit = { prefix ->
            allNames.addWithPrefix(methods, prefix)
            allNames.addWithPrefix(fields, prefix)
        }

        // Class methods/fields may be imported
        addMembers(this.canonicalName)
        if (this.isTopLevel()) {
            addMembers(this.packageName)
        }
        if (this.isInCompanionObject()) {
            addMembers(this.enclosingClass.canonicalName)
        }
    }

    /**
     * Check if the class was resolved and has a canonical name
     *
     * @return {@code true} if the class has a resolved canonicalName
     */
    @Suppress("SwallowedException", "TooGenericExceptionCaught")
    private fun <T> Class<out T>.hasCanonicalName() = try {
        this.canonicalName != null
    } catch (e: Throwable) {
        // if canonical name cannot be resolved
        false
    }

    /**
     * Add each member from [members] with the [prefix] into the set
     *
     * @param members each member should implement [Member] from the java.lang.reflect
     *  package and has the name property, e.g. [Method] or [Field]
     * @param prefix
     */
    private fun HashSet<String>.addWithPrefix(members: Set<Member>, prefix: String) =
        this.addAll(members.map { member -> "$prefix.${member.name}" })

    /**
     * Get all public methods of the class
     *
     * @return set of [Method]
     */
    @Suppress("SwallowedException", "TooGenericExceptionCaught")
    private fun <T> Class<out T>.publicMethods() = try {
        ReflectionUtils.getAllMethods(this, ReflectionUtils.withModifier(Modifier.PUBLIC))
    } catch (e: Throwable) {
        emptySet<Method>()
    }

    /**
     * Get all public fields of the class
     *
     * @return set of [Field]
     */
    @Suppress("SwallowedException", "TooGenericExceptionCaught")
    private fun <T> Class<out T>.publicFields() = try {
        ReflectionUtils.getAllFields(this, ReflectionUtils.withModifier(Modifier.PUBLIC))
    } catch (e: Throwable) {
        emptySet<Field>()
    }

    /**
     * Check if the class is a top-level function or a property
     *
     * @return {@code true} if the class is a top-level function or a property
     */
    private fun <T> Class<out T>.isTopLevel() = this.enclosingClass == null && this.simpleName.endsWith("Kt")

    /**
     * Check if the class is a function or property from a companion object
     *
     * @return {@code true} if the class is a function or property from a companion object
     */
    private fun <T> Class<out T>.isInCompanionObject() = this.enclosingClass != null && this.simpleName == "Companion"

    /**
     * Checks if specified import may be found in the classpath
     *
     * @param import
     * @return {@code true} if the set of [allNames] contains the fully qualified name of the [import]
     */
    private fun checkImport(import: Import) = import.fqName in allNames

    /**
     * For each import checks if this import may be found in the classpath
     *
     * @param imports list of imports
     * @return list of imports that may be found in the classpath
     */
    fun filterImports(imports: List<Import>) = imports.filter { checkImport(it) }
}
