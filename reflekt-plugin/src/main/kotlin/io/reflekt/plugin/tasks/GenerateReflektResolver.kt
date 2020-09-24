package io.reflekt.plugin.tasks

import io.reflekt.Reflekt
import io.reflekt.plugin.analysis.FunctionsFqNames
import io.reflekt.plugin.analysis.ReflektAnalyzer
import io.reflekt.plugin.dsl.reflekt
import io.reflekt.plugin.utils.Groups
import io.reflekt.plugin.utils.compiler.EnvironmentManager
import io.reflekt.plugin.utils.compiler.ParseUtil
import io.reflekt.plugin.utils.compiler.ResolveUtil
import io.reflekt.plugin.utils.myKtSourceSet
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.*
import org.jetbrains.kotlin.psi.KtClassOrObject
import java.io.File
import kotlin.reflect.KFunction1

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

    private fun getInvokedElements(fqName: String, analyzer: KFunction1<Array<out String>, Set<KtClassOrObject>>, asSuffix: String) = analyzer(arrayOf(fqName)).joinToString { "${it.fqName.toString()}$asSuffix" }

    private fun getFqNamesWithInvokedElements(fqNameList: List<String>, analyzer: KFunction1<Array<out String>, Set<KtClassOrObject>>, asSuffix: String): String {
        val builder = StringBuilder()
        fqNameList.forEach {
            builder.append("\"$it\" -> listOf(${getInvokedElements(it, analyzer, asSuffix)})\n")
        }
        return builder.toString().removeSuffix("\n").trimIndent()
    }

    @TaskAction
    fun act() {
        val environment = EnvironmentManager.create(classPath)
        val ktFiles = ParseUtil.analyze(myAllSources, environment)
        val resolved = ResolveUtil.analyze(ktFiles, environment)

        val analyzer = ReflektAnalyzer(ktFiles, resolved.bindingContext)
        // Todo: Can I get full name automatically?
        val (fqNameWithSubTypeListObjects, fqNameWithSubTypeListClasses) = analyzer.invokes(FunctionsFqNames.getReflektNames())
        val fqNameWithAnnotationListObjects = mapOf("io.reflekt.example.AInterface" to listOf("io.reflekt.example.FirstAnnotation", "io.reflekt.example.SecondAnnotation"),
            "io.reflekt.example.BInterface" to listOf("io.reflekt.example.SecondAnnotation")
        )

//        fqNameWithAnnotationListObjects.forEach{ withSubTypeFqName, withAnnotationList ->
//
//        }

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
                                    ${getFqNamesWithInvokedElements(fqNameWithSubTypeListObjects, analyzer::objects, " as T")}
                                    else -> error("Unknown fqName")
                                }
                                fun toSet(): Set<T> = toList().toSet()
                                
                                class WithAnnotation<T>(private val fqName: String, val withSubtypeFqName: String) {
                                    fun toList(): List<T> = when(withSubtypeFqName) {
                                        "io.reflekt.example.AInterface" -> {
                                            when (fqName) {
                                                "io.reflekt.example.FirstAnnotation" -> listOf(io.reflekt.example.A3 as T)
                                                "io.reflekt.example.SecondAnnotation" -> listOf(io.reflekt.example.A2 as T)
                                                else -> error("Unknown fqName")
                                            }
                                        }
                                        "io.reflekt.example.BInterface" -> {
                                            when (fqName) {
                                                "io.reflekt.example.SecondAnnotation" -> listOf(io.reflekt.example.A4 as T)
                                                else -> error("Unknown fqName")
                                            }
                                        }
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
                                    ${getFqNamesWithInvokedElements(fqNameWithSubTypeListClasses, analyzer::classes, "::class as KClass<T>")}
                                    else -> error("Unknown fqName")
                                }
                                fun toSet(): Set<KClass<T>> = toList().toSet()
                                
                                class WithAnnotation<T: Annotation>(private val fqName: String) {
                                    fun toList(): List<T> = error("This method should be replaced during compilation")
                                    fun toSet(): Set<T> = toList().toSet()
                                }
                    
                                fun <T: Annotation> withAnnotation(fqName: String) = WithAnnotation<T>(fqName)
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
