# Window 和 WindowManger



```java
   /**
     * 获取悬浮框的LayoutPar
     *
     * @return
     */
    private WindowManager.LayoutParams getLayoutParams() {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.format = PixelFormat.RGBA_8888;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
            | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
            | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        layoutParams.windowAnimations = 0;
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        return layoutParams;
    }

mWindowManger.addView(view, getLayoutParams());
```



WindowManager.LayoutParams 中的 falgs 和 type 这两个参数比较重要：

Flags 参数表示 Window 属性，他有很多的选项，通过这些选项可以控制 Window 的显示特征

具体的可以参见《Anroid 开发艺术探索》8 章第一节。