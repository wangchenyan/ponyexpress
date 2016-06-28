/**
 * 2015-4-2
 */
package me.wcy.express.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.wcy.express.R;
import me.wcy.express.model.SearchResult;

/**
 * @author wcy
 */
public class ResultAdapter extends BaseAdapter {
    private SearchResult mSearchResult;

    public ResultAdapter(SearchResult searchResult) {
        this.mSearchResult = searchResult;
    }

    @Override
    public int getCount() {
        return mSearchResult.getData().length;
    }

    @Override
    public Object getItem(int position) {
        return mSearchResult.getData()[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Context context = parent.getContext();
        convertView = LayoutInflater.from(context).inflate(R.layout.view_holder_search_result, parent, false);
        ViewHolder holder = new ViewHolder(convertView);

        holder.tvTime.setText(mSearchResult.getData()[position].getTime());
        holder.tvDetail.setText(mSearchResult.getData()[position].getContext());
        if (position == 0) {
            holder.tvTime.setTextColor(context.getResources().getColor(R.color.black));
            holder.tvDetail.setTextColor(context.getResources().getColor(R.color.black));
            holder.ivLogistics.setImageResource(R.drawable.ic_logistics_blue);
        }
        return convertView;
    }

    public static class ViewHolder {
        @Bind(R.id.tv_time)
        public TextView tvTime;
        @Bind(R.id.tv_detail)
        public TextView tvDetail;
        @Bind(R.id.iv_logistics)
        public ImageView ivLogistics;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
