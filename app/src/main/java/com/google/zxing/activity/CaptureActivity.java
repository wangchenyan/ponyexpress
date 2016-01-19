package com.google.zxing.activity;

import android.app.Activity;
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
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
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

import me.wcy.express.R;
import me.wcy.express.widget.MyAlertDialog;

/**
 * Initial the camera
 *
 * @author Ryan.Tang
 */
@SuppressWarnings("deprecation")
public class CaptureActivity extends Activity implements Callback, OnClickListener {
    public static final String SCAN_RESULT = "scan_result";
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);

        CameraManager.init(getApplication());
        viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);

        ImageView back = (ImageView) findViewById(R.id.iv_back);
        ImageView flashlight = (ImageView) findViewById(R.id.iv_flashlight);
        ImageView album = (ImageView) findViewById(R.id.iv_album);
        back.setOnClickListener(this);
        flashlight.setOnClickListener(this);
        album.setOnClickListener(this);
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
        characterSet = "ISO-8859-1";

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
        super.onDestroy();
    }

    /**
     * Handler scan result
     *
     * @param result
     * @param barcode
     */
    public void handleDecode(Result result, Bitmap barcode) {
        inactivityTimer.onActivity();
        playBeepSoundAndVibrate();
        String resultString = result.getText();
        // FIXME
        if (TextUtils.isEmpty(resultString)) {
            Toast.makeText(this, "Scan failed!", Toast.LENGTH_SHORT).show();
        } else {
            Intent resultIntent = new Intent();
            resultIntent.putExtra(SCAN_RESULT, resultString);
            setResult(RESULT_OK, resultIntent);
        }
        finish();
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
                CameraManager.get().flashlight();
                break;
            case R.id.iv_album:
                album();
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
            analyzeImage(uri);
        }
    }

    private void album() {
        Intent innerIntent = new Intent();
        innerIntent.setAction(Intent.ACTION_PICK);
        innerIntent.setType("image/*");
        startActivityForResult(innerIntent, REQUEST_ALBUM);
    }

    private void analyzeImage(Uri uri) {
        Bitmap bitmap = null;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true; // 仅获取大小
            bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri), null, options);
            //压缩尺寸,节约时间
            int inSampleSize = options.outHeight / 200;
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
            final MyAlertDialog dialog = new MyAlertDialog(this, true);
            dialog.show();
            dialog.setTitle(R.string.tips);
            dialog.setMessage(R.string.analyze_fail);
            dialog.setPositiveButton(R.string.sure, new OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.cancel();
                }
            });
        } else {
            String resultString = result.getText();
            Intent resultIntent = new Intent();
            resultIntent.putExtra(SCAN_RESULT, resultString);
            setResult(RESULT_OK, resultIntent);
            finish();
        }
    }
}