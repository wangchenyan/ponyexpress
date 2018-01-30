package me.wcy.express.activity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.zxing.encoding.EncodeHandler;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import me.wcy.express.R;
import me.wcy.express.utils.PermissionReq;
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

    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code);

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
        showProgress();
        bitmap = null;
        String text = etText.getText().toString();
        EncodeHandler.createQRCode(text, 500, bitmap -> {
            cancelProgress();
            this.bitmap = bitmap;
            ivQRCode.setImageBitmap(this.bitmap);
            ivQRCode.setVisibility((this.bitmap == null) ? View.GONE : View.VISIBLE);
        });
    }

    private void saveDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.tips)
                .setMessage(R.string.qrcode_save_tips)
                .setPositiveButton(R.string.save, (dialog, which) -> check()
                ).
                setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void check() {
        if (!Utils.hasSDCard()) {
            SnackbarUtils.show(this, R.string.qrcode_no_sdcard);
            return;
        }

        PermissionReq.with(this)
                .permissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .result(new PermissionReq.Result() {
                    @Override
                    public void onGranted() {
                        save();
                    }

                    @Override
                    public void onDenied() {
                        SnackbarUtils.show(QRCodeActivity.this, "没有读写存储权限，无法保存二维码图片！");
                    }
                })
                .request();
    }

    private void save() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        String fileName = getString(R.string.qrcode_file_name, sdf.format(new Date()));
        File file = new File(Utils.getPictureDir() + fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
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
