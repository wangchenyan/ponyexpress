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
    @Bind(R.id.lv_result_list)
    ListView lvResultList;
    @Bind(R.id.tv_post_id)
    TextView tvPostId;
    @Bind(R.id.tv_com_name)
    TextView tvComName;
    @Bind(R.id.iv_com_icon)
    ImageView ivComIcon;
    @Bind(R.id.ll_empty)
    LinearLayout llEmpty;
    @Bind(R.id.btn_remark)
    Button btnRemark;

    private QueryResult mQueryResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        Intent intent = getIntent();
        mQueryResult = (QueryResult) intent.getSerializableExtra(QueryActivity.QUERY_RESULT);
        tvPostId.setText(mQueryResult.getNu());
        tvComName.setText(mQueryResult.getCompany_name());
        int id = getResources().getIdentifier(mQueryResult.getCompany_icon(), "drawable", getPackageName());
        ivComIcon.setImageResource(id);
        if (mQueryResult.getStatus().equals("200")) {
            lvResultList.setAdapter(new ResultAdapter(this, mQueryResult));
        } else {
            lvResultList.setVisibility(View.GONE);
            llEmpty.setVisibility(View.VISIBLE);
        }
        btnRemark.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_remark) {
            try {
                DBHelper dbHelper = new DBHelper(this);
                final Dao<History, String> dao = dbHelper.getDao(History.class);
                final History history = dao.queryForId(mQueryResult.getNu());
                View view = getLayoutInflater().inflate(R.layout.activity_result_dialog, null);
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
                dialog.setNegativeButton(R.string.cancel, null);
                dialog.show().setCanceledOnTouchOutside(false);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
