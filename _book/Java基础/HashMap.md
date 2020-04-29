[TOC]



# Introduction

1. HashMap 顾名思义：通过 Hash 算法实现的散列表。

2. Hash是一个具有不可逆、唯一性的算法，主要用于对数据采集指纹特征，常见的 Hash 算法就是 MD5、长度为32  

   > 1. 不可逆：Hash提取的只是数据的部分特征，不能像压缩算法一样还原数据。
   > 2. 唯一性：指的是同一个数据，经过Hash算法得到的结果都是一样的。

3. HashMap底层使用的是数组的数据结构实现的，这个数组有自己的名字：bucket 桶。用户输入的 Key 值通过 hash 函数形成数组下表存储元素。 
    ![img](file:///C:/Users/Darius/Documents/My Knowledge/temp/eac90b9a-a56a-473d-aca2-b5fb5f45eb0e/128/index_files/e7f30220-fc42-4703-b9df-7780fd336e91.jpg)

4. The Default capcatity of HasMap is 1 << 4 = 16，Max capacity is 1 << 30 
   The laodFactor is 0.75

# HashMap使用的算法

最简单的方式是直接将得到的 HashCode 与 bucket size 取模，将得到的结果作为bucket 的下标，但是这有一个问题：bucket 动态扩容的时候数据要整体移动。这个问题在分布式一致性中同样存在。 

在HashMap中是这样处理的：

## 寻址

**1. 获取HasCode并加大其随机性**

```java
static final int hash(Object key) {
	int h;
	return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
}
```

将得到的 hashCode 的低16位于高16位进行或运算。这个操作的目的是为了加大hasCode的随机性。 通过源码可以看到 HashMap 的 key 是可以为 null 的，null的时候 key=0，所以也是唯一的。将 hasCode 与（bucket.size - 1）进行与操作。

```java
Node[] tab; Node p; int n, index;
if ((tab = table) != null && (n = tab.length) > 0 &&
(p = tab[index = (n - 1) & hash]) != null) {
```

关键点 `(n - 1) & hash` 这个操作看起来不起眼，却在巧妙解决动态扩容问题的同时，减少了Hash碰撞的可能性。 
 ![img](file:///C:/Users/Darius/Documents/My Knowledge/temp/eac90b9a-a56a-473d-aca2-b5fb5f45eb0e/128/index_files/b6a0d351-1aed-44e9-b8e8-c0e5a725f7cd.jpg)

# Hash碰撞

上面的两步走虽然减少了Hash碰撞的可能性，但是终究不能避免Hash碰撞，那么HashMap是如何解决这个的呢。 
 简单的就是使用链表，在冲突的bucket后面挂载 Entry

# 扩容（Resize）

当到达laode factor的时候扩容之前的两倍。

# 多线程导致死循环

因为是链表，多线程情况下容易造成链表成环：输入数据的时候可能会扩容，但是扩容的时候需要将数据 refresh 到新的数组，如果并发很可能链表循环引用而成环。

详情 ：<https://blog.csdn.net/sinat_31011315/article/details/78699655>

# 参考

<https://sylvanassun.github.io/2018/03/16/2018-03-16-map_family/#HashMap>