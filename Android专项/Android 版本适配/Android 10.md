### Android 10 适配

#### 文件相关
1. 图片的读取，获取相册中的图片
2. 图片的存储，将图片添加到相册
3. 下载文件到Download目录
4. 读取SD卡上非图片，音频，视频类的文件，需要使用文件选择器

#### 权限相关
1. 后台运行时访问设备位置信息需要权限    
Android 10 引入了 ACCESS_BACKGROUND_LOCATION 权限（危险权限）。    
该权限允许应用程序在后台访问位置。如果请求此权限，则还必须请求ACCESS_FINE_LOCATION 或 ACCESS_COARSE_LOCATION权限。只请求此权限无效果。
2. 一些电话、蓝牙和WLAN的API需要精确位置权限

#### 后台启动 Activity 的限制
1. 应用处于后台时，无法启动Activity。
2. 因为此项行为变更适用于在 Android 10 上运行的所有应用，所以这一限制导致最明显的问题就是点击推送信息时，有些应用无法进行正常的跳转（具体的实现问题导致）。所以针对这类问题，可以采取PendingIntent的方式，发送通知时使用setContentIntent方法。

#### 标识符和数据
1. 受影响的方法    
    Build    
    * getSerial()    

    TelephonyManager    
    * getImei()
    * getDeviceId()
    * getMeid()
    * getSimSerialNumber()
    * getSubscriberId()
2. 从 Android 10 开始，应用必须具有 READ_PRIVILEGED_PHONE_STATE 特许权限才能正常使用以上这些方法。
