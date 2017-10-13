package cn.veasion.main;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.UIManager;

import com.baidu.aip.ocr.AipOcr;

import cn.veasion.tools.FileFilter;
import cn.veasion.tools.MouseTransferable;
import cn.veasion.tools.Tools;
import cn.veasion.util.ImageUtil;
import cn.veasion.util.Rect;
import cn.veasion.util.SelectRect;
import cn.veasion.util.StaticValue;
import cn.veasion.util.VeaUtil;
import cn.veasion.util.baiduyun.TextResponseForBd;
import cn.veasion.util.face.ImageOperate;
import cn.veasion.util.face.ImageTextBean;
import net.sf.json.JSONObject;

/**
 * 截图JDialog窗体,程序主要框架，用于显示Robot截取屏幕图片显示<br>
 * JDialog容器可以解决窗体焦点问题 和 任务栏图标显示问题
 * 
 * @author zhuowei.luo
 */
public class Printscreen extends JDialog {
	
	private static final long serialVersionUID = 1L;

	// 屏幕尺寸
	private int screenWidth = Tools.SCREEN_WIDTH;
	private int screenHeight = Tools.SCREEN_HEIGHT;

	// 截图缓存对象
	private BufferedImage imageCache;

	// 截图矩形框
	private Rect r = new Rect(this);

	// 选中小矩形
	private SelectRect sr=new SelectRect(this, r);
	
	private JFileChooser jc;
	
