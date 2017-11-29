package cn.veasion.tools;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;

/**
 * 工具类
 */
public class Tools {
	
	public static final int  SCREEN_WIDTH  = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
	public static final int  SCREEN_HEIGHT = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
	
	//系统剪切板
	public static Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
	
	public static final Rectangle SCREEN_RECTANGLE = new Rectangle(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
	
	public static final Color RECT_COLOR = new Color(0, 174, 255);
	
}
