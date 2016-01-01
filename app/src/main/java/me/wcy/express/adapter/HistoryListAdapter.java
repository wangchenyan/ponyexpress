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
    private Context mContext;
    private List<History> mData;

    public HistoryListAdapter(Context context, List<History> data) {
        this.mContext = context;
        this.mData = data;
    }

    @Override
    public int getCount() {
        return mData.size();
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.activity_history_list_item, null);
            holder = new ViewHolder();
            holder.ivComIcon = (ImageView) convertView.findViewById(R.id.iv_com_icon);
            holder.tvComName = (TextView) convertView.findViewById(R.id.tv_com_name);
            holder.tvPostId = (TextView) convertView.findViewById(R.id.tv_post_id);
            holder.tvIsCheck = (TextView) convertView.findViewById(R.id.tv_is_check);
            holder.tvRemark = (TextView) convertView.findViewById(R.id.tv_remark);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        int id = mContext.getResources().getIdentifier(mData.get(position).getCompany_icon(), "drawable", mContext.getPackageName());
        String isCheck = mData.get(position).getIs_check();
        int checkTextColor;
        if (isCheck.equals("0")) {
            isCheck = mContext.getString(R.string.uncheck);
            checkTextColor = mContext.getResources().getColor(R.color.orange_700);
        } else {
            isCheck = mContext.getString(R.string.ischeck);
            checkTextColor = mContext.getResources().getColor(R.color.grey);
        }
        holder.ivComIcon.setImageResource(id);
        holder.tvComName.setText(mData.get(position).getCompany_name());
        holder.tvPostId.setText(mData.get(position).getPost_id());
        holder.tvIsCheck.setText(isCheck);
        holder.tvIsCheck.setTextColor(checkTextColor);
        holder.tvRemark.setText(mData.get(position).getRemark());
        return convertView;
    }

    class ViewHolder {
        ImageView ivComIcon;
        TextView tvComName;
        TextView tvPostId;
        TextView tvIsCheck;
        TextView tvRemark;
    }
}
