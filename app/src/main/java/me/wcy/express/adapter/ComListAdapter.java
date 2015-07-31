/**
 * 2015-4-1
 */
package me.wcy.express.adapter;

import me.wcy.express.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author wcy
 */
public class ComListAdapter extends BaseAdapter {
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

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.choose_com_list_item, null);
            holder = new ViewHolder();
            holder.comLayout = (LinearLayout) convertView
                    .findViewById(R.id.com_layout);
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.com = (TextView) convertView.findViewById(R.id.com);
            holder.icon = (ImageView) convertView.findViewById(R.id.icon);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (comNames[position].length() == 1) {
            holder.comLayout.setVisibility(View.GONE);
            holder.title.setVisibility(View.VISIBLE);
            if (comNames[position].equals("★")) {
                holder.title.setText("常用快递");
            } else {
                holder.title.setText(comNames[position]);
            }
        } else {
            holder.comLayout.setVisibility(View.VISIBLE);
            holder.title.setVisibility(View.GONE);
            holder.com.setText(comNames[position]);
            int id = context.getResources().getIdentifier(comIcons[position],
                    "drawable", context.getPackageName());
            holder.icon.setImageResource(id);
        }
        return convertView;
    }

    class ViewHolder {
        LinearLayout comLayout;
        TextView title;
        TextView com;
        ImageView icon;
    }

}
