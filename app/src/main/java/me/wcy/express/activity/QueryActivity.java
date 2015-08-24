package me.wcy.express.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
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
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.zxing.activity.CaptureActivity;

import java.sql.SQLException;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.wcy.express.R;
import me.wcy.express.adapter.HistoryListAdapter;
import me.wcy.express.database.History;
import me.wcy.express.model.ExpressInfo;
import me.wcy.express.model.QueryResult;
import me.wcy.express.request.JsonRequest;
import me.wcy.express.util.StorageManager;
import me.wcy.express.util.Utils;
import me.wcy.express.widget.MyAlertDialog;
import me.wcy.express.widget.MyProgressDialog;

@SuppressLint("InflateParams")
public class QueryActivity extends AppCompatActivity implements OnClickListener,
        TextWatcher, OnItemClickListener, NavigationView.OnNavigationItemSelectedListener {
    public static final String QUERY_RESULT = "query_result";
    public static final String EXPRESS_INFO = "express_info";
    public static final int REQUEST_CAPTURE = 0;
    public static final int REQUEST_COMPANY = 1;

    @Bind(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    @Bind(R.id.navigation_view)
    NavigationView navigationView;

    @Bind(R.id.post_id)
    EditText postIdText;

    @Bind(R.id.scan)
    ImageView scan;

    @Bind(R.id.clear)
    ImageView clear;

    @Bind(R.id.choose_com)
    RelativeLayout chooseCom;

    @Bind(R.id.com_name)
    TextView comNameText;

    @Bind(R.id.query)
    Button query;

    @Bind(R.id.ll_uncheck)
    LinearLayout llUnCheck;

    @Bind(R.id.lv_uncheck)
    ListView lvUnCheck;

    private MyProgressDialog progressDialog;
    private MyAlertDialog alertDialog;
    private ExpressInfo expressInfo;
    private RequestQueue requestQueue;
    private StorageManager storageManager;
    private List<History> unCheckList;
    private long exitTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.query);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
        }

        navigationView.setNavigationItemSelectedListener(this);
        postIdText.addTextChangedListener(this);
        query.setOnClickListener(this);
        query.setEnabled(false);
        chooseCom.setOnClickListener(this);
        scan.setOnClickListener(this);
        clear.setOnClickListener(this);

        progressDialog = new MyProgressDialog(this);
        requestQueue = Volley.newRequestQueue(this);
        storageManager = new StorageManager(this);
        expressInfo = new ExpressInfo();
    }

    private void initUnCheck() {
        try {
            unCheckList = storageManager.getUnCheckList();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        lvUnCheck.setAdapter(new HistoryListAdapter(this, unCheckList));
        lvUnCheck.setOnItemClickListener(this);
        if (unCheckList.size() == 0) {
            llUnCheck.setVisibility(View.GONE);
        } else {
            llUnCheck.setVisibility(View.VISIBLE);
        }
    }

    private void query() {
        if (!Utils.isNetworkAvailable(this)) {
            Toast.makeText(this, R.string.network_error, Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        progressDialog.show();
        progressDialog.setMessage(getResources().getString(R.string.querying));
        JsonRequest<QueryResult> request = new JsonRequest<>(Utils.getQueryUrl(expressInfo),
                QueryResult.class, new Listener<QueryResult>() {
            @Override
            public void onResponse(QueryResult queryResult) {
                Log.i("Query", queryResult.getMessage());
                progressDialog.cancel();
                if (queryResult.getStatus().equals("200")) {
                    onQuerySuccess(queryResult);
                } else {
                    onQueryFailure();
                }
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("Query", volleyError.getMessage(), volleyError);
                progressDialog.cancel();
                Toast.makeText(QueryActivity.this,
                        R.string.system_busy, Toast.LENGTH_SHORT)
                        .show();
            }
        });
        request.setShouldCache(false);
        requestQueue.add(request);
    }

    private void onQuerySuccess(QueryResult queryResult) {
        Intent intent = new Intent();
        intent.setClass(this, ResultActivity.class);
        queryResult.setCompanyName(expressInfo.getComName());
        queryResult.setCompanyIcon(expressInfo.getComIcon());
        intent.putExtra(QUERY_RESULT, queryResult);
        startActivity(intent);
        try {
            storageManager.storeData(queryResult);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void onQueryFailure() {
        String msg = getString(R.string.query_failure);
        msg = String.format(msg, expressInfo.getComName(), expressInfo.getPostId());
        alertDialog = new MyAlertDialog(this, true);
        alertDialog.show();
        alertDialog.setTitle(getResources().getString(R.string.app_name));
        alertDialog.setMessage(msg);
        alertDialog.setPositiveButton(getResources().getString(R.string.sure),
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        alertDialog.cancel();
                    }
                });
    }

    private void share() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_content));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(Intent.createChooser(intent, getString(R.string.share)));
    }

    private void about() {
        View dialogView = getLayoutInflater().inflate(R.layout.about_dialog,
                null);
        TextView version = (TextView) dialogView.findViewById(R.id.version);
        version.setText("V " + Utils.getVersion(this));
        Builder builder = new Builder(this);
        builder.setTitle(R.string.about);
        builder.setView(dialogView);
        builder.setPositiveButton(R.string.sure, null);
        builder.setCancelable(false);
        builder.show();
    }

    private void setBtnEnable() {
        if (postIdText.length() != 0 && comNameText.length() != 0) {
            query.setBackgroundResource(R.drawable.ic_btn_blue_pressed_effect);
            query.setEnabled(true);
        } else {
            query.setBackgroundResource(R.drawable.ic_btn_grey);
            query.setEnabled(false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initUnCheck();
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (postIdText.length() != 0) {
            scan.setVisibility(View.GONE);
            clear.setVisibility(View.VISIBLE);
        } else {
            scan.setVisibility(View.VISIBLE);
            clear.setVisibility(View.GONE);
        }
        setBtnEnable();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.scan:
                intent.setClass(this, CaptureActivity.class);
                startActivityForResult(intent, REQUEST_CAPTURE);
                break;
            case R.id.choose_com:
                intent.setClass(this, ChooseComActivity.class);
                startActivityForResult(intent, REQUEST_COMPANY);
                break;
            case R.id.query:
                expressInfo.setPostId(postIdText.getText().toString());
                query();
                break;
            case R.id.clear:
                postIdText.setText("");
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        History history = unCheckList.get(position);
        expressInfo.setPostId(history.getPost_id());
        expressInfo.setComParam(history.getType());
        expressInfo.setComName(history.getCom());
        expressInfo.setComIcon(history.getIcon());
        comNameText.setText(expressInfo.getComName());
        postIdText.setText(expressInfo.getPostId());
        postIdText.setSelection(postIdText.length());
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
                postIdText.setText(resultStr);
                postIdText.setSelection(postIdText.length());
                break;
            case REQUEST_COMPANY:
                String postId = expressInfo.getPostId();
                expressInfo = (ExpressInfo) data.getSerializableExtra(EXPRESS_INFO);
                expressInfo.setPostId(postId);
                comNameText.setText(expressInfo.getComName());
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
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        drawerLayout.closeDrawers();
        menuItem.setChecked(false);
        Intent intent = new Intent();
        switch (menuItem.getItemId()) {
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
                about();
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
        if (System.currentTimeMillis() - exitTime > 2000) {
            exitTime = System.currentTimeMillis();
            Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content),
                    R.string.click2exit, Snackbar.LENGTH_SHORT).show();
        } else {
            finish();
        }
    }


}
