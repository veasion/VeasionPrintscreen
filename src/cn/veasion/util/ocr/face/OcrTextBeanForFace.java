package cn.veasion.util.ocr.face;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import cn.veasion.util.ocr.OcrTextBean;
import cn.veasion.util.ocr.TextValue;

/**
 * Face文字识别解析
 * 
 * @author zhuowei.luo
 */
public class OcrTextBeanForFace implements OcrTextBean {

	private int status;
	private String message;
	private boolean isSuccess;
	private JSONObject json;
	private List<TextValue> textList;
	
	public OcrTextBeanForFace(FaceResponse resp) {
		if ((this.status = resp.getStatus()) != 200) {
			isSuccess = false;
			if (status == 400) {
				message = "1.参数<param>对应的图像无法正确解析，有可能不是一个图像文件、或有数据破损。<br/>\n";
				message += "2.客户上传的图像像素尺寸太大或太小，图片要求请参照本API描述。<param>对应图像太大的那个参数的名称 <br/>\n";
				message += "3.客户上传的图像文件太大。本 API 要求图片文件大小不超过 2 MB <br/>\n";
				message += "4.无法从指定的image_url下载图片，图片URL错误或者无效 <br/>\n";
			} else if (status == 412) {
				message = "下载图片超时！";
			} else if (status == 403) {
				message = "403，没有权限，请稍后重试...";
			} else {
				message = "服务器发生错误！status:" + status;
			}
		} else {
			try {
				json = JSONObject.fromObject(new String(resp.getContent(), "UTF-8"));
				this.fill();
				isSuccess = true;
			} catch (Exception e) {
				isSuccess = false;
				this.message = "json解析异常：" + e.getMessage();
				// e.printStackTrace();
			}
		}
	}

	/**
	 * 填充结果集
	 */
	private void fill() {
		textList = new ArrayList<>();
		JSONArray jsonArr = json.optJSONArray("result");
		for (Object obj : jsonArr) {
			JSONObject j = JSONObject.fromObject(obj);
			TextValue tv = new TextValue();
			// tv.setType(j.optString("type"));
			tv.setValue(j.optString("value"));
			try {
				JSONArray childs = j.optJSONArray("child-objects");
				int minX = -1, maxX = 0, minY = -1, maxY = 0;
				for (Object child : childs) {
					JSONObject object = JSONObject.fromObject(child);
					JSONArray positions = object.optJSONArray("position");
					for (Object position : positions) {
						JSONObject positionJson = JSONObject.fromObject(position);
						int x = positionJson.optInt("x", 0);
						int y = positionJson.optInt("y", 0);
						if (minX == -1) {
							minX = x;
						} else if (x < minX) {
							minX = x;
						}
						if (minY == -1) {
							minY = y;
						} else if (y < minY) {
							minY = y;
						}
						if (x > maxX) {
							maxX = x;
						}
						if (y > maxY) {
							maxY = y;
						}
					}
				}
				tv.setX(minX);
				tv.setY(minY);
				tv.setW(maxX - minX);
				tv.setH(maxY - minY);
			} catch (Exception e) {
				e.printStackTrace();
			}
			textList.add(tv);
		}
	}

	@Override
	public boolean isSuccess() {
		return this.isSuccess;
	}

	@Override
	public String errorMessage() {
		return this.message;
	}

	@Override
	public List<TextValue> getTextValues() {
		return this.textList;
	}

}
