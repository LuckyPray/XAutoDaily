package me.teble.xposed.autodaily.utils

import cn.hutool.core.util.ClassUtil
import cn.hutool.core.util.ClassUtil.isAllAssignableFrom
import cn.hutool.core.util.ReflectUtil
import java.lang.reflect.Field
import java.lang.reflect.Method

/**
 * @author teble
 * @date 2021/6/4 23:39
 * @description
 */

fun Any.getMethods(): Array<Method> {
    return ReflectUtil.getMethods(if (this is Class<*>) this else this::class.java)
}

fun Any.getFields(): Array<Field>? {
    return ReflectUtil.getFields(if (this is Class<*>) this else this::class.java)
}

fun Any.fieldValue(
    fieldType: Class<*>
): Any? {
    ReflectUtil.getFields(if (this is Class<*>) this else this::class.java).forEach {
        if (it.type == fieldType) {
            LogUtil.log("${it.type}: ${it.name} -> ${ReflectUtil.getFieldValue(this, it)}")
            return ReflectUtil.getFieldValue(this, it)
        }
    }
    return null
}

fun Any.field(
    fieldType: Class<*>
): Field? {
    ReflectUtil.getFields(if (this is Class<*>) this else this::class.java).forEach {
        if (it.type == fieldType) {
            it.isAccessible = true
            return it
        }
    }
    return null
}

fun Any.field(
    fieldName: String
): Field? {
    ReflectUtil.getFields(if (this is Class<*>) this else this::class.java).forEach {
        if (it.name == fieldName) {
            it.isAccessible = true
            return it
        }
    }
    return null
}

fun Any.fieldValue(
    name: String
): Any? = ReflectUtil.getFieldValue(this, name)

@Suppress("UNCHECKED_CAST")
fun <T> Any.fieldValueAs(fieldType: Class<*>): T? = this.fieldValue(fieldType) as T?

@Suppress("UNCHECKED_CAST")
fun <T> Any.fieldValueAs(name: String): T? = this.fieldValue(name) as T?

fun <T> Any.invoke(
    name: String,
    returnType: Class<T>,
    vararg args: Any?
): T? {
    ReflectUtil.getMethods(if (this is Class<*>) this else this.javaClass).let { arrayOfMethods ->
        arrayOfMethods.forEach {
            if (it.name == name && it.returnType == returnType
                && isAllAssignableFrom(ClassUtil.getClasses(*args), it.parameterTypes)
            ) {
                return ReflectUtil.invoke(this, it, *args)
            }
        }
        return null
    }
}

fun Any.invoke(
    name: String,
    vararg args: Any?
): Any? {
    ReflectUtil.getMethods(if (this is Class<*>) this else this.javaClass).let { arrayOfMethods ->
        arrayOfMethods.forEach {
            if (it.name == name
                && isAllAssignableFrom(it.parameterTypes, ClassUtil.getClasses(*args))
            ) {
                return ReflectUtil.invoke(this, it, *args)
            }
        }
        return null
    }
}

@Suppress("UNCHECKED_CAST")
fun <T> Any.invokeAs(name: String, vararg args: Any?): T? = this.invoke(name, *args) as T?

fun <T> Class<T>.new(vararg args: Any?): T = ReflectUtil.newInstance(this, *args)

fun Any.printAllField() {
    val obj = this
    LogUtil.d("PrintField", buildString {
        append("\n")
        ReflectUtil.getFields(if (obj is Class<*>) obj else obj.javaClass).forEach {
            it.isAccessible = true
            append("${it.type}: ${it.name} = ${it.get(obj)}\n")
        }
    })
}