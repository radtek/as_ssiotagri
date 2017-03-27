//package com.xiaomi.smarthome.library.common.widget;
package com.ssiot.remote.yun.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.TextView;

public class PieProgressBar extends ImageView {
	private final int a = 1;
	private Paint paint = new Paint(1);
	private PorterDuffXfermode xfermode = new PorterDuffXfermode(
			PorterDuff.Mode.SRC_IN);
	private RectF rectF;
	private Bitmap bitmap;
	private float percent = 0.0F;
	private PieProgressBarAnim mBarAnim;
	private PieProgressTxtAnim mTxtAnim;
	private TextView percentTextView = null;
	private boolean sweepangleDirection = true;

	public PieProgressBar(Context paramContext, AttributeSet paramAttributeSet) {
		super(paramContext, paramAttributeSet);
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(-65536);
		paint.setXfermode(xfermode);
		rectF = new RectF();
		rectF.left = 0.0F;
		rectF.top = 0.0F;
		bitmap = ((BitmapDrawable) getDrawable()).getBitmap();
		mBarAnim = new PieProgressBarAnim();
		mBarAnim.setInterpolator(new LinearInterpolator());
		mTxtAnim = new PieProgressTxtAnim();
	}

	public void startAnim() {//a()
		startAnimation(mBarAnim);
	}

	public void cancelAnim() {//b()
		mBarAnim.cancel();
	}

	public float getPercent() {
		return percent;
	}

	public float getPercentAnim() {
//		return PieProgressBarAnim.a(mBarAnim);//mBarAnim jingbo modifythis
		return mBarAnim.a();
	}

	@Override
	protected void onDraw(Canvas paramCanvas) {
		if (isInEditMode()) {
			super.onDraw(paramCanvas);
			return;
		}
		int k = paramCanvas.saveLayer(0.0F, 0.0F, 0 + getWidth(),
				0 + getHeight(), null, 31);
		rectF.right = getWidth();
		rectF.bottom = getHeight();
		paint.setXfermode(null);
		if (sweepangleDirection){
			paramCanvas.drawArc(rectF, -90.0F, 360.0F * percent / 100.0F, true,
					paint);
		} else {
			paramCanvas.drawArc(rectF, -90.0F, -360.0F * percent / 100.0F,
					true, paint);
		}
		paint.setXfermode(xfermode);
		paramCanvas.drawBitmap(bitmap, new Rect(0, 0, bitmap.getWidth(),
				bitmap.getHeight()), new RectF(0.0F, 0.0F, getWidth(),
				getHeight()), paint);
		paramCanvas.restoreToCount(k);
	}

	public void setDuration(long paramLong) {
		mBarAnim.setDuration(paramLong);
		mTxtAnim.setDuration(1L * paramLong / 2L);
	}

	public void setFromPercent(int paramInt) {
		mBarAnim.setFromInt(paramInt);
		mTxtAnim.setFromInt(paramInt);
	}

	public void setInterpolator(Interpolator paramInterpolator) {
		mBarAnim.setInterpolator(paramInterpolator);
	}

	public void setOri(boolean paramBoolean) {
		sweepangleDirection = paramBoolean;
	}

	public void setPercent(float paramFloat) {
		if (paramFloat < 0.0F){
			paramFloat = 0.0F;
		}
		if (paramFloat > 100.0F){
			paramFloat = 100.0F;
		}
		percent = paramFloat;
		if (percentTextView != null){
			percentTextView.setText((int) percent + "%");
		}
		invalidate();
	}

	public void setPercentView(TextView paramTextView) {
		if (paramTextView != null){
			percentTextView = paramTextView;
			percentTextView.setText((int) percent + "%");
		}
	}
	
	private TextView getMyTextView(View holderview){
		return percentTextView;
	}

	public void setToPercent(int paramInt) {
		mBarAnim.setEndInt(paramInt);
		mTxtAnim.setEndInt(paramInt);
	}

	public class PieProgressBarAnim extends Animation {
		private int startInt = 0;
		private int endInt = 0;
		private int currentInt = 0;

		public PieProgressBarAnim() {
		}
		
		private float a(){//jingbo add
			return currentInt;
		}

		public void setFromInt(int paramInt) {
			if (paramInt < 1)
				paramInt = 1;
			if (paramInt > 100)
				paramInt = 100;
			startInt = paramInt;
			currentInt = paramInt;
			PieProgressBar.this.setPercent(paramInt);
		}

		//第一个参数为动画的进度时间值，取值范围为[0.0f,1.0f]，第二个参数Transformation记录着动画某一帧中变形的原始数据。该方法在动画的每一帧显示过程中都会被调用
		@Override
		protected void applyTransformation(float interpolatedTime,
				Transformation paramTransformation) {
			super.applyTransformation(interpolatedTime, paramTransformation);
			int i = (int) (startInt + interpolatedTime * (endInt - startInt));
			if (currentInt != i) {
				currentInt = i;
				PieProgressBar.this.setPercent(i);
			}
		}

		public void setEndInt(int paramInt) {
			if (paramInt < 1){
				paramInt = 1;
			}
			if (paramInt > 100){
				paramInt = 100;
			}
			endInt = paramInt;
		}
	}

	public class PieProgressTxtAnim extends Animation {
		private int mstartInt = 0;
		private int mendInt = 0;
		private int mcurrentInt = 0;

		public PieProgressTxtAnim() {
		}

		public void setFromInt(int paramInt) {
			if (paramInt < 1){
				paramInt = 1;
			}
			if (paramInt > 100){
				paramInt = 100;
			}
			mstartInt = paramInt;
			mcurrentInt = paramInt;
		}

		protected void applyTransformation(float interpolatedTime,
				Transformation paramTransformation) {
			super.applyTransformation(interpolatedTime, paramTransformation);
			int i = (int) (mstartInt + interpolatedTime * (mendInt - mstartInt));
			if (mcurrentInt != i) {
				mcurrentInt = i;
				if (getMyTextView(PieProgressBar.this) != null)
					getMyTextView(PieProgressBar.this).setText(i + "%");
//				Log.e("PieProgressBar", "" + mcurrentInt);
			}
		}

		public void setEndInt(int paramInt) {
			if (paramInt < 1){
				paramInt = 1;
			}
			if (paramInt > 100){
				paramInt = 100;
			}
			mendInt = paramInt;
		}
	}
}