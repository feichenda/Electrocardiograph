package com.lenovo.feizai.electrocardiograph.customView;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class ShiBoQi extends SurfaceView implements SurfaceHolder.Callback {

	private static final int MAX_TOUCHPOINTS = 10;
	private Paint paint = new Paint();
	private Paint paint0 = new Paint();

	private Canvas gcc = null;
	private Canvas c = null;
	private SurfaceHolder holder = null;
	private Bitmap bitmap = null;
	private int width, height;
	private int maxX = 5000, maxY = 255, zxY = 50;
	private Boolean zt = false;
	private float qX = 0;

	private int qY = 0;
	private int ys=0xff00ff00;

	// private float scale = 1.0f;

	public ShiBoQi(Context context, AttributeSet attrs) {
		super(context, attrs);
		holder = getHolder();
		holder.addCallback(this);

		setFocusable(true); // 确保我们的View能获得输入焦点
		setFocusableInTouchMode(true); // 确保能接收到触屏事件
		init();
	}




	public void setmaxX(int vmaxX) {
		this.maxX = vmaxX;
	}

	public void setmaxY(int vmaxY) {
		this.maxY = vmaxY;
	}

	public void setzxY(int vzxY) {
		this.zxY = vzxY;
	}

	public void setYS(int vYS) {
		this.ys = vYS;
		paint.setColor(vYS);
	}

	public int getmaxX() {
		return this.maxX;
	}

	public int getmaxY() {
		return this.maxY;
	}

	public int getzxY() {
		return this.zxY;
	}

/*
	public void add0(int vY) {
		if (!zt && gcc!=null) {
			//ms = System.currentTimeMillis();
			//float ams = (ms - ms0) * width / maxX;// 5000为整个屏幕的毫秒数
			//ams=5;//不按时间，按固定间隔
			//ms0 = ms;
			int gcY = height - height * vY / maxY - height * zxY / 100;
			//gcc.drawLine(qX, qY, qX + 5, gcY, paint);

			//c = holder.lockCanvas();
			Rect dirty = new Rect(qX+1, qY<gcY?qY:gcY, qX + 5, qY<gcY?gcY:qY);
			//dirty.set(qX, qY<gcY?qY:gcY, qX + 5, qY<gcY?gcY:qY);
			c = holder.lockCanvas(dirty);
			c.drawLine(qX, qY, qX + 5, gcY, paint);
			//c.drawColor(Color.GREEN);
			//c.drawBitmap(bitmap, 0, 0, null);
			holder.unlockCanvasAndPost(c);
			c = null;
			qY = gcY;
			qX = qX + 5;
			if (qX > width) {
				qX = 0;
				//gcc.drawColor(Color.BLACK);
				c = holder.lockCanvas();
				c.drawColor(Color.BLACK);
				holder.unlockCanvasAndPost(c);
				c = null;
			}
		}
	}
	*/

	public void add(int vY) {
		if (!zt && gcc!=null) {
			float ax=17*width/5000;
			int gcY = height - height * vY / maxY - height * zxY / 100;
			gcc.drawLine(qX, qY, qX + ax, gcY, paint);
			gcc.drawRect(qX + ax+1, 0, qX + ax+width/10, height, paint0);
			qY = gcY;
			qX = qX + ax;
			if (qX > width) {
				qX = 0;
				gcc.drawRect(0, 0, width/10, height, paint0);
				//gcc.drawColor(Color.BLACK);
				//gcc.drawColor(Color.TRANSPARENT, Mode.CLEAR);

			}
			try {
				c = holder.lockCanvas();
				c.drawColor(Color.TRANSPARENT, Mode.CLEAR);
				c.drawBitmap(bitmap, 0, 0, null);
				holder.unlockCanvasAndPost(c);
				c = null;
			} catch (Exception e) {
				Log.e("str", e.toString());
			}
		}
	}

	public void hua(byte[] vY, int length) {
		if (!zt && gcc!=null) {
			int gcY;
			gcc.drawColor(Color.TRANSPARENT, Mode.CLEAR);
			for(int gci=0;gci<width && gci<length;gci++) {
				gcY=vY[gci] & 0xff;
				gcY=height - height * gcY / maxY - height * zxY / 100;
				gcc.drawPoint(gci, gcY, paint);
			}

			try {
				c = holder.lockCanvas();
				c.drawColor(Color.TRANSPARENT, Mode.CLEAR);
				c.drawBitmap(bitmap, 0, 0, null);
				holder.unlockCanvasAndPost(c);

				c = null;
			} catch (Exception e) {
				Log.e("str", e.toString());
			}
		}
	}

	private void init() {
		paint.setColor(ys);
		paint.setTextSize(80);
		paint.setAntiAlias(true); // 反锯齿
		paint0.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
		// paint.setStrokeWidth((float) 6.0); //线宽

	}

	/*
	 * 处理触屏事件
	 */

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// 获得屏幕触点数量
		int pointerCount = event.getPointerCount();
		if (pointerCount > MAX_TOUCHPOINTS) {
			pointerCount = MAX_TOUCHPOINTS;
		}

		if (event.getAction() == MotionEvent.ACTION_UP) {
			zt = !zt;

		} else {

		}

		return true;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		// TODO Auto-generated method stub
		this.width = width;
		this.height = height;
		paint.setStrokeWidth((float) width / 540); // 线宽
		paint.setTextSize((float) 20*width / 1080);
		bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		gcc = new Canvas(bitmap);
		if (gcc != null) {
			// 背景黑色
			//gcc.drawColor(Color.BLACK);
			gcc.drawColor(Color.TRANSPARENT, Mode.CLEAR);
			// gcc.drawText(gp_neq, 0, height / 2, textPaint);
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		// new Thread(new MyLoop()).start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
	}

	protected void onDestroy() {
		// TODO Auto-generated method stub
		gcc = null;
		c = null;
		bitmap = null;
	}


	public void add_R() {
		// TODO 自动生成的方法存根
		paint.setColor(0xffff0000);
		gcc.drawText("▼", qX-4*17*width/5000<0?width+qX-4*17*width/5000:qX-4*17*width/5000, 40, paint);
		paint.setColor(ys);
	}

}