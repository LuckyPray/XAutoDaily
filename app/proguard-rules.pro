# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Keep `Companion` object fields of serializable classes.
# This avoids serializer lookup through `getDeclaredClasses` as done for named companion objects.
-if @kotlinx.serialization.Serializable class **
-keepclassmembers class <1> {
    static <1>$Companion Companion;
}

# Keep `serializer()` on companion objects (both default and named) of serializable classes.
-if @kotlinx.serialization.Serializable class ** {
    static **$* *;
}
-keepclassmembers class <1>$<3> {
    kotlinx.serialization.KSerializer serializer(...);
}

# Keep `INSTANCE.serializer()` of serializable objects.
-if @kotlinx.serialization.Serializable class ** {
    public static ** INSTANCE;
}
-keepclassmembers class <1> {
    public static <1> INSTANCE;
    kotlinx.serialization.KSerializer serializer(...);
}

# @Serializable and @Polymorphic are used at runtime for polymorphic serialization.
-keepattributes RuntimeVisibleAnnotations,AnnotationDefault

# Serializer for classes with named companion objects are retrieved using `getDeclaredClasses`.
# If you have any, uncomment and replace classes with those containing named companion objects.
#-keepattributes InnerClasses # Needed for `getDeclaredClasses`.
#-if @kotlinx.serialization.Serializable class
#com.example.myapplication.HasNamedCompanion, # <-- List serializable classes with named companions.
#com.example.myapplication.HasNamedCompanion2
#{
#    static **$* *;
#}
#-keepnames class <1>$$serializer { # -keepnames suffices; class is kept when serializer() is kept.
#    static <1>$$serializer INSTANCE;
#}

# 基本混淆
-keep class * extends android.app.Activity
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keepclasseswithmembernames class * {
    native <methods>;
}
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}
-keepclassmembers class **.R$* {
    public static <fields>;
}
# Hook 混淆
-keep public class me.teble.xposed.autodaily.hook.MainHook
-keep class me.teble.xposed.autodaily.utils.LogUtil {
    *;
}
-keepclassmembers public class me.teble.xposed.autodaily.hook.MainHook {
    <init>(android.content.Context);
}
-keep class * extends me.teble.xposed.autodaily.hook.base.BaseHook {
    <methods>;
}
-keep class com.tencent.mmkv.** { *; }
# 插件代理混
-keep class me.teble.xposed.autodaily.hook.function.proxy.* {
    <methods>;
}
-keep class * extends me.teble.xposed.autodaily.hook.function.BaseFunction {
    <methods>;
}
# ByteBuddy混淆
-keep class com.android.dx.** {
    *;
}
-keep class net.bytebuddy.** {
    *;
}
-keep class me.teble.xposed.autodaily.shizuku.UserService {
    *;
}
# servlet混淆
-keep class me.teble.xposed.autodaily.hook.servlets.** {
    *;
}


##########################################################################################################
-assumenosideeffects class kotlin.jvm.internal.Intrinsics {
    public static void check*(...);
    public static void throw*(...);
}
# 去除 DebugMetadataKt() 注释
-assumenosideeffects class kotlin.coroutines.jvm.internal.BaseContinuationImpl {
  java.lang.StackTraceElement getStackTraceElement() return null;
}
-assumenosideeffects public final class kotlin.coroutines.jvm.internal.DebugMetadataKt {
   private static final kotlin.coroutines.jvm.internal.DebugMetadata getDebugMetadataAnnotation(kotlin.coroutines.jvm.internal.BaseContinuationImpl) return null;
}
##########################################################################################################
#-obfuscationdictionary          proguard-dic.txt
#-renamesourcefileattribute      proguard-dic.txt
#-classobfuscationdictionary     proguard-dic.txt
#-packageobfuscationdictionary   proguard-dic.txt
-repackageclasses ''
-keepattributes Signature
-keepattributes EnclosingMethod
-keepattributes *Annotation*
-keep class * extends java.lang.annotation.Annotation {*;}