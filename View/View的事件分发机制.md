[toc]



熟读《Android 开发艺术探索》第三章，一个字都不能放过的精读。



# 传递规则

这里要分析的对象实质上就是 MotionEvent。所谓点击事件的分发实质上就是 MotionEvent 事件分发的过程。

点击事件的分发过程有三个重要的方法参与完成 `dispatchTouchEvent`、`onInterceptToucchEvent`、 `onTouchEvent`.



```java
public boolean dispatchTouchEvent(MotionEvent ev)
```

==用来执行事件的分发==。 如果事件能够传递给当前 View， 那么此方法一定会被调用。返回的结果受当前 View 的 onTouchEvent 和下级 View 的 dispatchTouchEvent 方法的影响，表示是否消耗当前事件。



```java
public boolean onInterceptTouchEvent(MotionEvent ev)
```

在 dispatchTouchEvent(MotionEvent ev) 后调用，用于判断是否要拦截事件。==如果当前 ViewGroup ***拦截了***某个事件，那么同一个事件序列挡住，此方法不会被再次调起。==返回结果表示是否拦截事件。

```java
public boolean onTouchEvent(MotionEvent ev)
```

在 dispatchTouchEvent(MotionEvent ev) 调用，用户判断是否处理此事件，返回的结果表示是否消耗当前的事件。==如果不消耗，则同一个事件序列当中此 View 不会再会收到事件==。



三者关系的伪代码

```java
public boolean dispatchTouchEvent(MotionEvent ev) {
  boolean consume = false;
  if (onInterceptTouchEvent(ev)) {
    consume = onTouchEvent(ev);
  } esle {
    consume = child.dispatchToucnEvent(ev);
  }
  
  return consume;
}
```

对于一个 ViewGroup 来说，点击事件产生之后，首先它的 dispatchTouchEvent(MotionEvent ev) 会被调用，如果这个 ViewGroup 的 onInterceptTouchEvent 返回 ture 就表示他需要处理这个事件，接着这个事件就会交由这个 ViewGroup 处理。如果 onInterceptTouchEvent 方法返回 false 则表示不处理此事件，当前事件都将传递给子元素，接着子元素的 dispatchTouchEvent 方法就会被调用，如此反复直接时间传递到了具体 View。



如果一个 View 设置了 OnTouchListener,  那么 OnTouchListener 中的 onTouch 方法会被回调。这时事件如何处理还要看 onTouch 的返回值。**如果返回 false，则当前 View 的 onTouchEvent 方法会回调；如果返回 true，那么 onTouchEvent 将不会被调用**。由此可见，给 View 设置的有==OnTouchClickListener，其优先级比 onTouchEvent 要高==。

当一个点击事件产生后，他的传递顺序是 Activity -> Window -> View。即事件总是先传递给 Activity。Activity 再传递给 Window,最后 Window 在传递给 View。

如果一个 View 的 onTouchEvent 返回 false 那么他父 View 的 onTouchEvent() 将会被调用，以此类推。如果所有元素都不处理这个事件，那么这个容器将会最==终传递给 Activity 处理==

## 结论

1. 一个事件序列是指从手指接触屏幕—— ACTON_DOWN 那一刻，到手指离开屏幕—— ACTON_UP 的时候结束，加上整个过程中产生的无数个 ACTON_MOVE 事件，即 ACTION_MOVE 开始 + 中间无数个 ACTION_MOVE + 最终结尾的 ACTION_UP。

