[Introduction](#Introduction)
[Java 7 中的实现原理](#Java7中的实现原理)
[缺点](#缺点)
[Java 8 中的实现](#Java8中的实现)
[寻址](#寻址)

# Introduction

常见的Map都是线程不安全的，HashTable虽然是线程安全的，但会锁住这个对象，性能差。为此两种解决方案：

1. Collection.Synchronize（Map ），但是这个也是和HashTable一样，性能差
2. 使用ConcurrentHashMap 
    简单的讲，ConcurrentHashMap采用的是分段锁，减小了锁的颗粒度，但是并不能保证get到的元素是最新。

# Java 7 中的实现原理

在Java7中，ConcurrentHashMap将内部细化出很多小的HashMap组，他们有自己的名字 Segment

```java
/**
 * Stripped-down version of helper class used in previous version,
 * declared for the sake of serialization compatibility
 */
static class Segment<K,V> extends ReentrantLock implements Serializable {
    private static final long serialVersionUID = 2249069246763182397L;
    final float loadFactor;
    Segment(float lf) { this.loadFactor = lf; }
}
```

Segment是ConcurrentHashMap中的内部类，继承了 ReentrantLock  
 ![img](file:///C:/Users/Darius/Documents/My Knowledge/temp/55a9d59b-4734-4c39-b3c3-da61732d6a49/128/index_files/c1b764b9-9a91-41d2-a1ad-cf95a68be91a.png)



```java
/**
 * The default concurrency level for this table. Unused but
 * defined for compatibility with previous versions of this class.
 */
private static final int DEFAULT_CONCURRENCY_LEVEL = 16;
```

默认的并发等级是16，这是因为Segment的默认容量就是16

```java
/**
 * The default initial table capacity.  Must be a power of 2
 * (i.e., at least 1) and at most MAXIMUM_CAPACITY.
 */
private static final int DEFAULT_CAPACITY = 16;
```

每个Segment有自己的锁，他们互不影响，但是在获取例如size这样的全局方法的时候还是要遍历所有的Segment。

在插入或者get数据的时候要经过两次hash，第一次hash确定在哪个segment，第二次找到在bucket数组下标

## 缺点

1. 没有强一致性：get方法没有锁，所以线程获取的结果不一定是最新的。
2. 需要经过两次Hsash耗时。
3. Segment的存在使得ConcurrentHashMap显的臃肿。

# Java 8 中的实现

采用了和HashMap相似的方法：bucket数组+分离链接法（超过阈值编程红黑树），只不过额外的会用的CAS保证线程安全。

## 寻址

寻址方式和HashMap一样的（参见笔记HashMap篇）