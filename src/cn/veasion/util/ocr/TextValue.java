package cn.veasion.util.ocr;

/**
 * 文字识别内容和位置 
 * 
 * @author zhuowei.luo
 */
public class TextValue {
	
	public double x;
	public double y;
	public double w;
	public double h;
	public String value;
	
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
