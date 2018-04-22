package com.mypolice.poo.util;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**   
 * @Title: LoadDataToBeanUtil.java 
 * @Package com.mytv.rtmedia.util
 * @Description: 装载数据到Bean类
 * @author wangjl  
 * @crdate 2017-4-5
 * @update 2017-8-9
 * @version v1.1.6(8)   
 */
public class LoadDataToBeanUtil {

//	/**
//	 * 将 json字符串 转换为 {@link NewsCategoryBean}
//	 * @param jsonObj 服务端传递回来的 json 数据
//	 * @throws JSONException
//	 */
//	public static NewsCategoryBean loadDataToNewsCategoryBean(JSONObject jsonObj)
//			throws JSONException {
//		NewsCategoryBean cat = new NewsCategoryBean();
//		cat.setCatID(jsonObj.getInt(NewsCategoryConstant.T_CAT_ID));
//		cat.setCatName(jsonObj.getString(NewsCategoryConstant.T_CAT_NAME));
//		cat.setCatType(jsonObj.getString(NewsCategoryConstant.T_CAT_TYPE));
//		cat.setModelName(jsonObj.getString(NewsCategoryConstant.T_CAT_MODEL_NAME));
//		return cat;
//	}
//
//	/**
//	 * 将 json数组 转换为 {@link NewsCategoryBean}集合
//	 * @param jsonArray
//	 * @return
//	 * @throws JSONException
//	 */
//	public static List<NewsCategoryBean> loadDataToNewsCategoryList(String jsonArray)
//			throws JSONException {
//		List<NewsCategoryBean>  catList = new ArrayList<NewsCategoryBean>();
//		JSONArray arr = new JSONArray(jsonArray);
//		for (int i = 0; i < arr.length(); i++) {
//			NewsCategoryBean cat = loadDataToNewsCategoryBean(arr.getJSONObject(i));
//			catList.add(cat);
//		}
//		return catList;
//	}
	
}