2. 某个 ViewGroup 一旦拦截了 ACTION_DOWN 事件，那么这一个序列的所有事件都只能由他来处理(如果事件序列没有被人为阻断*，*能够传递给它的话)，并且它的 onInterceptTouchEvent 将不会被调用。也就是说==一个 ViewGroup 在成功拦截  ACTION_DOWN  事件后，那么系统会把同一个事件序列内的其他事件直接交由此 ViewGroup 的 onTouchEvent 处理。不会再调用这个 ViewGroup 的 onInterceptTouchEvent 方法询问是否需要拦截==。就算除了 ACTION_DOWN 以外的其他事件， onTouchEvent 都返回 false，那么也不会在询问是否拦截了。
3. 正常情况下一个事件序列只能被一个 View 拦截且消耗，因为一旦一个 ViewGroup 拦截了开始的  ACTION_DOWN 事件，那么同一个事件序列内的所有事件都将会交给它处理。但是我们可以通过特殊手段让一个 View 将本身自己要处理的事情转交给其他 View 处理。
4. 某个 View 开始处理事件，但如果他不能消耗 ACTION_DWON 即此时 onTouchEvent() 返回了 false，那么此事件序列将直接交于父 View 处理(调用父 View 的 onTouchEvent())，此 View 将接收不到此事件序列的其他任何事件。==意识就是说，一个事件序列的开始事件 ACTION_DOWN 交给你，如果你不消耗掉，这个这个序列剩下的事件都不会在交个你处理了，而是转由上级 View 处理==
5. 如果 View 不消耗 ACTION_DOWN  以外的其他事件，那么此这些事件就会消失，而且父 View 的onTouchEvent 并不会被调用。并且当前 View 可以持续收到后续的事件，最终这些消失的事件将传递给 Activity 处理。
6. ViewGroup 默认不拦截任何事件，Anroid 源码中 ViewGroup 的 onInterceptTouchEvent 默认返回 false。
7. View 没有 onIntercepterTouchEvent 方法，一旦事件传递给他，那么他的 onTouchEvent 方法就会调用
8. View 的 onTouchEvent 默认都是会消耗事件的 —— 返回 true。除非他是不可以点击的：==clickAble 和 longClickAble 同时为 false== 默认情况下 longClickable 默认情况都为**false**，clickable要看情况了：Button 默认就是  true，TextView 默认就是 false。==当我们给 View 设置点击事件的 setOnClickLisenter 后 clcikAble 就会自动变为 true==
9. View 的 enable 属性并不能影响 onTouchEvent 的返回值，哪怕一个 View 是 disable 的状态，只要它的 clickable 和 longClickAble 有一个为 true。 那么它的 onTouchEvent 返回的就是 true。 只不过 onClickListener 不会被调用了，原因是下面一条：
10. 我们常用的 OnClickListener 的优先级是最低的，即处于事件传递的尾端，当收到 up 事件的时候才会触发。触发的前提是当前 View 是 enable 的并且是可点击的（ clickAble = true），并且它收到了 down 和 up 的事件。
11. 事件传递是有外向内的，即事件总是先传递给父元素，然后再父元素分发给子 View，通过 requestDisallowInterceptTouchEvent 方法可以在子元素中干预父元素对于事件的分发过程，==但是 ACTION_DOWN  事件除外==。



**足以可见 ACTION_DOWN 是整个事件序列分发过程的关键。**

# 源码分析

## 顶级 View 的分发过程

首先回顾一下点击事件分发过程：点击事件到达顶级 View(一般是 ViewGroup)以后，会调用 ViewGroup 的 `dispatchTouchEvent`方法，然后逻辑分为两种可能：

1. 如果此 ViewGroup 的`onInterceptTouchEvent`返回 true 决定拦截事件，则此事件将交由此 ViewGroup 处理，正常情况下 `onInterceptTouchEvent` 方法将不会再被调用:

   情况 A：如果设置了 OnTouchListener，由于 OnTouchListener#onTouch 的优先级更高于 onTouchEvent 则 onTouch 将率先被调用。此时事件的如何情况要看 onTouch 的返回值。**如果返回 false，则当前 View 的 onTouchEvent 方法会回调；如果返回 true，那么 onTouchEvent 将不会被调用**

   情况 B：如果没有设置 OnTouchListener onTouchEvent 直接被调用。如果在 onTouchEvent 中设置了 mOnClickListener ,则 onClick **可能**会被调用，原因见上文 8、9、10 条结论。

   

2. 如果此 ViewGroup 不拦截事件，则事件会传递给他所在点击事件链上的 View，这时子 View 的   `dispatchTouchEvent` 方法会被调用。到此为止，事件已经从顶级 View 传递给下一层 View，接下来的传递规则和顶级 View 是一致的，如此循环，知道完成整个事件的分发。

### dispatchTouchEvent(MotionEvent ev)

