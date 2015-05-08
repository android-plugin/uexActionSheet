package org.zywx.wbpalmstar.plugin.uexActionSheet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.zywx.wbpalmstar.base.BUtility;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextUtils;

public class ImageColorUtils {

	public static int parseColor(String colorStr) {
		if (TextUtils.isEmpty(colorStr))
			return 0;
		colorStr.trim();
		if ("rgb".equals(colorStr.toLowerCase().substring(0, 3))) {
			if ("rgba".equals(colorStr.toLowerCase().subSequence(0, 4))) {
				String colorTemp = colorStr.substring(5, colorStr.length() - 1);
				String colorArray[] = colorTemp.split(",");
				return Color.argb(Integer.parseInt(colorArray[3]),
						Integer.parseInt(colorArray[0]),
						Integer.parseInt(colorArray[1]),
						Integer.parseInt(colorArray[2]));
			} else {
				String colorTemp = colorStr.substring(4, colorStr.length() - 1);
				String colorArray[] = colorTemp.split(",");
				return Color.rgb(Integer.parseInt(colorArray[0]),
						Integer.parseInt(colorArray[1]),
						Integer.parseInt(colorArray[2]));
			}
		} else if (colorStr.charAt(0) == '#' && colorStr.length() == 4) {
			String colorTemp = colorStr.substring(1);
			StringBuffer colorSb = new StringBuffer();
			colorSb.append("#");
			colorSb.append(colorTemp.charAt(0));
			colorSb.append(colorTemp.charAt(0));
			colorSb.append(colorTemp.charAt(1));
			colorSb.append(colorTemp.charAt(1));
			colorSb.append(colorTemp.charAt(2));
			colorSb.append(colorTemp.charAt(2));
			return Color.parseColor(colorSb.toString());
		} else if (colorStr.charAt(0) == '#' && colorStr.length() == 9) {
			return Color.argb(Integer.parseInt(colorStr.substring(1, 3), 16),
					Integer.parseInt(colorStr.substring(3, 5), 16),
					Integer.parseInt(colorStr.substring(5, 7), 16),
					Integer.parseInt(colorStr.substring(7, 9), 16));
		} else {
			return Color.parseColor(colorStr);
		}
	}

	public static int[] parseArgb(String colorStr) {
		if (TextUtils.isEmpty(colorStr))
			return new int[] { 0, 0, 0 };
		colorStr.trim();
		if ("rgb".equals(colorStr.toLowerCase().substring(0, 3))) {
			if ("rgba".equals(colorStr.toLowerCase().subSequence(0, 4))) {
				String colorTemp = colorStr.substring(5, colorStr.length() - 1);
				String colorArray[] = colorTemp.split(",");
				return new int[] { Integer.parseInt(colorArray[0]),
						Integer.parseInt(colorArray[1]),
						Integer.parseInt(colorArray[2]),
						Integer.parseInt(colorArray[3])};
			} else {
				String colorTemp = colorStr.substring(4, colorStr.length() - 1);
				String colorArray[] = colorTemp.split(",");
				return new int[] { Integer.parseInt(colorArray[0]),
						Integer.parseInt(colorArray[1]),
						Integer.parseInt(colorArray[2]),
						Integer.parseInt(colorArray[3])};
			}
		} else if (colorStr.charAt(0) == '#' && colorStr.length() == 9) {
			return new int[] { Integer.parseInt(colorStr.substring(3, 5), 16),
					Integer.parseInt(colorStr.substring(5, 7), 16),
					Integer.parseInt(colorStr.substring(7, 9), 16),0};
		} else {
			return new int[] {0,0,0,0};
		}
	}

