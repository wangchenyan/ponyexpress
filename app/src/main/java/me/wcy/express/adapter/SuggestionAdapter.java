package me.wcy.express.adapter;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import me.wcy.express.R;
import me.wcy.express.model.CompanyEntity;
import me.wcy.express.utils.binding.Bind;
import me.wcy.express.utils.binding.ViewBinder;

/**
 * Created by wcy on 2016/6/28.
 */
public class SuggestionAdapter extends BaseAdapter {
    private List<CompanyEntity> mSuggestionList;

    public SuggestionAdapter(List<CompanyEntity> companyList) {
        mSuggestionList = companyList;
    }

    @Override
    public int getCount() {
        return mSuggestionList.size();
    }

    @Override
    public Object getItem(int position) {
        return mSuggestionList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_suggestion, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tvSuggestion.setText(Html.fromHtml(mSuggestionList.get(position).getName()));
        return convertView;
    }

    public static class ViewHolder {
        @Bind(R.id.tv_suggestion)
        public TextView tvSuggestion;

        public ViewHolder(View view) {
            ViewBinder.bind(this, view);
        }
    }
}
