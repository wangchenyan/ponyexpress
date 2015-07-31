package me.wcy.express.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
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
import me.wcy.express.util.ViewInject;

public class HistoryActivity extends BaseActivity implements
        OnItemClickListener, OnItemLongClickListener {

    @ViewInject(id = R.id.history_list)
    private ListView historyListView;

    @ViewInject(id = R.id.nothing)
    private LinearLayout nothing;

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
                        if (queryResult.getStatus().equals("200")) {
                            onQuerySuccess(queryResult);
                        } else {
                            onQueryFailure();
                        }
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
        queryResult.setCompanyName(expressInfo.getComName());
        queryResult.setCompanyIcon(expressInfo.getComIcon());
        intent.putExtra(QueryActivity.QUERY_RESULT, queryResult);
        startActivity(intent);
        StorageManager storageManager = new StorageManager(this);
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

    @Override
    public void onItemClick(AdapterView<?> view, View arg1, int position,
                            long arg3) {
        expressInfo.setPostId(historyList.get(position).getPost_id());
        expressInfo.setComParam(historyList.get(position).getType());
        expressInfo.setComName(historyList.get(position).getCom());
        expressInfo.setComIcon(historyList.get(position).getIcon());
        query();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> view, View arg1,
                                   int position, long arg3) {
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
