package dev.slne.surf.event.base.util

import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter

fun instantiateWithArgs(clazz: KClass<*>, vararg args: Any?): Any? {
    clazz.objectInstance?.let { return it }

    val constructors =
        clazz.constructors.sortedBy { it.parameters.count { p -> !p.isOptional && !p.type.isMarkedNullable } }

    for (constructor in constructors) {
        val matchedArgs = matchArgsToConstructor(constructor, args.toList()) ?: continue

        return constructor.callBy(matchedArgs)
    }

    error("No suitable constructor found for class ${clazz.simpleName} with arguments: ${args.joinToString()}")
}

private fun matchArgsToConstructor(
    constructor: KFunction<Any>,
    args: List<Any?>
): Map<KParameter, Any?>? {
    val unusedArgs = args.toMutableList()
    val callArgs = mutableMapOf<KParameter, Any?>()

    for (param in constructor.parameters) {
        if (param.kind != KParameter.Kind.VALUE) continue

        val matchedIndex = unusedArgs.indexOfFirst { arg ->
            arg != null && (param.type.classifier as? KClass<*>)?.isInstance(arg) == true
        }

        if (matchedIndex != -1) {
            callArgs[param] = unusedArgs.removeAt(matchedIndex)
        } else if (param.isOptional || param.type.isMarkedNullable) {
            continue
        } else {
            return null
        }
    }

    return callArgs
}
