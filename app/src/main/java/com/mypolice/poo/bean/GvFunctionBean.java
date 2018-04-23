package com.mypolice.poo.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**   
 * @Title: GvFunctionBean.java 
 * @Package com.mypolice.poo.bean
 * @Description: GridView xml配置文件实体类
 * @author wangjl  
 * @crdate 2017-8-21
 * @update 2017-4-23	update
 * @version v1.0.0(1)
 */
public class GvFunctionBean implements Parcelable {

	private String name;
	private String packageName;
	private String activityName;
	private String displayName;

	private String icon;
//	private String bg1;
//	private String bg2;
	
	public GvFunctionBean() {
		
	}	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getActivityName() {
		return activityName;
	}

	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

//	public String getBg1() {
//		return bg1;
//	}
//
//	public void setBg1(String bg1) {
//		this.bg1 = bg1;
//	}
//
//	public String getBg2() {
//		return bg2;
//	}
//
//	public void setBg2(String bg2) {
//		this.bg2 = bg2;
//	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(name);
		dest.writeString(packageName);
		dest.writeString(activityName);
		dest.writeString(displayName);
		dest.writeString(icon);
//		dest.writeString(bg1);
//		dest.writeString(bg2);
	}
	
	public static final Creator<GvFunctionBean> CREATOR = new Creator<GvFunctionBean>() {
		
		@Override
		public GvFunctionBean[] newArray(int size) {
			return new GvFunctionBean[size];
		}
		
		@Override
		public GvFunctionBean createFromParcel(Parcel source) {
			return new GvFunctionBean(source);
		}
	};
	
	protected GvFunctionBean(Parcel source) {
		name = source.readString();
		packageName = source.readString();
		activityName = source.readString();
		displayName = source.readString();
		icon = source.readString();
//		bg1 = source.readString();
//		bg2 = source.readString();
	}
	
}
