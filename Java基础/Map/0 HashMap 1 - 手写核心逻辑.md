

源码地址：https://github.com/daishengda2018/JavaDemo/blob/master/src/com/darius/collection/map/HashMap.java



```java
package com.darius.collection.map;

import java.util.Objects;

/**
 * 手动实现 JDK 1.8 HashMap 核心逻辑
 * <p>
 * Create by im_dsd 2020/9/12 11:53 上午
 */
class HashMap<K, V> implements Map<K, V> {
    /**
     * 最大容量
     */
    static final int MAXIMUM_CAPACITY = 1 << 30;
    /**
     * 负载因子
     */
    static final float DEFAULT_LOAD_FACTOR = 0.75f;
    /**
     * The default initial capacity - MUST be a power of two.
     */
    static final int DEFAULT_INITIAL_CAPACITY = 1 << 4; // aka 16

    /**
     * HashMap 基础存储数据结构
     */
    private Node<K, V>[] mTable;
    /**
     * 存储数据的大小
     */
    private int mSize;
    /**
     * 扩容阈值 = 当前数组容量 * 负载因子
     */
    private int mResizeThreshold;
    /**
     * 记录修改 hashMap 的次数，用于判断在迭代器遍历过程中有没有其他线程修改过 hashmap
     * 如果有则抛出 ConcurrentModificationException 异常，就这是 fail-fast
     */
    private int mModCount;


    /**
     * 计算 hash
     * 主要的逻辑是将 hasCode 与其高位 16 位进行异或计算
     * 这样是为了保证在后面的运算中，如果出现数组（桶）很小的场景，高位也能参与运算，减小 hash 碰撞的可能。
     */
    int hash(K k) {
        if (k == null) {
            return 0;
        }
        int hashCode = k.hashCode();
        // (hashCode >>> 16) 无符号右移16为，剩下的结果为高16位
        return hashCode ^ (hashCode >>> 16);
    }

    /**
     * 计算位于数组中的索引
     */
    int indexFor(int hashCode, int length) {
        // 当 length = 2^n 的时候，下面的位运算等价于 hashCode % length。但位运算速度更快。
        // 所以在 table 的容量必须是 2 的 n 次幂
        return (length - 1) & hashCode;
    }

    @Override
    public V put(K key, V value) {
        Node<K, V>[] table = mTable;
        int length = (table == null ? 0 : table.length);
        int hash = hash(key);
        // 检测 table 有没有初始化
        if (table == null || length == 0) {
            length = (table = resize()).length;
        }
        // 计算索引 - 计算索引是个重点！！！！
        int index = indexFor(hash, length);
        // 如果当前位置没有使用过，直接创建 Node 存储
        if (table[index] == null) {
            table[index] = newNode(hash, key, value, null);
        } else {
            // hash 冲突函数
            V result = putOnHashCollision(table, table[index], hash, key, value);
            if (result != null) {
                return result;
            }
        }
        // 记录修改的次数，如果在遍历数据的过程中 mModCount 数量前后不一致则立即抛出 ConcurrentModificationException 异常
        ++mModCount;
        if (++mSize > mResizeThreshold) {
            // 查过了阈值，扩容为当前容量的二倍
            resize();
        }
        return null;
    }

    /**
     * hash 冲突时候的 put 逻辑
     */
    private V putOnHashCollision(Node<K, V>[] table, Node<K, V> curNode,
                                 int hashOfKey, K key, V value) {
        Node<K, V> linkedNode;
        if (isEqualsWith(curNode, key, hashOfKey)) {
            // key 相同则直接覆盖
            linkedNode = curNode;
        } else if (curNode instanceof TreeNode) {
            // todo 红黑数的操作，暂时忽略
            linkedNode = new TreeNode<>(hashOfKey, key, value, null);
        } else {
            // key 不同在链表中寻找
            for (int binCount = 0; ; binCount++) {
                linkedNode = curNode.next;
                // 遍历到了尾节点，直接插入 [尾插法]
                if (linkedNode == null) {
                    curNode.next = newNode(hashOfKey, key, value, null);
                    break;
                }
                // 找到与新数据 key 相同的 node，此时 curNode 就是需要更新的节点
                if (isEqualsWith(linkedNode, key, hashOfKey)) {
                    break;
                }
                // 遍历链表
                curNode = linkedNode;
            }
        }
        // 更新老值
        if (linkedNode != null) {
            V oldValue = curNode.value;
            linkedNode.value = value;
            return oldValue;
        }
        return null;
    }

    public boolean isEqualsWith(Node<K, V> node, K key, int hashOfKey) {
        boolean isSame = (node.hashOfKey == hashOfKey && node.key == key);
        boolean isEquals = (key != null && key.equals(node.key));
        return isSame || isEquals;
    }

    /**
     * 扩容或初始化
     *
     * @return
     */
    private Node<K, V>[] resize() {
        Node<K, V>[] olderTable = mTable;
        final int olderCapacity = olderTable == null ? 0 : olderTable.length;
        final int olderThreshold = mResizeThreshold;
        int newCapacity, newThreshold = 0;
        // 扩容
        if (olderCapacity > 0) {
            // 已经达到最大容量已经不再扩容
            if (olderCapacity >= MAXIMUM_CAPACITY) {
                mResizeThreshold = Integer.MAX_VALUE;
                // 返回老 tagle
                return olderTable;
                // 使用向左移动的方式，扩大新容量为原来的 2 倍，
            } else if ((newCapacity = olderCapacity << 1) < MAXIMUM_CAPACITY
                    && olderCapacity >= DEFAULT_INITIAL_CAPACITY) {
                // 阈值扩大为原来的二倍
                newThreshold = olderThreshold << 1;
            }
        } else if (olderThreshold > 0) {
            // 当前 table 为 null ，但是有阈值，说明用户指定了不同的初始化容量或者扩容因子
            newCapacity = olderThreshold;
        } else {
            // 还没有初始化过
            newCapacity = DEFAULT_INITIAL_CAPACITY;
            newThreshold = (int) (DEFAULT_INITIAL_CAPACITY * DEFAULT_LOAD_FACTOR);
        }

        if (newThreshold == 0) {
            // todo 重新判断阈值
        }
        mResizeThreshold = newThreshold;
        // 创建新的数组
        Node<K, V>[] newTable = (Node<K, V>[]) new Node[newCapacity];
        mTable = newTable;
        if (olderTable == null) {
            return newTable;
        }
        for (int i = 0; i < olderCapacity; i++) {
            Node<K, V> node = olderTable[i];
            if (node == null) {
                continue;
            }
            // !!!!!!! 清空老列表的索引，以便 GC !!!!!!!!!
            olderTable[i] = null;
            // 节点没有链表即没有发生 hash 冲突
            if (node.next == null) {
                // rehash 计算位置
                newTable[indexFor(node.hashOfKey, newCapacity)] = node;
            } else if (node instanceof TreeNode) {
                // todo 红黑数的操作，暂时忽略
            } else {
                // 存在链表，需要遍历每一个节点，重新确定位置 ： rehash。
                // 因为数组的容量是2的n次幂，根据数学推导 indexFor() 函数计算索引的过程。所以链表中的节点 rehash 后只两种结果
                // 1。 二进制末尾为 0 则保持位置不变
                // 2。 二进制末尾为 1，则新位置为当前位置 + 老数组容量
                // 此处的优化可以减少为每个节点都进行 rehash 的性能消耗
                Node<K, V> olderPosHead = null, olderPosTail = null; // 位置不变的链表头和尾，尾部节点的作用是趟平道路防止出现环
                Node<K, V> newPosHead = null, newPosTail = null;  // 位置改变的链表头和尾，同上
                Node<K, V> next;
                do {
                    next = node.next;
                    // 保留原来位置
                    if ((node.hashOfKey & olderCapacity) == 0) {
                        if (olderPosTail == null) {
                            // 记录链表头节点，相当于获取了整个链表
                            olderPosHead = node;
                        } else {
                            // 记录尾节点，他的作用相当于趟平道路防止末尾出现环
                            olderPosTail.next = node;
                        }
                        olderPosTail = node;
                    } else {
                        // 将节点保存到新的链表中
                        if (newPosTail == null) {
                            newPosHead = node;
                        } else {
                            newPosTail.next = node;
                        }
                        newPosTail = node;
                    }
                } while ((node = next) != null);

                // 位置不变的链表
                if (olderPosTail != null) {
                    // 清空末尾
                    olderPosTail.next = null;
                    // 保留位置
                    newTable[i] = olderPosHead;
                }
                // 位置改变的链表
                if (newPosTail != null) {
                    newPosTail.next = null;
                    // 改变位置
                    newTable[i + olderCapacity] = newPosHead;
                }
            }
        }
        return newTable;
    }

    /**
     * Create a regular (non-tree) node
     * 在 LinkedHashMap 中将会复写此方法用于实现双向链表
     *
     * @return
     */
    HashMap.Node<K, V> newNode(int hashOfKey, K key, V value, Node<K, V> next) {
        return new Node<>(hashOfKey, key, value, next);
    }


    @Override
    public V get(K key) {
        return null;
    }

    @Override
    public Integer size() {
        return null;
    }

    /**
     * 存储的元数据
     */
    private static class Node<K, V> implements Map.Entry<K, V> {
        final int hashOfKey;
        final K key;
        V value;
        Node<K, V> next;

        public Node(int hashOfKey, K key, V value, Node<K, V> next) {
            this.hashOfKey = hashOfKey;
            this.key = key;
            this.value = value;
            this.next = next;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        public V setValue(V value) {
            V olderValue = this.value;
            this.value = value;
            return olderValue;
        }

        public final boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (o instanceof Map.Entry) {
                Map.Entry<?, ?> e = (Map.Entry<?, ?>) o;
                return Objects.equals(key, e.getKey()) && Objects.equals(value, e.getValue());
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hash(key) ^ Objects.hash(value);
        }
    }

    /**
     * 模拟红黑数
     */
    static class TreeNode<K, V> extends Node<K, V> {

        public TreeNode(int hashKey, K key, V value, Node<K, V> next) {
            super(hashKey, key, value, next);
        }

        final TreeNode<K, V> putTreeVal(HashMap<K, V> map,
                                        HashMap.Node<K, V>[] tab,
                                        int h, K k, V v) {
            return new TreeNode<K, V>(h, k, v, null);
        }

        @Override
        public K getKey() {
            return null;
        }

        @Override
        public V getValue() {
            return null;
        }
    }
}

```

