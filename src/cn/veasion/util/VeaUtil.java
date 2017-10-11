package cn.veasion.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 帮助类.
 * 
 * @author zhuowei.luo
 */
public class VeaUtil {
	
	private static final String DATE_DEFAULT_PATTERN="yyyy-MM-dd HH:mm:ss";
	
	/**
	 * 字符数组是否有空
	 * Null or Empty 
	 */
	public static boolean hasNullEmpty(String ...str){
		if(isNullEmpty(str)) 
			return true;
		for (String s : str)
			if(isNullEmpty(s))
				return true;
		return false;
	}
	
	/**
	 * double保留x位小数
	 */
	public static double formatDouble(double d, int x) {
		if (x < 0)
			return d;
		else if (x == 0)
			return (double) ((int) d);
		else {
			StringBuilder sb = new StringBuilder("#.");
			if (x > 10) x = 10;
			for (int i = 0; i < x; i++) sb.append("0");
			return Double.parseDouble(
					new java.text.DecimalFormat(sb.toString()).format(d));
		}
	}
	
	/**
	 * Object转换为int
	 */
	public static int valueOfInt(Object obj,int defaultVal){
		if(isNullEmpty(obj))return defaultVal;
		try{
			return Integer.valueOf(String.valueOf(obj));
		}catch(Exception e){
			System.err.println(obj+"非数字！");
			return defaultVal;
		}
	}
	
	/**
	 * Object[]转List<?> 
	 */
	@SafeVarargs
	public static <T> List<T> asList(T ...obj){
		if(isNullEmpty(obj))
			return new ArrayList<>();
		else
			return new ArrayList<>(java.util.Arrays.asList(obj));
	}
	
	/**
	 *  List<?>去重复和Null
	 */
	public static <T> List<T> Arraydistinct(final List<T> list){
		if(isNullEmpty(list))
			return list;
		List<T> t=new ArrayList<>();
		T o=null;
		for (int i = 0, len=list.size(); i < len; i++) {
			o=list.get(i);
			if(o!=null && !t.contains(o))
				t.add(o);
		}
		return t;
	}
	
	/**
	 *  格式化时间，Locale=中国
	 *  
	 *  @param date 可空，默认当前时间
	 *  @param pattern 可空，默认yyyy-MM-dd HH:mm:ss
	 */
	public static String formatDate(java.util.Date date,String pattern){
		if(date==null) date=new java.util.Date();
		if(isNullEmpty(pattern)) pattern=DATE_DEFAULT_PATTERN;
		return new java.text.SimpleDateFormat(pattern, java.util.Locale.CHINA).format(date);
	}
	
	/**
	 * 去除字符串中的空格、回车、换行符、制表符
	 */
	public static String replaceBlank(String str) {
		String dest = "";
		if (!isNullEmpty(str)) {
			java.util.regex.Pattern p = 
					java.util.regex.Pattern.compile("\\s*|\t|\r|\n");
			java.util.regex.Matcher m = p.matcher(str);
			dest = m.replaceAll("");
		}
		return dest;
	}
	
	/**
	 * Object是否为空. 
	 */
	public static boolean isNullEmpty(Object obj){
		if(obj==null)return true;
		if(obj instanceof Object[]){
			return ((Object[])obj).length<1;
		}
		else if(obj instanceof List<?>){
			return ((Collection<?>)obj).isEmpty();
		}
		else if(obj instanceof Map<?, ?>){
			return ((Map<?,?>)obj).isEmpty();
		}
		else if(obj instanceof String){
			return "".equals(String.valueOf(obj).trim());
		}
		else return false;
	}
	
	/**
	 * 随机数字
	 * 
	 * @since 随机start-end中的任意数字，包含start和end
	 */
	public static int random(int start, int end){
		return new Random().nextInt(end - start + 1) + start;
	}
	
}
