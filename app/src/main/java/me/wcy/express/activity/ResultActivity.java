package me.wcy.express.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import me.wcy.express.R;
import me.wcy.express.adapter.ResultAdapter;
import me.wcy.express.model.SearchInfo;
import me.wcy.express.model.SearchResult;
import me.wcy.express.request.GsonRequest;
import me.wcy.express.utils.Constants;
import me.wcy.express.utils.DataManager;
import me.wcy.express.utils.Extras;
import me.wcy.express.utils.SnackbarUtils;
import me.wcy.express.utils.Utils;

public class ResultActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "ResultActivity";
    @Bind(R.id.iv_logo)
    ImageView ivLogo;
    @Bind(R.id.tv_post_id)
    TextView tvPostId;
    @Bind(R.id.tv_name)
    TextView tvName;
    @Bind(R.id.ll_result)
    LinearLayout llResult;
    @Bind(R.id.lv_result_list)
    ListView lvResultList;
    @Bind(R.id.btn_remark)
    Button btnRemark;
    @Bind(R.id.ll_no_exist)
    LinearLayout llNoExist;
    @Bind(R.id.btn_save)
    Button btnSave;
    @Bind(R.id.tv_searching)
    TextView tvSearching;
    private RequestQueue mRequestQueue;
    private DataManager mDataManager;
    private SearchInfo mSearchInfo;

    public static void start(Context context, SearchInfo searchInfo) {
        Intent intent = new Intent(context, ResultActivity.class);
        intent.putExtra(Extras.SEARCH_INFO, searchInfo);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        mRequestQueue = Volley.newRequestQueue(this);
        mDataManager = DataManager.getInstance();

        Intent intent = getIntent();
        mSearchInfo = (SearchInfo) intent.getSerializableExtra(Extras.SEARCH_INFO);
        Glide.with(this)
                .load(Utils.formatLogoUrl(mSearchInfo.getLogo()))
                .dontAnimate()
                .placeholder(R.drawable.default_logo)
                .into(ivLogo);
        refreshSearchInfo();

        search();
    }

    @Override
    protected void setListener() {
        btnRemark.setOnClickListener(this);
        btnSave.setOnClickListener(this);
    }

    @SuppressLint("SetTextI18n")
    private void refreshSearchInfo() {
        String remark = mDataManager.getRemark(mSearchInfo.getPost_id());
        if (TextUtils.isEmpty(remark)) {
            tvName.setText(mSearchInfo.getName());
            tvPostId.setText(mSearchInfo.getPost_id());
        } else {
            tvName.setText(remark);
            tvPostId.setText(mSearchInfo.getName() + " " + mSearchInfo.getPost_id());
        }
    }

    private void search() {
        GsonRequest<SearchResult> request = new GsonRequest<SearchResult>(Utils.formatSearchUrl(mSearchInfo),
                SearchResult.class, new Response.Listener<SearchResult>() {
            @Override
            public void onResponse(SearchResult searchResult) {
                Log.i(TAG, searchResult.getMessage());
                onSearch(searchResult);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e(TAG, volleyError.getMessage(), volleyError);
                llResult.setVisibility(View.GONE);
                llNoExist.setVisibility(View.VISIBLE);
                tvSearching.setVisibility(View.GONE);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put(Constants.HEADER_REFERER, Constants.REFERER);
                return headers;
            }
        };
        request.setShouldCache(false);
        mRequestQueue.add(request);
    }

    private void onSearch(SearchResult searchResult) {
        if (searchResult.getStatus().equals("200")) {
            llResult.setVisibility(View.VISIBLE);
            llNoExist.setVisibility(View.GONE);
            tvSearching.setVisibility(View.GONE);
            lvResultList.setAdapter(new ResultAdapter(searchResult));
            mSearchInfo.setIs_check(searchResult.getIscheck());
            mDataManager.updateHistory(mSearchInfo);
        } else {
            llResult.setVisibility(View.GONE);
            llNoExist.setVisibility(View.VISIBLE);
            tvSearching.setVisibility(View.GONE);
            btnSave.setText(mDataManager.idExists(mSearchInfo.getPost_id()) ? "运单备注" : "保存运单信息");
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
                    mSearchInfo.setIs_check("0");
                    mDataManager.updateHistory(mSearchInfo);
                    SnackbarUtils.show(this, "保存成功");
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(new Intent(ResultActivity.this, ExpressActivity.class));
                        }
                    }, 1000);
                }
                break;
        }
    }

    private void remark() {
        View view = getLayoutInflater().inflate(R.layout.activity_result_dialog, null);
        final EditText etRemark = (EditText) view.findViewById(R.id.et_remark);
        etRemark.setText(mDataManager.getRemark(mSearchInfo.getPost_id()));
        etRemark.setSelection(etRemark.length());
        new AlertDialog.Builder(this)
                .setTitle("备注名")
                .setView(view)
                .setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mDataManager.updateRemark(mSearchInfo.getPost_id(), etRemark.getText().toString());
                        refreshSearchInfo();
                        SnackbarUtils.show(ResultActivity.this, "备注成功");
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }
}
