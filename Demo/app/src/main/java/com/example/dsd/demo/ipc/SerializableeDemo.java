package com.example.dsd.demo.ipc;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Serializable 序列化、反序列化 Demo
 * <p>
 * Created by im_dsd on 2019-09-25
 */
public class SerializableeDemo implements Serializable {
    /**
     * 此属性属于类，所以不会被序列化
     */
    public static String sType = "";
    /**
     * transient 标识的字段也不会被序列化。
     */
    public transient String name;

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
}
