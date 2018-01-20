package me.wcy.express.viewholder;

import android.view.View;
import android.widget.TextView;

import me.wcy.express.R;
import me.wcy.express.model.CompanyEntity;
import me.wcy.express.utils.binding.Bind;
import me.wcy.express.widget.radapter.RLayout;
import me.wcy.express.widget.radapter.RViewHolder;

/**
 * Created by wcy on 2018/1/20.
 */
@RLayout(R.layout.view_holder_company_index)
public class CompanyIndexViewHolder extends RViewHolder<CompanyEntity> {
    @Bind(R.id.tv_index)
    private TextView tvIndex;

    public CompanyIndexViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void refresh() {
        tvIndex.setText(data.getName());
    }
}
