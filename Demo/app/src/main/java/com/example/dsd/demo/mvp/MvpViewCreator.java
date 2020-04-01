package com.example.dsd.demo.mvp;

import android.util.Log;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.unmodifiableMap;

/**
 * Create mvp view instance via Proxy
 *
 * Created by mrsimple on 31/3/17.
 */
final class MvpViewCreator {
    /**
     * Null Mvp View InvocationHandler
     */
    private static final InvocationHandler NULL_VIEW = new MvpViewInvocationHandler();

    private MvpViewCreator() {
    }

    /**
     * 创建 mvp view
     *
     * @param target presenter对象
     * @param <T> 对应的Null MvpView
     * @return 返回对应的Null MvpView
     */
    public static <T extends MvpView> T createEmptyView(Object target) {
        Class<T> mvpViewInterface = findMvpViewInterface(target) ;
        if ( mvpViewInterface != null ) {
            return (T) Proxy.newProxyInstance(mvpViewInterface.getClassLoader(),
                    new Class[]{mvpViewInterface}, NULL_VIEW);
        }
        return null;
    }


    /**
     * 根据泛型找到 MvpView的类型
     * @param presenter 目标Presenter
     * @param <T>
     * @return
     */
    private static <T extends MvpView> Class<T> findMvpViewInterface(Object presenter) {
        if (presenter == null) {
            return null;
        }
        Class<T> mvpViewClass = null;
        try {
            // Scan the inheritance hierarchy until we reached MvpNullObjectBasePresenter
            Class<?> currentClass = presenter.getClass();

            while (mvpViewClass == null) {
                Type genericSuperType = currentClass.getGenericSuperclass();

                while (!(genericSuperType instanceof ParameterizedType)) {
                    // Scan inheritance tree until we find ParameterizedType which is probably a MvpSubclass
                    currentClass = currentClass.getSuperclass();
                    genericSuperType = currentClass.getGenericSuperclass();
                }

                final Type[] types = ((ParameterizedType) genericSuperType).getActualTypeArguments();
                for (final Type curType : types) {
                    Class<?> genericType = extractMvpViewClass(curType);
                    if (genericType.isInterface() && isSubTypeOfMvpView(genericType)) {
                        mvpViewClass = (Class<T>) genericType;
                        break;
                    }
                }
                // Continue with next class in inheritance hierachy (see genericSuperType assignment at start of
                // while loop)
                currentClass = currentClass.getSuperclass();
            }
        } catch (Throwable t) {
            throw new IllegalArgumentException("The generic type <V extends MvpView> must be the first generic type "
            + "argument of class " + presenter.getClass().getSimpleName() + " (per convention). Otherwise we can't" + " " 
            + "determine which type of View this" + " Presenter coordinates.", t);
        }

        return mvpViewClass;
    }

    /**
     * 提取 mvp view class 类型.
     * 1. 如果是没有泛型参数的 MvpView类型, 直接讲 Type转为Class即可;
     * 2. 如果 MvpView本身带有泛型参数, 那么需要转换为 ParameterizedType 类型, 然后再得到 raw Type, 这个 raw type 就是MvpView的最终类型
     * @param curType
     * @return
     */
    private static Class<?> extractMvpViewClass(Type curType) {
        Class<?> genericType = null ;
        try {
            genericType = (Class<?>) curType;
        } catch (Exception e) {
            e.printStackTrace();
            // 当 mvp view 本身带有泛型参数时, curType 则是 ParameterizedType 类型
            if ( genericType == null && curType instanceof ParameterizedType) {
                genericType = (Class<?>)((ParameterizedType)curType).getRawType() ;
            }
        }
        return genericType;
    }


    /**
     * Scans the interface inheritnace hierarchy and checks if on the root is MvpView.class
     *
     * @param klass The leaf interface where to begin to scan
     * @return true if subtype of MvpView, otherwise false
     */
    private static boolean isSubTypeOfMvpView(Class<?> klass) {
        if (klass.equals(MvpView.class)) {
            return true;
        }
        Class[] superInterfaces = klass.getInterfaces();
        for (int i = 0; i < superInterfaces.length; i++) {
            if (isSubTypeOfMvpView(superInterfaces[i])) {
                return true;
            }
        }
        return false;
    }

    /**
     * 动态代理 InvocationHandler
     */
    static class MvpViewInvocationHandler implements InvocationHandler {

        private static final Map<Class<?>, Object> DEFAULTS =
                unmodifiableMap(new HashMap<Class<?>, Object>() {
                    {
                        put(Boolean.TYPE, false);
                        put(Byte.TYPE, (byte) 0);
                        put(Character.TYPE, '\000');
                        put(Double.TYPE, 0.0d);
                        put(Float.TYPE, 0.0f);
                        put(Integer.TYPE, 0);
                        put(Long.TYPE, 0L);
                        put(Short.TYPE, (short) 0);
                        put(String.class, "");
                    }
                });

        @SuppressWarnings("unchecked")
        public static <T> T defaultReturnValue(Class<T> type) {
            return (T) DEFAULTS.get(type);
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Log.e("", "### MvpViewInvocationHandler invoke -> " + method.getName());
            return defaultReturnValue(method.getReturnType());
        }
    }
}

