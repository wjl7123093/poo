package com.mypolice.poo.util.filecache;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

public class BitMapUtils {
	// 缩放/裁剪图片
	public static Bitmap zoomImg(Bitmap bm, int newWidth, int newHeight) {
		// 获得图片的宽高
		int width = bm.getWidth();
		int height = bm.getHeight();
		// 计算缩放比例
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		// 取得想要缩放的matrix参数
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		// 得到新的图片
		Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix,
				true);
		return newbm;
	}

	// 通过文件路径获取到bitmap
	public static Bitmap getBitmapFromPath(String path, int requiredSize) {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		// 设置为ture只获取图片大小
		opts.inJustDecodeBounds = true;
		// 返回为空
		BitmapFactory.decodeFile(path, opts);
		int width_tmp = opts.outWidth;
		int height_tmp = opts.outHeight;

		int scale = 1;
		while (true) {
			if (width_tmp / 2 < requiredSize || height_tmp / 2 < requiredSize)
				break;
			width_tmp /= 2;
			height_tmp /= 2;
			scale *= 2;
		}

		opts.inJustDecodeBounds = false;
		opts.inSampleSize = scale;
		return BitmapFactory.decodeFile(path, opts);
	}

	// 通过resources获取到bitmap
	public static Bitmap getBitmapFromRes(Resources resources, int resID,
			int requiredSize) {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		// 设置为ture只获取图片大小
		opts.inJustDecodeBounds = true;
		// 返回为空
		BitmapFactory.decodeResource(resources, resID, opts);
		int width_tmp = opts.outWidth;
		int height_tmp = opts.outHeight;

		int scale = 1;
		while (true) {
			if (width_tmp / 2 < requiredSize || height_tmp / 2 < requiredSize)
				break;
			width_tmp /= 2;
			height_tmp /= 2;
			scale *= 2;
		}

		opts.inJustDecodeBounds = false;
		opts.inSampleSize = scale;
		return BitmapFactory.decodeResource(resources, resID, opts);
	}

	/**
	 * 裁剪图片大小，减少内存消耗
	 * 
	 * @param is
	 *            输入流
	 * @param requiredSize
	 *            图片缩放尺寸
	 * @return
	 */
	public static Bitmap decodeFile(InputStream is, int requiredSize) {

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		// 保存数据数组
		byte[] data;

		byte[] buffer = new byte[1024];
		int len = 0;
		try {
			while ((len = is.read(buffer, 0, 1024)) != -1) {
				bos.write(buffer, 0, len);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// 获取数据
		data = bos.toByteArray();
		try {
			// 关闭数据流
			bos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 获取图片真实尺寸
		BitmapFactory.Options o = new BitmapFactory.Options();
		o.inJustDecodeBounds = true;
		BitmapFactory.decodeByteArray(data, 0, data.length, o);

		// 计算缩放比例
		int width_tmp = o.outWidth, height_tmp = o.outHeight;
		int scale = 1;
		while (true) {
			if (width_tmp / 2 < requiredSize || height_tmp / 2 < requiredSize)
				break;
			width_tmp /= 2;
			height_tmp /= 2;
			scale *= 2;
		}

		// 返回缩放后的图片
		o.inSampleSize = scale;
		o.inJustDecodeBounds = false;
		return BitmapFactory.decodeByteArray(data, 0, data.length, o);
	}

	public static Bitmap toRoundCorner(Bitmap bitmap, float pixels) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = pixels;

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);

		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		System.out.println("pixels+++++++" + pixels);

		return output;
	}

	public static Bitmap getNetBitmap(String imageUri) {
		// Log.d(TAG, "getbitmap:" + imageUri);
		// 显示网络上的图片
		Bitmap bitmap = null;
		try {
			URL myFileUrl = new URL(imageUri);
			HttpURLConnection conn = (HttpURLConnection) myFileUrl
					.openConnection();
			conn.setDoInput(true);
			conn.connect();
			InputStream is = conn.getInputStream();
			bitmap = BitmapFactory.decodeStream(is);
			is.close();
			// Log.v(TAG, "image download finished." + imageUri);
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
			bitmap = null;
		} catch (IOException e) {
			e.printStackTrace();
			// Log.v(TAG, "getbitmap bmp fail---");
			bitmap = null;
		}
		return bitmap;
	}
}