	public static Bitmap getImage(Context ctx, String imgUrl) {
		if (imgUrl == null || imgUrl.length() == 0) {
			return null;
		}

		Bitmap bitmap = null;
		InputStream is = null;
		try {
			if (imgUrl.startsWith(BUtility.F_Widget_RES_SCHEMA)) {
				is = BUtility.getInputStreamByResPath(ctx, imgUrl);
				bitmap = BitmapFactory.decodeStream(is);
			} else if (imgUrl.startsWith(BUtility.F_FILE_SCHEMA)) {
				imgUrl = imgUrl.replace(BUtility.F_FILE_SCHEMA, "");
				bitmap = BitmapFactory.decodeFile(imgUrl);
			} else if (imgUrl.startsWith(BUtility.F_Widget_RES_path)) {
				try {
					is = ctx.getAssets().open(imgUrl);
					if (is != null) {
						bitmap = BitmapFactory.decodeStream(is);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else if (imgUrl.startsWith("/")) {
				bitmap = BitmapFactory.decodeFile(imgUrl);
			} else if (imgUrl.startsWith("http://")) {
				bitmap = downloadNetworkBitmap(imgUrl);
			}
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return bitmap;
	}

	private static Bitmap downloadNetworkBitmap(String url) {
		byte[] data = downloadImageFromNetwork(url);
		if (data == null || data.length == 0) {
			return null;
		}
		return BitmapFactory.decodeByteArray(data, 0, data.length);
	}

	private static byte[] downloadImageFromNetwork(String url) {
		InputStream is = null;
		byte[] data = null;
		try {
			HttpGet httpGet = new HttpGet(url);
			BasicHttpParams httpParams = new BasicHttpParams();
			HttpResponse httpResponse = new DefaultHttpClient(httpParams)
					.execute(httpGet);
			int responseCode = httpResponse.getStatusLine().getStatusCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				is = httpResponse.getEntity().getContent();
				data = transStreamToBytes(is, 4096);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}

	private static byte[] transStreamToBytes(InputStream is, int buffSize) {
		if (is == null) {
			return null;
		}
		if (buffSize <= 0) {
			throw new IllegalArgumentException(
					"buffSize can not less than zero.....");
		}
		byte[] data = null;
		byte[] buffer = new byte[buffSize];
		int actualSize = 0;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			while ((actualSize = is.read(buffer)) != -1) {
				baos.write(buffer, 0, actualSize);
			}
			data = baos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				baos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return data;
	}

	/**
	 * bitmap圆角
	 * 
	 * @param mBitmap
	 * @return
	 */
	public static Bitmap getRoundedBitmap(Bitmap mBitmap) {
		if (mBitmap == null)
			return null;
		// 创建新的位图
		Bitmap bgBitmap = Bitmap.createBitmap(mBitmap.getWidth(),
				mBitmap.getHeight(), Config.ARGB_8888);
		// 把创建的位图作为画板
		Canvas mCanvas = new Canvas(bgBitmap);

		Paint mPaint = new Paint();
		Rect mRect = new Rect(0, 0, mBitmap.getWidth(), mBitmap.getHeight());
		RectF mRectF = new RectF(mRect);
		// 设置圆角半径为20
		float roundPx = 10;
		mPaint.setAntiAlias(true);
		// 先绘制圆角矩形
		mCanvas.drawRoundRect(mRectF, roundPx, roundPx, mPaint);

		// 设置图像的叠加模式
		mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		// 绘制图像
		mCanvas.drawBitmap(mBitmap, mRect, mRect, mPaint);
		return bgBitmap;
	}

	/**
	 * 设置listItem的背景
	 * 
	 * @param nomal
	 * @param focus
	 * @return
	 */
	public static StateListDrawable bgColorDrawableSelector(Bitmap nomal,
			Bitmap focus) {

		BitmapDrawable nomalBitmap = new BitmapDrawable(nomal);
		BitmapDrawable focusBitmap = new BitmapDrawable(focus);
		StateListDrawable selector = new StateListDrawable();
		selector.addState(new int[] { android.R.attr.state_pressed },
				focusBitmap);
		selector.addState(new int[] { android.R.attr.state_selected },
				focusBitmap);
		selector.addState(new int[] { android.R.attr.state_focused },
				focusBitmap);
		selector.addState(new int[] {}, nomalBitmap);
		return selector;
	}

	public static Bitmap setAlpha(Bitmap sourceImg, int number) {
		 int[] argb = new int[sourceImg.getWidth() * sourceImg.getHeight()];
		 sourceImg.getPixels(argb, 0, sourceImg.getWidth(), 0, 0, sourceImg
		 .getWidth(), sourceImg.getHeight());// 获得图片的ARGB值
		 number = number * 255 / 100;
		 for (int i = 0; i < argb.length; i++) {
			 argb[i] = (number << 24) | (argb[i] & 0x00FFFFFF);
		 }
		 sourceImg = Bitmap.createBitmap(argb, sourceImg.getWidth(), sourceImg
		 .getHeight(), Config.ARGB_8888);
		
		 return sourceImg;
	}

	/**
	 * 更改bitmap颜色值
	 * 
	 * @param final_bitmap
	 * @param color
	 * @return
	 */
	public static Bitmap effect_Bitmap(Bitmap from_bitmap, String color) {
		if(from_bitmap == null)
			return null;
		Bitmap final_bitmap = scaleBitmapByMaxSize(from_bitmap,70);
		int[] rgbs = ImageColorUtils.parseArgb(color);
		int height = final_bitmap.getHeight();
		int width = final_bitmap.getWidth();
		int[] pix = new int[width * height];
		final_bitmap.getPixels(pix, 0, width, 0, 0, width, height);
		int R, G, B;
		for (int y = 0; y < height; y++)
			for (int x = 0; x < width; x++) {
				int index = y * width + x;
				int r = (pix[index] >> rgbs[0]) & 0xff;
				int g = (pix[index] >> rgbs[1]) & 0xff;
				int b = (pix[index] >> rgbs[2]) & 0xff;
				int s = (r + g + b) / 3;
				R = s;
				R = (R < 0) ? 0 : ((R > 255) ? 255 : R);
				G = s;
				G = (G < 0) ? 0 : ((G > 255) ? 255 : G);
				B = s;
				B = (B < 0) ? 0 : ((B > 255) ? 255 : B);
				pix[index] = 0xff000000 | (R << 16) | (G << 8) | B;
			}
		Bitmap temp = Bitmap.createBitmap(width, height,
				Bitmap.Config.ARGB_8888);
		temp.setPixels(pix, 0, width, 0, 0, width, height);
		pix = null;
		Bitmap alphaBitmap = setAlpha(temp,rgbs[3]);
		return alphaBitmap;
	}
	
	/**
	 * 根据允许的最大尺寸缩放图片
	 * 
	 * @param bitmap
	 * @param maxSize
	 *            图片所允许的最大尺寸
	 * @return
	 */
	
	public static Bitmap scaleBitmapByMaxSize(Bitmap bitmap, int maxSize) {
		if (bitmap == null) {
			return null;
		}int srcWidth = bitmap.getWidth();
		int srcHeight = bitmap.getHeight();
		if (Math.max(srcWidth, srcHeight) <= maxSize) {
			// 图片实际最大尺寸小于允许的最大尺寸是不用裁剪，直接返回。
			return bitmap;
		}
		float scaleRate = ((float) maxSize) / Math.max(srcWidth, srcHeight);
		int targetWidth = (int) (scaleRate * srcWidth);
		int targetHeight = (int) (scaleRate * srcHeight);
		return Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, false);
	}

    /** 对TextView设置不同状态时其文字颜色。 */
    public static ColorStateList createColorStateList(int normal, int pressed, int focused, int unable) {
        int[] colors = new int[] { pressed, focused, normal, focused, unable, normal };
        int[][] states = new int[6][];
        states[0] = new int[] { android.R.attr.state_pressed, android.R.attr.state_enabled };
        states[1] = new int[] { android.R.attr.state_enabled, android.R.attr.state_focused };
        states[2] = new int[] { android.R.attr.state_enabled };
        states[3] = new int[] { android.R.attr.state_focused };
        states[4] = new int[] { android.R.attr.state_window_focused };
        states[5] = new int[] {};
        ColorStateList colorList = new ColorStateList(states, colors);
        return colorList;
    }
}
