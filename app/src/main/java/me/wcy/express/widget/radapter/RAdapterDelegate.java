package me.wcy.express.widget.radapter;

/**
 * Created by wcy on 2017/11/26.
 */
public interface RAdapterDelegate<T> {
    Class<? extends RViewHolder<T>> getViewHolderClass(int position);
}
