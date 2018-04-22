package com.mypolice.poo.util;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.res.XmlResourceParser;

import com.mypolice.poo.R;

/**   
 * @Title: GvFunctionXMLReader.java 
 * @Package com.hw.ics.adapter
 * @Description: xml 解析
 * @author wangjl  
 * @crdate 2017-3-6
 * @update
 * @version v1.0   
 */
public class FunctionXMLReader<T> {
	
	private final String TAG_FUNCTION = "Function";

	private Context mContext;
	private Class<T> mClassT;
	
	private XmlResourceParser mXmlParser;
	private List<T> mBeans;
	
	public FunctionXMLReader(Context context, Class<T> classT) {
		this.mContext = context;
		this.mClassT = classT;
		
		init();
	}
	
	private void init() {
		mXmlParser = mContext.getResources().getXml(R.xml.xml_gv_func_main_home);
		mBeans = new ArrayList<T>();
	}
	
	public void parse() {
		try {
			int eventType = mXmlParser.getEventType();
			while (XmlPullParser.END_DOCUMENT != eventType) {	// 未到末节点
				switch (eventType) {
				case XmlPullParser.START_DOCUMENT:	// 开始读取 xml 文档
					
					break;
				case XmlPullParser.START_TAG:	// 开始读取某个标签
					parseTags();
					break;
				case XmlPullParser.END_TAG:	// 读完某个标签
					
					break;
				case XmlPullParser.END_DOCUMENT:		// 读完 xml 文档
					
					break;

				default:
					break;
				}
				
				eventType = mXmlParser.next();
			}
			
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		
	}
	
	/** 解析标签 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException */
	private void parseTags() throws InstantiationException, IllegalAccessException {
		String tagName = mXmlParser.getName();
		if (TAG_FUNCTION.equals(tagName)) {
			T bean = mClassT.newInstance();
			Field[] fields = mClassT.getDeclaredFields();
			
			for (int i = 0; i < mXmlParser.getAttributeCount(); i++) {
				String attrName = mXmlParser.getAttributeName(i);
				String attrValue = mXmlParser.getAttributeValue(i);
				
				for (Field f : fields) {
					f.setAccessible(true);
					String fName = f.getName();
					
					if (fName.equals(attrName)) {
						f.set(bean, attrValue);
					}
				}
			}
			
			mBeans.add(bean);
		}
	}
	
	/** 获取 bean */
	public List<T> getBeans() {
		return mBeans;
	}
 	
}
