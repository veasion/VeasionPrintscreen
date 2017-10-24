package cn.veasion.tools;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.baidu.aip.ocr.AipOcr;

import cn.veasion.main.Printscreen;
import cn.veasion.util.ImageUtil;
import cn.veasion.util.StaticValue;
import cn.veasion.util.VeaUtil;
import cn.veasion.util.baiduyun.TextResponseForBd;
import cn.veasion.util.face.ImageOperate;
import cn.veasion.util.face.ImageTextBean;
import net.sf.json.JSONObject;

/**
 * 操作菜单.
 * 
 * @author zhuowei.luo
 */
public class MenuTool extends JPanel{

	private static final long serialVersionUID = 1L;
	
	private Printscreen ps;
	private Rect r;
	
	private JPanel ocrjp;
	private JTextArea ocrArea;
	
	private int ocrWidth=400;
	private int ocrHeight=300;
	
	public MenuTool(Printscreen ps, Rect r){
		this.ps=ps;
		this.r=r;
		this.init();
	}
	
	private void init(){
		GridLayout grid=new GridLayout(1, 0);
		this.setLayout(grid);
		Font f=new Font("华文楷体", 1, 15);
		JLabel label=new JLabel(StaticValue.name, JLabel.CENTER);
		label.setToolTipText(StaticValue.name+"题库录入截图工具 --Veasion");
		label.setForeground(new Color(237, 100, 20));
		label.setFont(f);
		JButton exit=new JButton("取消");
		exit.setToolTipText("退出截图（Esc）..");
		JButton save=new JButton("另存为");
		save.setToolTipText("另存为文件");
		JButton fill=new JButton("适应设备");
		fill.setToolTipText("截图自适应设备（620~460）");
		JButton ocr=new JButton("文字识别");
		ocr.setToolTipText("进行OCR文字识别，Ctrl+V 可粘贴识别结果");
		JButton complete=new JButton("完成");
		complete.setToolTipText("完成截图，Ctrl+V 可粘贴");
		JButton mspaint=new JButton("画图");
		mspaint.setToolTipText("打开画图进行编辑");
		JButton hough=new JButton("矫正");
		hough.setToolTipText("图片矫正");
		
		complete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ps.copyInShearPlate();
			}
		});
		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveImageFile();
			}
		});
		exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ps.finishAndinitialization();
			}
		});
		fill.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fillImageForDevice();
			}
		});
		ocr.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ocrImage();
			}
		});
		mspaint.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mspaintImage();
			}
		});
		hough.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				houghImage();
			}
		});
		this.add(label);
		this.add(save);
		// this.add(hough);
		this.add(mspaint);
		this.add(fill);
		this.add(ocr);
		this.add(exit);
		this.add(complete);
	}
	
	/**
	 * 图片自适应设备 
	 */
	public void fillImageForDevice(){
		Rectangle re = r.getRect();
		Image img=ps.getScreenImage();
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
		ps.finishAndinitialization();// 回收资源
	}
	
	/**
	 * 画图工具 
	 */
	public void mspaintImage(){
		try {
			Tools.clipboard.setContents(new MouseTransferable(ps.getScreenImage()), null);// 拷贝到剪切板里
			Runtime.getRuntime().exec("mspaint");
			Thread.sleep(500);
			Robot r=new Robot();
			r.keyPress(KeyEvent.VK_CONTROL);
			r.keyPress(KeyEvent.VK_V);
			r.keyRelease(KeyEvent.VK_V);
			r.keyRelease(KeyEvent.VK_CONTROL);
			r.delay(100);
			ps.finishAndinitialization();// 回收资源
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 图片矫正 
	 */
	public void houghImage(){
		Tools.clipboard.setContents(new MouseTransferable(ImageUtil.imageHough((BufferedImage)ps.getScreenImage())), null);
		ps.finishAndinitialization();
	}
	
	/**
	 * 保存文件（自定义文件保存位置方法）
	 */
	public void saveImageFile() {
		ps.setAlwaysOnTop(false);// 取消JFrame窗体置顶
		// this.setIconImage(ImageIO.read(PrintScreen.class.getResourceAsStream("veasion_ico.png")));
		JFileChooser jc = new JFileChooser();
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
				BufferedImage imageCache = (BufferedImage) ps.getScreenImage();
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
		// 初始化并结束
		ps.finishAndinitialization();
	}
	
	/**
	 * 图片文字识别 
	 */
	public void ocrImage(){
		Rectangle re = r.getRect();
		Image img=ps.getScreenImage();
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
			ps.finishAndinitialization();
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
			int fontSize=16;
			String text=null;
			try{
				Tools.clipboard.setContents(new StringSelection("正在识别.."), null);
				if(StaticValue.ocrEngine==1){
					if(VeaUtil.isNullEmpty(StaticValue.baiduAppId)){
						Tools.clipboard.setContents(new StringSelection("您没有权限访问百度云OCR，请与管理员联系（QQ：1456065030）"), null);
					}else{
						AipOcr client = new AipOcr(StaticValue.baiduAppId, StaticValue.baiduApiKey, StaticValue.baiduSecretKey);
						// 可选：设置网络连接参数
						client.setConnectionTimeoutInMillis(5000);
						client.setSocketTimeoutInMillis(100000);
						byte data[]=ImageUtil.imageToBytes(imgTemp, null);
						TextResponseForBd response=new TextResponseForBd(JSONObject.fromObject(client.general(data, new HashMap<String, String>()).toString()));
						text=response.getTextStr();
						if(response.getAvgFontHeight() != null){
							fontSize=response.getAvgFontHeight().intValue();
						}
					}
				}else{
					ImageOperate imgOpe=new ImageOperate(StaticValue.faceApiKey, StaticValue.faceApiSecret);
					ImageTextBean textBean=imgOpe.textRecognition(imgTemp);
					text=textBean.getTextStr();
					if(textBean.getAvgFontHeight() != null){
						fontSize=textBean.getAvgFontHeight().intValue();
					}
				}
			}catch(Exception e){
				text="发生错误："+e.getMessage();
				e.printStackTrace();
			}
			Tools.clipboard.setContents(new StringSelection(text), null);
			if(StaticValue.ocrModel==0){
				showOcrText(text, new Font("宋体", 0, fontSize));
			}
		});
		t.start();
		
		if(StaticValue.ocrModel==0){
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}else{
			ps.finishAndinitialization();
		}
	}
	
	private void showOcrText(String text, Font font){
		if(ocrjp==null){
			ocrjp=new JPanel(new GridLayout(1, 1));
			ocrArea=new JTextArea(text);
			ocrArea.setFont(font);
			ocrArea.setDragEnabled(true);
			ocrjp.add(new JScrollPane(ocrArea));
		}else{
			ocrArea.setText(text);
			ocrArea.setFont(font);
		}
		Rectangle re=r.getRect();
		int y = re.y - (re.height > ocrHeight ? -(re.height - ocrHeight) / 2 : (ocrHeight - re.height) / 2);
		if (re.x + re.width + ocrWidth + 5 > Tools.SCREEN_WIDTH) {
			ocrjp.setBounds(re.x - ocrWidth - 5, y, ocrWidth, ocrHeight);
		} else {
			ocrjp.setBounds(re.x + re.width + 5, y, ocrWidth, ocrHeight);
		}
		Rectangle or=ocrjp.getBounds();
		if(or.x < 0){
			or.x = 0;
		}
		if (or.y + or.height > Tools.SCREEN_HEIGHT) {
			or.y = Tools.SCREEN_HEIGHT - or.height;
		}else if(or.y < 0){
			or.y = 0;
		}
		ocrjp.setBounds(or);
		ps.clearSelectRect();
		ps.getLayeredPane().remove(ocrjp);
		ps.getLayeredPane().add(ocrjp, new Integer(Integer.MAX_VALUE));
	}
	
	public JPanel getOcrjp() {
		return ocrjp;
	}
}
