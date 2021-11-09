package io.reflekt.plugin.analysis.analyzer

import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingContext

/**
 * @property ktFiles
 * @property binding
 */
open class BaseAnalyzer(open val ktFiles: Set<KtFile>, open val binding: BindingContext)
