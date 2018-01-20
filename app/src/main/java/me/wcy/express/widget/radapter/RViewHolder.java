package me.wcy.express.widget.radapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import me.wcy.express.utils.binding.ViewBinder;

/**
 * Created by wcy on 2017/11/26.
 */
public abstract class RViewHolder<T> extends RecyclerView.ViewHolder {
    protected Context context;
    protected View itemView;
    protected RAdapter<T> adapter;
    protected T data;
    protected int position;

    public RViewHolder(View itemView) {
        super(itemView);
        this.itemView = itemView;
        context = itemView.getContext();
        ViewBinder.bind(this, itemView);
    }

    public void setAdapter(RAdapter<T> adapter) {
        this.adapter = adapter;
    }

    public void setData(T t) {
        this.data = t;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public abstract void refresh();
}
