[TOC]

# Fragment 基本概念

1. Fragment 必须依赖 Activity 使用，其生命周期直接受宿主 Activity 生命周期的影响。例如，当 Activity 暂停时，Activity 的所有片段也会暂停；当 Activity 被销毁时，所有片段也会被销毁。

![img](https://developer.android.com/images/fragment_lifecycle.png?hl=zh_cn)

# Fragment与Activity生命周期对照图

[原图地址](https://github.com/xxv/android-lifecycle)
![](https://github.com/xxv/android-lifecycle/raw/master/complete_android_fragment_lifecycle.png)

# 创建 Fragment

```java
public static class ExampleFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // 注意 inflate 的时候要传入 false ！！！！
        return inflater.inflate(R.layout.example_fragment, container, false);
    }
}
```

传递至 onCreateView() 的 container 参数是您的片段布局将插入到的父级 ViewGroup（来自 Activity 的布局）。savedInstanceState 参数是在恢复片段时，提供上一片段实例相关数据的 Bundle（处理片段生命周期部分对恢复状态做了详细阐述）。

inflate() 方法带有三个参数：

您想要扩展的布局的资源 ID。
将作为扩展布局父项的 ViewGroup。传递 container 对系统向扩展布局的根视图（由其所属的父视图指定）应用布局参数具有重要意义。
指示是否应在扩展期间将扩展布局附加至 ViewGroup（第二个参数）的布尔值。**（==在本例中，此值为 false，因为系统已将扩展布局插入 container，而传递 true 值会在最终布局中创建一个多余的视图组==。**



# 使用建议

1、对Fragment传递数据，建议使用`setArguments(Bundle args)`，而后在`onCreate`中使用`getArguments()`取出，在 “内存重启”前，系统会帮你保存数据，不会造成数据的丢失。和Activity的Intent恢复机制类似。

2、使用`newInstance(参数)` 创建Fragment对象，优点是调用者只需要关系传递的哪些数据，而无需关心传递数据的Key是什么。

3、**对于单 Activity + 多 Fragment 的见解**

**优点**：就是**大幅度提升性能降低内存占用**，让用户享受丝滑般的 App。同样的界面 Acitivty 占用的内存比 Fragment 更多，相应速度 Fragment 比 Activity 在低端手机上更快，甚至是好几倍，如果你的app当前或以后有**移植**平板等平台时，可以让你节省大量时间和精力。

**缺点**：Fragment 相比较 Activity 要难用很多，在多 Fragment 以及嵌套 Fragment 的情况下更是如此。更重要的是 Fragment 的坑真的太多了

**结论**： 所以我觉得如果是新产品、创业公司对于 App 的迭代很稳定性更看重，此时应该使用多 Activity 多 Fragment 的架构方案,开发人员更熟悉逻辑，可以更快的产出稳定的 App。如果产品已经稳定，此时可以采用单 Activity + Fragment 的方法优化性能、用户体验。



# Fragment常用方法

## isResumed

## add(), show(), hide(), replace()



## onHiddenChange()

google 老大哥推荐使用 add  show hide 的方法显示隐藏 fragment .

```java
	/**
     * 修改显示的内容 不会重新加载
     * newFragmeent 下一个fragment
     * currentFrament 当前的fragment
     */
    private void switchFragment(Fragment newFragmeent) {
        if (newFragmeent != currentFrament ) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            if (!newFragmeent.isAdded()) { // 判断是否被add过
                // 隐藏当前的fragment，将 下一个fragment 添加进去
     			transaction.hide(currentFrament).add(R.id.layout_content, newFragmeent).commit(); 
            } else {
                // 隐藏当前的fragment，显示下一个fragment
                transaction.hide(currentFrament).show(newFragmeent).commit(); 
            }
            currentFrament = newFragmeent
        }

    }
```

但 add hide show 进行 fragment 切换的时候  fragment  不会走任何的生命周期，无法通过生命周期进行刷新。此时 onHiddenChange() 大展身手

```java
@Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        // 不在最前端显示 相当于调用了onPause();
        if (hidden) { 
            // do sth.
            return;
        }else{  
           // 在最前端显示 相当于调用了onResume();
           //数据刷新做一些自己的事情--你懂得
        }
    }
```



## setUservisibleHint()

使用场景：当 fragment 结合 viewpager 使用的时候 这个方法会调用

```java
@Override
public void setUserVisibleHint(boolean isVisibleToUser) {
    super.setUserVisibleHint(isVisibleToUser);
    if (getUserVisibleHint()) {
   		//界面可见
    } else {
		//界面不可见 相当于onpause
    }
}
```



#  选择正确的 Fragment#commitXXX() 函数

**异常：Can not perform this action after onSaveInstaceState()**
当Actvity在后台被回收的时候会调用`onSaveInstanceState()`方法保存状态和数据，**直到再回到Avtivity之前（`onResume()`）提交FragmentTransaction就会报错**（这种情况一般出现在其他Activity回调让当前页面执行FragmentTransaction的时候）
结局
如何解决

1. `commit()`
   提交不会立即执行，而是排到了主线程等待准备好的时候执行
2. `commitAllowingStateLoss()`
   **允许在Actvity保存状态之后提交，即允许状态丢失。**
   与commit一样但是允许Activity的状态保存之后提交。他的实现是和`commit`几乎一样的，唯一的区别在于`commit`执行之后FragmentManager都会检测是否已经保存了状态（`onSaveInstaceState()`），如果已经保存状态再次使用`commit`会抛出异常：`java.lang.IllegalStateException: Can not perform this action after onSaveInstanceState`
   如果在`onSaveInstanceState()`之后调用`commitAllowingStateLoss()` FragmentManager保存的所有状态可能被丢失，而且重新启动之后在 onSaveInstaceStae() 之后添加和删除的 Fragment 也会丢失。
   **如果说保存状态对于业务需求不重要，则使用`onSaveInstanceState()`**
   使用
3. `commitNow()`
   立即将事务提交
4. `commitNowAllowingStateLoss()`
   立即提交而且允许在保存状态之后提交。



# 内存重启

安卓app有一种特殊情况，就是 app运行在后台的时候，系统资源紧张的时候导致把app的资源全部回收（杀死app的进程），这时把app再从后台返回到前台时，app会重启。这种情况下文简称为：**“内存重启”**。（屏幕旋转等配置变化也会造成当前Activity重启，本质与“内存重启”类似）

在系统要把app回收之前，系统会把Activity的状态保存下来，Activity的FragmentManager负责把Activity中的Fragment保存起来。在“内存重启”后，Activity的恢复是从栈顶逐步恢复，Fragment会在宿主Activity的`onCreate`方法调用后紧接着恢复（`从onAttach`生命周期开始）。



# getActivity() 空指针

可能你遇到过 getActivity() 返回 null，或者平时运行完好的代码，在“内存重启”之后，调用getActivity() 的地方却返回null，报了空指针异常。

大多数情况下的原因：你在调用了 getActivity() 时，当前的 Fragment 已经 `onDetach() `了宿主 Activity。 比如：你在pop了Fragment之后，该Fragment的异步任务仍然在执行，并且在执行完成后调用了getActivity()方法，这样就会空指针。

解决办法：

1. 对于Fragment已经onDetach这种情况，我们应该避免在这之后再去调用宿主Activity对象，比如取消这些异步任务，但我们的团队可能会有粗心大意的情况，所以下面给出的这个方案会保证安全。

2.  使用 getActivity() 时全部进行判空处理。

3. （**个人觉得不太可取**）在Fragment基类里设置一个Activity mActivity的全局变量，在`onAttach(Activity activity)`里赋值，使用mActivity代替`getActivity()`，保证Fragment即使在`onDetach`后，仍持有Activity的引用（有引起内存泄露的风险，但是异步任务没停止的情况下，本身就可能已内存泄漏，相比Crash，这种做法“安全”些），即：

   ```java
   protected Activity mActivity;
   @Override
   public void onAttach(Activity activity) {
       super.onAttach(activity);
       this.mActivity = activity;
   }
   
   /**
   *  如果你用了support 23的库，上面的方法会提示过时，有强迫症的小伙伴，可以用下面的方法代替
   */
   @Override
   public void onAttach(Context context) {
       super.onAttach(context);
       this.mActivity = (Activity)context;
   }
   ```

# Fragment的重叠问题

**原因：内存重启**

 这种情况出现在使用`add\show\hind`时，在异常情况下 Activity会被系统回收，此时Fragmen中的`onSaveInstaceState()`也会被Avtivity的`onSaveInstaceState()`递归调用，在恢复的时候Fragmeng会重新创建。

**解决方案** ：

1. 直接注释掉Activity或者Fragment中的`onSaveInstaceState()` 然后使用`commitAllowingLossSate()`提交事务
2. 在创建的时候的时候添加Tag并存储起来

```java
Fragment fragment = getSupportFragmentManager().findFragmentByTag("FragmentA");
if (fragment == null) {
    mFragmentA = new FragmentA();
    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
    fragmentTransaction.add(R.id.container, mFragmentA, "FragmentA").commit();
} else {
    mFragmentA = (FragmentA) fragment;
}
```



3. 通过savedInstanceState是否null，判断是否已经创建过

```java
if (savedInstanceState == null) {
    mFragmentA = new FragmentA();
    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
    fragmentTransaction.add(R.id.container, mFragmentA, "FragmentA").commit();
} else {
    mFragmentA = (FragmentA) getSupportFragmentManager().findFragmentByTag("FragmentA");
}


```

# 未必靠谱的出栈方法remove()

如果你想让某一个Fragment出栈，使用`remove()`在加入回退栈时并不靠谱。

如果你在add的同时将Fragment加入回退栈：addToBackStack(name)的情况下，它并不能真正将Fragment从栈内移除，如果你在2秒后（确保Fragment事务已经完成）打印`getSupportFragmentManager().getFragments()`，会发现该Fragment依然存在，并且依然可以返回到被remove的Fragment，而且是空白页面。

如果你没有将Fragment加入回退栈，remove方法可以正常出栈。

如果你加入了回退栈，`popBackStack()`系列方法才能真正出栈，这也就引入下一个深坑，`popBackStack(String tag,int flags)`等系列方法的BUG。



# 异常：Can not perform this action after onSaveInstanceState

在你离开当前Activity等情况下，系统会调用`onSaveInstanceState()`帮你保存当前Activity的状态、数据等，**直到再回到该Activity之前（onResume()之前），你执行Fragment事务，就会抛出该异常！**（一般是其他Activity的回调让当前页面执行事务的情况，会引发该问题）

**解决方法：**

- **1、该事务使用commitAllowingStateLoss()方法提交，但是有可能导致该次提交无效！（宿主Activity被强杀时）** 

  对于`popBackStack()`没有对应的`popBackStackAllowingStateLoss()`方法，所以可以在下次可见时提交事务，参考2

- **2、利用onActivityForResult()/onNewIntent()，可以做到事务的完整性，不会丢失事务**

一个简单的示例代码 ：

```java
// ReceiverActivity 或 其子Fragment:
void start(){
   startActivityForResult(new Intent(this, SenderActivity.class), 100);
}

@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
     super.onActivityResult(requestCode, resultCode, data);
     if (requestCode == 100 && resultCode == 100) {
         // 执行Fragment事务
     }
 }

// SenderActivity 或 其子Fragment:
void do() { // 操作ReceiverActivity（或其子Fragment）执行事务
    setResult(100);
    finish();
}
```

# Fragment转场动画

想要给Fragmengt设置进栈和出栈动画，需要使用四个参数的方法`setCustomAnimations(enter exit, popEnter, popExit)` -> `setCustomAnimations(进 exit, popEnter, 出)`，两个参数的方法`setCustomAnimations(enter, exit)`只能设置进场动画.

```java
final FragmentManager fm = getSupportFragmentManager();
final FragmentTransaction ft = fm.beginTransaction();
ft.setCustomAnimations(R.anim.slide_up, R.anim.slide_down, R.anim.slide_up, R.anim.slide_down)
  .add(R.id.fragment_container, new SomeFragment(), FRAGMENT_TAG)
  .addToBackStack(FRAGMENT_TAG)
  .commit();
```

# Fragment的坑

[Fragment全解析系列（一）：那些年踩过的坑](https://www.jianshu.com/p/d9143a92ad94)
[Fragment的那些坑](http://toughcoder.net/blog/2015/04/30/android-fragment-the-bad-parts/)



# 参考

[官方文档](<https://developer.android.com/guide/components/fragments?hl=zh_cn>)

