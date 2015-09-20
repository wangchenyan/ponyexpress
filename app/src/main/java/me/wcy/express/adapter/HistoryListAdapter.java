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

import java.util.List;

import me.wcy.express.R;
import me.wcy.express.database.History;

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
            holder.comIcon = (ImageView) convertView.findViewById(R.id.com_icon);
            holder.comName = (TextView) convertView.findViewById(R.id.com_name);
            holder.postId = (TextView) convertView.findViewById(R.id.post_id);
            holder.isCheck = (TextView) convertView.findViewById(R.id.is_check);
            holder.remark = (TextView) convertView.findViewById(R.id.remark);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        int id = context.getResources().getIdentifier(
                historyList.get(position).getCompany_icon(), "drawable",
                context.getPackageName());
        String isCheck = historyList.get(position).getIs_check();
        int checkTextColor;
        if (isCheck.equals("0")) {
            isCheck = context.getString(R.string.uncheck);
            checkTextColor = context.getResources().getColor(R.color.orange_700);
        } else {
            isCheck = context.getString(R.string.ischeck);
            checkTextColor = context.getResources().getColor(R.color.grey);
        }
        holder.comIcon.setImageResource(id);
        holder.comName.setText(historyList.get(position).getCompany_name());
        holder.postId.setText(historyList.get(position).getPost_id());
        holder.isCheck.setText(isCheck);
        holder.isCheck.setTextColor(checkTextColor);
        holder.remark.setText(historyList.get(position).getRemark());
        return convertView;
    }

    class ViewHolder {
        ImageView comIcon;
        TextView comName;
        TextView postId;
        TextView isCheck;
        TextView remark;
    }
}
