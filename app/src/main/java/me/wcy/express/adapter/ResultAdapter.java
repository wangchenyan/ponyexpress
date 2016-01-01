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

import me.wcy.express.R;
import me.wcy.express.model.QueryResult;

/**
 * @author wcy
 */
@SuppressLint({"InflateParams", "ViewHolder"})
public class ResultAdapter extends BaseAdapter {
    private Context mContext;
    private QueryResult mQueryResult;

    public ResultAdapter(Context context, QueryResult queryResult) {
        this.mContext = context;
        this.mQueryResult = queryResult;
    }

    @Override
    public int getCount() {
        return mQueryResult.getData().length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(mContext).inflate(R.layout.activity_result_list_item, null);
        ViewHolder holder = new ViewHolder();
        holder.tvTime = (TextView) convertView.findViewById(R.id.tv_time);
        holder.tvDetail = (TextView) convertView.findViewById(R.id.tv_detail);
        holder.ivLogistics = (ImageView) convertView.findViewById(R.id.iv_logistics);

        holder.tvTime.setText(mQueryResult.getData()[position].getTime());
        holder.tvDetail.setText(mQueryResult.getData()[position].getContext());
        if (position == 0) {
            holder.tvTime.setTextColor(mContext.getResources().getColor(R.color.black));
            holder.tvDetail.setTextColor(mContext.getResources().getColor(R.color.black));
            holder.ivLogistics.setImageResource(R.drawable.ic_logistics_blue);
        }
        return convertView;
    }

    class ViewHolder {
        public TextView tvTime;
        public TextView tvDetail;
        public ImageView ivLogistics;
    }
}
