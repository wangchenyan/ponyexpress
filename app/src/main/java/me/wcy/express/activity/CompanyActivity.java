package me.wcy.express.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import me.wcy.express.R;
import me.wcy.express.adapter.CompanyAdapter;
import me.wcy.express.model.CompanyEntity;
import me.wcy.express.model.SearchInfo;
import me.wcy.express.utils.Extras;
import me.wcy.express.widget.IndexBar;

public class CompanyActivity extends BaseActivity implements OnItemClickListener {
    @Bind(R.id.lv_company)
    ListView lvCompany;
    @Bind(R.id.ib_indicator)
    IndexBar ibIndicator;
    @Bind(R.id.tv_indicator)
    TextView tvIndicator;
    private List<CompanyEntity> mCompanyList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company);

        readCompany();
        lvCompany.setAdapter(new CompanyAdapter(mCompanyList));
        ibIndicator.setData(mCompanyList, lvCompany, tvIndicator);
    }

    @Override
    protected void setListener() {
        lvCompany.setOnItemClickListener(this);
    }

    private void readCompany() {
        try {
            InputStream is = getAssets().open("company.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer);

            Gson gson = new Gson();
            JsonParser parser = new JsonParser();
            JsonArray jArray = parser.parse(json).getAsJsonArray();
            for (JsonElement obj : jArray) {
                CompanyEntity company = gson.fromJson(obj, CompanyEntity.class);
                mCompanyList.add(company);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> view, View arg1, int position, long arg3) {
        SearchInfo searchInfo = new SearchInfo();
        searchInfo.setName(mCompanyList.get(position).getName());
        searchInfo.setLogo(mCompanyList.get(position).getLogo());
        searchInfo.setCode(mCompanyList.get(position).getCode());
        Intent intent = new Intent();
        intent.putExtra(Extras.SEARCH_INFO, searchInfo);
        setResult(RESULT_OK, intent);
        finish();
    }
}
