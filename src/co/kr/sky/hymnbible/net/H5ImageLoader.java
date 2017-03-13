package co.kr.sky.hymnbible.net;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.LruCache;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Transformation;

public class H5ImageLoader {

	private static H5ImageLoader instance;

	private Context context;
	public ImageLoader loader;
	private RequestQueue queue;
	private BitmapLruCache cache;

	public static H5ImageLoader getInstance(Context context) {

		if (instance == null)
			instance = new H5ImageLoader(context);
		return instance;

	}

	private H5ImageLoader(Context context) {

		this.context = context;
		this.cache = new BitmapLruCache();

		queue = Volley.newRequestQueue(context);
		loader = new ImageLoader(queue, cache);
	}

	public void set(String url, NetworkImageView iv) {

			iv.setImageUrl(url, loader);

	}
	static class BitmapLruCache extends LruCache<String, Bitmap> implements ImageCache {

		public BitmapLruCache() {

			super(getDefaultLruCacheSize());
		}

		public BitmapLruCache(int sizeInKiloBytes) {

			super(sizeInKiloBytes);
		}

		public static int getDefaultLruCacheSize() {

			final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
			final int cacheSize = maxMemory / 8;

			return cacheSize;
		}

		@Override
		protected int sizeOf(String key, Bitmap value) {

			return value.getRowBytes() * value.getHeight() / 1024;
		}

		// public boolean contains(String key) {
		//
		// return get(key) != null;
		// }

		@Override
		public Bitmap getBitmap(String url) {

			return get(url);
		}

		@Override
		public void putBitmap(String url, Bitmap bitmap) {

			// BitmapFactory.Options opition = new BitmapFactory.Options();
			// opition.inJustDecodeBounds = true;
			// opition.inSampleSize = 3;
			// opition.inScaled = true;
			// opition.inDither = false;
			// bitmap = bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / 4, bitmap.getHeight() / 4, true);

			put(url, bitmap);
		}

	}
	public static class CircleTransform implements Transformation {

		@Override
		public Bitmap transform(Bitmap source) {

			int size = Math.min(source.getWidth(), source.getHeight());

			int x = (source.getWidth() - size) / 2;

			int y = (source.getHeight() - size) / 2;

			Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);

			if (squaredBitmap != source) {

				source.recycle();

			}

			Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

			Canvas canvas = new Canvas(bitmap);

			Paint paint = new Paint();

			BitmapShader shader = new BitmapShader(squaredBitmap, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);

			paint.setShader(shader);

			paint.setAntiAlias(true);

			float r = size / 2f;

			canvas.drawCircle(r, r, r, paint);

			squaredBitmap.recycle();

			return bitmap;

		}

		@Override
		public String key() {

			return "circle";

		}

	}
}
