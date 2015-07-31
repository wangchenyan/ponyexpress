/**
 * 2015-5-27
 */
package me.wcy.express.activity;

import java.lang.reflect.Field;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import me.wcy.express.util.ViewInject;

/**
 * @author wcy
 * 
 */
public abstract class BaseFragment extends Fragment {

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		initInjectedView(view);
		init();
		super.onViewCreated(view, savedInstanceState);
	}

	protected abstract void init();

	private void initInjectedView(View sourceView) {
		Field[] fields = getClass().getDeclaredFields();
		if (fields != null && fields.length > 0) {
			for (Field field : fields) {
				try {
					field.setAccessible(true);
					if (field.get(this) != null) {
						continue;
					}
					ViewInject viewInject = field
							.getAnnotation(ViewInject.class);
					if (viewInject != null) {
						int viewId = viewInject.id();
						field.set(this, sourceView.findViewById(viewId));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

}
