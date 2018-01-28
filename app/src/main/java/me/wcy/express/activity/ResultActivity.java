package me.wcy.express.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.wcy.express.R;
import me.wcy.express.constants.Extras;
import me.wcy.express.http.HttpCallback;
import me.wcy.express.http.HttpClient;
import me.wcy.express.model.SearchInfo;
import me.wcy.express.model.SearchResult;
import me.wcy.express.utils.DataManager;
import me.wcy.express.utils.SnackbarUtils;
import me.wcy.express.utils.binding.Bind;
import me.wcy.express.viewholder.ResultViewHolder;
import me.wcy.express.widget.radapter.RAdapter;
import me.wcy.express.widget.radapter.RSingleDelegate;

public class ResultActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "ResultActivity";
    @Bind(R.id.iv_logo)
    private ImageView ivLogo;
    @Bind(R.id.tv_post_id)
    private TextView tvPostId;
    @Bind(R.id.tv_name)
    private TextView tvName;
    @Bind(R.id.ll_result)
    private LinearLayout llResult;
    @Bind(R.id.rv_result_list)
    private RecyclerView rvResultList;
    @Bind(R.id.btn_remark)
    private Button btnRemark;
    @Bind(R.id.ll_no_exist)
    private LinearLayout llNoExist;
    @Bind(R.id.btn_save)
    private Button btnSave;
    @Bind(R.id.ll_error)
    private LinearLayout llError;
    @Bind(R.id.btn_retry)
    private Button btnRetry;
    @Bind(R.id.tv_searching)
    private TextView tvSearching;

    private SearchInfo searchInfo;
    private List<SearchResult.ResultItem> resultItemList = new ArrayList<>();
    private RAdapter<SearchResult.ResultItem> adapter;

    public static void start(Context context, SearchInfo searchInfo) {
        Intent intent = new Intent(context, ResultActivity.class);
        intent.putExtra(Extras.SEARCH_INFO, searchInfo);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        Intent intent = getIntent();
        searchInfo = (SearchInfo) intent.getSerializableExtra(Extras.SEARCH_INFO);
        Glide.with(this)
                .load(HttpClient.urlForLogo(searchInfo.getLogo()))
                .dontAnimate()
                .placeholder(R.drawable.ic_default_logo)
                .into(ivLogo);
        refreshSearchInfo();

        btnRemark.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        btnRetry.setOnClickListener(this);

        adapter = new RAdapter<>(resultItemList, new RSingleDelegate<>(ResultViewHolder.class));
        rvResultList.setLayoutManager(new LinearLayoutManager(this));
        rvResultList.setAdapter(adapter);

        query();
    }

    private void refreshSearchInfo() {
        String remark = DataManager.getInstance().getRemark(searchInfo.getPost_id());
        if (TextUtils.isEmpty(remark)) {
            tvName.setText(searchInfo.getName());
            tvPostId.setText(searchInfo.getPost_id());
        } else {
            tvName.setText(remark);
            tvPostId.setText(searchInfo.getName().concat(" ").concat(searchInfo.getPost_id()));
        }
    }

    private void query() {
        HttpClient.query(searchInfo.getCode(), searchInfo.getPost_id(), new HttpCallback<SearchResult>() {
            @Override
            public void onResponse(SearchResult searchResult) {
                Log.i(TAG, searchResult.getMessage());
                onSearch(searchResult);
            }

            @Override
            public void onError(VolleyError volleyError) {
                Log.e(TAG, volleyError.getMessage(), volleyError);
                llResult.setVisibility(View.GONE);
                llNoExist.setVisibility(View.GONE);
                llError.setVisibility(View.VISIBLE);
                tvSearching.setVisibility(View.GONE);
            }
        });
    }

    private void onSearch(SearchResult searchResult) {
        if (searchResult.getStatus().equals("200")) {
            llResult.setVisibility(View.VISIBLE);
            llNoExist.setVisibility(View.GONE);
            llError.setVisibility(View.GONE);
            tvSearching.setVisibility(View.GONE);
            Collections.addAll(resultItemList, searchResult.getData());
            adapter.notifyDataSetChanged();
            searchInfo.setIs_check(searchResult.getIscheck());
            DataManager.getInstance().updateHistory(searchInfo);
        } else {
            llResult.setVisibility(View.GONE);
            llNoExist.setVisibility(View.VISIBLE);
            llError.setVisibility(View.GONE);
            tvSearching.setVisibility(View.GONE);
            btnSave.setText(DataManager.getInstance().idExists(searchInfo.getPost_id()) ? "运单备注" : "保存运单信息");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_remark:
                remark();
                break;
            case R.id.btn_save:
                if (TextUtils.equals(btnSave.getText().toString(), "运单备注")) {
                    remark();
                } else {
                    searchInfo.setIs_check("0");
                    DataManager.getInstance().updateHistory(searchInfo);
                    SnackbarUtils.show(this, "保存成功");
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (!ResultActivity.this.isFinishing()) {
                                startActivity(new Intent(ResultActivity.this, ExpressActivity.class));
                            }
                        }
                    }, 1000);
                }
                break;
            case R.id.btn_retry:
                llResult.setVisibility(View.GONE);
                llNoExist.setVisibility(View.GONE);
                llError.setVisibility(View.GONE);
                tvSearching.setVisibility(View.VISIBLE);
                query();
                break;
        }
    }

    private void remark() {
        View view = getLayoutInflater().inflate(R.layout.dialog_result, null);
        EditText etRemark = view.findViewById(R.id.et_remark);
        etRemark.setText(DataManager.getInstance().getRemark(searchInfo.getPost_id()));
        etRemark.setSelection(etRemark.length());
        new AlertDialog.Builder(this)
                .setTitle("备注名")
                .setView(view)
                .setPositiveButton(R.string.sure, (dialog, which) -> {
                    DataManager.getInstance().updateRemark(searchInfo.getPost_id(), etRemark.getText().toString());
                    refreshSearchInfo();
                    SnackbarUtils.show(ResultActivity.this, "备注成功");
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }
}
