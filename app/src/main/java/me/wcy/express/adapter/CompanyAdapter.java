/**
 * 2015-4-1
 */
package me.wcy.express.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import me.wcy.express.R;
import me.wcy.express.http.HttpClient;
import me.wcy.express.model.CompanyEntity;
import me.wcy.express.utils.binding.Bind;
import me.wcy.express.utils.binding.ViewBinder;

/**
 * @author wcy
 */
public class CompanyAdapter extends BaseAdapter {
    public static final int TYPE_TITLE = 0;
    public static final int TYPE_COMPANY = 1;
    private List<CompanyEntity> mCompanyList;

    public CompanyAdapter(List<CompanyEntity> companyList) {
        mCompanyList = companyList;
    }

    @Override
    public int getCount() {
        return mCompanyList.size();
    }

    @Override
    public Object getItem(int position) {
        return mCompanyList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        if (TextUtils.isEmpty(mCompanyList.get(position).getCode())) {
            return TYPE_TITLE;
        } else {
            return TYPE_COMPANY;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public boolean isEnabled(int position) {
        return !TextUtils.isEmpty(mCompanyList.get(position).getCode());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Context context = parent.getContext();
        IndexViewHolder indexHolder;
        CompanyViewHolder companyHolder;
        switch (getItemViewType(position)) {
            case TYPE_TITLE:
                if (convertView == null) {
                    convertView = LayoutInflater.from(context).inflate(R.layout.view_holder_company_index, parent, false);
                    indexHolder = new IndexViewHolder(convertView);
                    convertView.setTag(indexHolder);
                } else {
                    indexHolder = (IndexViewHolder) convertView.getTag();
                }
                indexHolder.tvIndex.setText(mCompanyList.get(position).getName());
                break;
            case TYPE_COMPANY:
                if (convertView == null) {
                    convertView = LayoutInflater.from(context).inflate(R.layout.view_holder_company, parent, false);
                    companyHolder = new CompanyViewHolder(convertView);
                    convertView.setTag(companyHolder);
                } else {
                    companyHolder = (CompanyViewHolder) convertView.getTag();
                }
                Glide.with(context)
                        .load(HttpClient.urlForLogo(mCompanyList.get(position).getLogo()))
                        .dontAnimate()
                        .placeholder(R.drawable.ic_default_logo)
                        .into(companyHolder.ivLogo);
                companyHolder.tvName.setText(mCompanyList.get(position).getName());
                break;
        }
        return convertView;
    }

    public static class IndexViewHolder {
        @Bind(R.id.tv_index)
        public TextView tvIndex;

        public IndexViewHolder(View view) {
            ViewBinder.bind(this, view);
        }
    }

    public static class CompanyViewHolder {
        @Bind(R.id.iv_logo)
        public ImageView ivLogo;
        @Bind(R.id.tv_name)
        public TextView tvName;

        public CompanyViewHolder(View view) {
            ViewBinder.bind(this, view);
        }
    }
}
