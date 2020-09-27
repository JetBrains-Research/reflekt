package io.reflekt.plugin.tasks

import io.reflekt.plugin.analysis.FunctionsFqNames
import io.reflekt.plugin.analysis.ReflektAnalyzer
import io.reflekt.plugin.generator.getWhenBodyForInvokes
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
        get() = project.configurations.getByName("runtimeClasspath").files

    @TaskAction
    fun act() {
        val environment = EnvironmentManager.create(classPath)
        val ktFiles = ParseUtil.analyze(myAllSources, environment)
        val resolved = ResolveUtil.analyze(ktFiles, environment)

        val analyzer = ReflektAnalyzer(ktFiles, resolved.bindingContext)
        val invokes = analyzer.invokes(FunctionsFqNames.getReflektNames())

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
                            fun <T> withSubType(fqName: String) = Objects.WithSubType<T>(fqName)
                    
                            class WithSubType<T>(val fqName: String) {
                                fun toList(): List<T> = when(fqName) {
                                    ${getWhenBodyForInvokes(invokes.withSubTypeObjects, analyzer::objects, " as T")}
                                    else -> error("Unknown fqName")
                                }
                                fun toSet(): Set<T> = toList().toSet()
                                
                                class WithAnnotation<T>(private val fqName: String, val withSubtypeFqName: String) {
                                    fun toList(): List<T> = when(withSubtypeFqName) {
                                        ${getWhenBodyForInvokes(invokes.withAnnotationObjects, analyzer::objects, " as T")}
                                        else -> error("Unknown fqName")
                                    }
                                    fun toSet(): Set<T> = toList().toSet()
                                }
                    
                                fun <T> withAnnotation(fqName: String, withSubtypeFqName: String) = WithAnnotation<T>(fqName, withSubtypeFqName)
                            }
                        }
                    
                        class Classes {
                            fun <T: Any> withSubType(fqName: String) = Classes.WithSubType<T>(fqName)
                    
                            class WithSubType<T: Any>(val fqName: String) {
                                fun toList(): List<KClass<T>> = when(fqName) {
                                    ${getWhenBodyForInvokes(invokes.withSubTypeClasses, analyzer::classes, "::class as KClass<T>")}
                                    else -> error("Unknown fqName")
                                }
                                fun toSet(): Set<KClass<T>> = toList().toSet()
                                
                                class WithAnnotation<T: Annotation>(private val fqName: String, val withSubtypeFqName: String) {
                                    fun toList(): List<T> = when(withSubtypeFqName) {
                                        ${getWhenBodyForInvokes(invokes.withAnnotationClasses, analyzer::objects, "::class as KClass<T>")}
                                        else -> error("Unknown fqName")
                                    }
                                    fun toSet(): Set<T> = toList().toSet()
                                }
                    
                                fun <T: Annotation> withAnnotation(fqName: String, withSubtypeFqName: String) = WithAnnotation<T>(fqName, withSubtypeFqName)
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
