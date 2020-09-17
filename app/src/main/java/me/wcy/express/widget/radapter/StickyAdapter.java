package me.wcy.express.widget.radapter;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

/**
 * Created by wcy on 2020/9/16.
 */
public interface StickyAdapter<VH extends RecyclerView.ViewHolder> {

    VH createViewHolder(ViewGroup parent, int viewType);

    void bindViewHolder(VH holder, int position);

    int getItemViewType(int position);

    int getItemCount();

    boolean isStickyViewType(int viewType);
}
