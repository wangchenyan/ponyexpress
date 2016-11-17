package me.wcy.express.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.zxing.WriterException;
import com.google.zxing.encoding.EncodingHandler;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import me.wcy.express.R;
import me.wcy.express.utils.SnackbarUtils;
import me.wcy.express.utils.Utils;
import me.wcy.express.utils.binding.Bind;

public class QRCodeActivity extends BaseActivity implements OnClickListener, TextWatcher {
    @Bind(R.id.et_text)
    private EditText etText;
    @Bind(R.id.btn_create)
    private Button btnCreate;
    @Bind(R.id.iv_qr_code)
    private ImageView ivQRCode;
    private Bitmap mBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code);

        ivQRCode.setVisibility(View.GONE);
    }

    @Override
    protected void setListener() {
        etText.addTextChangedListener(this);
        btnCreate.setOnClickListener(this);
        ivQRCode.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_create:
                createQRCode();
                break;
            case R.id.iv_qr_code:
                saveDialog();
                break;
        }
    }

    private void createQRCode() {
        try {
            String contentString = etText.getText().toString();
            mBitmap = EncodingHandler.createQRCode(contentString, 500);
            ivQRCode.setImageBitmap(mBitmap);
            ivQRCode.setVisibility(View.VISIBLE);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    private void saveDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.tips)
                .setMessage(R.string.qrcode_save_tips)
                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                saveQRCode();
                            }
                        }
                ).
                setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void saveQRCode() {
        String state = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(state)) {
            // SD卡不可用
            SnackbarUtils.show(this, R.string.qrcode_no_sdcard);
            return;
        }
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String fileName = getString(R.string.qrcode_file_name, sdf.format(new Date(System.currentTimeMillis())));
        File file = new File(Utils.getPictureDir() + fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
            SnackbarUtils.show(this, R.string.qrcode_save_failure);
            return;
        }
        // 刷新相册
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(file);
        intent.setData(uri);
        sendBroadcast(intent);

        SnackbarUtils.show(this, getString(R.string.qrcode_save_success, fileName));
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (etText.length() > 0) {
            btnCreate.setEnabled(true);
        } else {
            btnCreate.setEnabled(false);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }
}
