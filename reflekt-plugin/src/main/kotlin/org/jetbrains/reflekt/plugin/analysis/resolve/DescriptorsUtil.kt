package org.jetbrains.reflekt.plugin.analysis.resolve

import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.descriptors.impl.ModuleDescriptorImpl
import org.jetbrains.kotlin.incremental.components.NoLookupLocation
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.resolve.descriptorUtil.*
import org.jetbrains.kotlin.resolve.scopes.MemberScope
import org.jetbrains.reflekt.plugin.analysis.models.IrFunctionInfo
import org.jetbrains.reflekt.plugin.analysis.psi.function.*

// Descriptors for classes and objects
fun MemberScope.getClassifierDescriptors(): List<ClassifierDescriptor> =
    this.getClassifierNames()?.mapNotNull { this.getContributedClassifier(it, NoLookupLocation.WHEN_RESOLVE_DECLARATION) } ?: emptyList()

fun MemberScope.getClassDescriptors(): List<ClassifierDescriptor> =
    getClassifierDescriptors().filter { !it.isObject() }

fun MemberScope.getObjectDescriptors(): List<ClassifierDescriptor> =
    getClassifierDescriptors().filter { it.isObject() }

// Descriptors for top level functions
fun MemberScope.getTopLevelFunctionDescriptors(): List<FunctionDescriptor> =
    this.getFunctionNames().map { this.getContributedFunctions(it, NoLookupLocation.WHEN_RESOLVE_DECLARATION) }.flatten()

fun ModuleDescriptorImpl.getAllSubPackages(rootPackage: FqName, allSubPackages: MutableList<FqName> = mutableListOf()): MutableList<FqName> {
    allSubPackages.add(rootPackage)
    this.packageFragmentProvider.getSubPackagesOf(rootPackage) { true }.forEach {
        this.getAllSubPackages(it, allSubPackages)
    }
    return allSubPackages
}

fun ModuleDescriptorImpl.getDescriptors(packages: Set<FqName>): List<PackageFragmentDescriptor> =
    packages.map { this.packageFragmentProvider.packageFragments(it) }.flatten()


fun FunctionDescriptor.toFunctionInfo(): IrFunctionInfo =
    IrFunctionInfo(
        this.fqNameSafe.asString(),
        receiverFqName = receiverType()?.shortFqName(),
        isObjectReceiver = receiverType()?.isObject() ?: false
    )

fun ClassifierDescriptor.isSubtypeOf(klasses: Set<String>): Boolean {
    return this.getAllSuperClassifiers().filter { it is ClassDescriptor }.any {
        it.fqNameOrNull()?.asString() in klasses
    }
}
