/**
 * 2015-4-1
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

/**
 * @author wcy
 */
public class ComListAdapter extends BaseAdapter {
    public static final int TYPE_TITLE = 0;
    public static final int TYPE_COMPANY = 1;
    private Context mContext;
    private String[] mComNames;
    private String[] mComIcons;

    public ComListAdapter(Context context, String[] comNames, String[] comIcons) {
        this.mContext = context;
        this.mComNames = comNames;
        this.mComIcons = comIcons;
    }

    @Override
    public int getCount() {
        return mComNames.length;
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
    public int getItemViewType(int position) {
        if (mComNames[position].length() == 1) {
            return TYPE_TITLE;
        } else {
            return TYPE_COMPANY;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TitleViewHolder titleHolder;
        ComViewHolder comHolder;
        switch (getItemViewType(position)) {
            case TYPE_TITLE:
                if (convertView == null) {
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.activity_choose_com_list_item_title, null);
                    titleHolder = new TitleViewHolder();
                    titleHolder.tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
                    convertView.setTag(titleHolder);
                } else {
                    titleHolder = (TitleViewHolder) convertView.getTag();
                }
                if (mComNames[position].equals("☆")) {
                    titleHolder.tvTitle.setText("常用快递");
                } else {
                    titleHolder.tvTitle.setText(mComNames[position]);
                }
                break;
            case TYPE_COMPANY:
                if (convertView == null) {
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.activity_choose_com_list_item_com, null);
                    comHolder = new ComViewHolder();
                    comHolder.ivComIcon = (ImageView) convertView.findViewById(R.id.iv_com_icon);
                    comHolder.tvComName = (TextView) convertView.findViewById(R.id.tv_com_name);
                    convertView.setTag(comHolder);
                } else {
                    comHolder = (ComViewHolder) convertView.getTag();
                }
                int id = mContext.getResources().getIdentifier(mComIcons[position], "drawable", mContext.getPackageName());
                comHolder.ivComIcon.setImageResource(id);
                comHolder.tvComName.setText(mComNames[position]);
                break;
        }
        return convertView;
    }

    class TitleViewHolder {
        TextView tvTitle;
    }

    class ComViewHolder {
        ImageView ivComIcon;
        TextView tvComName;
    }
}
