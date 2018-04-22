package com.mypolice.poo.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.support.v4.app.TaskStackBuilder;
import android.view.View;
import android.widget.TextView;

import com.mypolice.poo.R;
import com.mypolice.poo.application.GlobalSet;
import com.mypolice.poo.widget.CenterDialog;

public class UpdateVersion {
	public final String TAG = "poo";
	public final static int UPDATA_NONEED = 0;
	public final static int UPDATA_CLIENT = 1;
	public final static int GET_UNDATAINFO_ERROR = 2;
	public final static int SDCARD_NOMOUNTED = 3;
	public final static int DOWN_ERROR = 4;
	public final static int PROGRESS = 5;
	public final static int START_INSTALL = 6;

	private ProgressDialog pbarDialog;
	private CenterDialog mProgressBarDialog;
	private TextView mTvProgress;

	private Context mContext;
	private int localVerCode = -1;
	private Thread mThread;
	private DownCallBack mCallBack;
	private HashMap<String, String> mHashMap = null;
	private final int DOWNNOTICE_ID = 1200;

	public UpdateVersion(Context mContext) {
		this.mContext = mContext;
	}

	public UpdateVersion(Context mContext, int localVerCode,
			DownCallBack callBack) {
		this.mContext = mContext;
		this.mCallBack = callBack;
		this.localVerCode = localVerCode;
	}

	public void checkVersion(String path) {
		CheckVersionTask cv = new CheckVersionTask(path);
		mThread = new Thread(cv);
		mThread.start();
	}

	/*
	 * 从服务器获取xml解析并进行比对版本号
	 */
	public class CheckVersionTask implements Runnable {

		private String path;

		public CheckVersionTask(String path) {
			this.path = path;
		}

		public void run() {
			try {
				// 从资源文件获取服务器 地址
				URL url = new URL(path);
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				// 设置超时时间
				conn.setConnectTimeout(5000);
				InputStream is = conn.getInputStream();

				ParseXml service = new ParseXml();
				try {
					mHashMap = service.parseXml(is);
				} catch (Exception e) {
					e.printStackTrace();
					Message msg = new Message();
					msg.what = GET_UNDATAINFO_ERROR;
					handler.sendMessage(msg);
				}
				if (Integer.parseInt(mHashMap.get("version")) > localVerCode) {
					Message msg = new Message();
					msg.what = UPDATA_CLIENT;
					handler.sendMessage(msg);
				} else {
					Message msg = new Message();
					msg.what = UPDATA_NONEED;
					handler.sendMessage(msg);
					// LoginMain();
				}
			} catch (Exception e) {
				// 待处理
				Message msg = new Message();
				msg.what = GET_UNDATAINFO_ERROR;
				handler.sendMessage(msg);
				e.printStackTrace();
			}
		}
	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case UPDATA_NONEED:
				mCallBack.onDownCallBack(UPDATA_NONEED, "版本号相同无需升级");
				break;
			case UPDATA_CLIENT:
				mCallBack.onDownCallBack(UPDATA_CLIENT, "版本号不相同升级");
				break;
			case GET_UNDATAINFO_ERROR:
				mCallBack.onDownCallBack(GET_UNDATAINFO_ERROR, "获取服务器更新信息失败");
				break;
			case SDCARD_NOMOUNTED:
				mCallBack.onDownCallBack(SDCARD_NOMOUNTED, "SD卡不可用");
				break;
			case DOWN_ERROR:
				mCallBack.onDownCallBack(DOWN_ERROR, "下载新版本失败");
				break;
			case PROGRESS:
				mTvProgress.setText("正在下载更新 " + msg.arg2 + "%");
				break;
			case START_INSTALL: // 开始安装
				final Intent intent = (Intent) msg.obj;
				
				CenterDialog centerDialog = new CenterDialog(mContext, 
						R.layout.dialog_download_completed, new int[]{R.id.btnOK, R.id.btnCancel});
				centerDialog.show();
				centerDialog.setOnCenterItemClickListener(new CenterDialog.OnCenterItemClickListener() {
					
					@Override
					public void OnCenterItemClick(CenterDialog dialog, View view) {
						switch (view.getId()) {
						case R.id.btnOK:	// 确定
							mContext.startActivity(intent);
							dialog.dismiss();
							break;
							
						case R.id.btnCancel:	// 取消
							dialog.dismiss();
							break;

						default:
							break;
						}
						
					}
				});
				
