package cn.veasion.util.ocr;

import java.util.List;

import cn.veasion.util.VeaUtil;

/**
 * OCR 识别后的文本结果排序排版
 * 
 * @author zhuowei.luo
 */
public class OcrTextResult {
	
	/**
	 * 等间距排版 
	 */
	public static final int TYPESETTING_EQUALLY=0;
	
	/**
	 * 忽略垂直间距排版
	 */
	public static final int TYPESETTING_IGNORE_Y=1;
	
	/**
	 * 忽略水平间距排版 
	 */
	public static final int TYPESETTING_IGNORE_X=2;
	
	/**
	 * 缩小间距排版
	 * 
	 * @see 不管间距有多大都只看成一个单位间距
	 */
	public static final int TYPESETTING_X_Y=3;
	
	/**
	 * 不排版 
	 */
	public static final int TYPESETTING_NULL=4;
	
	private List<TextValue> textValues;
	private int typesetting;
	private boolean isSuccess;
	private String result;
	
	private Double avgFontHeight;
	private Double maxFontHeight;
	
	private final String newline="\n";
	private final String space="  ";
	
	public OcrTextResult(OcrTextBean textBean, int typesetting){
		this.textValues = textBean.getTextValues();
		this.typesetting = typesetting;
		this.isSuccess = textBean.isSuccess();
		this.result = textBean.errorMessage();
		if(this.isSuccess){
			this.init();
		}
	}
	
	/**
	 * 文字识别结果初始化 
	 */
	private void init(){
		this.sort(); // 识别结果排序
		this.typesetting(); // 排序结果排版
	}
	
	/**
	 * 识别结果排序 
	 */
	private void sort(){
		// 文字识别，排序
		textValues.sort((t1, t2)->{
			double fh = this.maxFontHeight() / 2;
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
	
	/**
	 * 识别结果排版 
	 */
	public void typesetting() {
		double avgFontHeight = this.avgFontHeight();
		double minOcrTextX = this.minOcrTextX();
		if (!VeaUtil.isNullEmpty(textValues)) {
			StringBuilder sb = new StringBuilder();
			double y = textValues.get(0).getY();
			double topRightX = minOcrTextX;
			for (int i = 0, len = textValues.size(); i < len; i++) {
				TextValue tv = textValues.get(i);
				// 文字垂直间隔排版 （文本换行）
				if (tv.getY() > y + avgFontHeight) {
					int h = 0;
					if (TYPESETTING_IGNORE_Y == typesetting || TYPESETTING_NULL == typesetting) {
						h = 0;
					}else if(TYPESETTING_X_Y == typesetting){
						h = 1;
					}else{
						h = (int) Math.round((tv.getY() - y) / avgFontHeight);
					}
					for (int j = 0; j < h; j++) {
						sb.append(newline);
					}
					y = tv.getY();
					topRightX = minOcrTextX;
				}
				// 文字水平间隔排版 （追加空格）
				if (tv.getX() - topRightX > avgFontHeight) {
					int size = 0;
					if (TYPESETTING_IGNORE_X == typesetting || TYPESETTING_NULL == typesetting) {
						size = 0;
					}else if(TYPESETTING_X_Y == typesetting){
						size = 1;
					}else{
						size = (int) Math.round((tv.getX() - topRightX) / avgFontHeight);
					}
					for (int j = 0; j < size; j++) {
						sb.append(space);
					}
				}
				topRightX = tv.getX() + tv.getW();
				sb.append(tv.getValue());
			}
			this.result = sb.toString();
		} else {
			this.result = "";
		}
	}
	
	/**
	 * 根据X坐标排序 
	 */
	private int sortX(TextValue t1, TextValue t2){
		if (t1.getX() == t2.getX())
			return 0;
		else if (t1.getX() > t2.getX())
			return 1;
		else
			return -1;
	}
	
	/**
	 * 获取OCR平均字体高度
	 */
	private double avgFontHeight(){
		if (this.avgFontHeight == null) {
			if (!VeaUtil.isNullEmpty(textValues)) {
				return (this.avgFontHeight = textValues.stream()
						.mapToDouble((v) -> v.getH()).average().orElse(16));
			} else {
				return 16;
			}
		} else {
			return avgFontHeight;
		}
	}
	
	/**
	 * 获取OCR最小X坐标 
	 */
	private double minOcrTextX() {
		return textValues.stream().mapToDouble((v) -> v.getX()).min().orElse(0);
	}
	
	/**
	 * 获取OCR最大字体高度 
	 */
	private double maxFontHeight(){
		if(this.maxFontHeight == null){
			if(!VeaUtil.isNullEmpty(textValues)){
				return (this.maxFontHeight = textValues.stream()
						.mapToDouble((v)->v.getH()).max().orElse(16));
			}else{
				return 16;
			}
		}else{
			return this.maxFontHeight;
		}
	}
	
	/**
	 * 获取OCR后的HTML结果 
	 */
	public String getResultHtml() {
		return this.result != null ? 
				this.result
				.replace("\r\n", newline)
				.replace(newline, "<br>")
				.replace(space, "&nbsp;") : null;
	}
	
	/**
	 * 获取OCR文本结果 
	 */
	public String getResultTest() {
		return this.result;
	}
	
	/**
	 * 获取文本平均字体高度 
	 */
	public Double getAvgFontHeight() {
		return avgFontHeight;
	}
	
	/**
	 * 获取文本最大字体高度 
	 */
	public Double getMaxFontHeight() {
		return maxFontHeight;
	}
}
