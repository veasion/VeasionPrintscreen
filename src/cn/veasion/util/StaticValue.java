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
	 * Name 
	 */
	public static final String name="伟神";
	
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
	 * 图片旋转统计范围 
	 */
	public static int rangeValue=3;
	
	/**
	 * 文字识别模式，0 前台，1 后台
	 */
	public static int ocrModel = 1;
	
	/**
	 * 文字识别引擎，0 face++，1 百度云
	 */
	public static int ocrEngine = 0;
	
	/**
	 * Face
	 */
	public static String faceApiKey="rGhXZjSqWLOluq04axCW80jCm21I-j32";
    public static String faceApiSecret="imOWbNz3RUJ8GcIepLdVvxvoFrJsS9e-";
    
    /**
     * 百度云 
     */
    public static String baiduAppId="";
    public static String baiduApiKey="";
    public static String baiduSecretKey="";
    
    
    public static void write(){
    	// 写入配置
    	Map<String, Object> map=new HashMap<>();
    	map.put("printKey1", printKey1);
    	map.put("printKey2", printKey2);
    	map.put("deviceWidth", deviceWidth);
    	map.put("deviceBgColor", deviceBgColor==Color.black ? "黑色" : "白色");
    	map.put("ocrEngine", ocrEngine);
    	map.put("ocrModel", ocrModel);
    	map.put("faceApiKey", faceApiKey);
    	map.put("faceApiSecret", faceApiSecret);
    	map.put("baiduAppId", baiduAppId);
    	map.put("baiduApiKey", baiduApiKey);
    	map.put("baiduSecretKey", baiduSecretKey);
    	ConfigUtil.write(map);
    }
    
    static{
    	// 读取配置
    	printKey1=VeaUtil.valueOfInt(ConfigUtil.getProperty("printKey1", String.valueOf(printKey1)), printKey1);
    	printKey2=VeaUtil.valueOfInt(ConfigUtil.getProperty("printKey2", String.valueOf(printKey2)), printKey2);
    	deviceWidth=VeaUtil.valueOfInt(ConfigUtil.getProperty("deviceWidth", String.valueOf(deviceWidth)), deviceWidth);
    	deviceBgColor="黑色".equals(ConfigUtil.getProperty("deviceBgColor", "黑色")) ? Color.black : Color.white;
    	ocrEngine=VeaUtil.valueOfInt(ConfigUtil.getProperty("ocrEngine", String.valueOf(ocrEngine)), ocrEngine);
    	ocrModel=VeaUtil.valueOfInt(ConfigUtil.getProperty("ocrModel", String.valueOf(ocrModel)), ocrModel);
    	faceApiKey=ConfigUtil.getProperty("faceApiKey", faceApiKey);
    	faceApiSecret=ConfigUtil.getProperty("faceApiSecret", faceApiSecret);
    	baiduAppId=ConfigUtil.getProperty("baiduAppId", baiduAppId);
    	baiduApiKey=ConfigUtil.getProperty("baiduApiKey", baiduApiKey);
    	baiduSecretKey=ConfigUtil.getProperty("baiduSecretKey", baiduSecretKey);
    }
    
}
