

# Kotlin Builder 模式

学习自 OkHttp 4.9.0 -> OkHttpClient.Builder 源码

```java
class Builder {
    private var name: String = ""
    private var age: Int = 0
    // 使用 apply 的形式就可以达到 Java return this 的效果
    fun setName(name: String) = apply {
      this.name = name
    }

    fun setAge(age: Int) {
      this.age = age
    }
  }
```

使用方式和 Java 一致可以一直点下去

```kotlin
   Builder().setName("dd") .setAge(0)
```

