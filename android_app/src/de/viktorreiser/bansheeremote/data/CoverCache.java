package de.viktorreiser.bansheeremote.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import de.viktorreiser.bansheeremote.R;
import de.viktorreiser.toolbox.os.LruCache;

/**
 * Global cover cache pool.<br>
 * <br>
 * You can persist and load covers here. Every loaded cover is cached in memory for quick access.
 * 
 * @author Viktor Reiser &lt;<a href="mailto:viktorreiser@gmx.de">viktorreiser@gmx.de</a>&gt;
 */
public class CoverCache {
	
	// PRIVATE ====================================================================================
	
	private static LruCache<String, Bitmap> mCache = new LruCache<String, Bitmap>(1024 * 1024 * 2) {
		@Override
		public int sizeOf(String key, Bitmap value) {
			return value.getRowBytes() * value.getHeight();
		}
	};
	
	// PUBLIC =====================================================================================
	
	/**
	 * Does cover already exist?
	 * 
	 * @param id
	 *            cover ID returned by server or stored in database
	 * 
	 * @return {@code true} if cover is available locally
	 */
	public static boolean coverExists(String id) {
		return "".equals(id) || new File(App.CACHE_PATH + id + ".jpg").exists();
	}
	
	/**
	 * Get (unscaled) cover.<br>
	 * <br>
	 * This cover <b>won't</b> be cached and directly returned from file.
	 * 
	 * @param id
	 *            cover ID returned by server or stored in database
	 * 
	 * @return cover as bitmap or {@code null} when there is no cover for given ID
	 */
	public static Bitmap getUnscaledCover(String id) {
		File file = new File(App.CACHE_PATH + id + ".jpg");
		
		return file.exists() ? BitmapFactory.decodeFile(file.getAbsolutePath()) : null;
	}
	
	/**
	 * Get (scaled) cover ({@link App#getCacheSize()}).
	 * 
	 * @param id
	 *            cover ID returned by server or stored in database
	 * 
	 * @return cover as bitmap or {@code null} when there is no cover for given ID
	 */
	public static Bitmap getThumbCover(String id) {
		return getThumbCover(id, App.getCacheSize());
	}
	
	/**
	 * Get (scaled) cover ({@link App#getCacheSize()}).
	 * 
	 * @param id
	 *            cover ID returned by server or stored in database
	 * @param size
	 *            size of thumbnail in pixel
	 * 
	 * @return cover as bitmap or {@code null} when there is no cover for given ID
	 */
	public static Bitmap getThumbCover(String id, int size) {
		String thumbId = ("".equals(id) ? "__nocover_" : id) + "_" + size;
		
		Bitmap thumb = mCache.get(thumbId);
		
		if (thumb != null) {
			return thumb;
		}
		
		File thumbFile = new File(App.CACHE_PATH + thumbId + ".jpg");
		
		if (thumbFile.exists()) {
			thumb = BitmapFactory.decodeFile(thumbFile.getAbsolutePath());
			mCache.put(thumbId, thumb);
			return thumb;
		}
		
		if (thumbFile.exists()) {
			return BitmapFactory.decodeFile(thumbFile.getAbsolutePath());
		}
		
		File originalFile = new File(App.CACHE_PATH + id + ".jpg");
		Bitmap original = null;
		
		if ("".equals(id)) {
			original = ((BitmapDrawable) App.getContext().getResources()
					.getDrawable(R.drawable.no_cover)).getBitmap();
		} else if (!originalFile.exists()) {
			return getThumbCover("", size);
		} else {
			original = BitmapFactory.decodeFile(originalFile.getAbsolutePath());
		}
		
		float scale = Math.min((float) size / original.getWidth(),
				(float) size / original.getHeight());
		Matrix matrix = new Matrix();
		matrix.postScale(scale, scale);
		
		thumb = Bitmap.createBitmap(original, 0, 0,
				original.getWidth(), original.getHeight(), matrix, true);
		original.recycle();
		
		try {
			thumb.compress(CompressFormat.JPEG, 80, new FileOutputStream(thumbFile));
		} catch (FileNotFoundException e) {
		}
		
		return thumb;
	}
	
	/**
	 * Put cover into cache.
	 * 
	 * @param id
	 *            cover ID for which the cover should be persisted
	 * @param bitmapData
	 *            image data as raw byte array
	 * 
	 * @return {@code false} if cover couldn't be persisted on SD card
	 */
	public static boolean addCover(String id, byte [] bitmapData) {
		Bitmap cover = BitmapFactory.decodeByteArray(bitmapData, 0, bitmapData.length);
		
		if (cover != null) {
			try {
				File file = new File(App.CACHE_PATH + id + ".jpg");
				file.getParentFile().mkdirs();
				file.delete();
				cover.compress(CompressFormat.JPEG, 80, new FileOutputStream(file));
				
				return true;
			} catch (FileNotFoundException e) {
				// too bad, we have a valid cover but failed to persist
			}
		}
		
		return false;
	}
}