````java
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        …………
        // 第一部分：Handle an initial down.
        if (actionMasked == MotionEvent.ACTION_DOWN) {
            // Throw away all previous state when starting a new touch gesture.
            // The framework may have dropped the up or cancel event for the previous gesture
            // due to an app switch, ANR, or some other state change.
            cancelAndClearTouchTargets(ev);
            resetTouchState();
        }
      
        // 第二部分：Check for interception.
        final boolean intercepted;
        if (actionMasked == MotionEvent.ACTION_DOWN
                || mFirstTouchTarget != null) {
            // 不允许拦截 
            final boolean disallowIntercept = (mGroupFlags & FLAG_DISALLOW_INTERCEPT) != 0;
            if (!disallowIntercept) {
              // 开始调用 onInterceptTouchEvent
                intercepted = onInterceptTouchEvent(ev);
                ev.setAction(action); // restore action in case it was changed
            } else {
                intercepted = false;
            }
        } else {
            // There are no touch targets and this action is not an initial down
            // so this view group continues to intercept touches.
            intercepted = true;
        }
        …………
    }
````

在 `dispatchTouchEvent` 方法中我们可以清晰的看到 `onInterceptTouchEvent` 的调用时机（第二部分）：

**一个 ViewGroup onIntercepterTouchEvent 的调用时机：**

> 1.  接收到 ACTION_DOWN 事件;
> 2.  事件交给他的子 View 处理 -》 mFirstTouchEvent != null。

**onIntercepterTouchEvent 不调用时机：**

> 在接收到 ACTION_DOWN 事件成功拦截后。



有一个特殊情况：通过` requestDisallowInterceptTouchEent` 设置的标志位：`FLAG_DISALLOW_INTERCEPT`。一般用于子 View  中用于请求父 ViewGroup 不要拦截事件(作用域在 onIntercepterTouchEvent 方法中)。当设置成功以后 ViewGroup 将无法拦截除了 ACTION_DOWN 以外的其他事件，原因是 dispatchTouchEvent 在接收到 ACTION_DOWN 事件以后会重置 Flag。(第一部分)

==当然这东西有个前提就是父 ViewGroup 不能成功拦截 ACTION_DOWN 。如果拦截了父 ViewGroup 的onIntercepterTouchEvent 就不会在调用，何谈不要拦截。==



我们可以得到结论： `onInterceptTouchEvent` 并不会每次都调用，如果想提前处理所有点击事件，需要选择 `dispatchTouchEvent`方法。只有这个方法才能保证每次都会调用，当然前提是事件能够传递到它。



### onInterceptTouchEvent(MotionEvent ev)

## View 对于点击事件的处理

setOnLongClickListener中return的值决定是否在长按后再加一个短按动作。

以下代码只会执行长按事件

```java
mBtn.setOnLongClickListener(new OnLongClickListener() {
    @Override
    public boolean onLongClick(View v) {
        return true;
    }
});
```

当return返回值为true的时候，代表这个事件已经消耗完了，会自动讲一个震动效果

返回值为false的时候他还会继续传递，结果再加上一个 OnClick事件。==但是实践证明如果弹出的是一个 Dialog 那么 onClick 不会调用，但是在 onLongClick 里面加上短点就会调用，原因未知==



其次，Activity中的onKeyDown也是如此。

# 滑动冲突解决方案

常见的场景有三种：

1. 内外滑动方向不一致。
2. 内外滑动方向一致。
3. 上面两种情况都有。

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

* **ACTION_DOWN :这个事件，父容器必须返回 false **，既不拦截事件，一旦父容器拦截了ACTION_DOWN 事件，这个事件序列都将直接交由父容器处理，此时 `onInterceptTouchEvent` 将不会被调用，子 View 也就没有机会处理事件了。

* ACTION_MOVE: 这个事件就需要根据业务逻辑分类处理了，父容器要是想处理就返回 true，要交由子 View 处理就返回 false。

* **ACTION_UP: 这个事件必须返回 false**，因为如果父容器在 ACTION_UP 时返回了 true，子 View 就无法接收到 ACTION_UP 事件，导致子 View onClick 事件无法触发。==但是父容器不一样，一旦他开始拦截了一个事件，那么后续的所有事件都将会跳过  `onIntercepttouchEvent` 方法判断，直接交由自己处理，而作为最后的一个事件的 ACTION_UP 当然也是，即便父容器的 `onIntercepttouchEvent`方法在 ACTION_UP 的时候返回了 false，因为根本就不会走==。

### 总结:

