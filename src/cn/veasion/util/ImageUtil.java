package cn.veasion.util;

import java.awt.image.BufferedImage;

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
    
}
