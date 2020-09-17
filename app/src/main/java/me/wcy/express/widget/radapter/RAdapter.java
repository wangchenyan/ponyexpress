package me.wcy.express.widget.radapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wcy on 2017/11/26.
 */
public class RAdapter<T> extends RecyclerView.Adapter<RViewHolder<T>> implements StickyAdapter<RViewHolder<T>> {
    private static final String TAG = "RAdapter";

    private List<T> dataList;
    private RAdapterDelegate<T> delegate;
    private List<Class<? extends RViewHolder<T>>> viewHolderClassList;
    private Object tag;

    public RAdapter(List<T> dataList, RAdapterDelegate<T> delegate) {
        this.dataList = dataList;
        this.delegate = delegate;
        viewHolderClassList = new ArrayList<>();
    }

    @Override
    public RViewHolder<T> onCreateViewHolder(ViewGroup parent, int viewType) {
        try {
            Class<? extends RViewHolder<T>> clazz = viewHolderClassList.get(viewType);
            int resId = getLayoutResId(clazz);
            View view = LayoutInflater.from(parent.getContext()).inflate(resId, parent, false);
            Constructor constructor = clazz.getConstructor(View.class);
            RViewHolder<T> viewHolder = (RViewHolder<T>) constructor.newInstance(view);
            viewHolder.setAdapter(this);
            return viewHolder;
        } catch (Exception e) {
            throw new IllegalStateException("create view holder error", e);
        }
    }

    @Override
    public void onBindViewHolder(RViewHolder<T> holder, int position) {
        try {
            holder.setPosition(position);
            holder.setData(dataList.get(position));
            holder.refresh();
        } catch (Exception e) {
            Log.e(TAG, "bind view holder error", e);
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    @Override
    public int getItemViewType(int position) {
        Class<? extends RViewHolder<T>> clazz = delegate.getViewHolderClass(position);
        if (!viewHolderClassList.contains(clazz)) {
            viewHolderClassList.add(clazz);
        }
        return viewHolderClassList.indexOf(clazz);
    }

    @Override
    public boolean isStickyViewType(int viewType) {
        try {
            Class<? extends RViewHolder<T>> clazz = viewHolderClassList.get(viewType);
            RLayout layout = clazz.getAnnotation(RLayout.class);
            return layout.isSticky();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void insertItem(T t) {
        dataList.add(t);
        notifyItemInserted(dataList.size() - 1);
    }

    public void insertItem(T t, int position) {
        dataList.add(position, t);
        notifyItemInserted(position);
    }

    public void removeItem(int position) {
        dataList.remove(position);
        notifyItemRemoved(position);
    }

    public List<T> getDataList() {
        return dataList;
    }

    public Object getTag() {
        return tag;
    }

    public void setTag(Object tag) {
        this.tag = tag;
    }

    private int getLayoutResId(Class<?> clazz) {
        if (clazz == RViewHolder.class) {
            return 0;
        }
        RLayout layout = clazz.getAnnotation(RLayout.class);
        if (layout == null) {
            // 找不到去父类找
            return getLayoutResId(clazz.getSuperclass());
        }
        return layout.value();
    }
}
