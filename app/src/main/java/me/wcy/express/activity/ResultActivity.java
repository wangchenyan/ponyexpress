package me.wcy.express.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

import butterknife.Bind;
import me.wcy.express.R;
import me.wcy.express.adapter.ResultAdapter;
import me.wcy.express.database.DBHelper;
import me.wcy.express.database.History;
import me.wcy.express.model.QueryResult;

@SuppressLint("InlinedApi")
public class ResultActivity extends BaseActivity implements View.OnClickListener {
    @Bind(R.id.result_list)
    ListView resultListView;
    @Bind(R.id.post_id)
    TextView postId;
    @Bind(R.id.com_name)
    TextView comName;
    @Bind(R.id.com_icon)
    ImageView comIcon;
    @Bind(R.id.no_info)
    LinearLayout noInfo;
    @Bind(R.id.remark)
    Button remark;

    private QueryResult queryResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result);

        Intent intent = getIntent();
        queryResult = (QueryResult) intent.getSerializableExtra(QueryActivity.QUERY_RESULT);
        postId.setText(queryResult.getNu());
        comName.setText(queryResult.getCompany_name());
        int id = getResources().getIdentifier(queryResult.getCompany_icon(), "drawable", getPackageName());
        comIcon.setImageResource(id);
        if (queryResult.getStatus().equals("200")) {
            resultListView.setAdapter(new ResultAdapter(this, queryResult));
        } else {
            resultListView.setVisibility(View.GONE);
            noInfo.setVisibility(View.VISIBLE);
        }
        remark.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.remark) {
            try {
                DBHelper dbHelper = new DBHelper(this);
                final Dao<History, String> dao = dbHelper.getDao(History.class);
                final History history = dao.queryForId(queryResult.getNu());
                View view = getLayoutInflater().inflate(R.layout.result_dialog, null);
                final EditText etRemark = (EditText) view.findViewById(R.id.et_remark);
                etRemark.setText(history.getRemark());
                etRemark.setSelection(etRemark.length());
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setTitle(R.string.remark);
                dialog.setView(view);
                dialog.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        history.setRemark(etRemark.getText().toString());
                        try {
                            dao.createOrUpdate(history);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                });
                dialog.setNegativeButton(R.string.cancle, null);
                dialog.show().setCanceledOnTouchOutside(false);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
