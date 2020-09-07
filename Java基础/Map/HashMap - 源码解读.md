# HashMap 概述

HashMap 是基于 Map 接口实现的哈希表，它运行传递为 null 的 key 和 value。相比 HashTable 二者最大的不同在于 HashTable 不接受 null 值（只允许一个 key 为 null, 但 value 可以多个为 null），而且 HashTable 是线程安全的但 HashMap 并不是，其方面二者大致相同。要注意 HashMap 并不能保证映射的顺序性，而且随着事件的推移映射的顺序也可能发生改变（这是因为 hash 算法的随机性且在扩容时重新hash）。但是使用链表实现的 LinkedHashMap 可以保证顺序性。



在正常情况下 HashMap 提供的 get 和 put 方法的时间复杂度恒定为 O(1), 当如果存在大量 hash 碰撞的情况 get 方法会退化为 O(logn) ~ O(n) 之间。在遍历的时候 HashMap 的性能和容量、负载因子相关性非常大：容量越大、负载因子越小 HashMap 的性能就越差。如果迭代的性能很重要，则不要设置过高的容量和过低的负载因子。这一点非常重要。

初始容量（initialCapacity）和负载因子（loadFactor）是影响 HashMap 的重要指标，initialCapacity 指的是 HashMap 底层数组初始化时的容量，而 loadFactor 是指自动扩容的阈值。此阈值 = initialCapacity * loadFactor，当哈希表的使用条目大于了阈值。则将容量扩充为 initialCapacity 的 2 倍。

默认情况下 HashMap 的负载因子为 0.75, 这是一个在空间和时间成本上做了很好这中的经验值。如果负载因子较大表面上是可以较少扩容次数节约空间，但是 get 和 push 的执行时间成本将会增加。如果设置的过低，则会多次触发扩容和重新 hash 时间性能会大幅下降。所以权衡 initialCapacity 和 loadFactor 是很关键的。在设置他们的时候应该结合使用场景考虑映射中的预期条目数及其负载因子，要最大程度上减少重新哈希操作的次数。



HashMap 并不是线程安全的，即多个线程操作同一个 HashMap 的时候结果很可能不一致，而且还有可能造成链表相交的风险。对于此种场景可以使用  Collections 的 synchronizedMap 方法使 HashMap 具有线程安全的能力，或者使用ConcurrentHashMap。

HashTable 是个遗弃类，虽然它是线程安全的但其内部使用的主要操作都是用 synchronized 锁住了整个对象，这意味着每次仅能一个线程工作，其余的线程都将会被阻塞，效率是很低的。采用了分段锁的 ConcurrentHashMap 是最好的选择方案



```java
    /**
     * The load factor used when none specified in constructor.
     */
    static final float DEFAULT_LOAD_FACTOR = 0.75f;
```





> Hash table based implementation of the `Map` interface. This implementation provides all of the optional map operations, and permits `null` values and the `null` key. (The `HashMap` class is roughly equivalent to `Hashtable`, except that it is unsynchronized and permits nulls.) This class makes no guarantees as to the order of the map; in particular, it does not guarantee that the order will remain constant over time.

Hash 表基于 Map 接口实现。此实现提供了所有可选的 map 操作，并且允许 null 键和 null 值。（HashMap 与 HashTable 大致相同，除了 HashMap 不是同步线程安全的和允许 null）。此类并不能保证映射的顺序，特别的是它不能保证映射顺序随着时间的推移而保持恒定。

> This implementation provides constant-time performance for the basic operations (`get` and `put`), assuming the hash function disperses the elements properly among the buckets. Iteration over collection views requires time proportional to the "capacity" of the `HashMap` instance (the number of buckets) plus its size (the number of key-value mappings). Thus, it's very important not to set the initial capacity too high (or the load factor too low) if iteration performance is important.

假设在 hash 函数将元素正确分散在 buckets 的情况下，此实现为基础操作 `get` 和 `put`提供了恒定的时间性能。

An instance of `HashMap` has two parameters that affect its performance: *initial capacity* and *load factor*. The *capacity* is the number of buckets in the hash table, and the initial capacity is simply the capacity at the time the hash table is created. The *load factor* is a measure of how full the hash table is allowed to get before its capacity is automatically increased. When the number of entries in the hash table exceeds the product of the load factor and the current capacity, the hash table is *rehashed* (that is, internal data structures are rebuilt) so that the hash table has approximately twice the number of buckets.

As a general rule, the default load factor (.75) offers a good tradeoff between time and space costs. Higher values decrease the space overhead but increase the lookup cost (reflected in most of the operations of the `HashMap` class, including `get` and `put`). The expected number of entries in the map and its load factor should be taken into account when setting its initial capacity, so as to minimize the number of rehash operations. If the initial capacity is greater than the maximum number of entries divided by the load factor, no rehash operations will ever occur.

If many mappings are to be stored in a `HashMap` instance, creating it with a sufficiently large capacity will allow the mappings to be stored more efficiently than letting it perform automatic rehashing as needed to grow the table. Note that using many keys with the same `hashCode()` is a sure way to slow down performance of any hash table. To ameliorate impact, when keys are [`Comparable`](https://docs.oracle.com/javase/8/docs/api/java/lang/Comparable.html), this class may use comparison order among keys to help break ties.

**Note that this implementation is not synchronized.** If multiple threads access a hash map concurrently, and at least one of the threads modifies the map structurally, it *must* be synchronized externally. (A structural modification is any operation that adds or deletes one or more mappings; merely changing the value associated with a key that an instance already contains is not a structural modification.) This is typically accomplished by synchronizing on some object that naturally encapsulates the map. If no such object exists, the map should be "wrapped" using the [`Collections.synchronizedMap`](https://docs.oracle.com/javase/8/docs/api/java/util/Collections.html#synchronizedMap-java.util.Map-) method. This is best done at creation time, to prevent accidental unsynchronized access to the map:

```
   Map m = Collections.synchronizedMap(new HashMap(...));
```

The iterators returned by all of this class's "collection view methods" are *fail-fast*: if the map is structurally modified at any time after the iterator is created, in any way except through the iterator's own `remove` method, the iterator will throw a [`ConcurrentModificationException`](https://docs.oracle.com/javase/8/docs/api/java/util/ConcurrentModificationException.html). Thus, in the face of concurrent modification, the iterator fails quickly and cleanly, rather than risking arbitrary, non-deterministic behavior at an undetermined time in the future.

Note that the fail-fast behavior of an iterator cannot be guaranteed as it is, generally speaking, impossible to make any hard guarantees in the presence of unsynchronized concurrent modification. Fail-fast iterators throw `ConcurrentModificationException` on a best-effort basis. Therefore, it would be wrong to write a program that depended on this exception for its correctness: *the fail-fast behavior of iterators should be used only to detect bugs.*

This class is a member of the [Java Collections Framework](https://docs.oracle.com/javase/8/docs/technotes/guides/collections/index.html).



# Java 中的性能优化



# 参考

[java 8 HashMap Api](https://docs.oracle.com/javase/8/docs/api/java/util/HashMap.html)

[Java 8 HashMap Implementation and Performance](https://dzone.com/articles/java8-hashmap-implementation-and-performance)

[HashMap Performance Improvements in Java 8](https://dzone.com/articles/hashmap-performance)

[Java HashMap internal Implementation](https://medium.com/@mr.anmolsehgal/java-hashmap-internal-implementation-21597e1efec3)

