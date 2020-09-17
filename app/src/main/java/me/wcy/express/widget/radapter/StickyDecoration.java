package me.wcy.express.widget.radapter;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by wcy on 2020/9/16.
 */
public class StickyDecoration extends RecyclerView.ItemDecoration {
    private StickyAdapter mStickyAdapter;
    private View mStickyItemView;
    private int mStickyItemPosition = -1;

    public StickyDecoration(StickyAdapter stickyAdapter) {
        mStickyAdapter = stickyAdapter;
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        checkStickyView(parent);

        if (mStickyItemView == null) {
            return;
        }

        // 吸顶位置底部位置
        int stickyEndAt = mStickyItemView.getTop() + mStickyItemView.getHeight();
        // 找到吸顶位置底部的第一个 itemView
        View itemView = parent.findChildViewUnder(0, stickyEndAt);

        int stickyItemTop = 0;
        // 如果吸顶位置底部的第一个 itemView 是吸顶的，则要设置当前吸顶 view 的偏移量
        if (isStickyView(parent, itemView)) {
            stickyItemTop = itemView.getTop() - mStickyItemView.getHeight();
        }

        // 绘制
        c.save();
        c.translate(0, stickyItemTop);
        mStickyItemView.draw(c);
        c.restore();
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
    }

    /**
     * 初始化吸顶的 view
     * 1. 找到当前吸顶 view 的位置
     * 2. 构建 ViewHolder，绑定数据
     * 3. 测量，布局
     */
    private void checkStickyView(RecyclerView parent) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (!(layoutManager instanceof LinearLayoutManager)) {
            return;
        }

        int firstVisiblePosition = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
        int stickyItemPosition = findStickyItemPosition(firstVisiblePosition);

        // 没有可见的 item，或者没有可显示的 sticky item，不显示
        if (firstVisiblePosition < 0 || stickyItemPosition < 0) {
            mStickyItemView = null;
            mStickyItemPosition = -1;
            return;
        }

        // sticky item 没有变化，忽略
        if (stickyItemPosition == mStickyItemPosition) {
            return;
        }

        mStickyItemPosition = stickyItemPosition;
        int viewType = mStickyAdapter.getItemViewType(mStickyItemPosition);
        RecyclerView.ViewHolder viewHolder = mStickyAdapter.createViewHolder(parent, viewType);
        mStickyAdapter.bindViewHolder(viewHolder, mStickyItemPosition);
        mStickyItemView = viewHolder.itemView;

        ViewGroup.LayoutParams layoutParams = mStickyItemView.getLayoutParams();
        if (layoutParams == null) {
            layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            mStickyItemView.setLayoutParams(layoutParams);
        }

        int heightMode = View.MeasureSpec.getMode(layoutParams.height);
        int heightSize = View.MeasureSpec.getSize(layoutParams.height);

        if (heightMode == View.MeasureSpec.UNSPECIFIED) {
            heightMode = View.MeasureSpec.EXACTLY;
        }

        int maxHeight = parent.getHeight() - parent.getPaddingTop() - parent.getPaddingBottom();
        heightSize = Math.min(heightSize, maxHeight);

        int widthSize = parent.getWidth() - parent.getPaddingLeft() - parent.getPaddingRight();

        int widthSpec = View.MeasureSpec.makeMeasureSpec(widthSize, View.MeasureSpec.EXACTLY);
        int heightSpec = View.MeasureSpec.makeMeasureSpec(heightSize, heightMode);

        mStickyItemView.measure(widthSpec, heightSpec);

        mStickyItemView.layout(0, 0, mStickyItemView.getMeasuredWidth(), mStickyItemView.getMeasuredHeight());
    }

    /**
     * 从 endPosition 位置开始，找到最后一个吸顶 item 的位置
     */
    private int findStickyItemPosition(int endPosition) {
        if (endPosition >= mStickyAdapter.getItemCount()) {
            return -1;
        }
        for (int i = endPosition; i >= 0; i--) {
            int viewType = mStickyAdapter.getItemViewType(i);
            boolean isSticky = mStickyAdapter.isStickyViewType(viewType);
            if (isSticky) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 当前 item 是否是吸顶的
     */
    private boolean isStickyView(RecyclerView parent, View itemView) {
        int position = parent.getChildAdapterPosition(itemView);
        if (position == RecyclerView.NO_POSITION) {
            return false;
        }
        int viewType = mStickyAdapter.getItemViewType(position);
        return mStickyAdapter.isStickyViewType(viewType);
    }
}
