package cn.veasion.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Config配置Util类.
 * 
 * @author zhuowei.luo
 */
public class ConfigUtil {
	
	public final static String configPath=System.getProperty("user.home")+File.separator+"veasionPrintscreen.properties";
	
	private static Properties p;
	
	private static Map<String, String> cache=new HashMap<>();
	
	static{
		try {
			File f=new File(configPath);
			boolean first=false;
			if(!f.exists()){
				f.createNewFile();
				first=true;
			}
			p=new Properties();
			p.load(new FileInputStream(f));
			if(first){
				StaticValue.write();
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
	
	/**
	 * 设置值
	 */
	@SuppressWarnings("deprecation")
	public static String setProperty(String key, String value){
		if(p==null) return null;
		Object obj=p.setProperty(key, value);
		try {
			p.save(new FileOutputStream(configPath), " Veasion\r\n https://github.com/veasion\r\n QQ:1456065030");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return obj==null ? null : String.valueOf(obj);
	}
	
	/**
	 * 根据key获取值. 
	 */
	public static String getProperty(String key){
		if(!cache.isEmpty() && cache.containsKey(key)){
			return cache.get(key);
		}
		if(p!=null){
			try {
				String value=p.getProperty(key).trim();
				if (cache.size() > 200) {
					cache.clear();
				}
				cache.put(key, value);
				return value;
			} catch (Exception e) {
				System.out.println("读取"+key+"异常！");
				return null;
			}
		}else{
			System.err.println(configPath+"，加载异常！");
			return null;
		}
	}
	
	/**
	 * 根据key获取值. 
	 * 
	 * @param defaultValue 如果没有或报错就返回该默认值.
	 */
	public static String getProperty(String key, String defaultValue){
		String value=getProperty(key);
		return value != null ? value : defaultValue;
	}
	
	/**
	 * 根据key获取值. 
	 * 
	 * @param defaultValue 如果没有或报错就返回该默认值.
	 */
	public static int getProperty(String key, int defaultValue){
		return VeaUtil.valueOfInt(getProperty(key), defaultValue);
	}
	
	/**
	 * 根据key获取boolean值
	 */
	public static boolean getPropertyBoolean(String key, boolean defaultVal){
		String val=getProperty(key,"none").trim();
		if("true".equalsIgnoreCase(val))
			return true;
		else if("none".equals(val))
			return defaultVal;
		else
			return false;
	}
	
	@SuppressWarnings("deprecation")
	public static void write(Map<String, Object> map){
		if(p==null || map==null) return;
		try {
			map.forEach((k, v)->{
				p.setProperty(k, v!=null ? String.valueOf(v) : "");
			});
			p.save(new FileOutputStream(configPath), " Veasion\r\n https://github.com/veasion\r\n QQ:1456065030");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
}
