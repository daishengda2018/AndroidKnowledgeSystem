# 前言

Android 中最常见的控件 TextView 却是一个大胖子, 在 Android API 28 中，TextView 已经超过 1.2W行代码十足是个庞然大物。

![image-20200601184903088](images/轻量级TexView/image-20200601184903088.png)



而且 setTextView 方法的内部流程也是复杂的：设置各种 Listener、或者需要重现创建一个SpannableString，还需要根据情况重新创建Text Layout，这些操作让调用一次 setText 方法非常耗时，在展示大量文本的时候很容易造成卡顿。常见的常见例如 Feed 流、直播间留言等场景。可见 TextView 是个需要优化的痛点。



在 setText中，我们可以将耗时操作分为两部分：

1. 繁琐的准备工作例如设置Listener，处理的字符串、创建负责渲染的 Layout，而后触发 *onMeasure*、 *makeNewLayout* 方法，有兴趣的朋友可以去看看源码，看看 TextView的 *onMeasure*、 *makeNewLayout* 的过程有多复杂：一个方法动不动就上百行、if else 一层又一层。
2. 文字的测量、View 的测量、绘制

# TextView渲染基本原理

总的来说 TextView 的渲染都有 Layout 完成，TextView 根据文本的情况选择性创建 Layout。而 Layout 具体有三个实例：

## BoringLayout

