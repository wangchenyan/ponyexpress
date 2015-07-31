/**
 * 2015-4-2
 */
package me.wcy.express.adapter;

import me.wcy.express.R;
import me.wcy.express.model.QueryResult;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author wcy
 */
@SuppressLint({"InflateParams", "ViewHolder"})
public class ResultAdapter extends BaseAdapter {
    private Context context;
    private QueryResult queryResult;

    public ResultAdapter(Context context, QueryResult queryResult) {
        this.context = context;
        this.queryResult = queryResult;
    }

    @Override
    public int getCount() {
        return queryResult.getData().length;
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
        convertView = LayoutInflater.from(context).inflate(
                R.layout.result_list_item, null);
        ViewHolder holder = new ViewHolder();
        holder.timeView = (TextView) convertView.findViewById(R.id.time_view);
        holder.detailView = (TextView) convertView
                .findViewById(R.id.detail_view);
        holder.logisticsView = (ImageView) convertView
                .findViewById(R.id.logistics);

        holder.timeView.setText(queryResult.getData()[position].getTime());
        holder.detailView.setText(queryResult.getData()[position].getContext());
        if (position == 0) {
            holder.timeView.setTextColor(context.getResources().getColor(
                    R.color.black));
            holder.detailView.setTextColor(context.getResources().getColor(
                    R.color.black));
            holder.logisticsView.setImageResource(R.drawable.ic_logistics_blue);
        }
        return convertView;
    }

    class ViewHolder {
        public TextView timeView;
        public TextView detailView;
        public ImageView logisticsView;
    }

}
