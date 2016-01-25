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
import android.graphics.drawable.Drawable;
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
    private static final long ANIMATION_DELAY = 25L;
    private static int BORDER_WIDTH;
    private static int BORDER_HEIGHT;
    private static int LASER_HEIGHT;
    private static int LASER_PADDING;
    private static int LASER_OFFSET_INCREASE;
    private Paint mFinderMaskPaint;
    private Paint mBorderPaint;
    private Paint mLaserPaint;
    private final int mMaskColor;
    private final int mFrameColor;
    private final int mLaserColor;
    private Drawable mLaserDrawable;
    private int mLaserOffset = 0;

    // This constructor is used when the class is built from an XML resource.
    public ViewfinderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // Initialize these once for performance rather than calling them every time in onDraw().
        BORDER_WIDTH = dp2px(16);
        BORDER_HEIGHT = dp2px(2);
        LASER_HEIGHT = dp2px(2);
        LASER_PADDING = dp2px(16);
        LASER_OFFSET_INCREASE = dp2px(1);

        mFinderMaskPaint = new Paint();
        mBorderPaint = new Paint();
        mLaserPaint = new Paint();
        mMaskColor = getResources().getColor(R.color.viewfinder_mask);
        mFrameColor = getResources().getColor(R.color.viewfinder_frame);
        mLaserColor = getResources().getColor(R.color.viewfinder_laser);
        mLaserDrawable = getResources().getDrawable(R.drawable.ic_scan_line);

        mFinderMaskPaint.setColor(mMaskColor);
        mBorderPaint.setColor(mFrameColor);
        mBorderPaint.setStrokeWidth(BORDER_HEIGHT);
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
        int halfStroke = BORDER_HEIGHT / 2;
        canvas.drawLine((float) (frame.left + halfStroke), (float) (frame.top), (float) (frame.left + halfStroke), (float) (frame.top + BORDER_WIDTH), mBorderPaint);
        canvas.drawLine((float) (frame.left), (float) (frame.top + halfStroke), (float) (frame.left + BORDER_WIDTH), (float) (frame.top + halfStroke), mBorderPaint);
        canvas.drawLine((float) (frame.left + halfStroke), (float) (frame.bottom), (float) (frame.left + halfStroke), (float) (frame.bottom - BORDER_WIDTH), mBorderPaint);
        canvas.drawLine((float) (frame.left), (float) (frame.bottom - halfStroke), (float) (frame.left + BORDER_WIDTH), (float) (frame.bottom - halfStroke), mBorderPaint);
        canvas.drawLine((float) (frame.right - halfStroke), (float) (frame.top), (float) (frame.right - halfStroke), (float) (frame.top + BORDER_WIDTH), mBorderPaint);
        canvas.drawLine((float) (frame.right), (float) (frame.top + halfStroke), (float) (frame.right - BORDER_WIDTH), (float) (frame.top + halfStroke), mBorderPaint);
        canvas.drawLine((float) (frame.right - halfStroke), (float) (frame.bottom), (float) (frame.right - halfStroke), (float) (frame.bottom - BORDER_WIDTH), mBorderPaint);
        canvas.drawLine((float) (frame.right), (float) (frame.bottom - halfStroke), (float) (frame.right - BORDER_WIDTH), (float) (frame.bottom - halfStroke), mBorderPaint);
    }

    private void drawLaser(Canvas canvas, Rect frame) {
        if (mLaserOffset + LASER_HEIGHT >= frame.height()) {
            mLaserOffset = 0;
        }
        mLaserDrawable.setBounds(frame.left + LASER_PADDING, frame.top + mLaserOffset, frame.right - LASER_PADDING, frame.top + mLaserOffset + LASER_HEIGHT);
        mLaserDrawable.draw(canvas);
        mLaserOffset += LASER_OFFSET_INCREASE;
    }

    private int dp2px(float dpValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
