package cn.veasion.util;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import com.melloware.jintellitype.JIntellitype;

/**
 * 静态值
 * 
 * @author zhuowei.luo
 */
public class StaticValue {

	/**
	 * 快捷键 
	 */
	public static int printKey1=JIntellitype.MOD_CONTROL;
	public static int printKey2=(int)'B';
	
	/**
	 * 自适应设备宽度 
	 */
	public static int deviceWidth=630;
	
	/**
	 * 自适应背景颜色
	 */
	public static Color deviceBgColor=Color.white;
	
	/**
	 * 文字识别模式，0 前台，1 后台
	 */
	public static int ocrModel = 1;
	
	/**
	 * Face
	 */
	public static String faceApiKey="rGhXZjSqWLOluq04axCW80jCm21I-j32";
    public static String faceApiSecret="imOWbNz3RUJ8GcIepLdVvxvoFrJsS9e-";
    
    public static void write(){
    	// 写入配置
    	Map<String, Object> map=new HashMap<>();
    	map.put("printKey1", printKey1);
    	map.put("printKey2", printKey2);
    	map.put("deviceWidth", deviceWidth);
    	map.put("deviceBgColor", deviceBgColor==Color.black ? "黑色" : "白色");
    	map.put("ocrModel", ocrModel);
    	map.put("faceApiKey", faceApiKey);
    	map.put("faceApiSecret", faceApiSecret);
    	ConfigUtil.write(map);
    }
    
    static{
    	// 读取配置
    	printKey1=VeaUtil.valueOfInt(ConfigUtil.getProperty("printKey1", String.valueOf(printKey1)), printKey1);
    	printKey2=VeaUtil.valueOfInt(ConfigUtil.getProperty("printKey2", String.valueOf(printKey2)), printKey2);
    	deviceWidth=VeaUtil.valueOfInt(ConfigUtil.getProperty("deviceWidth", String.valueOf(deviceWidth)), deviceWidth);
    	deviceBgColor="黑色".equals(ConfigUtil.getProperty("deviceBgColor", "黑色")) ? Color.black : Color.white;
    	ocrModel=VeaUtil.valueOfInt(ConfigUtil.getProperty("ocrModel", String.valueOf(ocrModel)), ocrModel);
    	faceApiKey=ConfigUtil.getProperty("faceApiKey", faceApiKey);
    	faceApiSecret=ConfigUtil.getProperty("faceApiSecret", faceApiSecret);
    }
    
}
