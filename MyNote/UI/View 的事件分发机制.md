[toc]

# 点击事件的传递规则

一定要熟读：《Android 开发艺术探索 3.4节》

* 如果一个 View 设置了 OnTouchListener,  那么 OnTouchListener 中的 onTouch 方法会被回调。这时事件如何处理还要看 onTouch 的返回值。如果返回 false，则当前 View 的 onTouchEvent 方法会回调；如果返回 true，那么 onTouchEvent 将不会被调用。由此可见，给 View 设置的有 OnToouchClickListener，其优先级比 onTouchEvent 要高。
* 我们常用的 OnClickListener 的优先级是最低的，即处于事件传递的尾端。
* 事件的传递顺序： Activity -> Window -> View。事件总是先传递给 Activity，Activity 在传递给 Window，最后 Window 再传递给顶级 View。
* 如果一个 View 的 onTouchEvent 返回了 false， 那么它的父容器的 onTouchEvent 将会调用，以此类推。所有所有的元素都不处理这个事件，那么这个事件最终会传递给 Activity 处理。
* 同一个事件序列：当手指触摸屏幕的那一刻起，到手指离开屏幕的那一刻接受，在这个过程中产生的一系列事件，这个事件序列以 down 事件开始，中间还有不确定数量的 move 事件，最终以 up 事件结束。
* 正常情况下，一个事件序列只能被一个 View 拦截且消耗。



1. onInterceptTouchEvent 不是每次事件都会被调用，如果我们想提前处理所有的点击事件，要选择 dispatchTouchEvent 方法。只有这个方法才能保证每次都会调用，当然前提是事件能够传递到当前的 ViewGroup；
2. 正在觉得 View 不响应点击事件的是 clickAble 和 longClickAble 都不可以点击，只要有一个返回 ture ，都会相应点击事件

# 滑动冲突解决方案

## 1.外部拦截法

指点击事件都要经过父容器的拦截处理，如果父容器需要此事件就拦截，如果不需要此事件就不拦截，外部拦截法需要重写 `onIntercepttouchEvent` 方法，在内部做相应的拦截。

代码模板：

```java
   /**
     * 滑动冲突解决方案 1 ：外部拦截法
     *
     */
    @Override public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean intercept = false;
        float x = ev.getX();
        float y = ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // ACTION_DOWN 事件必须返回 false，否则以后所有的事件都会被自身器拦截，子 View 无法接受到事件
                intercept = false;
                break;
            case MotionEvent.ACTION_MOVE:
                if (父容器需要此点击事件) {
                    intercept = true;
                } else {
                    intercept = false;
                }
                break;
            case MotionEvent.ACTION_UP:
                // 这里必须返回 false
                intercept = false;
                break;
            default:

                break;
        }
        mLastTouchX = x;
        mLastTouchY = y;
        return intercept;
    }
```

* **ACTION_DOWN :这个事件，父容器必须返回 false **，既不拦截事件，一旦父容器拦截了 ACTION_DOWN 事件，这个事件序列都将直接交由父容器处理，此时 `onIntercepttouchEvent` 将不会被调用，子 View 也就没有机会处理事件了。

* ACTION_MOVE: 这个事件就需要根据业务逻辑分类处理了，父容器要是想处理就返回 true，要交由子 View 处理就返回 false。

* **ACTION_UP: 这个事件必须返回 false**，因为如果父容器在 ACTION_UP 时返回了 true，子 View 就无法接收到 ACTION_UP 事件，导致子 View onClick 事件无法触发。但是父容器不一样，一旦他开始拦截了一个事件，那么后续的所有事件都将会跳过  `onIntercepttouchEvent` 方法判断，直接交由自己处理，而作为最后的一个事件的 ACTION_UP 当然也是，即便父容器的 `onIntercepttouchEvent`方法在 ACTION_UP 的时候返回了 false，因为根本就不会走。



## 2. 内部拦截法

内部拦截法就是所有的事件都交由子 View 处理判断，需要处理直接消耗事件，不需要则交由父容器。这种方法比较复杂，需要配合 `requestDisallowInterceptTouchEvent` 方法才能正常工作，使用起来较外部拦截法稍显复杂。

代码模板：

```java
    /**
     * 内部拦截法：part 1
     * 在子 View 中重写方法。
     */
    @Override public boolean dispatchTouchEvent(MotionEvent ev) {
        float x = ev.getX();
        float y = ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                getParent().requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_MOVE:
                if (父容器需要此点击事件) {
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
                break;
            case MotionEvent.ACTION_UP:
            default:
                break;
        }
        mLastTouchX = x;
        mLastTouchY = y;

        return super.dispatchTouchEvent(ev);
    }
```

父容器要默认拦截除了 ACTION_DOWN 以外的其他事件，这样只有当子 View 调用 `requestDisallowInterceptTouchEvent(false)`的时候，父 View 才能继续拦截到所需的事件。

```java
    /**
     * 内部拦截法：part 2
     * 在父 View 中重写方法。
     */
    @Override public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (MotionEvent.ACTION_DOWN == ev.getAction()) {
            return false;
        } else {
            return true;
        }
    }
```

**注意** 父容器不能拦截 ACTION_DOWN 方法，因为 ACTION_DOWN 这个事件并不受 `requestDisallowInterceptTouchEvent ` 方法内的 FLAG_DISALLOW_INTERCEPT 这个标记位的控制。所以一旦父容器拦截 ACTION_DOWN 事件，那么所有的事件都无法传递到子元素中，这样内部拦截就失效了。



# 问：有一个ViewGroup, 然后手指头接触Button,手指头滑开了,滑开又松手的过程,整个事件发生了什么?经历了什么?

答：

一开始 ViewGroup 会接受到整个事件序列的第一个事件：ACTION_DOWN，ViewGroup#dispatchTouchEvent 收到 ACTION_DOWN 后开始询问 ViewGroup#onInterceptTouchEvent 是否需要拦截，默认情况下 ViewGroup#onInterceptTouchEvent 返回 false 不拦截，开始向下传递 ACTION_DOWN 事件，Buttton#dispatchTouchEvent 收到 ACTION_DOWN 询问 onTouchEvent 是否处理，Button 默认处理，此后的所有事件序列都直接跨过 ViewGroup#onInterceptTouchEvent 的判断直接传递给 Button，但 ViewGroup#dispatchTouchEvent 会收到所有事件。

在 move 过程中 Button#onTouchEvent 发现当前坐标已经移出 Button 区域，会 remove 掉 onClick 的回调(源码位于 View#onTouchEvent 尾部 case MotionEvent.ACTION_MOVE 中)，虽然 Button 收到并处理了 ACTION_DOWN -> ACTION_MOVE -> ACTION_UP 整个事件过程，但是并不会触发 onClick 回调。

这个事件过程并没有网上所说的 ACTION_CANCEL，ACTION_CANCEL 出现的条件是：ViewGroup 在传递过程中拦截了本应交由 Button 处理的事件，此时 Button 会收到 ACTION_CANCEL 表示事件中断