# 基于 HashMap

LinkedHashMap 是基于 HashMap 实现的顺序 Map，也就是说他可以保存数据 put 或者操作时候的顺序，并在迭代的时候按顺序输出。

其内部并没有复写 HashMap 的 put、putVal 方法，而是复写了 newNode 方法

```java
    Node<K,V> newNode(int hash, K key, V value, Node<K,V> e) {
        LinkedHashMap.Entry<K,V> p = new LinkedHashMap.Entry<K,V>(hash, key, value, e);
        linkNodeLast(p);
        return p;
    }
```

构建的每个节点都带有前后指针，这样所有的节点构建成了一个双向链表，保存了操作的顺序。

```java
static class Entry<K,V> extends HashMap.Node<K,V> {
        Entry<K,V> before, after;
        Entry(int hash, K key, V value, Node<K,V> next) {
            super(hash, key, value, next);
        }
    }
```

# 构造函数

在构造函数方面只要关注 accessOrder 一点就行。

accessOrder = true 内部的顺序为操作的顺序；accessOrder = false 内部顺序为插入顺序；==accessOrder 默认为 false==

```java
   public LinkedHashMap(int initialCapacity, float loadFactor,boolean accessOrder) {
        super(initialCapacity, loadFactor);
        this.accessOrder = accessOrder;
    }
```

# accessOrder  参数 Demo

```java
Map<String, String> map = new LinkedHashMap<>();
        map.put("1", "a");
        map.put("2", "b");
        map.put("3", "c");
        map.put("4", "d");

        Iterator<Map.Entry<String, String>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            System.out.println(iterator.next());
        }

        System.out.println("以下是accessOrder=true的情况:");

        map = new LinkedHashMap<String, String>(10, 0.75f, true);
        map.put("1", "a");
        map.put("2", "b");
        map.put("3", "c");
        map.put("4", "d");
        map.get("2");//2移动到了内部的链表末尾
        map.get("4");//4调整至末尾
        map.put("3", "e");//3调整至末尾
        map.put(null, null);//插入两个新的节点 null
        map.put("5", null);//5
        iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            System.out.println(iterator.next());
        }
```



输出结果

```shell
1=a
2=b
3=c
4=d
以下是accessOrder=true的情况:
1=a
2=b
4=d
3=e
null=null
5=null
```



# containsValue 的优化

LinkedHashMap 对 containsValue 方法进行了优化

```java
public boolean containsValue(Object value) {
        //遍历一遍链表，去比较有没有value相等的节点，并返回
        for (LinkedHashMap.Entry<K,V> e = head; e != null; e = e.after) {
            V v = e.value;
            if (v == value || (value != null && value.equals(v)))
                return true;
        }
        return false;
    }
```

相比 HashMap 的两层循环效率更高

```java
public boolean containsValue(Object value) {
        Node<K,V>[] tab; V v;
        if ((tab = table) != null && size > 0) {
            for (int i = 0; i < tab.length; ++i) {
                for (Node<K,V> e = tab[i]; e != null; e = e.next) {
                    if ((v = e.value) == value ||
                        (value != null && value.equals(v)))
                        return true;
                }
            }
        }
        return false;
    }
```





