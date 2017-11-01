package cn.veasion.util.ocr;

import java.util.List;

/**
 * 文字识别结果接口
 * 
 * @author zhuowei.luo
 */
public interface OcrTextBean {
	
	public boolean isSuccess();
	
	public String errorMessage();
	
	public List<TextValue> getTextValues();
	
}