外部拦截法的关进是讲：在 ViewGroup#onInterceptTouchEvent 中 ACTION_DOWN、ACTION_UP 不能做拦截，必须返回 false，ACTION_DOWN 是让所有的事件都可以传递到 child 中，否者 ACTION_DOWN 一旦被拦截（返回 true）则  ViewGroup#onInterceptTouchEvent 将不会在执行。而 ACTION_UP 不做拦截是为了保证 child 可以正常接受到它从而触发 onClick 方法，ViewGroup 如果处理了事件，那么 ViewGroup#onInterceptTouchEvent 将不会在执行，不会扰乱正常的时间流程。ACTION_MOVE 中就是具体的业务逻辑了，在不同的条件下判断将事件交给谁去处理 => child 处理 返回 false 不拦截，父容器要处理返回 true ，此后 ViewGroup#onInterceptTouchEvent 将不会被调用了。



这种处理方式应该比较常用了，符合正常事件分发机制。



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

### 总结：

内部拦截法是一种特殊的方式，通过 `requestDisallowInterceptTouchEvent(true)`干预事件分发过程。

**`requestDisallowInterceptTouchEvent(true)` 的作用是请求父容器不要拦截除了 ACTION_DOWN 以外的事件**

设置为 true 后，所有的事件都将交由 child 处理并且 ViewGroup#onInterceptTouchEvent() 将被跳过不再调用（原因在上面 dispatchTouchEvent 源码分析：//不允许拦截部分 ）。为了让你 child 获得 ACTION_DOWN 事件，ViewGroup#onInterceptTouchEvent() 就不能拦截 ACTION_DOWN 事件（返回 false），在 ACTION_MOVE 中根据业务逻辑判断将事件传递给谁，如果 ViewGroup 需要处理，则将  `requestDisallowInterceptTouchEvent(false)` 设置为 false，让  ViewGroup#onInterceptTouchEvent() 重新调用，为了能够正常处理事件，需要将接收到的所有事件拦截（返回 true）

## 总结

- 当我们滑动==方向不同的时候，采用外部解决法和内部解决法，复杂度差不多==。
- 当我们滑动的==方向相同的话，建议采用内部解决法来解决==，因为采用外部解决法复杂度比较高。而且有时候我们是采用别人的开源控件，这时候去修改别人的源码可能会发生一些意想不到的bug。

# 滑动冲突实战

[ViewPager，ScrollView 嵌套ViewPager滑动冲突解决](https://blog.csdn.net/gdutxiaoxu/article/details/52939127)





# 问：有一个ViewGroup, 然后手指头接触Button,手指头滑开了,滑开又松手的过程,整个事件发生了什么?经历了什么?

答：

一开始 ViewGroup 会接受到整个事件序列的第一个事件：ACTION_DOWN，ViewGroup#dispatchTouchEvent 收到 ACTION_DOWN 后开始询问 ViewGroup#onInterceptTouchEvent 是否需要拦截，默认情况下 ViewGroup#onInterceptTouchEvent 返回 false 不拦截，开始向下传递 ACTION_DOWN 事件，Buttton#dispatchTouchEvent 收到 ACTION_DOWN 询问 onTouchEvent 是否处理，Button 默认处理，~~此后的所有事件序列都直接跨过 ViewGroup#onInterceptTouchEvent 的判断直接传递给 Button，但 ViewGroup#dispatchTouchEvent 会收到所有事件。~~（此时 ViewGroup#onInterceptTouchEvent 每次都会调用，而如果此事件被 ViewGroup 自己消耗了，则 ViewGroup#onInterceptTouchEvent才不会调用）

在 move 过程中 Button#onTouchEvent 发现当前坐标已经移出 Button 区域，会 remove 掉 onClick 的回调(源码位于 View#onTouchEvent 尾部 case MotionEvent.ACTION_MOVE 中)，虽然 Button 收到并处理了 ACTION_DOWN -> ACTION_MOVE -> ACTION_UP 整个事件过程，但是并不会触发 onClick 回调。

这个事件过程并没有网上所说的 ACTION_CANCEL，ACTION_CANCEL 出现的条件是：==ViewGroup 在传递过程中拦截了本应交由 Button 处理的事件，此时 Button 会收到 ACTION_CANCEL 表示事件中断==



