# DeepLink

>When a clicked link or programmatic request invokes a web URI intent, the Android system tries each of the following actions, in sequential order, until the request succeeds:
>
>1. Open the user's preferred app that can handle the URI, if one is designated.
>2. Open the only available app that can handle the URI.
>3. Allow the user to select an app from a dialog.
>
>Follow the steps below to create and test links to your content. You can also use the [App Links Assistant](https://developer.android.com/studio/write/app-link-indexing) in Android Studio to add Android App Links.

当点击链接或者编程调用 Web URI  意图的时候， Android 系统按照顺序依次尝试调用下面每一个操作，知道请求成功为止：

1. 打开用户首选的 App，他可以处理 URI 如果指定的话
2. 打开可以处理 URI 的唯一可用应用程序
3. 允许用户从弹窗中选择 App

简单说，就是用户可以写一个特殊格式的字符串（Path），系统会对字符串解析，然后调用注册量了对应 scheme 的应用程序，如果注册了多个，那么弹窗会让用户自己选择。



## 定义 scheme

```xml
        <activity
            android:name="com.share.max.mvp.splash.SelectLanguageActivity"
            android:launchMode="singleTop"
            android:screenOrientation="portrait"
            android:theme="@style/SplashTheme">
          
             <!-- 注意是 Main Activity -->
            <intent-filter>
                <action android:name="android.intent.action.MAIN"/>
                <category android:name="android.intent.category.LAUNCHER"/>
            </intent-filter>
         
            <intent-filter>
                 <!-- 注意这个 action 和 下面两个 category 是必须的 -->
                <action android:name="android.intent.action.VIEW" />

                <category android:name="android.intent.category.DEFAULT" />
                <category android:name="android.intent.category.BROWSABLE" />
                <!-- 一定要包含 scheme --> 
                <data
                    android:host="feed"
                    android:scheme="yoyo" />
                <data
                    android:host="activity"
                    android:scheme="yoyo" />
                <data
                    android:host="tag"
                    android:scheme="yoyo" />
           …………
```

例如：

```XML
<intent-filter>
  ...
  <data android:scheme="https" android:host="www.example.com" />
  <data android:scheme="app" android:host="open.my.app" />
</intent-filter>
```

对应的 URI 分别是：https://www.example.com 和 app://open.my.app



## 在 Intent 中读取数据

在对应的==主 Activity 例如 SelectLanguageActivity==获取数据

```java
@Override
public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    Intent intent = getIntent();
    String action = intent.getAction();
    Uri data = intent.getData();
}
```

getIntent可以在Activity的生命周期的任何时段进行获取，不过一般别人应用要调你应用，肯定都是希望进入你的应用某个界面，或实现某个功能。其他应用会把该传的信息都传给你，最好的解析地方肯定是 `onCreate()`



# 参考

[Create Deep Links to App Content](https://developer.android.com/training/app-links/deep-linking)

[DeepLink用法及原理解析](https://www.jianshu.com/p/d5db3d2def3b)



# AppLink

https://www.jianshu.com/p/49d9b2c54c64