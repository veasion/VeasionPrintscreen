package cn.veasion.main;

import java.awt.AWTException;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.UIManager;
import cn.veasion.tools.MouseTransferable;
import cn.veasion.tools.Rect;
import cn.veasion.tools.SelectRect;
import cn.veasion.tools.Tools;
import cn.veasion.util.StaticValue;
import cn.veasion.util.VeaUtil;

/**
 * JDialog截图窗体
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
	
	private boolean overDrawImage=false;
	
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
						e.printStackTrace();
					}
				}
				// 绘制截图
				g.drawImage(imageCache, 0, 0, screenWidth, screenHeight, null);
				// 绘制矩形
				r.drawRect(g);
				// 绘制选中小矩形
				if(r.getMenu() != null){
					sr.drawRect(g);
				}
			}
		});
		
		this.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				int mouseKeyCode = e.getButton();
				if(r.getMenu() != null && sr.getRect().contains(e.getX(), e.getY())){
					sr.mousePressed(e);
				}else{
					if(!(StaticValue.psFixed && !VeaUtil.isNullEmpty(sr.getOperations()))){
						r.mousePressed(e);
					}
					if(r.getMenu() != null && mouseKeyCode==3){
						sr.mousePressed(e);
					}
				}
				if (e.getClickCount() == 2) {
					copyInShearPlate();// 复制到剪切板
				}
			}
			public void mouseReleased(MouseEvent e) {// 鼠标按键弹起
				r.mouseReleased(e);
				if(r.getMenu() != null){
					sr.mouseReleased(e);
				}
			}
		});

		this.addMouseMotionListener(new MouseMotionListener() {
			public void mouseDragged(MouseEvent e) {
				if(!(StaticValue.psFixed && !VeaUtil.isNullEmpty(sr.getOperations()))){
					r.mouseDragged(e);
				}
				if(r.getMenu() != null){
					sr.mouseDragged(e);
				}
				repaint();
			}
			public void mouseMoved(MouseEvent e) {
				if(!(StaticValue.psFixed && !VeaUtil.isNullEmpty(sr.getOperations()))){
					r.mouseMoved(e);// 矩形选区鼠标移动事件
				}
			}
		});

		this.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == 27) {// ESC键
					finishAndinitialization();
				} else if(e.getKeyCode() == 10){// 回车
					copyInShearPlate();
				} else if(e.getKeyCode() == 90){// Z 撤销
					if(!VeaUtil.isNullEmpty(sr.getOperations())){
						sr.getOperations().remove(sr.getOperations().size()-1);
						repaint();
					}
				} else if(e.getKeyCode() == 107){// +
					r.getMenu().houghImage();
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
		if(r.getMenu()!=null){
			if(r.getMenu().getOcrjp()!=null){
				this.getLayeredPane().remove(r.getMenu().getOcrjp());
			}
			this.getLayeredPane().remove(r.getMenu());
			r.setMenu(null);
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
		// 拷贝到剪切板里
		Tools.clipboard.setContents(new MouseTransferable(getScreenImage()), null);
		// 回收资源
		finishAndinitialization();
	}
	
	/**
	 * 获取截图内容
	 * 
	 * @return Image 返回矩形选区里的图像信息
	 */
	public Image getScreenImage() {
		Rectangle re = r.getRect();
		// 获取截图操作过图片
		BufferedImage image=this.getDrawImage();
		// 获取正在操作的图片
		image = image.getSubimage(re.x, re.y, re.width, re.height);
		return image;
	}
	
	public void clearSelectRect(){
		sr=new SelectRect(this, r);
	}
	
	/**
	 * 获取绘画后的全屏截图 
	 */
	public BufferedImage getDrawImage(){
		this.overDrawImage=true;
		BufferedImage image=new BufferedImage(Tools.SCREEN_WIDTH, Tools.SCREEN_HEIGHT, BufferedImage.TYPE_3BYTE_BGR);
		// 获取截图操作过图片
		this.getContentPane().paint(image.getGraphics());
		this.overDrawImage=false;
		// this.repaint();
		return image;
	}
	
	public boolean isOverDrawImage() {
		return overDrawImage;
	}
}
