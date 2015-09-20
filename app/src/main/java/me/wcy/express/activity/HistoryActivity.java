package me.wcy.express.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import java.sql.SQLException;
import java.util.List;

import butterknife.Bind;
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

public class HistoryActivity extends BaseActivity implements
        OnItemClickListener, OnItemLongClickListener {

    @Bind(R.id.history_list)
    ListView historyListView;

    @Bind(R.id.nothing)
    LinearLayout nothing;

    private MyProgressDialog progressDialog;
    private MyAlertDialog alertDialog;
    private ExpressInfo expressInfo;
    private RequestQueue requestQueue;
    private StorageManager storageManager;
    private List<History> historyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history);

        progressDialog = new MyProgressDialog(this);
        requestQueue = Volley.newRequestQueue(this);
        storageManager = new StorageManager(this);
        expressInfo = new ExpressInfo();
    }

    private void init() {
        try {
            historyList = storageManager.getHistoryList();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        historyListView.setAdapter(new HistoryListAdapter(this, historyList));
        historyListView.setOnItemClickListener(this);
        historyListView.setOnItemLongClickListener(this);
        if (historyList.size() == 0) {
            nothing.setVisibility(View.VISIBLE);
        } else {
            nothing.setVisibility(View.GONE);
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
        JsonRequest<QueryResult> request = new JsonRequest<>(
                Utils.getQueryUrl(expressInfo), QueryResult.class,
                new Response.Listener<QueryResult>() {

                    @Override
                    public void onResponse(QueryResult queryResult) {
                        Log.i("Query", queryResult.getMessage());
                        progressDialog.cancel();
                        onQuerySuccess(queryResult);
                    }
                }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Query", error.getMessage(), error);
                progressDialog.cancel();
                Toast.makeText(HistoryActivity.this,
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
        queryResult.setCompany_name(expressInfo.getCompany_name());
        queryResult.setCompany_icon(expressInfo.getCompany_icon());
        intent.putExtra(QueryActivity.QUERY_RESULT, queryResult);
        startActivity(intent);
        StorageManager storageManager = new StorageManager(this);
        if (queryResult.getStatus().equals("200")) {
            expressInfo.setIs_check(queryResult.getIscheck());
        } else {
            expressInfo.setIs_check("0");
        }
        try {
            storageManager.storeData(expressInfo);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> view, View arg1, int position, long arg3) {
        expressInfo.setPost_id(historyList.get(position).getPost_id());
        expressInfo.setCompany_param(historyList.get(position).getCompany_param());
        expressInfo.setCompany_name(historyList.get(position).getCompany_name());
        expressInfo.setCompany_icon(historyList.get(position).getCompany_icon());
        query();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> view, View arg1, int position, long arg3) {
        final int which = position;
        alertDialog = new MyAlertDialog(this);
        alertDialog.show();
        alertDialog.setTitle(getResources().getString(R.string.tips));
        alertDialog.setMessage(getResources().getString(
                R.string.sure_delete_history));
        alertDialog.setPositiveButton(getResources().getString(R.string.sure),
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        alertDialog.cancel();
                        try {
                            storageManager.deleteById(historyList.get(which)
                                    .getPost_id());
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        init();
                    }
                });
        alertDialog.setNegativeButton(
                getResources().getString(R.string.cancle),
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        alertDialog.cancel();
                    }
                });
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

}