[`BoringLayout`](https://developer.android.com/reference/android/text/BoringLayout.html)用于在视图上绘制单行文本。之所以称为“Boring”，是因为它只处理一行从左到右的文本，而没有诸如emoji表情这样的有趣字符。这种简化允许类`onDraw`使用比默认值更有效的逻辑进行覆盖。如果您想自己看看，[这里是源代码](https://github.com/android/platform_frameworks_base/blob/master/core/java/android/text/BoringLayout.java)。

## DynamicLayout

绘制单行文本且内容Spannable的时候，TextView就会使用它来负责文本的显示，在内部设置了SpanWatcher，当检测到span改变的时候，会进行reflow，重新计算布局。

## StaticLayout

当文本为非单行文本，且非Spannable的时候，就会使用StaticLayout，内部并不会监听span的变化，因此效率上会比DynamicLayout高，只需一次布局的创建即可，但其实内部也能显示SpannableString，只是不能在span变化之后重新进行布局而已。



TextView 的*makeNewLayout* 方法主要工作就是更具内容选择创建Layout，并完成字符的测量。

TextView 的绘制核心就是上述的三个 Layout，所以很早就有了对于 setText 第一步耗时（复杂设置流程）的解决方案：使用 StaticLayout 自定义View ，将绘制流程极简化 [Drawing multiline text to Canvas on Android](https://medium.com/over-engineering/drawing-multiline-text-to-canvas-on-android-9b98f0bfa16a)。

## TextLayoutCache

对于 setTex t第二部分的性能问题，早在 Android 4.0版本 Google 就给出了一个解决方案：TextLayoutCache。

Canvas在`drawText`的时候，如果需要每次都计算字体的大小，边距等之类的话，就会非常耗时，导致drawText时间会拉的很长，为了提高效率，android在4.0之后引入了TextLayoutCache，使用LRU Cache缓存了字形，边距等数据，提升了drawText的速度，在4.4中，这个cache的大小是0.5M，全局使用，并且会在Activity的`configurationChanged`, `onResume`, `lowMemory`, `updateVisibility`等时机，会调用`Canvas.freeTextLayoutCache`来释放这部分内存。由于这部分的cache是系统底层控制的，我们无法做具体的控制。

虽然 Google 为 Layout 提供了缓存，但是第一次创建 Layout 所带来的耗时，还是很明显的

## PrecomputedText

在 Android 9.0 的 Api 中 Google 有提供了一套预计在字符预加载逻辑：PrecomputedText，还有一个能够向下兼容到 API 14  的  [PrecomputedTextCompat](https://developer.android.com/reference/androidx/core/text/PrecomputedTextCompat) 

将具体文字加载后在调用 setText 具可以缩减百分之90的时间。



![img](images/轻量级TexView/0*scMKbx3mMo4hya7x.png)



具体的测试和使用方法可以阅读这篇文章[What is new in Android P — PrecomputedText](https://medium.com/appnroll-publication/what-is-new-in-android-p-precomputedtext-2a62ec9e8613)。

总的来说 PrecomputedText 、PrecomputedTextCompat 就是在异步场景下提前加载字符串，并完成测量、分割等工作。但二者的实现原理不同：

1. PrecomputedText 作为新 API 直接修改了 StaticLayout的内部实现，StaticLayout内部识别到字符串是 PrecomputedText 类型的（PrecomputedText 是 Spannable子类）直接使用内部计算好的数据，不在重复计算。
2. PrecomputedTextCompat 作为兼容产品，继续沿用 TextLayoutCache 让 TextView 内部提前命中缓存。



### PrecomputedText 简单源码分析

PrecomputedText 的关键方法

```java
    /** @hide */
    public static ParagraphInfo[] createMeasuredParagraphs(
            @NonNull CharSequence text, @NonNull Params params,
            @IntRange(from = 0) int start, @IntRange(from = 0) int end, boolean computeLayout) {
        ArrayList<ParagraphInfo> result = new ArrayList<>();

        Preconditions.checkNotNull(text);
        Preconditions.checkNotNull(params);
        final boolean needHyphenation = params.getBreakStrategy() != Layout.BREAK_STRATEGY_SIMPLE
                && params.getHyphenationFrequency() != Layout.HYPHENATION_FREQUENCY_NONE;

        int paraEnd = 0;
        for (int paraStart = start; paraStart < end; paraStart = paraEnd) {
            paraEnd = TextUtils.indexOf(text, LINE_FEED, paraStart, end);
            if (paraEnd < 0) {
                // No LINE_FEED(U+000A) character found. Use end of the text as the paragraph
                // end.
                paraEnd = end;
            } else {
                paraEnd++;  // Includes LINE_FEED(U+000A) to the prev paragraph.
            }

            result.add(new ParagraphInfo(paraEnd, MeasuredParagraph.buildForStaticLayout(
                    params.getTextPaint(), text, paraStart, paraEnd, params.getTextDirection(),
                    needHyphenation, computeLayout, null /* no hint */,
                    null /* no recycle */)));
        }
        return result.toArray(new ParagraphInfo[result.size()]);
    }
```

`createMeasuredParagraphs` 将输入的文字按照 ‘/n’ 分组后交由 `MeasuredParagraph.buildForStaticLayout`处理，再将处理好的结果装到 ArrayList 中返回.

` MeasuredParagraph.buildForStaticLayout` 内部使用 Native 方法，结合 bid 算法（将字符传反转，例如 RTL）等文字属性，将文字分段后返回。



既然指名道姓的使用了`buildForStaticLayout`方法，这就去看看 StaticLayout 内部是怎么使用这个预加载。

```````````java
void generate(Builder b, boolean includepad, boolean trackpad) {
  ......
        PrecomputedText.ParagraphInfo[] paragraphInfo = null;
        final Spanned spanned = (source instanceof Spanned) ? (Spanned) source : null;
        if (source instanceof PrecomputedText) {
            PrecomputedText precomputed = (PrecomputedText) source;
          // 检查结果是否可用
            final @PrecomputedText.Params.CheckResultUsableResult int checkResult =
                    precomputed.checkResultUsable(bufStart, bufEnd, textDir, paint,
                            b.mBreakStrategy, b.mHyphenationFrequency);
            switch (checkResult) {
                case PrecomputedText.Params.UNUSABLE:
                    break;
                case PrecomputedText.Params.NEED_RECOMPUTE:
                    final PrecomputedText.Params newParams =
                            new PrecomputedText.Params.Builder(paint)
                                .setBreakStrategy(b.mBreakStrategy)
                                .setHyphenationFrequency(b.mHyphenationFrequency)
                                .setTextDirection(textDir)
                                .build();
                    precomputed = PrecomputedText.create(precomputed, newParams);
                    paragraphInfo = precomputed.getParagraphInfo();
                    break;
                case PrecomputedText.Params.USABLE:
                    // 可以使用，直接赋值
                    // Some parameters are different from the ones when measured text is created.
                    paragraphInfo = precomputed.getParagraphInfo();
                    break;
            }
        }

        if (paragraphInfo == null) {
            final PrecomputedText.Params param = new PrecomputedText.Params(paint, textDir,
                    b.mBreakStrategy, b.mHyphenationFrequency);
            paragraphInfo = PrecomputedText.createMeasuredParagraphs(source, param, bufStart,
                    bufEnd, false /* computeLayout */);
        }
  ......
}
```````````

原来 StaticLayout 内部是借助 PrecomputedText 来计算文字的，如果传入的内容是 PrecomputedText 类型 并可用，那么就不在测量了，从而达到预加载。



<font color = red>但是 StaticLayout  的源码看罢，发现 StaticLayout 的核心方法 `generate`、`out` 发现这两个方法也很复杂，要是能把 StaticLayout 初始化的过程直接放到异步线程去操作就完美了</font>





值得注意的是，在使用的时候需要我们手动开启子线程，并将结果切换到主线程后调用 setTextView。

掘金上有一篇文章也很不错：[Android 9.0中的新功能 - PrecomputedText](https://juejin.im/post/5cb3e62ae51d456e2809fb72) 但是作者使用错了:

> 通过调用`consumeTextFutureAndSetBlocking`方法的`future.get()`<font color = red>**阻塞**</font>计算线程来获取计算结果，最终setText到对用的TextView上。

作者认识到了 `future.get()` 是会阻塞线程的，但是还在主线程中直接使用，可不越优化越卡！！！

### 对于 Recyclerview 的优化

 TextView 造成卡顿的场景基本上都源自列表，像是文本阅读器这种常见也是有的。但我们接触最多的就是在 RecyclerView 中展示文本。

 RecyclerView 在 API 25 引入了一个Prefetch ——  预取机制。关于这个机制的原理可以阅读： [RecyclerView Prefetch](https://medium.com/google-developers/recyclerview-prefetch-c2f269075710) 、[中文翻译](https://juejin.im/entry/58a30bf461ff4b006b5b53e3)

总的来说就是 RecyclerView 默认会在 ViewHolder 展示前几帧提前调用 `onBindViewHolder` 将数据绑定完成，这样可以减少展示时绑定数据造成的掉帧、不连续性。

```kotlin
onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
  holder.itemView.textView.text = "Hello"
}
```



所以在 `onBindViewHolder` 内使用 PrecomputedText、PrecomputedTextCompat 异步提前加载字符串，是个很好的时机

```kotlin
onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
  val textView = holder.itemView.textView
  val mParams = textView.textMetricsParams
  val ref = WeakReference(textView)
  
  GlobalScope.launch(Dispatchers.Default) {
    // worker thread
    val pText = PrecomputedText.create("Hello", mParams)
    GlobalScope.launch(Dispatchers.Main) {
      // main thread
      ref.get()?.let { textView ->
        textView.text = pText
      }
    }
  }
}
```



# 基于以上方案自定义 AwesomeTextView

调研完毕、开始实现 AwesomeTextView。

带着两个目标开始做：

1. 自定义 View 简化功能，较少 setTextView 中间复杂过程以及onMasure 、创建 Layout 的过程（直接使用 StaticLayout 绘制）
2. 异步创建并 cache StaticLayout

而经过调研之后确认此方案不会对 Recyclerview 造成影响。





参考

[TextView预渲染研究]([https://github.com/hehonghui/android-tech-frontier/blob/master/issue-21/TextView%E9%A2%84%E6%B8%B2%E6%9F%93%E7%A0%94%E7%A9%B6.md](https://github.com/hehonghui/android-tech-frontier/blob/master/issue-21/TextView预渲染研究.md))

 [PrecomputedText New API in Android Pie](https://medium.com/mindorks/precomputedtext-new-api-in-android-pie-74eb8f420ee6)