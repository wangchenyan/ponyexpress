package me.wcy.express.activity;

import me.wcy.express.R;
import me.wcy.express.adapter.ResultAdapter;
import me.wcy.express.model.QueryResult;
import me.wcy.express.util.ViewInject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

@SuppressLint("InlinedApi")
public class ResultActivity extends BaseActivity {

    @ViewInject(id = R.id.result_list)
    private ListView resultListView;

    @ViewInject(id = R.id.post_id)
    private TextView postId;

    @ViewInject(id = R.id.com)
    private TextView comName;

    @ViewInject(id = R.id.icon)
    private ImageView comIcon;

    private QueryResult queryResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result);

        Intent intent = getIntent();
        queryResult = (QueryResult) intent
                .getSerializableExtra(QueryActivity.QUERY_RESULT);
        postId.setText(queryResult.getNu());
        comName.setText(queryResult.getCompanyName());
        int id = getResources().getIdentifier(queryResult.getCompanyIcon(),
                "drawable", getPackageName());
        comIcon.setImageResource(id);
        resultListView.setAdapter(new ResultAdapter(this, queryResult));
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
