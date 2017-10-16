package cn.veasion.util.face;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import cn.veasion.util.VeaUtil;

/**
 * 文字识别排序
 * 
 * @author zhuowei.luo
 */
public class ImageTextBean {
	
	private int status;
	private String message;
	private JSONObject json;
	private List<TextValue> textList;
	// 上到下 y-- 左到右 x++
	
	public ImageTextBean(FaceResponse resp){
		if((this.status=resp.getStatus())!=200){
			if(status==400){
				message="1.参数<param>对应的图像无法正确解析，有可能不是一个图像文件、或有数据破损。<br/>\n";
				message+="2.客户上传的图像像素尺寸太大或太小，图片要求请参照本API描述。<param>对应图像太大的那个参数的名称 <br/>\n";
				message+="3.客户上传的图像文件太大。本 API 要求图片文件大小不超过 2 MB <br/>\n";
				message+="4.无法从指定的image_url下载图片，图片URL错误或者无效 <br/>\n";
			}else if(status==412){
				message="下载图片超时！";
			}else if(status==403){
				message="403，没有权限，请稍后重试...";
			}else{
				message="服务器发生错误！status:"+status;
			}
		}else{
			try {
				json=JSONObject.fromObject(new String(resp.getContent(),"UTF-8"));
				this.fill();
			} catch (Exception e) {
				this.status=-1;
				e.printStackTrace();
			}
		}
	}
	
	private void fill(){
		textList=new ArrayList<>();
		JSONArray jsonArr=json.optJSONArray("result");
		for (Object obj : jsonArr) {
			JSONObject j=JSONObject.fromObject(obj);
			TextValue tv=new TextValue();
			tv.setType(j.optString("type"));
			tv.setValue(j.optString("value"));
			try{
				JSONArray childs=j.optJSONArray("child-objects");
				int minX = -1, maxX = 0, minY = -1, maxY = 0;
				for (Object child : childs) {
					JSONObject object=JSONObject.fromObject(child);
					JSONArray positions=object.optJSONArray("position");
					for (Object position : positions) {
						JSONObject positionJson=JSONObject.fromObject(position);
						int x=positionJson.optInt("x", 0);
						int y=positionJson.optInt("y", 0);
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
				tv.setW(maxX-minX);
				tv.setH(maxY-minY);
			}catch(Exception e){
				e.printStackTrace();
			}
			textList.add(tv);
		}
		
		// 文字识别，排序
		textList.sort((t1, t2)->{
			int fh = this.maxFontHeight() / 2;
			if (t1.getY() > t2.getY()) {
				if (Math.abs(t1.getY() - t2.getY()) < fh)
					return sortX(t1, t2);
				else
					return 1;
			} else if (t1.getY() == t2.getY()) {
				return sortX(t1, t2);
			} else {
				if (Math.abs(t1.getY() - t2.getY()) < fh)
					return sortX(t1, t2);
				else
					return -1;
			}
		});
	}
	
	// 排序X坐标
	private int sortX(TextValue t1,TextValue t2){
		if (t1.getX() == t2.getX())
			return 0;
		else if (t1.getX() > t2.getX())
			return 1;
		else
			return -1;
	}
	
	public int getStatus() {
		return status;
	}
	public String getMessage() {
		return message;
	}
	public JSONObject getJson() {
		return json;
	}
	public List<TextValue> getTextList() {
		return textList;
	}
	
	/**获取text for html*/
	public String getTextHtml(){
		int avgFontHeight = this.avgFontHeight();
		if (!VeaUtil.isNullEmpty(textList)) {
			StringBuilder sb = new StringBuilder();
			int y = textList.get(0).getY();
			for (TextValue tv : textList) {
				if (tv.getY() > y + avgFontHeight) {
					y = tv.getY();
					sb.append("<br/>");
				}
				sb.append(tv.value).append("&nbsp;");
			}
			return sb.toString();
		} else if (this.status != 200) {
			return "识别失败：" + this.getStatus();
		} else {
			return "";
		}
	}
	
	/**获取text，没有换行*/
	public String getText(){
		if (!VeaUtil.isNullEmpty(textList)) {
			StringBuilder sb = new StringBuilder();
			for (TextValue tv : textList) {
				sb.append(tv.value).append(" ");
			}
			return sb.toString();
		} else if (this.status != 200) {
			return "识别失败：" + this.getStatus();
		} else {
			return "";
		}
	}
	
	/**获取text，有换行\n*/
	public String getTextStr(){
		int avgFontHeight = this.avgFontHeight();
		if (!VeaUtil.isNullEmpty(textList)) {
			StringBuilder sb = new StringBuilder();
			int y = textList.get(0).getY();
			for (TextValue tv : textList) {
				if (tv.getY() > y + avgFontHeight) {
					y = tv.getY();
					sb.append("\n");
				}
				sb.append(tv.value).append(" ");
			}
			return sb.toString();
		} else if (this.status != 200) {
			return "识别失败：" + this.getStatus();
		} else {
			return "";
		}
	}
	
	/**
	 * 获取平均字体高度
	 */
	private int avgFontHeight(){
		if(!VeaUtil.isNullEmpty(textList)){
			return textList.stream().mapToInt((v)->v.getH()).sum() / textList.size();
		}else{
			return 0;
		}
	}
	
	/**
	 * 获取最大字体高度 
	 */
	private int maxFontHeight(){
		if(!VeaUtil.isNullEmpty(textList)){
			return textList.stream().mapToInt((v)->v.getH()).max().orElse(0);
		}else{
			return 0;
		}
	}
	
	@Override
	public String toString() {
		return getText();
	}

	public class TextValue{
		private int x;
		private int y;
		private int w;
		private int h;
		private String value;
		private String type;
		
		public int getX() {
			return x;
		}
		public void setX(int x) {
			this.x = x;
		}
		public int getY() {
			return y;
		}
		public void setY(int y) {
			this.y = y;
		}
		public String getValue() {
			return value;
		}
		public void setValue(String value) {
			this.value = value;
		}
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public void setW(int w) {
			this.w = w;
		}
		public int getW() {
			return w;
		}
		public void setH(int h) {
			this.h = h;
		}
		public int getH() {
			return h;
		}
		
		@Override
		public String toString() {
			return "TextValue [x=" + x + ", y=" + y + ", w=" + w + ", h=" + h + ", value=" + value + ", type=" + type
					+ "]";
		}
	}
}
