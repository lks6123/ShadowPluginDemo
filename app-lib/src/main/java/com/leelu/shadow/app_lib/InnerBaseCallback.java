package com.leelu.shadow.app_lib;

import android.view.View;

import com.tencent.shadow.dynamic.host.EnterCallback;


/**
 * CreateDate: 2022/5/12 13:41
 * Author: 李露
 * Email: lilu2@haier.com
 * Version: 1.0
 * Description: waring: 这里如果使用Kotlin接口。 编译出来的aar. java实现时会导致default失效。
 * -Xjvm-default=all 以及 @JvmDefaultWithoutCompatibility 注解之后。 使用到的地方也要添加 -Xjvm-default=all
 * 会比较麻烦。 故而直接采用 java 的 default 接口
 */
public interface InnerBaseCallback extends EnterCallback {
    @Override
    default void onShowLoadingView(View view) {

    }

    @Override
    default void onEnterComplete() {

    }

    @Override
    default void onCloseLoadingView() {

    }

    default void onSuccess() {

    }

    default void onError(Exception e) {

    }
}
