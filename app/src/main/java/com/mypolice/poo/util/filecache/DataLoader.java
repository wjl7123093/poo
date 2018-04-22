package com.mypolice.poo.util.filecache;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;

/**   
 * @Title: DataLoader.java 
 * @Package com.mytv.rtmedia.util.filecache
 * @Description: 公共方法类
 * @author wangjl  
 * @crdate 2017-4-5
 * @update 2017-5-6
 * @version v1.1   
 */
public class DataLoader {
	// 硬盘缓存工具类
	private DiskCache mDiskCache;

	public DataLoader(Context context) {
		mDiskCache = new DiskCache(context, 2, 0);
	}

	// 单例模式
	private static DataLoader instance;	
	/** 保证多个页面共享一个 DataLoader 缓存 */
	public static DataLoader getInstance(Context context) {
		if (null == instance)
			instance = new DataLoader(context);
		return instance;
	}

	/**
	 * 保存文件
	 * 
	 * @param key
	 *            文件名使用小写字母加数字
	 * @param data
	 */
	public void saveFileData(final String key, final String data) {

		// TODO Auto-generated method stub
		if (data != null) {
			mDiskCache.addDataToCache(key, data.getBytes(), true);
		}

	}

	/**
	 * 读取文件
	 * 
	 * @param key
	 * @return
	 */
	public String getDataFromDisk(String key) {

		// TODO Auto-generated method stub
		InputStream is = mDiskCache.getStreamFromDiskCache(key);
		if (is == null) {
			return "";
		}
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		try {
			while ((len = is.read(buffer, 0, 1024)) != -1) {
				bos.write(buffer, 0, len);
			}
			return new String(bos.toByteArray());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				is.close();
				bos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return "";

	}

}
