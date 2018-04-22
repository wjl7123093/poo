/**   
 * @Title: DiskCache.java 
 * @Description: 硬盘文件缓存。
 * @author qingxd   
 * @date 2015年3月30日 下午3:42:17 
 * @version V1.0   
 */

package com.mypolice.poo.util.filecache;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.os.Environment;

import com.mypolice.poo.util.CommonFuncUtil;

public class DiskCache {

	private DiskLruCache mDiskLruCache = null;
	// 1MB数据长度
	private final int DISK_CACHE_SIZE = 1024 * 1024;
	// 期望获取图片尺寸
	private int mImgSize = 100;

	private Context mContext;

	public DiskCache(Context context) {
		mContext = context;
	}

	/**
	 * 
	 * @param context
	 * @param type
	 *            文件类型 1:图片，不是1:其他文件
	 * @param size
	 *            图片尺寸 type为图片时需要传入，默认为100。获取数据不是图片传入0
	 */
	public DiskCache(Context context, int type, int size) {
		// TODO Auto-generated constructor stub
		mContext = context;
		// 获取文件存储路径
		File cacheDir = null;
		if (type == 1) {
			cacheDir = getDiskCacheDir(context, "images");
			if (size != 0) {
				mImgSize = size;
			}
		} else {
			cacheDir = getDiskCacheDir(context, "userdata");
		}

		// 如果文件夹不存在就创建文件夹
		if (!cacheDir.exists()) {
			cacheDir.mkdirs();
		}

		try {
			if (type == 1) {
				// 图片缓存空间为20MB
				mDiskLruCache = DiskLruCache.open(cacheDir, CommonFuncUtil
						.getVersion(context).getVersionCode(), 1,
						DISK_CACHE_SIZE * 20);
			} else {
				// 其他文件缓存空间为4MB
				mDiskLruCache = DiskLruCache.open(cacheDir, CommonFuncUtil
						.getVersion(context).getVersionCode(), 1,
						DISK_CACHE_SIZE * 4);
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 获取储存的路径
	 * 
	 * @param context
	 * @param uniqueName
	 * @return
	 */
	public File getDiskCacheDir(Context context, String uniqueName) {
		// 获取储存路径
		String cachePath = Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())
				|| !Environment.isExternalStorageRemovable() ? context
				.getExternalCacheDir().getPath() : context.getCacheDir()
				.getPath();
		return new File(cachePath + File.separator + uniqueName);
	}

	/**
	 * 将流数据缓存到本地
	 * 
	 * @param key
	 * @param is
	 * @param isReplace
	 *            是否替换原有同名数据
	 */
	public void addDataToCache(String key, InputStream is, boolean isReplace) {

		if (is == null) {
			return;
		}

		try {
			if (mDiskLruCache != null) {
				if (isReplace || (mDiskLruCache.get(key) == null)) {
					DiskLruCache.Editor editor = mDiskLruCache.edit(key);
					OutputStream os = editor.newOutputStream(0);
					byte buffer[] = new byte[1024];
					int len = 0;
					while ((len = is.read(buffer, 0, 1024)) != -1) {
						os.write(buffer, 0, len);
					}
					editor.commit();
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 将byte数组数据缓存到本地
	 * 
	 * @param key
	 * @param b
	 * @param isReplace是否替换原有同名数据
	 */
	public void addDataToCache(String key, byte[] b, boolean isReplace) {

		if (b == null) {
			return;
		}

		try {
			if (mDiskLruCache != null) {
				if (isReplace || (mDiskLruCache.get(key) == null)) {
					DiskLruCache.Editor editor = mDiskLruCache.edit(key);
					OutputStream os = editor.newOutputStream(0);
					os.write(b, 0, b.length);
					editor.commit();
				}

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 获取图片
	 * 
	 * @param key
	 * @return
	 */
	public Bitmap getBitmapFromDiskCache(String key) {

		InputStream is = getStreamFromDiskCache(key);
		if (is != null) {
			return BitMapUtils.decodeFile(is, mImgSize);
		}

		return null;
	}

	/**
	 * 获取数据流
	 * 
	 * @param key
	 *            文件名称
	 * @return 数据流
	 */
	public InputStream getStreamFromDiskCache(String key) {

		if (mDiskLruCache != null) {
			try {
				DiskLruCache.Snapshot data = mDiskLruCache.get(key);
				if (data != null) {
					return data.getInputStream(0);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return null;
	}

	/**
	 * 删除SD卡缓存文件
	 */
	public void deleteSDCardCache() {
		/*
		 * File file = getDiskCacheDir(mContext, "userdata"); File imgFile =
		 * getDiskCacheDir(mContext, "images"); deleteFile(file);
		 * deleteFile(imgFile);
		 */
		try {
			mDiskLruCache.delete();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 删除文件
	 * 
	 * @param file
	 */
	private void deleteFile(File file) {
		if (file.isFile()) {
			file.delete();
			return;
		}

		if (file.isDirectory()) {
			File[] childFiles = file.listFiles();
			/*
			 * if (childFiles == null || childFiles.length == 0) {
			 * file.delete(); return; }
			 */

			for (int i = 0; i < childFiles.length; i++) {
				deleteFile(childFiles[i]);
			}
			// file.delete();
		}
	}

}
