package cn.veasion.util.baiduyun;

import java.util.ArrayList;
import java.util.List;
import cn.veasion.util.VeaUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class TextResponseForBd {
	
	private List<TextValueForBd> textValues=new ArrayList<>();
	private JSONObject json;
	private String logId;
	private int resultCount;
	
	public TextResponseForBd(JSONObject json) {
		this.json=json;
		this.logId=json.optString("log_id");
		this.resultCount=json.optInt("words_result_num", 0);
		this.fill();
	}
	
	private void fill(){
		if (this.resultCount > 0) {
			JSONArray ret=json.optJSONArray("words_result");
			if (ret.size() > 0) {
				for (Object object : ret) {
					JSONObject obj=JSONObject.fromObject(object);
					TextValueForBd tv=new TextValueForBd();
					tv.setValue(obj.optString("words", ""));
					JSONObject location=obj.optJSONObject("location");
					tv.setX(location.optDouble("left", 0));
					tv.setY(location.optDouble("top", 0));
					tv.setW(location.optDouble("width", 0));
					tv.setH(location.optDouble("height", 0));
					textValues.add(tv);
				}
			}
			// 文字识别，排序
			textValues.sort((t1, t2)->{
				double fh = this.maxFontHeight() / 2;
				if (t1.getY() > t2.getY()) {
					if (jdz(t1.getY() - t2.getY()) < fh)
						return sortX(t1, t2);
					else
						return 1;
				} else if (t1.getY() == t2.getY()) {
					return sortX(t1, t2);
				} else {
					if (jdz(t1.getY() - t2.getY()) < fh)
						return sortX(t1, t2);
					else
						return -1;
				}
			});
		}
	}
	
	// 排序X坐标
	private int sortX(TextValueForBd t1, TextValueForBd t2){
		if (t1.getX() == t2.getX())
			return 0;
		else if (t1.getX() > t2.getX())
			return 1;
		else
			return -1;
	}
	
	// 绝对值
	private double jdz(double xxx){
		if (xxx < 0)
			return -xxx;
		else
			return xxx;
	}
	
	// 最大
	private double max(double x, double y){
		return x > y ? x : y;
	}
	
	/**
	 * 获取平均字体高度
	 */
	private double avgFontHeight(){
		if(!VeaUtil.isNullEmpty(textValues)){
			return textValues.stream().mapToDouble((v)->v.getH()).sum() / textValues.size();
		}else{
			return 0;
		}
	}
	
	/**
	 * 获取最大字体高度 
	 */
	private double maxFontHeight(){
		if(!VeaUtil.isNullEmpty(textValues)){
			return textValues.stream().mapToDouble((v)->v.getH()).max().orElse(0);
		}else{
			return 0;
		}
	}

	/**获取text for html*/
	public String getTextHtml(){
		double avgFontHeight = this.avgFontHeight();
		if (!VeaUtil.isNullEmpty(textValues)) {
			StringBuilder sb = new StringBuilder();
			double y = textValues.get(0).getY();
			for (TextValueForBd tv : textValues) {
				if (tv.getY() > y + avgFontHeight) {
					y = tv.getY();
					sb.append("<br/>");
				}
				sb.append(tv.value).append("&nbsp;");
			}
			return sb.toString();
		} else if (this.resultCount < 1) {
			return "识别失败！";
		} else {
			return "";
		}
	}
	
	/**获取text，没有换行*/
	public String getText(){
		if (!VeaUtil.isNullEmpty(textValues)) {
			StringBuilder sb = new StringBuilder();
			for (TextValueForBd tv : textValues) {
				sb.append(tv.value).append(" ");
			}
			return sb.toString();
		} else if (this.resultCount < 1) {
			return "识别失败！";
		} else {
			return "";
		}
	}
	
	/**获取text，有换行\n*/
	public String getTextStr(){
		double avgFontHeight = this.avgFontHeight();
		if (!VeaUtil.isNullEmpty(textValues)) {
			StringBuilder sb = new StringBuilder();
			double y = textValues.get(0).getY();
			for (TextValueForBd tv : textValues) {
				if (tv.getY() > y + avgFontHeight) {
					y = tv.getY();
					sb.append("\n");
				}
				sb.append(tv.getValue()).append(" ");
			}
			return sb.toString();
		} else if (this.resultCount < 1) {
			return "识别失败！";
		} else {
			return "";
		}
	}
	
	public String getRequestId() {
		return logId;
	}
	
	public boolean isSuccess() {
		return this.resultCount > 0;
	}
	
	public class TextValueForBd{
		private double x;
		private double y;
		private double w;
		private double h;
		private String value;
		
		public double getX() {
			return x;
		}
		public void setX(double x) {
			this.x = x;
		}
		public double getY() {
			return y;
		}
		public void setY(double y) {
			this.y = y;
		}
		public double getW() {
			return w;
		}
		public void setW(double w) {
			this.w = w;
		}
		public double getH() {
			return h;
		}
		public void setH(double h) {
			this.h = h;
		}
		public String getValue() {
			return value;
		}
		public void setValue(String value) {
			this.value = value;
		}
	}
}
