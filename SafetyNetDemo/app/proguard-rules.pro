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

# Platform calls Class.forName on types which do not exist on Android to determine platform.
-dontnote retrofit2.Platform
# Platform used when running on Java 8 VMs. Will not be used at runtime.
-dontwarn retrofit2.Platform$Java8
# Retain generic type information for use by reflection by converters and adapters.
-keepattributes Signature
# Retain declared checked exceptions for use by a Proxy instance.
-keepattributes Exceptions

-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions

-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}
-dontwarn retrofit2.Platform$Java8
-dontwarn sun.misc.Unsafe
-dontwarn org.w3c.dom.bootstrap.DOMImplementationRegistry

# OkHttp
-dontwarn okhttp3.internal.platform.*

-keepattributes Signature
-keepattributes Annotation
-keep class okhttp3.* { *; }
-keep interface okhttp3.* { *; }
-dontwarn okhttp3.**
-dontwarn okio.**

-keep class com.google.android.gms.safetynet.** { *; }
-dontwarn com.google.android.gms.safetynet.**

-keep class com.dishant.safetynetdemo.model.** { *; }