				break;
			}
		}
	};

	/**
	 * 从服务器中下载APK [2]
	 */
	public void downLoadApk2() {
		
		CenterDialog centerDialog = new CenterDialog(mContext, 
				R.layout.dialog_download_apk, new int[]{R.id.btnOK, R.id.btnCancel});
		centerDialog.show();
		centerDialog.setOnCenterItemClickListener(new CenterDialog.OnCenterItemClickListener() {
			
			
			@Override
			public void OnCenterItemClick(CenterDialog dialog, View view) {
				switch (view.getId()) {
				case R.id.btnOK:	// 确定
					dialog.dismiss();
					doDownload();
					break;
					
				case R.id.btnCancel:	// 取消
					dialog.dismiss();
					break;

				default:
					break;
				}
			}
		});

	}

	/** 执行下载操作 */
	private void doDownload() {
		mProgressBarDialog = new CenterDialog(mContext, R.layout.dialog_downloading, new int[]{});
		mProgressBarDialog.show();
		mTvProgress = (TextView) mProgressBarDialog.findViewById(R.id.tvProgressText);

		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			Message msg = new Message();
			msg.what = SDCARD_NOMOUNTED;
			handler.sendMessage(msg);
		} else {
			new Thread() {
				@Override
				public void run() {
					try {
						File file = getFileFromServer2(mHashMap.get("apkurl"));
						bindInstallApk2(file, pbarDialog);

					} catch (Exception e) {
						e.printStackTrace();
						mTvProgress.setText("下载失败...");
						mProgressBarDialog.dismiss();
					}
				}
			}.start();
		}
	}

	private void bindInstallApk2(File file, ProgressDialog pbarDialog) {
		mProgressBarDialog.dismiss();

		final Intent intent = new Intent();
		// 执行动作
		intent.setAction(Intent.ACTION_VIEW);
		// 执行的数据类型
		intent.setDataAndType(Uri.fromFile(file),
				"application/vnd.android.package-archive");
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		Message msg = new Message();
		msg.what = START_INSTALL;
		msg.obj = intent;
		handler.sendMessage(msg);

	}

	/**
	 * 从服务器下载apk [2]
	 * 
	 * @param path
	 * @param pd
	 * @return
	 * @throws Exception
	 */
	public File getFileFromServer2(String path)
			throws Exception {
		// 如果相等的话表示当前的sdcard挂载在手机上并且是可用的
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			URL url = new URL(path);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(10000);
			// 获取到文件的大小
			// pd.setMax(100);
			int totlelength = conn.getContentLength();
			int bl = totlelength / 100;
//			pbarDialog.setMax(bl);
			InputStream is = conn.getInputStream();
			String fileName = getFileName(path);
			File file = new File(Environment.getExternalStorageDirectory(),
					fileName);
			FileOutputStream fos = new FileOutputStream(file);
			BufferedInputStream bis = new BufferedInputStream(is);
			byte[] buffer = new byte[1024];
			int len;
			int total = 0;
			int lastpos = 0;
			while ((len = bis.read(buffer)) != -1) {
				fos.write(buffer, 0, len);
				total += len;
				// 获取当前下载量
				int currentpos = total / bl;

				Message msg = new Message();
				msg.what = PROGRESS;
				msg.arg1 = total;
				msg.arg2 = total / bl;
				handler.sendMessage(msg);

			}

			fos.close();
			bis.close();
			is.close();
			return file;
		} else {
			return null;
		}
	}

	public interface DownCallBack {
		public void onDownCallBack(int cmd, Object data);
	}

	private String getFileName(String path) {
		String fileName = "";
		int filePos = path.lastIndexOf("/");
		fileName = path.substring(filePos + 1);
		if (fileName == null || fileName.equals("")) {
			fileName = GlobalSet.APP_DOWNLOAD_NAME;
		}
		return fileName;
	}
}
