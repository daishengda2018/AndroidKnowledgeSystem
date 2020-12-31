# Activity 的视图结构

在每一个 Activity 中都包含了一个 Window，而这个 Window 通常上是由PhoneWindow 实现的，而 PhoneWindow 又将 DecorView 设置为整个界面的根布局，DecorView 作为根布局将要显示的具体内容呈现在 PhoneWindow上，并提供了一些通用方法来操作界面。这里所有 View 的交互事件都由WindowManagerService（WMS）进行接收，并通过 Activity 回调相应的onClickListener。
![UI界面架构图](https://imgconvert.csdnimg.cn/aHR0cDovL3VwbG9hZC1pbWFnZXMuamlhbnNodS5pby91cGxvYWRfaW1hZ2VzLzcyNzc5MC1iMzY5YzNjOGIxODQxMDc1LnBuZw?x-oss-process=image/format,png)

在上面的视图上我们可以看到此时屏幕被分成了两部分：TitleView与ContentView。如图红色的区域就是ContentView，contentView是一个ID为content 的 Framelayou 这也是我们通过布局文件可以控制的区域，实际上我们所有的布局都设置在这样的 Fragmelayout 中。
![](https://imgconvert.csdnimg.cn/aHR0cDovL3VwbG9hZC1pbWFnZXMuamlhbnNodS5pby91cGxvYWRfaW1hZ2VzLzcyNzc5MC00MGNlYTZmM2ZjZjEyYmQ0LnBuZw?x-oss-process=image/format,png)
这也就是为什么Activity、Fragment中设置根布局的方法叫做setContentView了。

**插播： requestWindowFeature(Window.FEATURE_NO_TITLE) 与 setContentView() 调用顺序的关系**

> 在设置setContentView()方法**之前**我们可以通过`requestWindowFeature(Window.FEATURE_NO_TITLE)`方法设置标签来显示全屏。如果你看了Activity源码中的setContentVeiw()方法你会发现，当setContentView()一旦调用，ContentView布局与TitleView会同时被加载，加载之后在调用`requestWindowFeature(Window.FEATURE_NO_TITLE)`方法设置标签已经没有作用了。所以只有在`setContentView()`方法之前设置标签才能剔除`TitleView`达到`ContentView`占据全屏的效果。(更详细的说明请参见[【Android View源码分析（一）】setContentView加载视图机制深度分析](http://blog.csdn.net/qq_23191031/article/details/77172090))

![视图树](https://imgconvert.csdnimg.cn/aHR0cDovL3VwbG9hZC1pbWFnZXMuamlhbnNodS5pby91cGxvYWRfaW1hZ2VzLzcyNzc5MC0yZWU1NzJhOTE3MjllZWUyLnBuZw?x-oss-process=image/format,png)
当Acitivity的生命周期中，当onCreate()方法中调用setContentView方法后，ActivityManagerService（AMS）会调用onResume()方法，此时系统才会把整个DecorView添加到PhoneWindow中显示出来，至此界面回执完成。

**贴一张图汇总一下吧**
![总结](https://imgconvert.csdnimg.cn/aHR0cDovL3VwbG9hZC1pbWFnZXMuamlhbnNodS5pby91cGxvYWRfaW1hZ2VzLzcyNzc5MC00ZWI3YjgyYmE2NWI0M2FjLnBuZw?x-oss-process=image/format,png)

更详细的说明请参见[【Android View源码分析（一）】setContentView加载视图机制深度分析](http://blog.csdn.net/qq_23191031/article/details/77172090)