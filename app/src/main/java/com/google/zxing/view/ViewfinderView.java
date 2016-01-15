/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.zxing.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.google.zxing.ResultPoint;
import com.google.zxing.camera.CameraManager;

import me.wcy.express.R;

/**
 * This view is overlaid on top of the camera preview. It adds the viewfinder
 * rectangle and partial transparency outside it, as well as the laser scanner
 * animation and result points.
 */
public final class ViewfinderView extends View {
    private static final int[] SCANNER_ALPHA = {0, 64, 128, 192, 255, 192, 128, 64};
    private static final long ANIMATION_DELAY = 100L;
    private final int mMaskColor;
    private final int mFrameColor;
    private final int mLaserColor;
    private int mScannerAlpha;
    private Paint mFinderMaskPaint;
    private Paint mBorderPaint;
    private Paint mLaserPaint;

    // This constructor is used when the class is built from an XML resource.
    public ViewfinderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // Initialize these once for performance rather than calling them every time in onDraw().
        mMaskColor = getResources().getColor(R.color.viewfinder_mask);
        mFrameColor = getResources().getColor(R.color.viewfinder_frame);
        mLaserColor = getResources().getColor(R.color.viewfinder_laser);
        mScannerAlpha = 0;
        mFinderMaskPaint = new Paint();
        mFinderMaskPaint.setColor(mMaskColor);
        mBorderPaint = new Paint();
        mBorderPaint.setColor(mFrameColor);
        mLaserPaint = new Paint();
        mLaserPaint.setColor(mLaserColor);
    }

    @Override
    public void onDraw(Canvas canvas) {
        Rect frame = CameraManager.get().getFramingRect();
        if (frame == null) {
            return;
        }

        drawViewFinderMask(canvas, frame);
        drawViewFinderBorder(canvas, frame);
        drawLaser(canvas, frame);

        // Request another update at the animation interval, but only repaint the laser line,not the entire viewfinder mask.
        postInvalidateDelayed(ANIMATION_DELAY, frame.left, frame.top, frame.right, frame.bottom);
    }

    public void drawViewfinder() {
        invalidate();
    }

    public void addPossibleResultPoint(ResultPoint point) {
        // do nothing
    }

    private void drawViewFinderMask(Canvas canvas, Rect frame) {
        canvas.drawRect(0, 0, canvas.getWidth(), frame.top, mFinderMaskPaint);
        canvas.drawRect(0, frame.top, frame.left, frame.bottom, mFinderMaskPaint);
        canvas.drawRect(frame.right, frame.top, canvas.getWidth(), frame.bottom, mFinderMaskPaint);
        canvas.drawRect(0, frame.bottom, canvas.getWidth(), canvas.getHeight(), mFinderMaskPaint);
    }

    private void drawViewFinderBorder(Canvas canvas, Rect frame) {
        int strokeWidth = dp2px(2);
        int borderLength = dp2px(16);
        int offset = strokeWidth / 2;
        mBorderPaint.setStrokeWidth(strokeWidth);
        canvas.drawLine((float) (frame.left + offset), (float) (frame.top), (float) (frame.left + offset), (float) (frame.top + borderLength), mBorderPaint);
        canvas.drawLine((float) (frame.left), (float) (frame.top + offset), (float) (frame.left + borderLength), (float) (frame.top + offset), mBorderPaint);
        canvas.drawLine((float) (frame.left + offset), (float) (frame.bottom), (float) (frame.left + offset), (float) (frame.bottom - borderLength), mBorderPaint);
        canvas.drawLine((float) (frame.left), (float) (frame.bottom - offset), (float) (frame.left + borderLength), (float) (frame.bottom - offset), mBorderPaint);
        canvas.drawLine((float) (frame.right - offset), (float) (frame.top), (float) (frame.right - offset), (float) (frame.top + borderLength), mBorderPaint);
        canvas.drawLine((float) (frame.right), (float) (frame.top + offset), (float) (frame.right - borderLength), (float) (frame.top + offset), mBorderPaint);
        canvas.drawLine((float) (frame.right - offset), (float) (frame.bottom), (float) (frame.right - offset), (float) (frame.bottom - borderLength), mBorderPaint);
        canvas.drawLine((float) (frame.right), (float) (frame.bottom - offset), (float) (frame.right - borderLength), (float) (frame.bottom - offset), mBorderPaint);
    }

    private void drawLaser(Canvas canvas, Rect frame) {
        mLaserPaint.setAlpha(SCANNER_ALPHA[mScannerAlpha]);
        mScannerAlpha = (mScannerAlpha + 1) % SCANNER_ALPHA.length;
        int middle = frame.height() / 2 + frame.top;
        int offsetX = dp2px(2);
        int offsetY = dp2px(0.5f);
        canvas.drawRect(frame.left + offsetX, middle - offsetY, frame.right - offsetX, middle + offsetY, mLaserPaint);
    }

    private int dp2px(float dpValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
