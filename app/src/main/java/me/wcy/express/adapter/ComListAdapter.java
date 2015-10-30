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
    private Context context;
    private String[] comNames;
    private String[] comIcons;

    public ComListAdapter(Context context, String[] comNames, String[] comIcons) {
        super();
        this.context = context;
        this.comNames = comNames;
        this.comIcons = comIcons;
    }

    @Override
    public int getCount() {
        return comNames.length;
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
        if (comNames[position].length() == 1) {
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
                    convertView = LayoutInflater.from(context).inflate(R.layout.choose_com_list_item_title, null);
                    titleHolder = new TitleViewHolder();
                    titleHolder.title = (TextView) convertView.findViewById(R.id.title);
                    convertView.setTag(titleHolder);
                } else {
                    titleHolder = (TitleViewHolder) convertView.getTag();
                }
                if (comNames[position].equals("★")) {
                    titleHolder.title.setText("常用快递");
                } else {
                    titleHolder.title.setText(comNames[position]);
                }
                break;
            case TYPE_COMPANY:
                if (convertView == null) {
                    convertView = LayoutInflater.from(context).inflate(R.layout.choose_com_list_item_com, null);
                    comHolder = new ComViewHolder();
                    comHolder.com = (TextView) convertView.findViewById(R.id.com);
                    comHolder.icon = (ImageView) convertView.findViewById(R.id.icon);
                    convertView.setTag(comHolder);
                } else {
                    comHolder = (ComViewHolder) convertView.getTag();
                }
                comHolder.com.setText(comNames[position]);
                int id = context.getResources().getIdentifier(comIcons[position],
                        "drawable", context.getPackageName());
                comHolder.icon.setImageResource(id);
                break;
        }
        return convertView;
    }

    class TitleViewHolder {
        TextView title;
    }

    class ComViewHolder {
        TextView com;
        ImageView icon;
    }
}
