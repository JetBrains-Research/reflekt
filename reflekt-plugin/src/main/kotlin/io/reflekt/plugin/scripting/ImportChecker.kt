package io.reflekt.plugin.scripting

import io.reflekt.plugin.analysis.models.Import
import org.jetbrains.kotlin.descriptors.runtime.components.tryLoadClass
import org.reflections.ReflectionUtils
import org.reflections.Reflections
import org.reflections.scanners.SubTypesScanner
import org.reflections.util.ClasspathHelper
import org.reflections.util.ConfigurationBuilder
import java.io.File
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.net.URLClassLoader

/* Checks if specified imports can be found in classpath. */
class ImportChecker(classpath: List<File>) {
    /* Fully qualified names of public packages, classes, functions and properties in classpath */
    private val allNames = HashSet<String>()

    init {
        val urls = classpath.map { it.toURI().toURL() }
        val classLoader = URLClassLoader(urls.toTypedArray())
        val reflections = Reflections(
            ConfigurationBuilder()
                .addClassLoader(classLoader)
                .setUrls(urls)
                .setScanners(SubTypesScanner(false))
        )

        reflections.getSubTypesOf(Object::class.java)
            .filter { Modifier.isPublic(it.modifiers) }
            .filter {
                try {
                    it.canonicalName != null
                } catch (e: Throwable) {
                    false
                }
            }
            .forEach { clazz ->
                val methods = try {
                    ReflectionUtils.getAllMethods(clazz, ReflectionUtils.withModifier(Modifier.PUBLIC))
                } catch (e: Throwable) {
                    emptySet<Method>()
                }
                val fields = try {
                    ReflectionUtils.getAllFields(clazz, ReflectionUtils.withModifier(Modifier.PUBLIC))
                } catch (e: Throwable) {
                    emptySet<Field>()
                }

                val addMembers: (prefix: String) -> Unit = { prefix ->
                    allNames.addAll(methods.map { method -> "$prefix.${method.name}" })
                    allNames.addAll(fields.map { field -> "$prefix.${field.name}" })
                }

                allNames.add(clazz.packageName)
                allNames.add(clazz.canonicalName)
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

    fun checkImport(import: Import) = import.fqName in allNames

    fun filterImports(imports: List<Import>) = imports.filter { checkImport(it) }
}
