package com.google.zxing.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.Result;
import com.google.zxing.camera.CameraManager;
import com.google.zxing.decoding.CaptureActivityHandler;
import com.google.zxing.decoding.DecodeFile;
import com.google.zxing.decoding.DecodeFormatManager;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;

import me.wcy.express.R;
import me.wcy.express.constants.Extras;
import me.wcy.express.utils.SnackbarUtils;
import me.wcy.express.utils.binding.Bind;
import me.wcy.express.utils.binding.ViewBinder;

public class CaptureActivity extends AppCompatActivity implements Callback, OnClickListener {
    private static final boolean PLAY_BEEP = true;
    private static final boolean VIBRATE = false;
    private static final long VIBRATE_DURATION = 200L;
    private static final float BEEP_VOLUME = 0.1f;
    private static final int REQUEST_ALBUM = 0;

    private CaptureActivityHandler handler;
    private boolean hasSurface = false;
    private final Vector<BarcodeFormat> decodeFormats = new Vector<>();
    private final String characterSet = "UTF-8";
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private boolean isBarcode;

    private ProgressDialog progressDialog;

    @Bind(R.id.iv_back)
    private ImageView ivBack;
    @Bind(R.id.iv_flashlight)
    private ImageView ivFlashlight;
    @Bind(R.id.iv_album)
    private ImageView ivAlbum;

    public static void start(Activity activity, boolean onlyOneD, int requestCode) {
        Intent intent = new Intent(activity, CaptureActivity.class);
        intent.putExtra(Extras.BARCODE, onlyOneD);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);

        isBarcode = getIntent().getBooleanExtra(Extras.BARCODE, false);

        initDecodeFormats();
        CameraManager.init(getApplicationContext());
        CameraManager.get().setBarcode(isBarcode);

        progressDialog = new ProgressDialog(this);

        ViewBinder.bind(this);
        ivBack.setOnClickListener(this);
        ivFlashlight.setOnClickListener(this);
        ivAlbum.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }

        AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        playBeep = PLAY_BEEP && (audioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL);
        initBeepSound();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        CameraManager.get().closeDriver();
    }

    /**
     * Handler scan result
     */
    public void handleDecode(Result result) {
        playBeepSoundAndVibrate();

        if (TextUtils.isEmpty(result.getText())) {
            Toast.makeText(this, "扫描失败", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            handleScanResult(result.getText());
        }
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
        } catch (Exception e) {
            return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(this, decodeFormats, characterSet);
        }
    }

    private void initDecodeFormats() {
        decodeFormats.addAll(DecodeFormatManager.ONE_D_FORMATS);
        if (!isBarcode) {
            decodeFormats.addAll(DecodeFormatManager.QR_CODE_FORMATS);
            decodeFormats.addAll(DecodeFormatManager.DATA_MATRIX_FORMATS);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;
    }

    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            // The volume on STREAM_SYSTEM is not adjustable, and users found it too loud,
            // so we now play on the music stream.
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (VIBRATE) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final OnCompletionListener beepListener = new OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_flashlight:
                ivFlashlight.setSelected(CameraManager.get().setFlashlight());
                break;
            case R.id.iv_album:
                openAlbum();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null) {
            return;
        }

        if (requestCode == REQUEST_ALBUM) {
            Uri uri = data.getData();
            decodeFile(uri);
        }
    }

    private void openAlbum() {
        Intent innerIntent = new Intent();
        innerIntent.setAction(Intent.ACTION_PICK);
        innerIntent.setType("image/*");
        startActivityForResult(innerIntent, REQUEST_ALBUM);
    }

    private void decodeFile(Uri uri) {
        showProgress();
        Hashtable<DecodeHintType, Object> hints = new Hashtable<>();
        hints.put(DecodeHintType.POSSIBLE_FORMATS, decodeFormats);
        hints.put(DecodeHintType.CHARACTER_SET, characterSet);

        DecodeFile.decodeFile(getContentResolver(), uri, hints, new com.google.zxing.activity.Callback<Result>() {
            @Override
            public void onEvent(Result result) {
                cancelProgress();
                if (result != null && !TextUtils.isEmpty(result.getText())) {
                    handleScanResult(result.getText());
                } else {
                    new AlertDialog.Builder(CaptureActivity.this)
                            .setTitle(R.string.tips)
                            .setMessage(R.string.analyze_fail)
                            .setPositiveButton(R.string.sure, null)
                            .show();
                }
            }
        });
    }

    private void handleScanResult(final String result) {
        if (isBarcode) {
            Intent data = new Intent();
            data.putExtra(Extras.SCAN_RESULT, result);
            setResult(RESULT_OK, data);
            finish();
        } else {
            View view = LayoutInflater.from(this).inflate(R.layout.dialog_capture, null);
            TextView tvResult = (TextView) view.findViewById(R.id.tv_result);
            tvResult.setText(result);
            tvResult.setAutoLinkMask(Linkify.ALL);
            tvResult.setMovementMethod(LinkMovementMethod.getInstance());
            new AlertDialog.Builder(this)
                    .setTitle("扫描结果")
                    .setView(view)
                    .setPositiveButton("复制文本", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            ClipboardManager cmb = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                            cmb.setText(result);
                            SnackbarUtils.show(CaptureActivity.this, "复制成功");
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            if (handler != null) {
                                // 继续扫描
                                handler.restartPreviewAndDecode();
                            }
                        }
                    })
                    .show();
        }
    }

    private void showProgress() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setCancelable(false);
        }

        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    private void cancelProgress() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.cancel();
        }
    }
}