package me.wcy.express.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.WriterException;
import com.google.zxing.encoding.EncodingHandler;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.Bind;
import me.wcy.express.R;
import me.wcy.express.util.Utils;

@SuppressLint("SimpleDateFormat")
public class QRCodeActivity extends BaseActivity implements OnClickListener,
        OnLongClickListener, TextWatcher {
    @Bind(R.id.string)
    EditText string;
    @Bind(R.id.create)
    Button create;
    @Bind(R.id.qrcode)
    ImageView qrCode;

    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qrcode);

        string.addTextChangedListener(this);
        create.setOnClickListener(this);
        qrCode.setOnLongClickListener(this);
        qrCode.setVisibility(View.GONE);
    }

    private void createQRCode() {
        try {
            String contentString = string.getText().toString();
            bitmap = EncodingHandler.createQRCode(contentString, 500);
            qrCode.setImageBitmap(bitmap);
            qrCode.setVisibility(View.VISIBLE);
            Toast.makeText(this, R.string.qrcode_create_success, Toast.LENGTH_SHORT).show();
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    private void saveQRCode() {
        String state = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(state)) {
            // SD卡不可用
            Toast.makeText(this, R.string.qrcode_no_sdcard, Toast.LENGTH_SHORT).show();
            return;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String fileName = "QRCode" + sdf.format(new Date(System.currentTimeMillis())) + ".png";
        File file = new File(Utils.getPictureDir() + fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, R.string.qrcode_save_failure, Toast.LENGTH_SHORT).show();
            return;
        }
        // 刷新相册
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(file);
        intent.setData(uri);
        sendBroadcast(intent);

        String dir = getResources().getString(R.string.qrcode_save_success);
        Toast.makeText(this, dir + fileName, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.create:
                createQRCode();
                break;
        }
    }

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.qrcode:
                saveQRCode();
                break;
        }
        return false;
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (string.length() > 0) {
            create.setEnabled(true);
        } else {
            create.setEnabled(false);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

}
