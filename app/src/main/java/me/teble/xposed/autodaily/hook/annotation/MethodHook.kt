package me.teble.xposed.autodaily.hook.annotation

@Retention(AnnotationRetention.RUNTIME)
@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER
)
annotation class MethodHook(
    /**
     * @return 方法描述
     */
    val desc: String
)