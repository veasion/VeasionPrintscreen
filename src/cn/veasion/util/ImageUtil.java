package cn.veasion.util;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

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
    
}
