package me.wcy.util;

import me.wcy.express.R;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

/**
 * @author wcy
 * 
 */
public class MyProgressDialog extends ProgressDialog {
	private Context context;
	private TextView message;

	/**
	 * 自定义进度条的构造函数
	 * 
	 * @param context
	 *            上下文
	 */
	public MyProgressDialog(Context context) {
		super(context, R.style.AppTheme_Dialog);
		this.context = context;
	}

	/**
	 * 设置消息
	 * 
	 * @param message
	 *            消息
	 */
	@Override
	public void setMessage(CharSequence message) {
		super.setMessage(message);
		this.message.setText(message);
	}

	/**
	 * 设置消息
	 * 
	 * @param messageId
	 *            消息Id
	 */
	public void setMessage(int messageId) {
		String message = context.getResources().getString(messageId);
		setMessage(message);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setCanceledOnTouchOutside(false);
		setContentView(R.layout.progress_dialog);
		message = (TextView) findViewById(R.id.progress_dialog_message);
	}

}
