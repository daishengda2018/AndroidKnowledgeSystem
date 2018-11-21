# Lifecycle


## Activity异常生命周期
### 资源相关的系统配置发生改变导致Activity重建（例：当屏幕旋转的时候）

当手机屏幕旋转之后，系统的相关配置发生了改变，为了重新加载资源系统会默认先销毁Activity
在重新创建，为了保护现场，Activity会调用 `onSaveInstanceSate` 和 `onRestoreInstanceState`
方法，具体的生命周期如下：

onPause -> onSaveInstanceState -> onStop -> onDestroy

onCreate -> onStart-> onRestoreInstanceState -> onResume.

**这里要注意：**
>`onSaveInstanceState` 在V21 API（Android 5.0）以上采用了新的方法：
`onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState)`
但是需要在ActivityMainest.xml中为Activity配置 `android:persistableMode="persistAcrossReboots"`
而在Android 5.0一下还是会调用`onSaveInstanceState(Bundle outState) `

`onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState)`的优点在于可以在Activity被销毁后，回复现场。


