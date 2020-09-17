# 协调布局的使用方式

````java
    <androidx.coordinatorlayout.widget.CoordinatorLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent">

        <com.google.android.material.appbar.AppBarLayout
            android:id="@+id/app_bar"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="vertical"
            app:elevation="0dp">

            <LinearLayout
           	 		android:layout_width="match_parent"
            		android:layout_height="wrap_content"
            		app:contentScrim="#82E492"
           			app:layout_scrollFlags="scroll|exitUntilCollapsed"
                app:scrimAnimationDuration="200"
                app:scrimVisibleHeightTrigger="60dp">// 关键

        </com.google.android.material.appbar.AppBarLayout>

     <androidx.core.widget.NestedScrollView
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:background="@color/color_f2f2f2"
        app:layout_behavior="@string/appbar_scrolling_view_behavior"> // 关键

    </androidx.coordinatorlayout.widget.CoordinatorLayout>
````



# 监听 AppBar 的状态

```java

import com.google.android.material.appbar.AppBarLayout;

/**
 * AppBarLayout 折叠状态监听
 */
public abstract class AppBarStateChangeListener implements AppBarLayout.OnOffsetChangedListener {

    private State mCurrentState = State.IDLE;

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if (verticalOffset == 0) {
            if (mCurrentState != State.EXPANDED) {
                onStateChanged(appBarLayout, State.EXPANDED);
            }
            mCurrentState = State.EXPANDED;
        } else if (Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()) {
            if (mCurrentState != State.COLLAPSED) {
                onStateChanged(appBarLayout, State.COLLAPSED);
            }
            mCurrentState = State.COLLAPSED;
        } else {
            if (mCurrentState != State.IDLE) {
                onStateChanged(appBarLayout, State.IDLE);
            }
            mCurrentState = State.IDLE;
        }
    }

    protected abstract void onStateChanged(AppBarLayout appBarLayout, State expanded);

    /**
     * 折叠状态
     */
    public enum State {
        /** 展开*/
        EXPANDED,
        /** 折叠*/      
        COLLAPSED,
        /** 空闲*/      
        IDLE
    }
}

```

