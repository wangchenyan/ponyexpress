package me.wcy.express.widget;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import me.wcy.express.R;

/**
 * @author wcy
 */
public class CustomAlertDialog extends AlertDialog {
    private Context mContext;
    private boolean mSingleButton;
    private TextView tvTitle;
    private TextView tvMessage;
    private Button btnLeft;
    private Button btnRight;

    /**
     * 自定义对话框的构造函数,默认为两个按钮
     *
     * @param context 上下文
     */
    public CustomAlertDialog(Context context) {
        this(context, false);
    }

    /**
     * 自定义对话框的构造函数
     *
     * @param context      上下文
     * @param singleButton 是否是单个按钮
     */
    public CustomAlertDialog(Context context, boolean singleButton) {
        super(context, R.style.AppTheme_Dialog);
        this.mContext = context;
        this.mSingleButton = singleButton;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCanceledOnTouchOutside(false);
        setContentView(R.layout.alert_dialog);
        tvTitle = (TextView) findViewById(R.id.tv_alert_dialog_title);
        tvMessage = (TextView) findViewById(R.id.tv_alert_dialog_message);
        btnLeft = (Button) findViewById(R.id.btn_alert_dialog_btn_left);
        btnRight = (Button) findViewById(R.id.btn_alert_dialog_btn_right);
        tvTitle.getPaint().setFakeBoldText(true);
        if (mSingleButton) {
            btnLeft.setBackgroundResource(R.drawable.alert_dialog_btn_selector);
            btnRight.setVisibility(View.GONE);
        }
    }

    /**
     * 设置标题
     *
     * @param title 标题
     */
    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
        this.tvTitle.setText(title);
    }

    /**
     * 设置标题
     *
     * @param titleId 标题Id
     */
    @Override
    public void setTitle(int titleId) {
        super.setTitle(titleId);
        String title = mContext.getString(titleId);
        setTitle(title);
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

    /**
     * 设置"确定"按钮
     *
     * @param text     文字
     * @param listener 监听器
     */
    public void setPositiveButton(CharSequence text, View.OnClickListener listener) {
        btnLeft.setText(text);
        btnLeft.setOnClickListener(listener);
    }

    /**
     * 设置"确定"按钮
     *
     * @param textId   文字Id
     * @param listener 监听器
     */
    public void setPositiveButton(int textId, View.OnClickListener listener) {
        String text = mContext.getString(textId);
        setPositiveButton(text, listener);
    }

    /**
     * 设置"取消"按钮
     *
     * @param text     文字
     * @param listener 监听器
     */
    public void setNegativeButton(CharSequence text, View.OnClickListener listener) {
        btnRight.setText(text);
        btnRight.setOnClickListener(listener);
    }

    /**
     * 设置"取消"按钮
     *
     * @param textId   文字Id
     * @param listener 监听器
     */
    public void setNegativeButton(int textId, View.OnClickListener listener) {
        String text = mContext.getString(textId);
        setNegativeButton(text, listener);
    }
}
