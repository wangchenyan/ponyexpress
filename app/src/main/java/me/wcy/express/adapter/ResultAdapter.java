/**
 * 2015-4-2
 */
package me.wcy.express.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import me.wcy.express.R;
import me.wcy.express.model.SearchResult;
import me.wcy.express.utils.Utils;
import me.wcy.express.utils.binding.Bind;
import me.wcy.express.utils.binding.ViewBinder;

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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Context context = parent.getContext();
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.view_holder_search_result, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tvTime.setText(mSearchResult.getData()[position].getTime());
        holder.tvDetail.setText(mSearchResult.getData()[position].getContext());
        boolean first = (position == 0);
        holder.line.setPadding(0, Utils.dp2px(context, first ? 12 : 0), 0, 0);
        holder.ivLogistics.setSelected(first);
        holder.tvTime.setSelected(first);
        holder.tvDetail.setSelected(first);
        return convertView;
    }

    private static class ViewHolder {
        @Bind(R.id.line)
        private View line;
        @Bind(R.id.iv_logistics)
        private ImageView ivLogistics;
        @Bind(R.id.tv_time)
        private TextView tvTime;
        @Bind(R.id.tv_detail)
        private TextView tvDetail;

        public ViewHolder(View view) {
            ViewBinder.bind(this, view);
        }
    }
}
