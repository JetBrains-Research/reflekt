package io.reflekt.plugin.analysis.processor

import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.isAbstract
import org.jetbrains.kotlin.psi.psiUtil.isPublic

internal val KtElement.isPublicObject: Boolean
    get() = this is KtObjectDeclaration && this.isPublic

internal val KtElement.isPublicFunction: Boolean
    get() = this is KtNamedFunction && this.isPublic

internal val KtElement.isTopLevelPublicFunction: Boolean
    get() = isPublicFunction && this is KtNamedFunction && this.isTopLevel

internal val KtElement.isMainFunction: Boolean
    get() = isPublicFunction && (this as KtNamedFunction).nameIdentifier?.let { it.text == "main" } ?: false

internal val KtElement.isPublicNotAbstractClass: Boolean
    get() = this is KtClass && this.isPublic && !this.isAbstract()

typealias FileID = String

internal fun getFullName(packageFqName: FqName, name: String? = null) : FileID {
    val postfix = name?.let{ ".${name}" } ?: ""
    return "${packageFqName.asString()}$postfix"
}

// TODO: is it enough to identify a file?
internal val KtFile.fullName: FileID
    get() = getFullName(this.packageFqName, this.name)

internal val DeclarationDescriptor.isPublicNotAbstractClass: Boolean
    get() = this is ClassDescriptor && this.isClass && !this.isAbstractClass && this.isPublic

internal val DeclarationDescriptor.isPublicObject: Boolean
    get() = this is ClassDescriptor && this.isObject && this.isPublic

internal val DeclarationDescriptor.isPublicTopLevelFunction: Boolean
    get() = this is FunctionDescriptor && this.isPublic && this.isTopLevelFunction

internal val DeclarationDescriptor.isMainFunction: Boolean
    get() = isPublicTopLevelFunction && (this as FunctionDescriptor).name.asString() == "main"

internal val FunctionDescriptor.isTopLevelFunction: Boolean
    get() = this.isTopLevelInPackage()

internal val FunctionDescriptor.isPublic: Boolean
    get() = this.visibility.isPublicAPI

internal val ClassDescriptor.isAbstractClass: Boolean
    get() = this.modality == Modality.ABSTRACT

internal val ClassDescriptor.isPublic: Boolean
    get() = this.visibility.isPublicAPI

internal val ClassDescriptor.isClass: Boolean
    get() = this.kind != ClassKind.OBJECT && this.kind != ClassKind.ENUM_ENTRY

internal val ClassDescriptor.isObject: Boolean
    get() = this.kind == ClassKind.OBJECT

internal fun <K, V : MutableSet<K>> getInvokesGroupedByFiles(fileToInvokes: HashMap<FileID, V>) =
    groupFilesByInvokes(fileToInvokes).keys.flatten().toMutableSet()

// To avoid repeated checks for belonging invokes in different files,
// we will group files by invokes and process each of them once
// MutableSet<*> here is ClassOrObjectInvokes = MutableSet<SupertypesToAnnotations>
//   or FunctionInvokes = MutableSet<SignatureToAnnotations> MutableSet<*>
private fun <T> groupFilesByInvokes(fileToInvokes: HashMap<FileID, T>): HashMap<T, MutableSet<String>> {
    val filesByInvokes = HashMap<T, MutableSet<FileID>>()
    fileToInvokes.forEach { (file, invoke) ->
        filesByInvokes.getOrPut(invoke) { mutableSetOf() }.add(file)
    }
    return filesByInvokes
}
