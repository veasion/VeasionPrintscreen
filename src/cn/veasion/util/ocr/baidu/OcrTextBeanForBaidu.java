package cn.veasion.util.ocr.baidu;

import java.util.ArrayList;
import java.util.List;
import cn.veasion.util.ocr.OcrTextBean;
import cn.veasion.util.ocr.TextValue;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 百度云文字识别解析
 * 
 * @author zhuowei.luo
 */
public class OcrTextBeanForBaidu implements OcrTextBean {

	private List<TextValue> textValues;
	private JSONObject json;
	private String logId;
	private int resultCount;

	private boolean isSuccess;
	private String errorMessage;

	public OcrTextBeanForBaidu(JSONObject json) {
		this.json = json;
		this.textValues = new ArrayList<>();
		this.logId = json.optString("log_id");
		this.resultCount = json.optInt("words_result_num", 0);
		this.isSuccess = this.resultCount > 0;
		this.fill();
	}

	private void fill() {
		if (this.resultCount > 0) {
			try {
				JSONArray ret = json.optJSONArray("words_result");
				if (ret.size() > 0) {
					for (Object object : ret) {
						JSONObject obj = JSONObject.fromObject(object);
						TextValue tv = new TextValue();
						tv.setValue(obj.optString("words", ""));
						JSONObject location = obj.optJSONObject("location");
						tv.setX(location.optDouble("left", 0));
						tv.setY(location.optDouble("top", 0));
						tv.setW(location.optDouble("width", 0));
						tv.setH(location.optDouble("height", 0));
						this.textValues.add(tv);
					}
				}
			} catch (Exception e) {
				this.isSuccess = false;
				this.errorMessage = "json解析异常：" + e.getMessage();
				// e.printStackTrace();
			}
		}
	}

	public String getLogId() {
		return this.logId;
	}

	@Override
	public boolean isSuccess() {
		return this.isSuccess;
	}

	@Override
	public String errorMessage() {
		return this.errorMessage;
	}

	@Override
	public List<TextValue> getTextValues() {
		return this.textValues;
	}

}
