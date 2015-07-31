package me.wcy.express.activity;

import java.sql.SQLException;
import java.util.List;

import me.wcy.express.R;
import me.wcy.express.adapter.HistoryListAdapter;
import me.wcy.express.database.History;
import me.wcy.express.model.QueryResult;
import me.wcy.express.request.JsonRequest;
import me.wcy.express.util.StorageManager;
import me.wcy.express.util.Utils;
import me.wcy.express.widget.MyAlertDialog;
import me.wcy.express.widget.MyProgressDialog;
import me.wcy.express.util.ViewInject;

import android.annotation.SuppressLint;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.zxing.activity.CaptureActivity;

public class QueryActivity extends BaseActivity implements OnClickListener,
        TextWatcher, OnItemClickListener {
    public static final String QUERY_RESULT = "query_result";
    public static final String POST_ID = "post_id";
    public static final String COM_PARAM = "com_param";
    public static final String COM_NAME = "com_name";
    public static final String COM_ICON = "com_icon";
    public static final String IS_CHECK = "is_check";
    public static final int RESULT_COMPANY = 1;

    @ViewInject(id = R.id.post_id)
    private EditText postIdText;

    @ViewInject(id = R.id.scan)
    private ImageView scan;

    @ViewInject(id = R.id.clear)
    private ImageView clear;

    @ViewInject(id = R.id.choose_com)
    private RelativeLayout chooseCom;

    @ViewInject(id = R.id.com_name)
    private TextView comNameText;

    @ViewInject(id = R.id.list_divider)
    private View listDivider;

    @ViewInject(id = R.id.query)
    private Button query;

    @ViewInject(id = R.id.uncheck_list)
    private ListView unCheckListView;

    private MyProgressDialog progressDialog;
    private MyAlertDialog alertDialog;
    private String postId;
    private String comParam;
    private String comName;
    private String comIcon;
    private RequestQueue requestQueue;
    private StorageManager storageManager;
    private List<History> unCheckList;
    private long exitTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.query);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        postIdText.addTextChangedListener(this);
        query.setOnClickListener(this);
        query.setEnabled(false);
        chooseCom.setOnClickListener(this);
        scan.setOnClickListener(this);
        clear.setOnClickListener(this);

        progressDialog = new MyProgressDialog(this);
        requestQueue = Volley.newRequestQueue(this);
        storageManager = new StorageManager(this);
    }

    private void initUnCheck() {
        try {
            unCheckList = storageManager.getUnCheckList();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        unCheckListView.setAdapter(new HistoryListAdapter(this, unCheckList));
        unCheckListView.setOnItemClickListener(this);
        if (unCheckList.size() == 0) {
            listDivider.setVisibility(View.GONE);
        } else {
            listDivider.setVisibility(View.VISIBLE);
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
        JsonRequest<QueryResult> request = new JsonRequest<>(Utils.getQueryUrl(comParam, postId), QueryResult.class, new Listener<QueryResult>() {
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
        queryResult.setCompanyName(comName);
        queryResult.setCompanyIcon(comIcon);
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
        msg = String.format(msg, comName, postId);
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

    @SuppressLint("InflateParams")
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
            case R.id.choose_com:
                intent.setClass(this, ChooseComActivity.class);
                startActivityForResult(intent, 0);
                break;
            case R.id.query:
                postId = postIdText.getText().toString();
                query();
                break;
            case R.id.scan:
                intent.setClass(this, CaptureActivity.class);
                startActivityForResult(intent, 0);
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
        postId = history.getPost_id();
        comParam = history.getType();
        comName = history.getCom();
        comIcon = history.getIcon();
        comNameText.setText(comName);
        postIdText.setText(postId);
        postIdText.setSelection(postIdText.length());
        query();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            // 处理扫描结果（在界面上显示）
            String resultStr = data.getStringExtra(CaptureActivity.SCAN_RESULT);
            resultStr = Utils.formatString(resultStr);
            postIdText.setText(resultStr);
            postIdText.setSelection(postIdText.length());
        } else if (resultCode == RESULT_COMPANY) {
            comName = data.getStringExtra(COM_NAME);
            comIcon = data.getStringExtra(COM_ICON);
            comParam = data.getStringExtra(COM_PARAM);
            comNameText.setText(comName);
            setBtnEnable();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_query, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent();
        switch (item.getItemId()) {
            case R.id.action_history:
                intent.setClass(this, HistoryActivity.class);
                startActivity(intent);
                break;
            case R.id.action_qrcode:
                intent.setClass(this, QRCodeActivity.class);
                startActivity(intent);
                break;
            case R.id.action_share:
                share();
                break;
            case R.id.action_about:
                about();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - exitTime > 2000) {
            Toast.makeText(this, R.string.click2exit, Toast.LENGTH_SHORT)
                    .show();
        } else {
            finish();
        }
        exitTime = System.currentTimeMillis();
    }

}
