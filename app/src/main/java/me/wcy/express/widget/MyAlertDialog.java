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
public class MyAlertDialog extends AlertDialog {
    private Context context;
    private boolean singleButton;
    private TextView title;
    private TextView message;
    private Button leftButton;
    private Button rightButton;

    /**
     * 自定义对话框的构造函数,默认为两个按钮
     *
     * @param context 上下文
     */
    public MyAlertDialog(Context context) {
        this(context, false);
    }

    /**
     * 自定义对话框的构造函数
     *
     * @param context      上下文
     * @param singleButton 是否是单个按钮
     */
    public MyAlertDialog(Context context, boolean singleButton) {
        super(context, R.style.AppTheme_Dialog);
        this.context = context;
        this.singleButton = singleButton;
    }

    /**
     * 设置标题
     *
     * @param title 标题
     */
    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
        this.title.setText(title);
    }

    /**
     * 设置标题
     *
     * @param titleId 标题Id
     */
    @Override
    public void setTitle(int titleId) {
        super.setTitle(titleId);
        String title = context.getString(titleId);
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
        this.message.setText(message);
    }

    /**
     * 设置消息
     *
     * @param messageId 消息Id
     */
    public void setMessage(int messageId) {
        String message = context.getString(messageId);
        setMessage(message);
    }

    /**
     * 设置"确定"按钮
     *
     * @param text     文字
     * @param listener 监听器
     */
    public void setPositiveButton(CharSequence text, View.OnClickListener listener) {
        leftButton.setText(text);
        leftButton.setOnClickListener(listener);
    }

    /**
     * 设置"确定"按钮
     *
     * @param textId   文字Id
     * @param listener 监听器
     */
    public void setPositiveButton(int textId, View.OnClickListener listener) {
        String text = context.getString(textId);
        setPositiveButton(text, listener);
    }

    /**
     * 设置"取消"按钮
     *
     * @param text     文字
     * @param listener 监听器
     */
    public void setNegativeButton(CharSequence text, View.OnClickListener listener) {
        rightButton.setText(text);
        rightButton.setOnClickListener(listener);
    }

    /**
     * 设置"取消"按钮
     *
     * @param textId   文字Id
     * @param listener 监听器
     */
    public void setNegativeButton(int textId, View.OnClickListener listener) {
        String text = context.getString(textId);
        setNegativeButton(text, listener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCanceledOnTouchOutside(false);
        setContentView(R.layout.alert_dialog);
        title = (TextView) findViewById(R.id.alert_dialog_title);
        message = (TextView) findViewById(R.id.alert_dialog_message);
        leftButton = (Button) findViewById(R.id.alert_dialog_btn_left);
        rightButton = (Button) findViewById(R.id.alert_dialog_btn_right);
        title.getPaint().setFakeBoldText(true);
        if (singleButton) {
            leftButton.setBackgroundResource(R.drawable.ic_alert_dialog_btn_pressed_effect);
            rightButton.setVisibility(View.GONE);
        }
    }

}
