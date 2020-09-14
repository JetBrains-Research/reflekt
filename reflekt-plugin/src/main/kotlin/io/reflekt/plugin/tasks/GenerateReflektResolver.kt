package io.reflekt.plugin.tasks

import io.reflekt.plugin.analysis.ReflektAnalyzer
import io.reflekt.plugin.dsl.reflekt
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
        get() = project.configurations.getByName("runtimeClasspath").files()

    @TaskAction
    fun act() {
        val environment = EnvironmentManager.create(classPath)
        val ktFiles = ParseUtil.analyze(myAllSources, environment)
        val resolved = ResolveUtil.analyze(ktFiles, environment)

        val analyzer = ReflektAnalyzer(ktFiles, resolved.bindingContext)


        with(File(generationPath, "io/reflekt/ReflektImpl.kt")) {
            delete()
            parentFile.mkdirs()
            writeText(
                //language=kotlin
                """
                    package io.reflekt
                    
                    import kotlin.reflect.KClass
                    
                    object ReflektImpl {
                        class Objects {
                            fun <T> withSubType() = Objects.WithSubType<T>()
                    
                            class WithSubType<T> {
                                fun toList(): List<T> = listOf(${analyzer.objects("io.reflekt.example.AInterface").joinToString { it.fqName.toString() + " as T" }})
                                fun toSet(): Set<T> = toList().toSet()
                            }
                        }
                    
                        class Classes {
                            fun <T: Any> withSubType() = Classes.WithSubType<T>()
                    
                            class WithSubType<T: Any> {
                                fun toList(): List<KClass<T>> = listOf(${analyzer.classes("io.reflekt.example.BInterface").joinToString { it.fqName.toString() + "::class as KClass<T>" }})
                                fun toSet(): Set<KClass<T>> = toList().toSet()
                            }
                        }
                    
                        fun objects() = Objects()
                    
                        fun classes() = Classes()
                    }
                """.trimIndent()
            )
        }
    }
}
