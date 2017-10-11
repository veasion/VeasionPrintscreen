package cn.veasion.util.face;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import javax.imageio.ImageIO;
import sun.misc.BASE64Encoder;

/**
 *  图片识别接口的封装，使用国际版请忽视这个类
 */
public class ImageOperate {

    public final static String IMAGE_RE = "https://api-cn.faceplusplus.com/imagepp/beta/detectsceneandobject";
    public final static String TEXT_RE = "https://api-cn.faceplusplus.com/imagepp/beta/recognizetext";

    private String apiKey;
    private String apiSecret;
    
    public ImageOperate(String apiKey, String apiSecret){
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
    }
    
    /**
     * 文字识别
     */
	public ImageTextBean textRecognition(Image image) throws Exception {
		HashMap<String, String> map = new HashMap<>();
		HashMap<String, byte[]> fileMap = new HashMap<>();
		map.put("api_key", apiKey);
		map.put("api_secret", apiSecret);
		// 对字节数组Base64编码
		BASE64Encoder encoder = new BASE64Encoder();
		// 返回Base64编码过的字节数组字符串
		map.put("image_base64", encoder.encode(imageToBytes(image, null)));
		//System.out.println(map.get("image_base64"));
		return new ImageTextBean(HttpRequest.post(TEXT_RE, map, fileMap));
	}
	
	private byte[] imageToBytes(Image image, String format) throws IOException {
		BufferedImage bImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		Graphics bg = bImage.getGraphics();
		bg.drawImage(image, 0, 0, null);
		bg.dispose();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ImageIO.write(bImage, format==null ? "png" : format, out);
		return out.toByteArray();
	}
	
}
