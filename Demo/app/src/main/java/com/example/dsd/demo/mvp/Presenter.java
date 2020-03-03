package com.example.dsd.demo.mvp;

import android.content.Context;

import java.lang.ref.WeakReference;

/**
 * Mvp Presenter 抽象类. 通过 弱引用持有 Context 和 View对象, 避免产生内存泄露。
 *
 * 用法:
 * <code>
 *
 * public class MainActivity extends Activity implements XxxView {
 *
 *      XxxPresenter mPresenter ;
 *      onCreate() {
 *          mPresenter = new XxxPresenter();
 *          mPresenter.attach(this, this);
 *      }
 *
 *      onDestroy() {
 *          mPresenter.detach();
 *      }
 * }
 *
 *<code/>
 *
 * @param <T> Presenter 对应的 MvpView类型
 */
public abstract class Presenter<T extends MvpView> {
    /**
     * context weak reference
     */
    protected WeakReference<Context> mContextRef;
    /**
     * mvp view weak reference
     */
    protected WeakReference<T> mViewRef;

    /**
     * attach context & mvp view
     */
    public void attach(Context context, T view) {
        mContextRef = new WeakReference<>(context);
        mViewRef = new WeakReference<>(view);
    }


    /**
     * release resource
     */
    public void detach() {
        if (mContextRef != null) {
            mContextRef.clear();
        }
        if (mViewRef != null) {
            mViewRef.clear();
        }
    }

    /**
     * Mvp view 是否 attach 到了Presenter 上. UI展示相关的操作需要判断一下 Activity 是否已经 finish. 比如 弹出Dialog、Window、跳转Activity等操作.
     *
     * @return
     */
    public boolean isAttached() {
        return mViewRef != null && mViewRef.get() != null;
    }

    protected Context getContext() {
        return mContextRef != null ? mContextRef.get() : null;
    }

    /**
     * 返回 Mvp View对象. 如果真实的 View对象已经被销毁, 那么会通过动态代理构建一个View,
     * 确保调用 View对象执行操作时不会crash.
     *
     * @return Mvp View
     */
    protected T getView() {
        return mViewRef != null ? mViewRef.get() : null;
    }

    protected String getString(int rid) {
        Context context = getContext();
        if (context != null) {
            return context.getString(rid);
        }
        return "";
    }
}
