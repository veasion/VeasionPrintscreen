package cn.veasion.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class HoughTest {

	public static void main(String[] args) throws IOException {
		String path="C:\\Users\\zhuowei.luo\\Desktop\\";
		BufferedImage src = ImageIO.read(new File(path+"abc2.png")); 
        BufferedImage des = ImageUtil.imageHough(src);
        ImageIO.write(des, "png", new File(path+"abcd2.png"));
		
	}
}
