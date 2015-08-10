package me.wcy.express.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import butterknife.Bind;
import me.wcy.express.R;
import me.wcy.express.adapter.ResultAdapter;
import me.wcy.express.model.QueryResult;

@SuppressLint("InlinedApi")
public class ResultActivity extends BaseActivity {

    @Bind(R.id.result_list)
    ListView resultListView;

    @Bind(R.id.post_id)
    TextView postId;

    @Bind(R.id.com)
    TextView comName;

    @Bind(R.id.icon)
    ImageView comIcon;

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
