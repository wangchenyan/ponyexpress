package me.wcy.express.util;

import android.app.Activity;
import android.view.View;

import java.lang.reflect.Field;

/**
 * Created by chenyan.wang on 2015/8/10.
 */
public class ViewInjector {

    public static void initInjectedView(Activity activity) {
        initInjectedView(activity, activity.getWindow().getDecorView());
    }

    public static void initInjectedView(Object injectedSource, View sourceView) {
        Field[] fields = injectedSource.getClass().getDeclaredFields();
        if (fields != null && fields.length > 0) {
            for (Field field : fields) {
                try {
                    field.setAccessible(true);
                    if (field.get(injectedSource) != null) {
                        continue;
                    }
                    ViewInject viewInject = field.getAnnotation(ViewInject.class);
                    if (viewInject != null) {
                        int viewId = viewInject.id();
                        field.set(injectedSource, sourceView.findViewById(viewId));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
