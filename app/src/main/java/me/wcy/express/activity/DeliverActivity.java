package me.wcy.express.activity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.just.library.AgentWeb;

public class DeliverActivity extends BaseActivity {
    private static final String URL = "https://m.kuaidi100.com/courier/";

    private AgentWeb mAgentWeb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout frameLayout = new FrameLayout(this);
        frameLayout.setFitsSystemWindows(true);
        setContentView(frameLayout);

        mAgentWeb = AgentWeb.with(this)
                .setAgentWebParent(frameLayout, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
                .useDefaultIndicator()
                .defaultProgressBarColor()
                .createAgentWeb()
                .ready()
                .go(URL);
    }

    @Override
    protected void onResume() {
        mAgentWeb.getWebLifeCycle().onResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        mAgentWeb.getWebLifeCycle().onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mAgentWeb.getWebLifeCycle().onDestroy();
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mAgentWeb.handleKeyEvent(keyCode, event)) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
