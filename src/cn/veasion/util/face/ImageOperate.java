package cn.veasion.util.face;

import java.awt.Image;
import java.util.HashMap;

import cn.veasion.util.ImageUtil;

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
		// 返回Base64编码过的字节数组字符串
		map.put("image_base64", ImageUtil.imageToBase64(image, null));
		//System.out.println(map.get("image_base64"));
		return new ImageTextBean(HttpRequest.post(TEXT_RE, map, fileMap));
	}
	
}
