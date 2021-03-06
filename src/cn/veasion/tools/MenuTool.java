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
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.JTextComponent;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

import com.baidu.aip.ocr.AipOcr;

import cn.veasion.main.Printscreen;
import cn.veasion.util.ImageUtil;
import cn.veasion.util.StaticValue;
import cn.veasion.util.VeaUtil;
import cn.veasion.util.ocr.OcrTextResult;
import cn.veasion.util.ocr.baidu.OcrTextBeanForBaidu;
import cn.veasion.util.ocr.face.ImageOperate;
import cn.veasion.util.ocr.face.OcrTextBeanForFace;
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
	private JTextComponent ocrTextCom;
	private UndoManager undo;
	
	private int ocrWidth=400;
	private int ocrHeight=300;
	
	private String ocrText;
	private Font ocrFont;
	private boolean isOcr;
	
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
		label.setToolTipText(StaticValue.name + "截图工具 --Veasion");
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
				if(isOcr && StaticValue.ocrModel==0 && !VeaUtil.isNullEmpty(ocrTextCom.getText())){
					Tools.clipboard.setContents(new StringSelection(ocrTextCom.getText()), null);
					ps.finishAndinitialization();
				}else{
					ps.copyInShearPlate();
				}
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
		if (StaticValue.ocrEngine == 1) {
			// face++ 图片识别压缩
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
				if(StaticValue.ocrModel==0){
					ocrText = "识别失败：图片不能超过1200*1200";
					ocrFont = new Font("宋体", 0, 16);
					this.showOcrText(ocrText, ocrFont);
				}else{
					Tools.clipboard.setContents(new StringSelection("识别失败：图片不能超过1200*1200"), null);
					ps.finishAndinitialization();
				}
				return;
			} else if (re.width > 800 && re.width < 1200) {
				// 压缩
				img = ImageUtil.zoomImage((BufferedImage) img, (float) 800 / re.width);
			} else if (re.height > 800 && re.height < 1200) {
				// 压缩
				img = ImageUtil.zoomImage((BufferedImage) img, (float) 800 / re.height);
			}
		}
		final Image imgTemp=img;
		Thread t=new Thread(()->{
			int fontSize=16;
			String text="正在识别...";
			try{
				if (StaticValue.ocrEngine == 0) {
					if(VeaUtil.isNullEmpty(StaticValue.baiduApiKey)){
						text="\n          ========识别失败！=========\n\n     您还没有配置百度云文字识别的Key，\n请在设置界面中点击百度云进行配置，谢谢！\n\n如有疑问请联系\nQQ：1456065030\n";
					}else{
						AipOcr client = new AipOcr(StaticValue.baiduAppId, StaticValue.baiduApiKey, StaticValue.baiduSecretKey);
						// 可选：设置网络连接参数
						client.setConnectionTimeoutInMillis(5000);
						client.setSocketTimeoutInMillis(100000);
						byte data[]=ImageUtil.imageToBytes(imgTemp, null);
						String json=client.general(data, new HashMap<String, String>()).toString();
						OcrTextBeanForBaidu response=new OcrTextBeanForBaidu(JSONObject.fromObject(json));
						OcrTextResult result=new OcrTextResult(response, StaticValue.ocrTypesetting);
						text=result.getResultTest();
						if(StaticValue.ocrSize <= 0 && result.getAvgFontHeight() != null){
							fontSize=result.getAvgFontHeight().intValue();
						}else{
							fontSize = StaticValue.ocrSize;
						}
						
						/*json=client.accurateGeneral(data, new HashMap<String, String>()).toString();
						response=new OcrTextBeanForBaidu(JSONObject.fromObject(json));
						text+="\r\n\r\n=======精度高的========\r\n\r\n"+new OcrTextResult(response).getResultTest();*/
					}
				}else{
					if(VeaUtil.isNullEmpty(StaticValue.faceApiKey)){
						text="\n          ========识别失败！=========\n\n     您还没有配置Face++文字识别的Key，\n请在设置界面中点击Face++进行配置，谢谢！\n\n如有疑问请联系\nQQ：1456065030\n";
					}else{
						ImageOperate imgOpe=new ImageOperate(StaticValue.faceApiKey, StaticValue.faceApiSecret);
						OcrTextBeanForFace textBean=imgOpe.textRecognition(imgTemp);
						OcrTextResult result=new OcrTextResult(textBean, StaticValue.ocrTypesetting);
						text=result.getResultTest();
						if(StaticValue.ocrSize <= 0 && result.getAvgFontHeight() != null){
							fontSize=result.getAvgFontHeight().intValue();
						}else{
							fontSize = StaticValue.ocrSize;
						}
					}
				}
			}catch(Exception e){
				text="发生错误："+e.getMessage();
				e.printStackTrace();
			}
			Tools.clipboard.setContents(new StringSelection(text), null);
			if(StaticValue.ocrModel==0){
				ocrText = text;
				ocrFont = new Font("宋体", 0, fontSize);
				this.showOcrText(ocrText, ocrFont);
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
	
	/**
	 * 显示OCR文本框 
	 */
	public void showOcrText(){
		if(StaticValue.ocrModel == 0 && ocrText != null && ocrFont != null){
			this.showOcrText(ocrText, ocrFont);
		}
	}
	
	/**
	 * 显示OCR文本框 
	 */
	private void showOcrText(String text, Font font){
		if(ocrjp==null){
			// 初始化OCR文本框
			this.initOcrText(text, font);
		}else{
			ocrTextCom.setText(text);
			ocrTextCom.setFont(font);
		}
		
		Rectangle or=null;
		if (StaticValue.ocrLocation == 1) {
			or = this.ocrLocationX();
		} else if (StaticValue.ocrLocation == 2) {
			or = this.ocrLocationY();
		} else {
			Rectangle re = r.getRect();
			if (re.getWidth() > Tools.SCREEN_WIDTH / 2 - 10) {
				or = this.ocrLocationY();
			} else {
				or = this.ocrLocationX();
			}
		}
		ocrjp.setBounds(or);
		ps.clearSelectRect();
		ps.getLayeredPane().remove(ocrjp);
		ps.getLayeredPane().add(ocrjp, new Integer(Integer.MAX_VALUE));
	}
	
	/**
	 * 初始化OCR文本框 
	 */
	private void initOcrText(String text, Font font){
		this.isOcr = true;
		ocrjp=new JPanel(new GridLayout(1, 1));
		ocrTextCom=new JTextArea(text);
		undo=new UndoManager();
		ocrTextCom.getDocument().addUndoableEditListener(new UndoableEditListener() {
			public void undoableEditHappened(UndoableEditEvent e) {
				undo.addEdit(e.getEdit());
			}
		});
		ocrTextCom.getActionMap().put("Undo", new AbstractAction("Undo") {
			private static final long serialVersionUID = 1L;
			public void actionPerformed(ActionEvent evt) {
				try {
					if (undo.canUndo()) {
						undo.undo();
					}
				} catch (CannotUndoException e) {
				}
			}
		});
		ocrTextCom.getInputMap().put(KeyStroke.getKeyStroke("control Z"), "Undo");
		ocrTextCom.getActionMap().put("Redo", new AbstractAction("Redo") {
			private static final long serialVersionUID = 1L;
			public void actionPerformed(ActionEvent evt) {
				try {
					if (undo.canRedo()) {
						undo.redo();
					}
				} catch (CannotRedoException e) {
				}
			}
		});
		ocrTextCom.getInputMap().put(KeyStroke.getKeyStroke("control Y"), "Redo");
		ocrTextCom.setFont(font);
		ocrTextCom.setDragEnabled(true);
		ocrjp.add(new JScrollPane(ocrTextCom));
	}
	
	/**
	 * 计算OCR左右展示位置
	 */
	private Rectangle ocrLocationX(){
		Rectangle re=r.getRect();
		ocrWidth = (int)re.getWidth();
		ocrHeight = (int)re.getHeight();
		double maxWidth = Math.max(re.getX() - 0, Tools.SCREEN_WIDTH - re.getX() - re.getWidth()) - 6;
		if (ocrWidth > maxWidth) {
			ocrWidth = (int)maxWidth;
		}
		if(ocrWidth < 400){
			ocrWidth = 400;
		}
		if(ocrHeight > Tools.SCREEN_HEIGHT / 2){
			ocrHeight = (int)(Tools.SCREEN_HEIGHT / 2);
		}
		if(ocrHeight < 300){
			ocrHeight = 300;
		}
		
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
		return or;
	}
	
	/**
	 * 计算OCR上下展示位置
	 */
	private Rectangle ocrLocationY(){
		Rectangle re=r.getRect();
		ocrWidth = (int)re.getWidth();
		ocrHeight = (int)re.getHeight();
		double maxHeight = Math.max(re.getY() - 0, Tools.SCREEN_WIDTH - re.getY() - re.getHeight()) - 6;
		if (ocrWidth > Tools.SCREEN_WIDTH -20) {
			ocrWidth = Tools.SCREEN_WIDTH -20;
		}
		if(ocrWidth < 400){
			ocrWidth = 400;
		}
		if(ocrHeight > maxHeight){
			ocrHeight = (int)maxHeight;
		}
		if(ocrHeight < 300){
			ocrHeight = 300;
		}
		
		int x= re.x - (re.width > ocrWidth ? -(re.width - ocrWidth) / 2 : (ocrWidth - re.width) / 2);
		if (re.y + re.height + ocrHeight + 45 > Tools.SCREEN_HEIGHT) {
			ocrjp.setBounds(x, re.y - ocrHeight - 5, ocrWidth, ocrHeight);
		} else {
			ocrjp.setBounds(x, re.y + re.height + 45, ocrWidth, ocrHeight);
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
		return or;
	}
	
	
	public JPanel getOcrjp() {
		return ocrjp;
	}
}
