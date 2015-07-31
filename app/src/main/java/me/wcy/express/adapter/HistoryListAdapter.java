/**
 * 2015-4-2
 */
package me.wcy.express.adapter;

import java.util.List;

import me.wcy.express.R;
import me.wcy.express.database.History;

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
@SuppressLint("InflateParams")
public class HistoryListAdapter extends BaseAdapter {
    private Context context;
    private List<History> historyList;

    public HistoryListAdapter(Context context, List<History> historyList) {
        this.context = context;
        this.historyList = historyList;
    }

    @Override
    public int getCount() {
        return historyList.size();
    }

    @Override
    public Object getItem(int arg0) {
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.history_list_item, null);
            holder = new ViewHolder();
            holder.icon = (ImageView) convertView.findViewById(R.id.icon);
            holder.com = (TextView) convertView.findViewById(R.id.com);
            holder.postId = (TextView) convertView.findViewById(R.id.post_id);
            holder.isCheck = (TextView) convertView.findViewById(R.id.is_check);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        int id = context.getResources().getIdentifier(
                historyList.get(position).getIcon(), "drawable",
                context.getPackageName());
        String isCheck = historyList.get(position).getIs_check();
        if (isCheck.equals("1")) {
            isCheck = context.getResources()
                    .getString(R.string.ischeck);
        } else {
            isCheck = context.getResources()
                    .getString(R.string.uncheck);
        }
        holder.icon.setImageResource(id);
        holder.com.setText(historyList.get(position).getCom());
        holder.postId.setText(historyList.get(position).getPost_id());
        holder.isCheck.setText(isCheck);
        return convertView;
    }

    class ViewHolder {
        ImageView icon;
        TextView com;
        TextView postId;
        TextView isCheck;
    }
}
