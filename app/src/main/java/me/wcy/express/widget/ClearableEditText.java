package me.wcy.express.widget;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;
import android.view.MotionEvent;

import me.wcy.express.R;

/**
 * 带有删除按钮的EditText
 * Created by hzwangchenyan on 2016/3/5.
 */
public class ClearableEditText extends AppCompatEditText {
    private Drawable mDrawable;

    public ClearableEditText(Context context) {
        super(context);
        init();
    }

    public ClearableEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ClearableEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mDrawable = getResources().getDrawable(R.drawable.ic_edit_text_clear);
        assert mDrawable != null;
        mDrawable.setBounds(0, 0, mDrawable.getMinimumWidth(), mDrawable.getMinimumHeight());
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        showClearIcon(hasFocus() && text.length() > 0);
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        showClearIcon(focused && length() > 0);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                Drawable drawable = getCompoundDrawables()[2];
                if (drawable != null && event.getRawX() >= (getRight() - drawable.getBounds().width())) {
                    setText("");
                    return true;
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    private void showClearIcon(boolean show) {
        setCompoundDrawables(null, null, show ? mDrawable : null, null);
    }
}
