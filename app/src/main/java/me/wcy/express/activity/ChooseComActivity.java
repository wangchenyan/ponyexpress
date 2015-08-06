package me.wcy.express.activity;

import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import me.wcy.express.R;
import me.wcy.express.adapter.ComListAdapter;
import me.wcy.express.model.ExpressInfo;
import me.wcy.express.util.Utils;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;

@SuppressLint("InlinedApi")
public class ChooseComActivity extends BaseActivity implements
        OnItemClickListener {

    @Bind(R.id.com_listview)
    ListView comListView;

    @Bind(R.id.index_layout)
    LinearLayout indexLayout;

    private String[] comNames;
    private String[] comIcons;
    private String[] comParams;
    private String[] comIndexs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_com);

        comNames = getResources().getStringArray(R.array.company_names);
        comIcons = getResources().getStringArray(R.array.company_icons);
        comParams = getResources().getStringArray(R.array.company_params);
        comIndexs = getResources().getStringArray(R.array.company_indexs);

        init();
    }

    private void init() {
        comListView.setAdapter(new ComListAdapter(this, comNames, comIcons));
        comListView.setOnItemClickListener(this);

        for (String id : comIndexs) {
            Button text = new Button(this);
            text.setText(id);
            text.setTextSize(14);
            text.setTextColor(getResources().getColor(R.color.gray));
            text.setPadding(0, 0, 0, 0);
            text.setGravity(Gravity.CENTER);
            text.setBackgroundResource(R.color.transparent);
            LayoutParams params = new LayoutParams(Utils.dip2px(this, 30), 0, 1);
            text.setLayoutParams(params);
            text.setOnClickListener(new IndexListener(id));
            indexLayout.addView(text);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> view, View arg1, int position,
                            long arg3) {
        if (comNames[position].length() == 1) {
            return;
        }
        ExpressInfo expressInfo = new ExpressInfo();
        expressInfo.setComName(comNames[position]);
        expressInfo.setComIcon(comIcons[position]);
        expressInfo.setComParam(comParams[position]);
        Intent intent = new Intent();
        intent.putExtra(QueryActivity.EXPRESS_INFO, expressInfo);
        setResult(RESULT_OK, intent);
        finish();
    }

    class IndexListener implements OnClickListener {
        private String id;
        private List<String> comNameList;

        public IndexListener(String id) {
            super();
            this.id = id;
            comNameList = Arrays.asList(comNames);
        }

        @Override
        public void onClick(View v) {
            int index = comNameList.indexOf(id);
            comListView.setSelection(index);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return false;
    }

}
