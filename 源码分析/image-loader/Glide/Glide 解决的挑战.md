# 图片加载临的挑战：

1. OOM 异常
2. 图片加载速度缓慢
3. UI 变得卡段，不能顺滑滚动



# Glide 是如何解决问题的

## 1、OOM异常

自动降低采样率，压缩图片体积

>GlideApp.with(context).load(url).into(imageView);

如果直接使用 ImageView 作为 int() 方法的参数，则 Glide 会自动知道它的尺寸。这样就可以降低采样率，不需要整个图片全部加载进内存。通过这种方式，可让 bitmap 使用更少的内存。

## 2、加载慢

加载慢的主要原因之一是:就算一个 View 没有展示在 Window 上，我们也不会取消下载、bitmap 进行解码任务。这导致了很多我们并不需要的任务也在执行。所以加载到窗口中的时间图像需要花费一些时间。而这个问题 Glide 有很好的的处理：他会监听 Activity 和 Fragment 的生命周期，因此他能够知道取消哪些图像下载任务。



不仅如此 Glide 提供了另外一种提升速度的方式就是增加缓存：这样就不需要一次又一次的解码图像。Glide 提供了两个可配置大小的缓存空间：

1. memory cache
2. disk cache

> **When we provide the URL to the Glide, it does the following:**
>
> 1. It checks if the image with that URL key is available in the memory cache or not.
> 2. If present in the memory cache, it just shows the bitmap by taking it from the memory cache.
> 3. If not present in the memory cache, it checks in the disk cache.
> 4. If present in the disk cache, it loads the bitmap from the disk, also puts it in the memory cache and load the bitmap into the view.
> 5. If not present in the disk cache, it downloads the image from the network, puts it in the disk cache, also puts it in the memory cache and load the bitmap into the view.
>
> This way it makes loading fast as showing directly from the memory cache is always faster.

## 3、不流畅的 UI

UI 不流畅的原因通常是主线程执行了太多的任务。主线程会每 16ms 更新一次 UI，如果在主线程做的任务较重，就会造成丢帧、卡顿。

加载 bitmap 比较特殊：就算我们在子线程加载它，也会造成 UI 不流畅，这是因为 bitmap 体积很大，这回导致 GC 运行非常频繁。GC 运行的时候会 Stop-the-world，频繁的 GC 会让迫使主线程跳过很多帧。因此 GC 是罪魁祸首。



Glide 是如何解决这个问题的呢？答案是池化：BitmapPool。通过使用 BitmapPool，它避免了在应用程序中连续分配内存、释放内存，减少 GC 调用次数减小开销，让应用程序更加平滑。

这样做的原理是什么？原理是通过 bitmap 的`inBitmap`属性，达到重用的内存的效果。



>假设我们必须在Android应用程序中加载一些位图。
>
>假设我们必须一张一张地加载两个位图（bitmapOne，bitmapTwo）。当我们加载bitmapOne时，它将为bitmapOne分配内存。然后，如果当我们不再需要bitmapOne时，不要回收该位图（因为回收涉及调用GC）。而是，将此bitmapOne用作[bitmapTwo](https://developer.android.com/reference/android/graphics/BitmapFactory.Options.html#inBitmap)的inBitmap。这样，可以将相同的内存重用于bitmapTwo。

```java
Bitmap bitmapOne = BitmapFactory.decodeFile(filePathOne);
imageView.setImageBitmap(bitmapOne);
// lets say , we do not need image bitmapOne now and we have to set // another bitmap in imageView
final BitmapFactory.Options options = new BitmapFactory.Options();
options.inJustDecodeBounds = true;
BitmapFactory.decodeFile(filePathTwo, options);
options.inMutable = true;
options.inBitmap = bitmapOne;
options.inJustDecodeBounds = false;
Bitmap bitmapTwo = BitmapFactory.decodeFile(filePathTwo, options);
imageView.setImageBitmap(bitmapTwo);
```

> 因此，我们在解码bitmapTwo时重用了bitmapOne的内存。
>
> 这样，我们不允许一次又一次地调用GC，因为我们没有遗漏bitmapOne的引用，而是将bitmapTwo加载到bitmapOne的内存中。
>
> ==重要的一点是，bitmapOne的大小应等于或大于bitmapTwo，以便可以重新使用bitmapOne的内存。==

