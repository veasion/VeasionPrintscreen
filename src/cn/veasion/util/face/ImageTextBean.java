package cn.veasion.util.face;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import cn.veasion.util.VeaUtil;

/**
 * 文字识别 
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
				JSONObject xy=j.optJSONArray("child-objects").getJSONObject(0).optJSONArray("position").getJSONObject(0);
				tv.setX(xy.optInt("x", 0));
				tv.setY(xy.optInt("y", 0));
			}catch(Exception e){
				e.printStackTrace();
			}
			textList.add(tv);
		}
		// 文字识别，排序
		textList.sort((t1,t2)->{
			if(t1.getY()>t2.getY()){
				if(jdz(t1.getY()-t2.getY())<10)
					return sortX(t1, t2);
				else
					return 1;
			}else if(t1.getY()==t2.getY()){
				return sortX(t1, t2);
			}else{
				if(jdz(t1.getY()-t2.getY())<10)
					return sortX(t1, t2);
				else
					return -1;
			}
		});
	}
	
	// 排序X坐标
	private int sortX(TextValue t1,TextValue t2){
		if(t1.getX()==t2.getX())
			return 0;
		else if(t1.getX()>t2.getX())
			return 1;
		else return -1;
	}
	
	// 绝对值
	private int jdz(int xxx){
		if(xxx<0) return -xxx;
		else return xxx;
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
		if(!VeaUtil.isNullEmpty(textList)){
			StringBuilder sb=new StringBuilder();
			int y=textList.get(0).getY();
			for (TextValue tv : textList) {
				if(tv.getY()>y+10){
					y=tv.getY();
					sb.append("<br/>");
				}
				sb.append(tv.value).append("&nbsp;");
			}
			return sb.toString();
		}else if (this.status != 200) {
			return "识别识别："+this.getStatus();
		}else{
			return "";
		}
	}
	
	/**获取text，没有换行*/
	public String getText(){
		if(!VeaUtil.isNullEmpty(textList)){
			StringBuilder sb=new StringBuilder();
			for (TextValue tv : textList) {
				sb.append(tv.value).append(" ");
			}
			return sb.toString();
		}else if (this.status != 200) {
			return "识别识别："+this.getStatus();
		}else{
			return "";
		}
	}
	
	/**获取text，有换行\n*/
	public String getTextStr(){
		if(!VeaUtil.isNullEmpty(textList)){
			StringBuilder sb=new StringBuilder();
			int y=textList.get(0).getY();
			for (TextValue tv : textList) {
				if(tv.getY()>y+10){
					y=tv.getY();
					sb.append("\n");
				}
				sb.append(tv.value).append(" ");
			}
			return sb.toString();
		}else if (this.status != 200) {
			return "识别识别："+this.getStatus();
		}else{
			return "";
		}
	}
	
	@Override
	public String toString() {
		return getText();
	}

	public class TextValue{
		private int x;
		private int y;
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
		@Override
		public String toString() {
			return "TextValue [x=" + x + ", y=" + y + ", value=" + value + ", type=" + type + "]";
		}
	}
}
