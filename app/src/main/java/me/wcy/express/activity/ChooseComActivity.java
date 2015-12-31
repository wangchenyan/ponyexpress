package me.wcy.express.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import me.wcy.express.R;
import me.wcy.express.adapter.ComListAdapter;
import me.wcy.express.model.ExpressInfo;
import me.wcy.express.widget.IndexBar;

@SuppressLint("InlinedApi")
public class ChooseComActivity extends BaseActivity implements OnItemClickListener {
    @Bind(R.id.lv_com)
    ListView lvCom;
    @Bind(R.id.ll_indicator)
    IndexBar llIndicator;
    @Bind(R.id.tv_indicator)
    TextView tvIndicator;

    private String[] mComNames;
    private String[] mComIcons;
    private String[] mComParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_com);

        mComNames = getResources().getStringArray(R.array.company_names);
        mComIcons = getResources().getStringArray(R.array.company_icons);
        mComParams = getResources().getStringArray(R.array.company_params);

        init();
    }

    private void init() {
        lvCom.setAdapter(new ComListAdapter(this, mComNames, mComIcons));
        lvCom.setOnItemClickListener(this);
        List<String> data = new ArrayList<>();
        Collections.addAll(data, mComNames);
        llIndicator.setData(data, lvCom, tvIndicator);
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
}