	static {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Printscreen() {
		this.setLayout(null);
		// 不显示窗体装饰
		this.setUndecorated(true);
		this.setBounds(0, 0, screenWidth, screenHeight);
		
		this.setContentPane(new JPanel() {
			private static final long serialVersionUID = 1L;
			
			public void paint(Graphics g) {
				this.setLayout(null);
				super.paint(g);
				if (imageCache == null) {
					try {
						imageCache = new Robot().createScreenCapture(Tools.SCREEN_RECTANGLE);// 截取屏幕图片
					} catch (AWTException e) {
						System.out.println("Robot class create picture cache failed!");
					}
				}
				// 绘制截图
				g.drawImage(imageCache, 0, 0, screenWidth, screenHeight, null);
				// 绘制矩形
				r.drawRect(g);
				// 绘制选中小矩形
				if(r.menu != null){
					sr.drawRect(g);
				}
			}
		});

		this.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				r.mousePressed(e);// 传递事件给选区
				if(r.menu != null){
					sr.mousePressed(e);
				}
				if (e.getClickCount() == 2) {
					copyInShearPlate();// 复制到剪切板
				}
			}
			public void mouseReleased(MouseEvent e) {// 鼠标按键弹起
				r.mouseReleased(e);
				if(r.menu != null){
					sr.mousePressed(e);
				}
			}
		});

		this.addMouseMotionListener(new MouseMotionListener() {
			public void mouseDragged(MouseEvent e) {
				r.mouseDragged(e);
				if(r.menu != null){
					sr.mouseDragged(e);
				}
				repaint();
			}
			public void mouseMoved(MouseEvent e) {
				r.mouseMoved(e);// 矩形选区鼠标移动事件
			}
		});

		this.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == 27) {// ESC键
					finishAndinitialization();
				}else if(e.getKeyCode() == 90){// Z 撤销
					if(!VeaUtil.isNullEmpty(sr.getRectangles())){
						sr.getRectangles().remove(sr.getRectangles().size()-1);
						repaint();
					}
				}
				sr.keyReleased(e);
			}
		});

	}
	
	/**
	 * 启动截图
	 */
	public void start() {
		this.setAlwaysOnTop(true); // 窗口最前面显示
		this.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));// 设置鼠标为十字架
		this.setVisible(true);
	}

	/**
	 * 初始化，并结束截图
	 */
	public void finishAndinitialization() {
		if(r.menu!=null){
			this.getLayeredPane().remove(r.menu);
			r.menu=null;
		}
		this.dispose();// 遗弃窗体
		imageCache = null;// 清空缓存
		// 重新构建选区
		this.r = new Rect(this);
		this.sr=new SelectRect(this, r);
	}
	
	/**
	 * 复制到剪切板
	 */
	public void copyInShearPlate() {
		Tools.clipboard.setContents(new MouseTransferable(getScreenImage()), null);// 拷贝到剪切板里
		finishAndinitialization();// 回收资源
	}

	/**
	 * 保存文件（自定义文件保存位置方法）
	 */
	public void saveImageFile() {
		this.setAlwaysOnTop(false);// 取消JFrame窗体置顶
		// this.setIconImage(ImageIO.read(PrintScreen.class.getResourceAsStream("veasion_ico.png")));
		jc = new JFileChooser();

		jc.setDialogTitle("保存截图");

		// 保存文件格式
		jc.addChoosableFileFilter(new FileFilter("png", ".png (*.png)"));
		jc.addChoosableFileFilter(new FileFilter("jpg", ".jpg (*.jpg; *.jpeg; *.jpe)"));
		jc.addChoosableFileFilter(new FileFilter("bmp", ".bmp (*.bmp)"));
		jc.addChoosableFileFilter(new FileFilter("gif", ".gif (*.gif)"));

		// 不显示所有文件
		jc.setAcceptAllFileFilterUsed(false);

		// 显示保存文件选择器窗口
		int result = jc.showSaveDialog(this);

		if (result == JFileChooser.APPROVE_OPTION) { // 用户点击了“确定”按钮
			File file = jc.getSelectedFile(); // 获得文件名
			String ends = ((FileFilter) jc.getFileFilter()).getEnds(); // 获得过滤器的扩展名
			File newFile = null;
			if (file.getAbsolutePath().toUpperCase().endsWith(ends.toUpperCase())) { // 如果文件是以选定扩展名结束的，则使用原名
				newFile = file;
			} else {
				newFile = new File(file.getAbsolutePath() + "." + ends);// 否则加上选定的扩展名
			}
			try {
				newFile.getCanonicalPath();// 获取产生异常证明不能保存
				imageCache = (BufferedImage) getScreenImage();
				ImageIO.write(imageCache, ends, newFile);
			} catch (IOException e) {
				System.err.println("保存发生错误! ");
				saveImageFile();
			}
		} else if (result == JFileChooser.CANCEL_OPTION) {// 取消按钮
			jc.setVisible(false);
			return;
		} else if (result == JFileChooser.ERROR_OPTION) {// 错误
			System.out.println("错误");
		}
		finishAndinitialization();// 初始化并结束
	}

	/**
	 * 获取截图内容
	 * 
	 * @return Image 返回矩形选区里的图像信息
	 */
	public Image getScreenImage() {
		Rectangle re = r.getRect();
		BufferedImage images=imageCache.getSubimage(re.x, re.y, re.width, re.height);
		if(!VeaUtil.isNullEmpty(sr.getRectangles())){
			// 清除操作
			BufferedImage buff=new BufferedImage(re.width, re.height, BufferedImage.TYPE_INT_RGB);
			Graphics g=buff.getGraphics();
			g.drawImage(images, 0, 0, null);
			g.setColor(StaticValue.deviceBgColor);
			for (Rectangle rec : sr.getRectangles()) {
				g.fillRect((int)(rec.getX()-re.getX()), (int)(rec.getY()-re.getY()), (int)rec.getWidth(), (int)rec.getHeight());
			}
			return buff;
		}else{
			return images;
		}
	}
	
	/**
	 * 图片自适应设备 
	 */
	public void fillImageForDevice(){
		Rectangle re = r.getRect();
		Image img=getScreenImage();
		int width=StaticValue.deviceWidth;
		if (re.width < width) {
			// 填充
			BufferedImage buff=new BufferedImage(width, re.height, BufferedImage.TYPE_INT_RGB);
			Graphics g=buff.getGraphics();
			g.drawImage(img, 0, 0, re.width, re.height, null);
			g.setColor(StaticValue.deviceBgColor);
			g.fillRect(re.width, 0, width-re.width, re.height);
			img=(Image)buff;
		}else{
			// 压缩
			img = ImageUtil.zoomImage((BufferedImage) img, (float) StaticValue.deviceWidth / re.width);
		}
		// 拷贝到剪切板里
		Tools.clipboard.setContents(new MouseTransferable(img), null);
		finishAndinitialization();// 回收资源
	}
	
	/**
	 * 画图工具 
	 */
	public void mspaintImage(){
		try {
			Tools.clipboard.setContents(new MouseTransferable(getScreenImage()), null);// 拷贝到剪切板里
			Runtime.getRuntime().exec("mspaint");
			Thread.sleep(500);
			Robot r=new Robot();
			r.keyPress(KeyEvent.VK_CONTROL);
			r.keyPress(KeyEvent.VK_V);
			r.keyRelease(KeyEvent.VK_V);
			r.keyRelease(KeyEvent.VK_CONTROL);
			r.delay(100);
			finishAndinitialization();// 回收资源
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 图片文字识别 
	 */
	public void ocrImage(){
		Rectangle re = r.getRect();
		Image img=getScreenImage();
		if (re.width < 48 || re.height < 48) {
			BufferedImage buff=new BufferedImage(re.width < 48 ? 50 : re.width, re.height < 48 ? 50 : re.height, BufferedImage.TYPE_INT_RGB);
			Graphics g=buff.getGraphics();
			g.drawImage(img, 0, 0, re.width, re.height, null);
			g.setColor(StaticValue.deviceBgColor);
			if(re.width<48){
				g.fillRect(re.width, 0, 50-re.width, re.height);
			}
			if(re.height<48){
				g.fillRect(0, re.height, re.width < 48 ? 50 : re.width, 50 - re.height);
			}
			img=(Image)buff;
		} else if (re.width > 1200 || re.height > 1200){
			Tools.clipboard.setContents(new StringSelection("识别失败：图片不能超过1200*1200"), null);
			finishAndinitialization();
			return;
		} else if (re.width > 800 && re.width < 1200) {
			// 压缩
			img = ImageUtil.zoomImage((BufferedImage) img, (float) 800 / re.width);
		} else if (re.height > 800 && re.height < 1200) {
			// 压缩
			img = ImageUtil.zoomImage((BufferedImage) img, (float) 800 / re.height);
		}
		final Image imgTemp=img;
		Thread t=new Thread(()->{
			try{
				Tools.clipboard.setContents(new StringSelection("正在识别.."), null);
				String text=null;
				if(StaticValue.ocrEngine==1){
					if(VeaUtil.isNullEmpty(StaticValue.baiduAppId)){
						Tools.clipboard.setContents(new StringSelection("您没有权限访问阿里云OCR，请与管理员联系（QQ：1456065030）"), null);
					}else{
						AipOcr client = new AipOcr(StaticValue.baiduAppId, StaticValue.baiduApiKey, StaticValue.baiduSecretKey);
						// 可选：设置网络连接参数
						client.setConnectionTimeoutInMillis(5000);
						client.setSocketTimeoutInMillis(100000);
						byte data[]=ImageUtil.imageToBytes(imgTemp, null);
						TextResponseForBd response=new TextResponseForBd(JSONObject.fromObject(client.general(data, new HashMap<String, String>()).toString()));
						text=response.getTextStr();
					}
				}else{
					ImageOperate imgOpe=new ImageOperate(StaticValue.faceApiKey, StaticValue.faceApiSecret);
					ImageTextBean textBean=imgOpe.textRecognition(imgTemp);
					text=textBean.getTextStr();
				}
				Tools.clipboard.setContents(new StringSelection(text), null);
			}catch(Exception e){
				Tools.clipboard.setContents(new StringSelection("发生错误："+e.getMessage()), null);
				e.printStackTrace();
			}
		});
		t.start();
		
		if(StaticValue.ocrModel==0){
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		finishAndinitialization();
	}
	
	public void clearSelectRect(){
		sr=new SelectRect(this, r);
	}
	
}
