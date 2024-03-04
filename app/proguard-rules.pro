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
#-keep class com.yeel.digitalbank.activity.signup.PlaidConnectionActivity {*; }
#-keep class com.yeel.digitalbank.activity.signup.PlaidConnectionActivity {*; }



-keepnames class com.squareup.okhttp3.*{ *; }

-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase
-keepattributes LocalVariableTable, LocalVariableTypeTable
-optimizations !method/removal/parameter
-keepclassmembers class *.R$ {
    public static <fields>;
}

-keep class *.R$


#-keepclassmembers class fqcn.of.javascript.interface.for.webview {public *;}




-dontwarn retrofit2.**
-dontwarn org.codehaus.mojo.**
-keep class com.squareup.retrofit2.* { *; }
-keepattributes Signature
-keepattributes Exceptions
-keepattributes *Annotation*

-keepattributes RuntimeVisibleAnnotations
-keepattributes RuntimeInvisibleAnnotations
-keepattributes RuntimeVisibleParameterAnnotations
-keepattributes RuntimeInvisibleParameterAnnotations

-keepattributes EnclosingMethod

-keepclasseswithmembers class * {
    @retrofit2.* <methods>;
}

-keepclasseswithmembers interface * {
    @retrofit2.* <methods>;
}




#Room
-dontwarn android.arch.util.paging.CountedDataSource
-dontwarn android.arch.persistence.room.paging.LimitOffsetDataSource
-keep class  com.yeel.drc.api.APIInterface { *; }
-keep class  com.yeel.drc.model.phonecontacts.* { *; }

#-keep class com.yeel.digitalbank.model.SignUpModel { *; }

-keepattributes *Annotation*

# Jackson
-keep @com.fasterxml.jackson.annotation.JsonIgnoreProperties class * { *; }
-keep @com.fasterxml.jackson.annotation.JsonCreator class * { *; }
-keep @com.fasterxml.jackson.annotation.JsonValue class * { *; }
-keep class com.fasterxml.** { *; }
-keep class org.codehaus.** { *; }
-keepnames class com.fasterxml.jackson.** { *; }
-keepclassmembers public final enum com.fasterxml.jackson.annotation.JsonAutoDetect$Visibility {
    public static final com.fasterxml.jackson.annotation.JsonAutoDetect$Visibility *;
}

# General
-keepattributes SourceFile,LineNumberTable,*Annotation*,EnclosingMethod,Signature,Exceptions,InnerClasses
-optimizations class/marking/final
-optimizations field/marking/private

#-keepnames class org.codehaus.jackson.** { *; }
#-keepnames class com.fasterxml.jackson.databind.** { *; }
#-dontwarn com.fasterxml.jackson.databind.**
#-keep public class classes.to.be.serialized** {public private protected *;}

-adaptresourcefilenames
-renamesourcefileattribute
-keepattributes InnerClasses
-keep class io.jsonwebtoken.** { *; }
-keepnames class io.jsonwebtoken.* { *; }
-keepnames interface io.jsonwebtoken.* { *; }

-keep class org.bouncycastle.** { *; }
-keepnames class org.bouncycastle.** { *; }
-dontwarn org.bouncycastle.**

-optimizations code/simplification/arithmetic

-optimizationpasses 3
-overloadaggressively
-repackageclasses 'o'
-allowaccessmodification

-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable,SerializedName

# Retrofit does reflection on generic parameters. InnerClasses is required to use Signature and
# EnclosingMethod is required to use InnerClasses.
-keepattributes Signature, InnerClasses, EnclosingMethod

# Retrofit does reflection on method and parameter annotations.
#-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations

# Retain service method parameters when optimizing.
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}


-dontwarn android.support.**
# Ignore annotation used for build tooling.
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement


# Ignore JSR 305 annotations for embedding nullability information.
-dontwarn javax.annotation.**

# Guarded by a NoClassDefFoundError try/catch and only used when on the classpath.
-dontwarn kotlin.Unit

# Top-level functions that can only be used by Kotlin.
-dontwarn retrofit2.KotlinExtensions
-dontwarn retrofit2.KotlinExtensions*

# With R8 full mode, it sees no subtypes of Retrofit interfaces since they are created with a Proxy
# and replaces all potential values with null. Explicitly keeping the interfaces prevents this.
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface <1>

-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

#-keepattributes SourceFile,LineNumberTable,*Annotation*,EnclosingMethod,Signature,Exceptions,InnerClasses
-keepattributes LineNumberTable,*Annotation*,EnclosingMethod,Signature,Exceptions,InnerClasses
# Animal Sniffer compileOnly dependency to ensure APIs are compatible with older versions of Java.
-dontwarn org.codehaus.mojo.animal_sniffer.*

# Remove debug logs in release build
-assumenosideeffects class android.util.Log {
    public static *** d(...);



}



