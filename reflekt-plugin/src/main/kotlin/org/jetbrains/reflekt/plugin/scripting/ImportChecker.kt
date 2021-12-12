package org.jetbrains.reflekt.plugin.scripting

import org.jetbrains.reflekt.plugin.analysis.models.Import
import org.reflections.ReflectionUtils
import org.reflections.Reflections
import org.reflections.scanners.SubTypesScanner
import org.reflections.util.ConfigurationBuilder
import java.io.File
import java.lang.reflect.*
import java.net.URLClassLoader

/* Checks if specified imports can be found in classpath. */
@Suppress("ConvertSecondaryConstructorToPrimary")
class ImportChecker {
    /* Fully qualified names of public packages, classes, functions and properties in classpath */
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

        // Get all classes (each class is subtype of java.lang.Object)
        // FIXME: logic should be revised here, as caught exception is too generic (what kind of exception was intended to catch?)
        @Suppress("SwallowedException", "TooGenericExceptionCaught")
        reflections.getSubTypesOf(Object::class.java)
            // Only public classes can be imported
            .filter { Modifier.isPublic(it.modifiers) }
            .filter {
                @Suppress("AVOID_NULL_CHECKS")
                if (it == null) false else it.canonicalName != null
            }
            .forEach { clazz ->
                // All public methods of class
                val methods = try {
                    ReflectionUtils.getAllMethods(clazz, ReflectionUtils.withModifier(Modifier.PUBLIC))
                } catch (e: Throwable) {
                    emptySet<Method>()
                }

                // All public fields of class
                val fields = try {
                    ReflectionUtils.getAllFields(clazz, ReflectionUtils.withModifier(Modifier.PUBLIC))
                } catch (e: Throwable) {
                    emptySet<Field>()
                }

                // Save method and field names with specified prefix
                val addMembers: (prefix: String) -> Unit = { prefix ->
                    allNames.addAll(methods.map { method -> "$prefix.${method.name}" })
                    allNames.addAll(fields.map { field -> "$prefix.${field.name}" })
                }

                // Full package may be imported
                allNames.add(clazz.packageName)
                // Class may be imported
                allNames.add(clazz.canonicalName)
                // Class methods/fields may be imported
                addMembers(clazz.canonicalName)

                // Top-level functions and properties
                if (clazz.enclosingClass == null && clazz.simpleName.endsWith("Kt")) {
                    addMembers(clazz.packageName)
                }

                // Functions and properties in companion object
                if (clazz.enclosingClass != null && clazz.simpleName == "Companion") {
                    addMembers(clazz.enclosingClass.canonicalName)
                }
            }
    }

    // Checks if specified import may be found in the classpath.
    private fun checkImport(import: Import) = import.fqName in allNames

    // Returns imports that may be found in the classpath.
    fun filterImports(imports: List<Import>) = imports.filter { checkImport(it) }
}
