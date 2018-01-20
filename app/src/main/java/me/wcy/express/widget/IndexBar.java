package me.wcy.express.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import me.wcy.express.R;
import me.wcy.express.utils.Utils;

/**
 * 快速定位侧边栏
 * Created by hzwangchenyan on 2015/12/31.
 */
public class IndexBar extends LinearLayout {
    private static final String[] INDEXES = new String[]{"#", "A", "B", "C", "D", "E", "F", "G", "H",
            "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
    private static final int TOUCHED_BACKGROUND_COLOR = 0x40000000;
    private OnIndexChangedListener mListener;

    public void setOnIndexChangedListener(OnIndexChangedListener listener) {
        mListener = listener;
    }

    public IndexBar(Context context) {
        this(context, null);
    }

    public IndexBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IndexBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.IndexBar);
        float indexTextSize = ta.getDimension(R.styleable.IndexBar_indexTextSize, Utils.sp2px(getContext(), 12));
        int indexTextColor = ta.getColor(R.styleable.IndexBar_indexTextColor, 0xFF616161);
        ta.recycle();

        setOrientation(VERTICAL);
        for (String index : INDEXES) {
            TextView text = new TextView(getContext());
            text.setText(index);
            text.setTextSize(TypedValue.COMPLEX_UNIT_PX, indexTextSize);
            text.setTextColor(indexTextColor);
            text.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1);
            text.setLayoutParams(params);
            addView(text);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                setBackgroundColor(TOUCHED_BACKGROUND_COLOR);
                handle(event);
                return true;
            case MotionEvent.ACTION_MOVE:
                handle(event);
                return true;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                setBackgroundColor(Color.TRANSPARENT);
                handle(event);
                return true;
        }
        return super.onTouchEvent(event);
    }

    private void handle(MotionEvent event) {
        int y = (int) event.getY();
        int height = getHeight();
        int position = INDEXES.length * y / height;
        if (position < 0) {
            position = 0;
        } else if (position >= INDEXES.length) {
            position = INDEXES.length - 1;
        }

        String index = INDEXES[position];
        boolean showIndicator = event.getAction() != MotionEvent.ACTION_UP && event.getAction() != MotionEvent.ACTION_CANCEL;
        if (mListener != null) {
            mListener.onIndexChanged(index, showIndicator);
        }
    }

    public interface OnIndexChangedListener {
        void onIndexChanged(String index, boolean isDown);
    }
}
