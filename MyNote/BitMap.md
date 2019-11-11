[toc]

# Bitmap 的高效加载

## 如何加载 Bitmap

使用 BitmapFactory#decodeXXXX 方法。

```java
   BitmapFactory.decodeResource();
   BitmapFactory.decodeByteArray();
   BitmapFactory.decodeFile();
   BitmapFactory.decodeStream();
```

高效加载 Bitmap 的重点是：采用 BitmapFactory.Options

# Bitmap的处理

## 内存泄漏

## inSampleSize

为了防止内存泄漏，可以通过指定 `inSampleSize` 的值缩小图片的大小。主要的步骤就是通过设置 `options.inJustDecodeBound = true`的方式只将图片加载到内存中，而不输出（也就是说：此时`BitmapFactory.decodeResource(getResources(), resId, options);` 返回的结果为null！！！！） 

在获取 `options.outWidth & options.outHeight` 之后需要将 `inJustDecodeBound ` 设置为 false 



inSampleSize的含义是将原来图片缩小为原来的：1/inSampleSize。Api上写明**inSampleSize 必须是2的整数倍，如果指定的大小不是2的整数倍，则在运行时对指定值处理：取最接近2整数倍的值**

```java
   /**
     * 从资源文件中获取Bitmap
     *
     * @param resId 资源ID
     * @return Bitmap
     */
    private Bitmap getBitmap(@DrawableRes int resId) {
        if (resId == 0) {
            return null;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), resId, options);
        options.inJustDecodeBounds = false;
        options.inSampleSize = BitmapUtils.calculateInSampleSize(options, getWidth(), getHeight());
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), resId, options);
        Matrix matrix = getMatrix(bitmap);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }
```



```java
   /**
   	 * 计算采样率代码
     * @param options
     * @param reqWidth 目标宽度
     * @param reqHeight 目标高度
     * @return
     */
    public static int calculateInSampleSize(
        BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
```





