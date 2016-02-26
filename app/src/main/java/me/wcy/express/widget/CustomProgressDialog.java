package me.wcy.express.widget;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import me.wcy.express.R;

/**
 * @author wcy
 */
public class CustomProgressDialog extends ProgressDialog {
    private Context mContext;
    private TextView tvMessage;

    /**
     * 自定义进度条的构造函数
     *
     * @param context 上下文
     */
    public CustomProgressDialog(Context context) {
        super(context, R.style.AppTheme_Dialog);
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCanceledOnTouchOutside(false);
        setContentView(R.layout.progress_dialog);
        tvMessage = (TextView) findViewById(R.id.tv_progress_dialog_message);
    }

    /**
     * 设置消息
     *
     * @param message 消息
     */
    @Override
    public void setMessage(CharSequence message) {
        super.setMessage(message);
        this.tvMessage.setText(message);
    }

    /**
     * 设置消息
     *
     * @param messageId 消息Id
     */
    public void setMessage(int messageId) {
        String message = mContext.getString(messageId);
        setMessage(message);
    }
}
