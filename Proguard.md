# @Keep

在排除 proguard 混淆名称时，除了使用配置文件外，还可以使用 `@Keep`注解。

 Android Gradle plugin 已经将 @Keep 注解默认开启, 网上说的要各种配置都是放屁！！误人子弟只要保持如下默认配置即可



前提是使用了 AndroidX 注解库（在开启 AndroidX 后此库会自动引入，但不排除异常情况需要手动引入）

```groovy
implementation 'androidx.annotation:annotation:1.1.0'
```

并且在模块 build.gradle 中使用 AndroidX 自带的 proguard 配置文件 [proguard-android-optimize.txt()（开启 AndroidX 后会自动引入，但不排除被开发者删除）

```groovy
android {
        buildTypes {
            release {
                // Enables code shrinking, obfuscation, and optimization for only
                // your project's release build type.
                minifyEnabled true

                // Enables resource shrinking, which is performed by the
                // Android Gradle plugin.
                shrinkResources true

                // Includes the default ProGuard rules files that are packaged with
                // the Android Gradle plugin. To learn more, go to the section about
                // R8 configuration files.
                proguardFiles getDefaultProguardFile(
                        'proguard-android-optimize.txt'), // Android Gradle plugin 自带的规则
                        'proguard-rules.pro'
            }
        }
        ...
    }
```

二者缺一不可。

proguard-android-optimize.txt 文件位于：android sdk 目录下： sdk/tools/proguard/proguard-android-optimize.txt，以下是内容：

```shell
# This is a configuration file for ProGuard.
# http://proguard.sourceforge.net/index.html#manual/usage.html
#
# This file is no longer maintained and is not used by new (2.2+) versions of the
# Android plugin for Gradle. Instead, the Android plugin for Gradle generates the
# default rules at build time and stores them in the build directory.

# Optimizations: If you don't want to optimize, use the
# proguard-android.txt configuration file instead of this one, which
# turns off the optimization flags.  Adding optimization introduces
# certain risks, since for example not all optimizations performed by
# ProGuard works on all versions of Dalvik.  The following flags turn
# off various optimizations known to have issues, but the list may not
# be complete or up to date. (The "arithmetic" optimization can be
# used if you are only targeting Android 2.0 or later.)  Make sure you
# test thoroughly if you go this route.
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*
-optimizationpasses 5
-allowaccessmodification
-dontpreverify

# The remainder of this file is identical to the non-optimized version
# of the Proguard configuration file (except that the other file has
# flags to turn off optimization).

-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-verbose

-keepattributes *Annotation*
-keep public class com.google.vending.licensing.ILicensingService
-keep public class com.android.vending.licensing.ILicensingService

# For native methods, see http://proguard.sourceforge.net/manual/examples.html#native
-keepclasseswithmembernames class * {
    native <methods>;
}

# keep setters in Views so that animations can still work.
# see http://proguard.sourceforge.net/manual/examples.html#beans
-keepclassmembers public class * extends android.view.View {
   void set*(***);
   *** get*();
}

# We want to keep methods in Activity that could be used in the XML attribute onClick
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

# For enumeration classes, see http://proguard.sourceforge.net/manual/examples.html#enumerations
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keepclassmembers class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator CREATOR;
}

-keepclassmembers class **.R$* {
    public static <fields>;
}

# The support library contains references to newer platform versions.
# Don't warn about those in case this app is linking against an older
# platform version.  We know about them, and they are safe.
-dontwarn android.support.**

# Understand the @Keep support annotation.
-keep class android.support.annotation.Keep

-keep @android.support.annotation.Keep class * {*;}

-keepclasseswithmembers class * {
    @android.support.annotation.Keep <methods>;
}

-keepclasseswithmembers class * {
    @android.support.annotation.Keep <fields>;
}

-keepclasseswithmembers class * {
    @android.support.annotation.Keep <init>(...);
}

```

==可以看到只要使用了 getDefaultProguardFile('proguard-android-optimize.txt’)  support 包下的 @Keep 注解也会自动生效不用配置==

如果说开发者没有使用 getDefaultProguardFile('proguard-android-optimize.txt’) 则 AndroidX 自动配置文件也会生效：

[annotations/src/main/resources/META-INF/proguard/androidx-annotations.pro](https://android-review.googlesource.com/c/platform/frameworks/support/+/903818/1/annotations/src/main/resources/META-INF/proguard/androidx-annotations.pro)

```shell
-keep,allowobfuscation @interface androidx.annotation.Keep
-keep @androidx.annotation.Keep class * {*;}

-keepclasseswithmembers class * {
    @androidx.annotation.Keep <methods>;
}

-keepclasseswithmembers class * {
    @androidx.annotation.Keep <fields>;
}
```

```
-keep class androidx.annotation.Keep {*;}
```

```
javadocs 'androidx.annotation:annotation:1.1.0'
implementation 'androidx.annotation:annotation:1.1.0'
```



如需添加特定于每个构建变体的规则，请为每个变种分别配置额外的 [`proguardFiles`](https://google.github.io/android-gradle-dsl/current/com.android.build.gradle.internal.dsl.ProductFlavor.html#com.android.build.gradle.internal.dsl.ProductFlavor:proguardFiles) 属性。例如，以下示例将 `flavor2-rules.pro` 添加到“flavor2”。现在，“flavor2”的发布版本使用全部三个规则文件，因为该变种还应用了 release 代码块中的规则文件。

```groovy
android {
  ...
  buildTypes {
    release {
      minifyEnabled true
      proguardFiles getDefaultProguardFile('proguard-android.txt'),
             'proguard-rules.pro'
    }
  }
  productFlavors {
    flavor1 {
      ...
    }
    flavor2 {
      proguardFile 'flavor2-rules.pro'
    }
  }
}
...
```

