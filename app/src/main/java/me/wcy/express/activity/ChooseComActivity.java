package me.wcy.express.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import me.wcy.express.R;
import me.wcy.express.adapter.ComListAdapter;
import me.wcy.express.model.ExpressInfo;
import me.wcy.express.util.Utils;

@SuppressLint("InlinedApi")
public class ChooseComActivity extends BaseActivity implements OnItemClickListener {
    @Bind(R.id.lv_com)
    ListView lvCom;
    @Bind(R.id.ll_indicator)
    LinearLayout llIndicator;
    @Bind(R.id.tv_indicator)
    TextView tvIndicator;

    private String[] mComNames;
    private String[] mComIcons;
    private String[] mComParams;
    private String[] mComIndexes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_com);

        mComNames = getResources().getStringArray(R.array.company_names);
        mComIcons = getResources().getStringArray(R.array.company_icons);
        mComParams = getResources().getStringArray(R.array.company_params);
        mComIndexes = getResources().getStringArray(R.array.company_indexes);

        init();
    }

    private void init() {
        lvCom.setAdapter(new ComListAdapter(this, mComNames, mComIcons));
        lvCom.setOnItemClickListener(this);
        llIndicator.setOnTouchListener(new IndicatorListener(mComIndexes.length, mComNames));

        for (String id : mComIndexes) {
            TextView text = new TextView(this);
            text.setText(id);
            text.setTextSize(14);
            text.setTextColor(getResources().getColor(R.color.grey));
            text.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(Utils.dip2px(this, 30), 0, 1);
            text.setLayoutParams(params);
            llIndicator.addView(text);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> view, View arg1, int position, long arg3) {
        if (mComNames[position].length() == 1) {
            return;
        }
        ExpressInfo expressInfo = new ExpressInfo();
        expressInfo.setCompany_name(mComNames[position]);
        expressInfo.setCompany_icon(mComIcons[position]);
        expressInfo.setCompany_param(mComParams[position]);
        Intent intent = new Intent();
        intent.putExtra(QueryActivity.EXPRESS_INFO, expressInfo);
        setResult(RESULT_OK, intent);
        finish();
    }

    class IndicatorListener implements View.OnTouchListener {
        private int mCount;
        private int mHeight;
        private List<String> mComNameList;

        public IndicatorListener(int count, String[] comNames) {
            this.mCount = count;
            mComNameList = new ArrayList<>();
            Collections.addAll(mComNameList, comNames);
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int y;
            int index;
            int position;
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    tvIndicator.setVisibility(View.VISIBLE);
                    y = (int) event.getY();
                    mHeight = v.getHeight();
                    index = mCount * y / mHeight;
                    if (index < 0) {//防止数组越界
                        index = 0;
                    } else if (index >= mCount) {
                        index = mCount - 1;
                    }
                    position = mComNameList.indexOf(mComIndexes[index]);
                    tvIndicator.setText(mComNameList.get(position));
                    lvCom.setSelection(position);
                    break;
                case MotionEvent.ACTION_MOVE:
                    y = (int) event.getY();
                    index = mCount * y / mHeight;
                    if (index < 0) {
                        index = 0;
                    } else if (index >= mCount) {
                        index = mCount - 1;
                    }
                    position = mComNameList.indexOf(mComIndexes[index]);
                    tvIndicator.setText(mComNameList.get(position));
                    lvCom.setSelection(position);
                    break;
                case MotionEvent.ACTION_UP:
                    tvIndicator.setVisibility(View.GONE);
                    y = (int) event.getY();
                    index = mCount * y / mHeight;
                    if (index < 0) {
                        index = 0;
                    } else if (index >= mCount) {
                        index = mCount - 1;
                    }
                    position = mComNameList.indexOf(mComIndexes[index]);
                    tvIndicator.setText(mComNameList.get(position));
                    lvCom.setSelection(position);
                    break;
            }
            return true;
        }
    }

}
