package me.wcy.express.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.zxing.activity.CaptureActivity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.wcy.express.R;
import me.wcy.express.adapter.HistoryListAdapter;
import me.wcy.express.database.History;
import me.wcy.express.model.ExpressInfo;
import me.wcy.express.model.QueryResult;
import me.wcy.express.request.GsonRequest;
import me.wcy.express.utils.DataManager;
import me.wcy.express.utils.SnackbarUtils;
import me.wcy.express.utils.UpdateUtils;
import me.wcy.express.utils.Utils;
import me.wcy.express.widget.CustomAlertDialog;
import me.wcy.express.widget.CustomProgressDialog;

public class QueryActivity extends AppCompatActivity implements OnClickListener, TextWatcher,
        OnItemClickListener, NavigationView.OnNavigationItemSelectedListener {
    public static final String QUERY_RESULT = "query_result";
    public static final String EXPRESS_INFO = "express_info";
    public static final int REQUEST_CAPTURE = 0;
    public static final int REQUEST_COMPANY = 1;

    @Bind(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @Bind(R.id.navigation_view)
    NavigationView navigationView;
    @Bind(R.id.et_post_id)
    EditText etPostId;
    @Bind(R.id.iv_scan)
    ImageView ivScan;
    @Bind(R.id.iv_clear)
    ImageView ivClear;
    @Bind(R.id.rl_choose_com)
    RelativeLayout rlChooseCom;
    @Bind(R.id.tv_com_name)
    TextView tvComName;
    @Bind(R.id.btn_query)
    Button btnQuery;
    @Bind(R.id.ll_un_check)
    LinearLayout llUnCheck;
    @Bind(R.id.lv_un_check)
    ListView lvUnCheck;

    private CustomProgressDialog mProgressDialog;
    private CustomAlertDialog mAlertDialog;
    private ExpressInfo mExpressInfo;
    private RequestQueue mRequestQueue;
    private DataManager mDataManager;
    private List<History> mUnCheckList;
    private long mExitTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
        }

        navigationView.setNavigationItemSelectedListener(this);
        etPostId.addTextChangedListener(this);
        btnQuery.setOnClickListener(this);
        rlChooseCom.setOnClickListener(this);
        ivScan.setOnClickListener(this);
        ivClear.setOnClickListener(this);

        mProgressDialog = new CustomProgressDialog(this);
        mRequestQueue = Volley.newRequestQueue(this);
        mDataManager = DataManager.getInstance().setContext(this);
        mExpressInfo = new ExpressInfo();

        UpdateUtils.checkUpdate(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initUnCheck();
    }

    private void initUnCheck() {
        mUnCheckList = mDataManager.getUnCheckList();
        lvUnCheck.setAdapter(new HistoryListAdapter(this, mUnCheckList));
        lvUnCheck.setOnItemClickListener(this);
        if (mUnCheckList.size() == 0) {
            llUnCheck.setVisibility(View.GONE);
        } else {
            llUnCheck.setVisibility(View.VISIBLE);
        }
    }

    private void query() {
        if (!Utils.isNetworkAvailable(this)) {
            SnackbarUtils.show(this, R.string.network_error);
            return;
        }
        mProgressDialog.show();
        mProgressDialog.setMessage(getResources().getString(R.string.querying));
        GsonRequest<QueryResult> request = new GsonRequest<QueryResult>(Utils.getQueryUrl(mExpressInfo),
                QueryResult.class, new Listener<QueryResult>() {
            @Override
            public void onResponse(QueryResult queryResult) {
                Log.i("Query", queryResult.getMessage());
                mProgressDialog.cancel();
                if (queryResult.getStatus().equals("200")) {
                    onQuerySuccess(queryResult);
                } else {
                    onQueryFailure(queryResult);
                }
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("Query", volleyError.getMessage(), volleyError);
                mProgressDialog.cancel();
                SnackbarUtils.show(QueryActivity.this, R.string.system_busy);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put(Utils.HEADER_REFERER, Utils.REFERER);
                return headers;
            }
        };
        request.setShouldCache(false);
        mRequestQueue.add(request);
    }

    private void onQuerySuccess(QueryResult queryResult) {
        Intent intent = new Intent();
        intent.setClass(this, ResultActivity.class);
        queryResult.setCompany_name(mExpressInfo.getCompany_name());
        queryResult.setCompany_icon(mExpressInfo.getCompany_icon());
        intent.putExtra(QUERY_RESULT, queryResult);
        startActivity(intent);
        mExpressInfo.setIs_check(queryResult.getIscheck());
        mDataManager.updateHistory(mExpressInfo);
    }

    private void onQueryFailure(QueryResult queryResult) {
        if (mExpressInfo.getRequest_type() == ExpressInfo.RequestType.INPUT) {
            String msg = getString(R.string.query_failure, mExpressInfo.getCompany_name(), mExpressInfo.getPost_id());
            mAlertDialog = new CustomAlertDialog(this, false);
            mAlertDialog.show();
            mAlertDialog.setTitle(getString(R.string.app_name));
            mAlertDialog.setMessage(msg);
            mAlertDialog.setPositiveButton(R.string.save_id,
                    new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            mAlertDialog.cancel();
                            mExpressInfo.setIs_check("0");
                            mDataManager.updateHistory(mExpressInfo);
                            initUnCheck();
                        }
                    });
            mAlertDialog.setNegativeButton(getString(R.string.cancel),
                    new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            mAlertDialog.cancel();
                        }
                    });
        } else {
            Intent intent = new Intent();
            intent.setClass(this, ResultActivity.class);
            queryResult.setCompany_name(mExpressInfo.getCompany_name());
            queryResult.setCompany_icon(mExpressInfo.getCompany_icon());
            queryResult.setNu(mExpressInfo.getPost_id());
            intent.putExtra(QUERY_RESULT, queryResult);
            startActivity(intent);
        }
    }

    private void share() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_content));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(Intent.createChooser(intent, getString(R.string.share)));
    }

    private void setBtnEnable() {
        if (etPostId.length() != 0 && tvComName.length() != 0) {
            btnQuery.setEnabled(true);
        } else {
            btnQuery.setEnabled(false);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.toString().contains(" ")) {
            etPostId.setText(s.toString().replace(" ", ""));
            etPostId.setSelection(etPostId.length());
            return;
        }
        if (etPostId.length() > 0) {
            ivScan.setVisibility(View.GONE);
            ivClear.setVisibility(View.VISIBLE);
        } else {
            ivScan.setVisibility(View.VISIBLE);
            ivClear.setVisibility(View.GONE);
        }
        setBtnEnable();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.iv_scan:
                intent.setClass(this, CaptureActivity.class);
                startActivityForResult(intent, REQUEST_CAPTURE);
                break;
            case R.id.rl_choose_com:
                intent.setClass(this, ChooseComActivity.class);
                startActivityForResult(intent, REQUEST_COMPANY);
                break;
            case R.id.btn_query:
                mExpressInfo.setPost_id(etPostId.getText().toString());
                mExpressInfo.setRequest_type(ExpressInfo.RequestType.INPUT);
                query();
                break;
            case R.id.iv_clear:
                etPostId.setText("");
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        History history = mUnCheckList.get(position);
        mExpressInfo.setPost_id(history.getPost_id());
        mExpressInfo.setCompany_param(history.getCompany_param());
        mExpressInfo.setCompany_name(history.getCompany_name());
        mExpressInfo.setCompany_icon(history.getCompany_icon());
        mExpressInfo.setRequest_type(ExpressInfo.RequestType.HISTORY);
        tvComName.setText(mExpressInfo.getCompany_name());
        etPostId.setText(mExpressInfo.getPost_id());
        etPostId.setSelection(etPostId.length());
        query();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case REQUEST_CAPTURE:
                // 处理扫描结果（在界面上显示）
                String resultStr = data.getStringExtra(CaptureActivity.SCAN_RESULT);
                etPostId.setText(resultStr);
                etPostId.setSelection(etPostId.length());
                break;
            case REQUEST_COMPANY:
                String postId = mExpressInfo.getPost_id();
                mExpressInfo = (ExpressInfo) data.getSerializableExtra(EXPRESS_INFO);
                mExpressInfo.setPost_id(postId);
                tvComName.setText(mExpressInfo.getCompany_name());
                setBtnEnable();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(final MenuItem item) {
        drawerLayout.closeDrawers();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                item.setChecked(false);
            }
        }, 500);
        Intent intent = new Intent();
        switch (item.getItemId()) {
            case R.id.action_history:
                intent.setClass(this, HistoryActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_qrcode:
                intent.setClass(this, QRCodeActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_share:
                share();
                return true;
            case R.id.action_about:
                intent.setClass(this, AboutActivity.class);
                startActivity(intent);
                return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers();
            return;
        }
        if (System.currentTimeMillis() - mExitTime > 2000) {
            mExitTime = System.currentTimeMillis();
            SnackbarUtils.show(this, R.string.click2exit);
        } else {
            finish();
        }
    }
}
