# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\raghav\AppData\Local\Android\Sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

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


-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.AppGlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

-keep class io.paperdb.** { *; }
-keep class com.esotericsoftware.** { *; }
-dontwarn com.esotericsoftware.**
-keep class de.javakaffee.kryoserializers.** { *; }
-dontwarn de.javakaffee.kryoserializers.**
-keep class android.support.v7.widget.SearchView { *; }

-keepattributes Signature

-keepclassmembers class com.blackboxindia.PostIT.dataModels.AdData.** { *; }
-keepclassmembers class com.blackboxindia.PostIT.dataModels.AdTypes.** { *; }
-keepclassmembers class com.blackboxindia.PostIT.dataModels.UserInfo.** { *; }

-keep public class com.blackboxindia.PostIT.dataModels.** {
  public protected *;
}


# for DexGuard only
#-keepresourcexmlelements manifest/application/meta-data@value=GlideModule