# Add project specific ProGuard rules here.
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-keep public class * extends java.lang.Exception

-keep class com.childwatch.manager.** { *; }
-keepclassmembers class com.childwatch.manager.** { *; }
