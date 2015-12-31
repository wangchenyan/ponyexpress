package me.wcy.express.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import me.wcy.express.R;

/**
 * 快速定位侧边栏
 * Created by hzwangchenyan on 2015/12/31.
 */
public class IndexBar extends LinearLayout implements View.OnTouchListener {
    private ListView lvData;
    private TextView tvIndicator;
    private List<String> mTitles;
    private String[] mIndexes;
    private int mHeight;

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
        float indexTextSize = ta.getDimension(R.styleable.IndexBar_indexTextSize, 36.0f);
        int indexTextColor = ta.getColor(R.styleable.IndexBar_indexTextColor, 0xFF616161);
        ta.recycle();

        mIndexes = new String[]{"☆", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "#"};
        setOrientation(VERTICAL);
        setOnTouchListener(this);
        for (String index : mIndexes) {
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

    public void setData(List<String> titles, ListView lvData, TextView tvIndicator) {
        this.mTitles = titles;
        this.lvData = lvData;
        this.tvIndicator = tvIndicator;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int y;
        int i;
        int position;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                setBackgroundColor(0x40000000);
                tvIndicator.setVisibility(View.VISIBLE);
                y = (int) event.getY();
                mHeight = v.getHeight();
                i = mIndexes.length * y / mHeight;
                if (i < 0) {// 防止数组越界
                    i = 0;
                } else if (i >= mIndexes.length) {
                    i = mIndexes.length - 1;
                }
                position = mTitles.indexOf(mIndexes[i]);
                tvIndicator.setText(mIndexes[i]);
                if (position != -1) {
                    lvData.setSelection(position);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                y = (int) event.getY();
                i = mIndexes.length * y / mHeight;
                if (i < 0) {
                    i = 0;
                } else if (i >= mIndexes.length) {
                    i = mIndexes.length - 1;
                }
                position = mTitles.indexOf(mIndexes[i]);
                tvIndicator.setText(mIndexes[i]);
                if (position != -1) {
                    lvData.setSelection(position);
                }
                break;
            case MotionEvent.ACTION_UP:
                setBackgroundColor(0x00000000);
                tvIndicator.setVisibility(View.GONE);
                y = (int) event.getY();
                i = mIndexes.length * y / mHeight;
                if (i < 0) {
                    i = 0;
                } else if (i >= mIndexes.length) {
                    i = mIndexes.length - 1;
                }
                position = mTitles.indexOf(mIndexes[i]);
                tvIndicator.setText(mIndexes[i]);
                if (position != -1) {
                    lvData.setSelection(position);
                }
                break;
        }
        return true;
    }
}
