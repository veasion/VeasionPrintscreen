package cn.veasion.main;

import java.awt.AWTException;
import java.awt.Cursor;
import java.awt.Graphics;
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

import javax.imageio.ImageIO;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.UIManager;

import cn.veasion.tools.FileFilter;
import cn.veasion.tools.MouseTransferable;
import cn.veasion.tools.Tools;
import cn.veasion.util.Rect;
import cn.veasion.util.StaticValue;
import cn.veasion.util.face.ImageOperate;
import cn.veasion.util.face.ImageTextBean;

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

	private BufferedImage imageCache;// 截图缓存对象

	// 截图帮助工具
	private Rect r = new Rect(this);// 矩形框对象

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
				g.drawImage(imageCache, 0, 0, screenWidth, screenHeight, null);// 绘制截图
				r.drawRect(g);// 绘制矩形
			}
		});

		this.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				r.mousePressed(e);// 传递事件给选区
				if (e.getClickCount() == 2) {
					copyInShearPlate();// 复制到剪切板
				}
			}
			public void mouseReleased(MouseEvent e) {// 鼠标按键弹起
				r.mouseReleased(e);
			}
		});

		this.addMouseMotionListener(new MouseMotionListener() {
			public void mouseDragged(MouseEvent e) {
				r.mouseDragged(e);
				repaint();// 拖拽时必须重绘,如果是局部重绘，就会出现影子显现,只有牺牲效率了
			}
			public void mouseMoved(MouseEvent e) {
				r.mouseMoved(e);// 矩形选区鼠标移动事件
			}
		});

		this.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == 27) {// ESC键
					finishAndinitialization();// 初始化
				}
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
				System.err.println("save failed! ");
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
		return imageCache.getSubimage(re.x, re.y, re.width, re.height);
	}
	
	/**
	 * 图片自适应设备 
	 */
	public void fillImageForDevice(){
		Rectangle re = r.getRect();
		Image img=getScreenImage();
		int width=StaticValue.deviceWidth;
		if (re.width < width) {
			BufferedImage buff=new BufferedImage(width, re.height, BufferedImage.TYPE_INT_RGB);
			Graphics g=buff.getGraphics();
			g.drawImage(img, 0, 0, re.width, re.height, null);
			g.setColor(StaticValue.deviceColor);
			g.fillRect(re.width, 0, width-re.width, re.height);
			img=(Image)buff;
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
			g.setColor(StaticValue.deviceColor);
			if(re.width<48){
				g.fillRect(re.width, 0, 50-re.width, re.height);
			}
			if(re.height<48){
				g.fillRect(0, re.height, re.width < 48 ? 50 : re.width, 50 - re.height);
			}
			img=(Image)buff;
		} else if (re.width > 800 || re.height > 800){
			Tools.clipboard.setContents(new StringSelection("识别失败：图片不能超过800*800"), null);
			finishAndinitialization();
			return;
		}
		final Image imgTemp=img;
		Thread t=new Thread(()->{
			try{
				ImageOperate imgOpe=new ImageOperate(StaticValue.faceApiKey, StaticValue.faceApiSecret);
				Tools.clipboard.setContents(new StringSelection("正在识别.."), null);
				ImageTextBean textBean=imgOpe.textRecognition(imgTemp);
				String text=textBean.getTextStr();
				Tools.clipboard.setContents(new StringSelection(text), null);
			}catch(Exception e){
				e.printStackTrace();
			}
		});
		t.start();
		
		/*
		try {
			t.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}*/
		
		finishAndinitialization();
	}
	
}