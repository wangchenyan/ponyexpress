/**
 * 2015-5-27
 */
package me.wcy.express.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import me.wcy.express.util.ViewInjector;

/**
 * @author wcy
 */
public abstract class BaseFragment extends Fragment {

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ViewInjector.initInjectedView(this, view);
        init();
        super.onViewCreated(view, savedInstanceState);
    }

    protected abstract void init();

}
