# Android 中的多进程模式

## 多进程的运行机制

在不同进程中的四大组件会拥有独立的虚拟机，Application 和内存空间。



# IPC 基础概念

## Serializable

* Serializable 通过 IO 流的形式将数据从磁盘中读\写，从而实现序列化。所有可以实现数据的持久化保存。
* 静态成员变量属于类不属于对象，所以不会参与序列化过程
* transient 关键字标识的成员变量不参与序列化过程。
* serialVersionUID ：序列化后的数据中的 serialVersionUID 只有和当前类的 serialVersionUID 相同才能够正常地被反序列化。具体参见 《Android 开发艺术探究》

```java
    /**
     * 序列化
     */
    public static void serialization() {
        SerializableeDemo demo = new SerializableeDemo();
        try {
            // 将 object 写入一个文件
            ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream("create.txt"));
            stream.writeObject(demo);
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

   /**
     * 反序列化
     */
    public static SerializableeDemo  deserialization() {
        try {
            ObjectInputStream stream = new ObjectInputStream(new FileInputStream("create.txt"));
            return (SerializableeDemo) stream.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
```



## Parcelable

* 基于内存完成的序列化，相比 Serializable 性能更好，但是不能持久化。



## Parcelable VS. Serializable

1. 在内存的使用中,前者在性能方面要强于后者

2. 后者在序列化操作的时候会产生大量的临时变量,(原因是使用了反射机制)从而导致GC的频繁调用,因此在性能上会稍微逊色

3. Parcelable是以Ibinder作为信息载体的.在内存上的开销比较小,因此在内存之间进行数据传递的时候,Android推荐使用Parcelable,既然是内存方面比价有优势,那么自然就要优先选择.

4. 在读写数据的时候,Parcelable是在内存中直接进行读写,而Serializable是通过使用IO流的形式将数据读写入在硬盘上.

  但是：虽然Parcelable的性能要强于Serializable,但是仍然有特殊的情况需要使用Serializable,而不去使用Parcelable,因为Parcelable无法将数据进行持久化,因此在将数据保存在磁盘的时候,仍然需要使用后者,因为前者无法很好的将数据进行持久化.(原因是在不同的Android版本当中,Parcelable可能会不同,因此数据的持久化方面仍然是使用Serializable)

## Binder

Binder 是 Andoid 中的一个类，他实现了 IBinder 接口，从 IPC 角度来说，IBinder 是 Android 中的一种跨进程通信方式，Binder 可以理解为一种虚拟设备，它的设备驱动是 /dev/binder，该通讯方式在 Linux 中没有。

从 Android Framework 角度来说，Binder 是 ServiceManager 链接各种 Manager (ActivityManager、WindowManager,等等)和相应 ManagerService的桥梁。从 Android 应用层来说，Binder 是客户端和服务端进行通信的媒介。