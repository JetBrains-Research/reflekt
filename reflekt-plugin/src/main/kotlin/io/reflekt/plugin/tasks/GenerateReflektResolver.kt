package io.reflekt.plugin.tasks

import io.reflekt.plugin.analysis.ReflektAnalyzer
import io.reflekt.plugin.analysis.ReflektUses
import io.reflekt.plugin.analysis.SubTypesToAnnotations
import io.reflekt.plugin.dsl.reflekt
import io.reflekt.plugin.generator.ReflektImplGenerator
import io.reflekt.plugin.utils.Groups
import io.reflekt.plugin.utils.compiler.EnvironmentManager
import io.reflekt.plugin.utils.compiler.ParseUtil
import io.reflekt.plugin.utils.compiler.ResolveUtil
import io.reflekt.plugin.utils.myKtSourceSet
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.*
import java.io.File

open class GenerateReflektResolver : DefaultTask() {
    init {
        group = Groups.reflekt
    }

    @get:OutputDirectory
    val generationPath: File
        get() = reflekt.generationPathOrDefault(project)

    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    val myAllSources: Set<File>
        get() = project.myKtSourceSet.toSet()

    @get:InputFiles
    val classPath: Set<File>
        get() = project.configurations.getByName("runtimeClasspath").files

    @TaskAction
    fun act() {
        val environment = EnvironmentManager.create(classPath)
        val ktFiles = ParseUtil.analyze(myAllSources, environment)
        val resolved = ResolveUtil.analyze(ktFiles, environment)

        val analyzer = ReflektAnalyzer(ktFiles, resolved.bindingContext)
        val invokes = analyzer.invokes()
        // TODO implements uses processors
//         val uses = analyzer.uses(invokes)

        val s1 = setOf("subTypeFqName1", "subTypeFqName2")
        val s2 = setOf("subTypeFqName3", "subTypeFqName4")

        val a1 = setOf("annotationFqName1", "annotationFqName2")
        val a2 = setOf("annotationFqName3", "annotationFqName4")

        val a3 = emptySet<String>()

        val uN1 = listOf("fqName1", "fqName2")
        val uN2 = listOf("fqName3", "fqName4")

        val map1 = mutableMapOf(a1 to mapOf(s1 to uN1, s2 to uN1), a2 to mapOf(s2 to uN1, s1 to uN2), a3 to mapOf(s1 to uN1, s2 to uN1))

        val uses = ReflektUses(
            objects = map1,
            classes = map1
        )

        with(File(generationPath, "io/reflekt/ReflektImpl.kt")) {
            delete()
            parentFile.mkdirs()
            writeText(
                // TODO-isomethane use analyzer
                ReflektImplGenerator(uses).generate()
            )
        }
    }
}
