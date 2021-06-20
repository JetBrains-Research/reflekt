package io.reflekt.plugin.analysis.parametrizedtype

import io.reflekt.plugin.analysis.AnalysisSetupTest
import io.reflekt.plugin.analysis.AnalysisUtil
import io.reflekt.plugin.analysis.common.matchInto
import io.reflekt.plugin.analysis.models.ParameterizedType
import io.reflekt.plugin.analysis.models.ParameterizedTypeVariance
import io.reflekt.plugin.analysis.psi.KtDefaultVisitor
import io.reflekt.plugin.analysis.psi.function.*
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.resolve.BindingContext
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Tag
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.io.File

class ParametrizedTypeUtilTest {
    companion object {
//        @JvmStatic
//        fun getSimpleTypesMatches(): List<Arguments> {
//            return listOf(
//                // Everything matches with Any:
//                intPT to anyPT,
//                numberPT to anyPT,
//                // Everything matches with themself:
//                anyPT to anyPT,
//                intPT to intPT,
//                numberPT to numberPT,
//                // Type matches with its supertype:
//                intPT to numberPT
//            ).map { (k, v) -> Arguments.of(k, v) }
//        }
//
//        @JvmStatic
//        fun getNullableTypesMatches(): List<Arguments> {
//            return listOf(
//                // Non-nullable matches with nullable:
//                intPT to intPT.nullable(),
//                numberPT to anyPT.nullable(),
//                // Nullable matches with nullable:
//                anyPT.nullable() to anyPT.nullable(),
//                intPT.nullable() to numberPT.nullable(),
//                numberPT.nullable() to anyPT.nullable(),
//            ).map { (k, v) -> Arguments.of(k, v) }
//        }
//
//        @JvmStatic
//        fun getTypesWithParametersMatches(): List<Arguments> {
//            return listOf(
//                listPT(intPT) to listPT(numberPT),
//                listPT(numberPT) to listPT(starPT),
//                listPT(listPT(listPT(intPT))) to listPT(starPT),
//                pairPT(intPT, intPT) to pairPT(numberPT, anyPT),
//                pairPT(intPT.nullable(), numberPT) to pairPT(starPT, starPT),
//                arrayPT(intPT.withVariance(ParameterizedTypeVariance.OUT)) to arrayPT(numberPT.withVariance(ParameterizedTypeVariance.OUT)),
//                arrayPT(numberPT.nullable()) to arrayPT(starPT)
//            ).map { (k, v) -> Arguments.of(k, v) }
//        }


    }

//    @Tag("analysis")
//    @MethodSource("getSimpleTypesMatches")
//    @ParameterizedTest(name = "test {index}")
//    fun `simple types match test`(parameterizedType: ParameterizedType, parameterizedTypeToMatch: ParameterizedType) {
//        Assertions.assertTrue(parameterizedType.matchInto(parameterizedTypeToMatch))
//    }
//
//    @Tag("analysis")
//    @MethodSource("getNullableTypesMatches")
//    @ParameterizedTest(name = "test {index}")
//    fun `nullable types match test`(parameterizedType: ParameterizedType, parameterizedTypeToMatch: ParameterizedType) {
//        Assertions.assertTrue(parameterizedType.matchInto(parameterizedTypeToMatch))
//    }
//
//
//    @Tag("analysis")
//    @MethodSource("getTypesWithParametersMatches")
//    @ParameterizedTest(name = "test {index}")
//    fun `types with parameters match test`(parameterizedType: ParameterizedType, parameterizedTypeToMatch: ParameterizedType) {
//        Assertions.assertTrue(parameterizedType.matchInto(parameterizedTypeToMatch))
//    }
//
//    @Tag("analysis")
//    @MethodSource("getFunctionsTypesMatches")
//    @ParameterizedTest(name = "test {index}")
//    fun `function types match test`(parameterizedType: ParameterizedType, parameterizedTypeToMatch: ParameterizedType) {
//        Assertions.assertTrue(parameterizedType.matchInto(parameterizedTypeToMatch))
//    }
}





fun main() {
    val reflektClassPath = AnalysisSetupTest.getReflektProjectJars()
    val file = File("/Users/Elena.Lyulina/IdeaProjects/reflekt/reflekt-plugin/src/test/resources/io/reflekt/plugin/analysis/parameterizedType/Main.kt")
    val baseAnalyzer = AnalysisUtil.getBaseAnalyzer(classPath = reflektClassPath, sources = setOf(file))
    val visitor = MyVisitor(baseAnalyzer.binding)
    baseAnalyzer.ktFiles.forEach {
        it.acceptChildren(visitor)
    }
}

class MyVisitor(val binding: BindingContext) : KtDefaultVisitor() {
    override fun visitNamedFunction(function: KtNamedFunction) {
        println(function.toSignature(binding))
//        function.argumentTypes(binding).map { it.toParameterizedType() }.forEach { println(it) }
        super.visitNamedFunction(function)
    }
}


/**
 * 1. параметризованные типы переводятся в тип +список параметром
 * 2. непараметризованные типы переводятся в тип + пустой список параметров
 * 3. как выглядит инвариантный тип??
 * 4. у функций хочется сохранить структуру: расширение, дженерик, аргументы, возвращающий тип, для этого можно или собрать
 * - делать заглушки для пустых типов (или хранить null)
 * - сделать общий тип для параметров и не делать его списком хм
 *
 * хранить там итерабельную штуку по параметрам, но в случае функций это будет какой-то другой тип??
 *
 * надо правда понять, как звездочку трактовать везде.....
 * 1. +посмотреть, зачем вообще используется functionN когда можно просто вызывать toSignature везде
 * 2. +посмотреть, как строится toSignature и что она выдает когда мы строим функции
 *
 *
 *
 * новый план:
 * 1. сделать примеры параметризованных типов такими, какими они должны быть (в реальности пока не исправлять)
 * 2. подогнать matchInto под них
 *
 *
 *
 *
 * 1. + поправить супертипы, чтобы там считались все предыдущие типы тоже
 * 2. сделать пример, как должен выглядеть тип Int
 * 2. сделать тесты для функций и добавить заглушку
 *
 *
 * есть KotlinType, TypeProjection (там хранятся аргументы?), Descriptor, TypeParameterDescriptor
 */

