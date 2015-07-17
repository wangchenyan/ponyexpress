package me.wcy.express;

import java.util.Arrays;
import java.util.List;

import me.wcy.express.adapter.ComListAdapter;
import me.wcy.express.util.Utils;
import me.wcy.util.BaseActivity;
import me.wcy.util.ViewInject;

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

    @ViewInject(id = R.id.com_listview)
    private ListView comListView;

    @ViewInject(id = R.id.index_layout)
    private LinearLayout indexLayout;

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
        Intent intent = new Intent();
        intent.putExtra(QueryActivity.COM_NAME, comNames[position]);
        intent.putExtra(QueryActivity.COM_ICON, comIcons[position]);
        intent.putExtra(QueryActivity.COM_PARAM, comParams[position]);
        setResult(QueryActivity.RESULT_COMPANY, intent);
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
