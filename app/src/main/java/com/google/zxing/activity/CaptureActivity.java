package com.google.zxing.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.camera.CameraManager;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.decoding.CaptureActivityHandler;
import com.google.zxing.decoding.InactivityTimer;
import com.google.zxing.decoding.RGBLuminanceSource;
import com.google.zxing.view.ViewfinderView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.wcy.express.R;
import me.wcy.express.utils.Extras;
import me.wcy.express.utils.SnackbarUtils;

/**
 * Initial the camera
 *
 * @author Ryan.Tang
 */
@SuppressWarnings("deprecation")
public class CaptureActivity extends AppCompatActivity implements Callback, OnClickListener {
    private static final int REQUEST_ALBUM = 0;
    private CaptureActivityHandler handler;
    private ViewfinderView viewfinderView;
    private boolean hasSurface;
    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;
    private InactivityTimer inactivityTimer;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private static final float BEEP_VOLUME = 0.10f;
    private boolean vibrate;
    @Bind(R.id.iv_back)
    ImageView ivBack;
    @Bind(R.id.iv_flashlight)
    ImageView ivFlashlight;
    @Bind(R.id.iv_album)
    ImageView ivAlbum;
    public static boolean onlyOneD;

    public static void start(Activity activity, boolean onlyOneD, int requestCode) {
        Intent intent = new Intent(activity, CaptureActivity.class);
        intent.putExtra(Extras.ONLY_ONE_D, onlyOneD);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);

        onlyOneD = getIntent().getBooleanExtra(Extras.ONLY_ONE_D, false);

        CameraManager.init(getApplicationContext());
        viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);

        ButterKnife.bind(this);
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
        decodeFormats = null;
        characterSet = "UTF-8";

        playBeep = true;
        AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        vibrate = false;
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

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        CameraManager.get().release();
        super.onDestroy();
    }

    /**
     * Handler scan result
     */
    public void handleDecode(Result result, Bitmap barcode) {
        inactivityTimer.onActivity();
        playBeepSoundAndVibrate();
        // FIXME
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
        } catch (IOException | RuntimeException ioe) {
            return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(this, decodeFormats, characterSet);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;
    }

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();
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

    private static final long VIBRATE_DURATION = 200L;

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
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
                ivFlashlight.setSelected(CameraManager.get().flashlight());
                break;
            case R.id.iv_album:
                openAlbum();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_ALBUM) {
            Uri uri = data.getData();
            parseBitmap(uri);
        }
    }

    private void openAlbum() {
        Intent innerIntent = new Intent();
        innerIntent.setAction(Intent.ACTION_PICK);
        innerIntent.setType("image/*");
        startActivityForResult(innerIntent, REQUEST_ALBUM);
    }

    private void parseBitmap(Uri uri) {
        Bitmap bitmap = null;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true; // 仅获取大小
            bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri), null, options);
            int inSampleSize = options.outHeight / 200;// 压缩尺寸,节约时间
            if (inSampleSize <= 0) {
                inSampleSize = 1;
            }
            options.inSampleSize = inSampleSize;
            options.inJustDecodeBounds = false; // 获取bitmap
            bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri), null, options);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Hashtable<DecodeHintType, String> hints = new Hashtable<>();
        hints.put(DecodeHintType.CHARACTER_SET, characterSet);
        RGBLuminanceSource source = new RGBLuminanceSource(bitmap);
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));
        MultiFormatReader reader = new MultiFormatReader();
        Result result = null;
        try {
            result = reader.decode(binaryBitmap, hints);
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
        if (result == null || TextUtils.isEmpty(result.getText())) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.tips)
                    .setMessage(R.string.analyze_fail)
                    .setPositiveButton(R.string.sure, null)
                    .show();
        } else {
            handleScanResult(result.getText());
        }
    }

    private void handleScanResult(final String result) {
        if (onlyOneD) {
            Intent intent = new Intent();
            intent.putExtra(Extras.SCAN_RESULT, result);
            setResult(RESULT_OK, intent);
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
                                // 连续扫描
                                handler.sendEmptyMessage(R.id.restart_preview);
                            }
                        }
                    })
                    .show();
        }
    }
}