package org.jetbrains.reflekt.plugin.analysis.processor

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
    get() = isPublicFunction && (this as KtNamedFunction).isTopLevel

internal val KtElement.isMainFunction: Boolean
    get() = isPublicFunction && (this as KtNamedFunction).nameIdentifier?.let { it.text == "main" } ?: false

internal val KtElement.isPublicNotAbstractClass: Boolean
    get() = this is KtClass && this.isPublic && !this.isAbstract()

// TODO: is it enough to identify a file?
internal val KtFile.fullName: FileId
    get() = getNameWithPackage(this.packageFqName, this.name)

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
    get() = this.kind != ClassKind.OBJECT && this.kind != ClassKind.ENUM_ENTRY && this.kind != ClassKind.INTERFACE

internal val ClassDescriptor.isObject: Boolean
    get() = this.kind == ClassKind.OBJECT

typealias FileId = String

internal fun getNameWithPackage(packageFqName: FqName, name: String? = null): FileId {
    val postfix = name?.let { ".$name" } ?: ""
    return "${packageFqName.asString()}$postfix"
}

internal fun <K, V : MutableSet<K>> getInvokesGroupedByFiles(fileToInvokes: HashMap<FileId, V>) =
    groupFilesByInvokes(fileToInvokes).keys.flatten().toMutableSet()

// To avoid repeated checks for belonging invokes in different files,
// we will group files by invokes and process each of them once
// MutableSet<*> here is ClassOrObjectInvokes = MutableSet<SupertypesToAnnotations>
// or FunctionInvokes = MutableSet<SignatureToAnnotations> MutableSet<*>
@Suppress("TYPE_ALIAS")
private fun <T : MutableSet<*>> groupFilesByInvokes(fileToInvokes: HashMap<FileId, T>): HashMap<T, MutableSet<String>> {
    val filesByInvokes = HashMap<T, MutableSet<FileId>>()
    fileToInvokes.forEach { (file, invoke) ->
        filesByInvokes.getOrPut(invoke) { mutableSetOf() }.add(file)
    }
    return filesByInvokes
}
