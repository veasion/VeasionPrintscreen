package cn.veasion.util;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import cn.veasion.util.hough.Gerade;
import cn.veasion.util.hough.Hough;
import cn.veasion.util.hough.HoughUtils;
import sun.misc.BASE64Encoder;

/**
 * 图片操作类
 * 
 * @author zhuowei.luo
 */
public class ImageUtil {

	/** 
     * 等比例压缩图片
     */  
    public static BufferedImage zoomImage(BufferedImage resource, float resize) {
		return zoomImage(resource, (int) (resource.getWidth() * resize), (int) (resource.getHeight() * resize));
    }
    
    /**
     * 固定宽高压缩图片
     */
    public static BufferedImage zoomImage(BufferedImage resource, int toWidth, int toHeight){
		BufferedImage result = null;
		try {
			result = new BufferedImage(toWidth, toHeight, BufferedImage.TYPE_INT_RGB);
			result.getGraphics().drawImage(resource.getScaledInstance(toWidth, toHeight, java.awt.Image.SCALE_SMOOTH), 0, 0, null);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("创建缩略图发生异常" + e.getMessage());
		}
		return result == null ? resource : result;
    }
    
    /**
     * 图片转字节 
     */
    public static byte[] imageToBytes(Image image, String format) throws IOException {
		BufferedImage bImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		Graphics bg = bImage.getGraphics();
		bg.drawImage(image, 0, 0, null);
		bg.dispose();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ImageIO.write(bImage, format==null ? "png" : format, out);
		return out.toByteArray();
	}
    
    /**
     * 图片转Base64 
     */
    public static String imageToBase64(Image image, String format) throws IOException{
    	return new BASE64Encoder().encode(ImageUtil.imageToBytes(image, null));
    }
    
    /**
     * 图片旋转 
     * 
     * @param src 原图片
     * @param angel 角度
     * @see 缺点：失真比较大
     */
	public static BufferedImage Rotate(Image src, int angel) {
		int src_width = src.getWidth(null);
		int src_height = src.getHeight(null);
		// 重新计算图片旋转大小
		Rectangle rect_des = CalcRotatedSize(new Rectangle(new Dimension(src_width, src_height)), angel);

		BufferedImage res = null;
		res = new BufferedImage(rect_des.width, rect_des.height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = res.createGraphics();
		// 旋转
		g2.translate((rect_des.width - src_width) / 2, (rect_des.height - src_height) / 2);
		g2.rotate(Math.toRadians(angel), src_width / 2, src_height / 2);

		g2.drawImage(src, null, null);
		return res;
	}
	
	/**
	 * 计算矩形旋转后的大小
	 */
	public static Rectangle CalcRotatedSize(Rectangle src, int angel) {
		if (angel >= 90) {
			if (angel / 90 % 2 == 1) {
				int temp = src.height;
				src.height = src.width;
				src.width = temp;
			}
			angel = angel % 90;
		}
		double r = Math.sqrt(src.height * src.height + src.width * src.width) / 2;
		double len = 2 * Math.sin(Math.toRadians(angel) / 2) * r;
		double angel_alpha = (Math.PI - Math.toRadians(angel)) / 2;
		double angel_dalta_width = Math.atan((double) src.height / src.width);
		double angel_dalta_height = Math.atan((double) src.width / src.height);

		int len_dalta_width = (int) (len * Math.cos(Math.PI - angel_alpha - angel_dalta_width));
		int len_dalta_height = (int) (len * Math.cos(Math.PI - angel_alpha - angel_dalta_height));
		int des_width = src.width + len_dalta_width * 2;
		int des_height = src.height + len_dalta_height * 2;
		return new java.awt.Rectangle(new Dimension(des_width, des_height));
	}
	
	/**
	 * 图片转RGB 
	 */
	public static int[][][] ImageToRGBArray(BufferedImage source) {
		int[][][] rgbArray = new int[source.getHeight()][source.getWidth()][3];
		// System.out.println("图片：" + source.getHeight() + ", " + source.getWidth());
		for (int r = 0; r < source.getHeight(); r++)
			for (int c = 0; c < source.getWidth(); c++) {
				rgbArray[r][c][0] = (source.getRGB(c, r) >> 16) & 255;
				rgbArray[r][c][1] = (source.getRGB(c, r) >> 8) & 255;
				rgbArray[r][c][2] = (source.getRGB(c, r) >> 0) & 255;
			}
		return rgbArray;
	}
	
	/**
	 * 图片矫正 
	 */
	public static BufferedImage imageHough(BufferedImage source){
		List<Gerade> angles=new Hough(source).lineRange();
		double angel=0;
		double avg=HoughUtils.avgAngle(angles);
		double range=HoughUtils.avgRangeAngle(angles, StaticValue.rangeValue);
		if(Math.round(avg-range)>StaticValue.rangeValue*2){
			angel=avg;
		}
		// TODO 失真太大，反向旋转
		return Rotate(source, new Long(Math.round(360-angel)).intValue());
	}
	
}
