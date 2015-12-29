package com.drizzle.drizzledaily.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 图片操作类
 */
public class Imageutils {

	private Imageutils() {
		/* cannot be instantiated */
		throw new UnsupportedOperationException("cannot be instantiated");
	}

	/**
	 * 固定宽度压缩图片
	 */
	public static Bitmap resizeBitmap(Bitmap bitmap, int newWidth) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		float temp = ((float) height) / ((float) width);
		int newHeight = (int) ((newWidth) * temp);
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		Matrix matrix = new Matrix();
		// resize the bit map
		matrix.postScale(scaleWidth, scaleHeight);
		// matrix.postRotate(45);
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
		//bitmap.recycle();
		return resizedBitmap;
	}

	/**
	 * 从存储路径获取bitmap
	 */
	public static Bitmap getBitmapFromFile(String filePath) {
		Bitmap bitmap = BitmapFactory.decodeFile(filePath);
		return bitmap;
	}

	/**
	 * 保存图片到具体位置
	 */
	public void saveBitmap(Bitmap bitmap, String floderName, String saveName) {
		File f = new File(floderName, saveName + ".jpg");
		if (f.exists()) {
			f.delete();
		}
		try {
			FileOutputStream out = new FileOutputStream(f);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
