package com.example.dsd.demo.mvp;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

/**
 * Mvp Presenter 抽象类. 通过 弱引用持有 Context 和 View对象, 避免产生内存泄露。
 * <p>
 * fixme : 注意, 继承自SafePresenter的子类必须在定义时指定MvpView的类型, 否则在创建 {@link #mNullView} 时会找不到具体的MvpView类,
 * 导致初始化失败, 最终在调用 getView 获取mNullView 的时候会出现空指针异常. 例如 :
 * <code>
 * class LoginPresenter<V extends MvpView> extends SafePresenter<V> {
 * <p>
 * }
 * <code/>
 * fixme : 然后初始化 LoginPresenter<LoginMvpView> p = new LoginPresenter<>(); 当调用 p.getView() 时就可能产生空指针 !!!
 * 因为必须在定义LoginPresenter就指定具体的MvpView类型, 例如:
 * <code>
 * class LoginPresenter extends SafePresenter<LoginView> {
 * <p>
 * }
 * <code/>
 * 通过这种形式定义才能避免空指针. 可以参考 SafePresenterTestCase.java 查看使用方法 !!!
 * <p>
 * 用法:
 * <p>
 * public class MainActivity extends Activity implements XxxView {
 * <p>
 * XxxPresenter mPresenter ;
 * <p>
 * onCreate() {
 * mPresenter = new XxxPresenter();
 * mPresenter.attach(this, this);
 * }
 * <p>
 * <p>
 * onDestroy() {
 * mPresenter.detach();
 * }
 * }
 *
 *
 * <p>
 * 当 Context (通常是指Activity)被销毁时如果客户端程序
 * 再调用Context, 那么直接返回 Application 的Context. 因此如果用户需要调用与Activity相关的UI操作(例如弹出Dialog)时,
 * 应该先调用 {@link #isAttached()} ()} 来判断Activity是否还存活.
 * </p>
 * <p>
 * 当 View 对象销毁时如果用户再调用 View对象, 那么则会
 * 通过动态代理创建一个View对象 {@link #mNullView}, 这样保证 view对象不会为空.
 *
 * @param <T> Presenter 对应的 MvpView类型
 */
public abstract class SafePresenter<T extends MvpView> extends Presenter<T> {
    /**
     * application context
     */
    protected static Context sAppContext;
    /**
     * Mvp View created by dynamic Proxy
     */
    protected T mNullView;

    /*
      init application context with reflection.
     */
    static {
        try {
            // 先通过 ActivityThread 来获取 Application Context
            Application application = (Application) Class.forName("android.app.ActivityThread").getMethod
                    ("currentApplication").invoke(null, (Object[]) null);
            if (application != null) {
                sAppContext = application;
            }
            if (sAppContext == null) {
                // 第二种方式初始化
                application = (Application) Class.forName("android.app.AppGlobals").getMethod
                        ("getInitialApplication").invoke(null, (Object[]) null);
                if (application != null) {
                    sAppContext = application;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * attach context & mvp view
     *
     * @param context
     * @param view
     */
    @Override
    public void attach(Context context, T view) {
        super.attach(context, view);
        if (sAppContext == null && context != null) {
            sAppContext = context.getApplicationContext();
        }
    }


    /**
     * 返回 Context. 如果 Activity被销毁, 那么返回Application Context.
     * <p>
     * 注意:
     * 通过过Context进行UI方面的操作时应该调用 {@link #isAttached()}
     * 判断Activity是否还已经被销毁, 在Activity未销毁的状态下才能操作. 否则会引发crash.
     * <p>
     * 而获取资源等操作使用Application Context也没什么影响.
     *
     * @return
     */
    @Override
    protected Context getContext() {
        Context context = getRealContext();
        if (context == null) {
            context = sAppContext;
        }
        return context;
    }


    protected Context getRealContext() {
        return mContextRef != null ? mContextRef.get() : null;
    }


    /**
     * 返回 Mvp View对象. 如果真实的 View对象已经被销毁, 那么会通过动态代理构建一个View,
     * 确保调用 View对象执行操作时不会crash.
     *
     * @return Mvp View
     */
    @Override
    protected T getView() {
        T mvpView = super.getView();
        if (mvpView == null) {
            if (mNullView == null) {
                mNullView = MvpViewCreator.createEmptyView(this);
            }
            mvpView = mNullView;
        }
        return mvpView;
    }

    @Override
    protected String getString(int rid) {
        String value = super.getString(rid);
        if (TextUtils.isEmpty(value) && sAppContext != null) {
            return sAppContext.getString(rid);
        }
        return value;
    }
}
