package cn.veasion.util;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import cn.veasion.main.Printscreen;
import cn.veasion.tools.Direction;
import cn.veasion.tools.Tools;

/**
 * 绘制空心矩形和灰暗效果的对象
 * 
 * @author zhuowei.luo
 */
public class Rect {

	private int screenWidth = Tools.SCREEN_WIDTH;// 屏幕的宽度
	private int screenHeight = Tools.SCREEN_HEIGHT;// //屏幕的高度
	private Printscreen ps;
	private JPanel menu;
 
	private int leftUpX;// 左上角x
	private int leftUpY;// 左上角y
	private int rightDownX;// 右下角x
	private int rightDownY;// 右下角y
	private int width;// 宽度
	private int height;// 高度

	private int dot = 5;// 移动线条节点

	// 绘制各个方向的可移动点
	private int lxd;// 左x点
	private int uyd;// 上y点
	private int rxd;// 右x点
	private int dyd;// 下y点
	private int cxd;// 中x点
	private int cyd;// 中y点
	
	//鼠标 和 两点临时位置信息
	private int mXTemp;
	private int mYTemp;
	private Point tdATemp;
	private Point tdBTemp;
	
	public Direction direction = Direction.STOP;//鼠标拖动方向
	private boolean xuanDingMove = true; //第一步：选定
	private int mouseKeyCode;//鼠标按键
	
	private Cursor NW = new Cursor(Cursor.NW_RESIZE_CURSOR);
	private Cursor  W = new Cursor(Cursor.W_RESIZE_CURSOR );
	private Cursor SW = new Cursor(Cursor.SW_RESIZE_CURSOR);
	private Cursor NE = new Cursor(Cursor.NE_RESIZE_CURSOR);
	private Cursor  E = new Cursor(Cursor.E_RESIZE_CURSOR );
	private Cursor SE = new Cursor(Cursor.SE_RESIZE_CURSOR);
	private Cursor  S = new Cursor(Cursor.N_RESIZE_CURSOR );
	private Cursor  D = new Cursor(Cursor.DEFAULT_CURSOR);
	private Cursor  M = new Cursor(Cursor.MOVE_CURSOR);

	public Rect(Printscreen ps){
		this.ps = ps;
	}
	
	public void drawRect(Graphics g) {
		//Color c = g.getColor();// 获取原色
		g.setColor(Tools.RECT_COLOR);
		
		width  = rightDownX - leftUpX;
		height = rightDownY - leftUpY;
		
		g.drawRect(leftUpX, leftUpY, width, height);// 绘制矩形选区

		//计算一些通用的值，避免重复计算，提高执行效率。
		lxd = leftUpX - 2;// 左x点
		uyd = leftUpY - 2;// 上y点
		rxd = rightDownX - 2;// 右x点
		dyd = rightDownY - 2;// 下y点
		cxd = (leftUpX+rightDownX-1)>>1;// 中x点
		cyd = (leftUpY+rightDownY-1)>>1;// 中y点
		
		// 绘制各个方向的可移动点
		g.fillRect(lxd, uyd, dot, dot);// left-up
		g.fillRect(lxd, dyd, dot, dot);// left-down
		g.fillRect(rxd, uyd, dot, dot);// right-up
		g.fillRect(rxd, dyd, dot, dot);// right-down
		g.fillRect(lxd, cyd, dot, dot);// left
		g.fillRect(rxd, cyd, dot, dot);// right
		g.fillRect(cxd, uyd, dot, dot);// up
		g.fillRect(cxd, dyd, dot, dot);// down

		//绘制灰色透明区域
		Graphics2D g2d = (Graphics2D) g;
		g.setColor(Color.BLACK);
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,(float) 0.32));
		g2d.fillRect(0, leftUpY, leftUpX, height);// left
		g2d.fillRect(0, 0, screenWidth, leftUpY);// up
		g2d.fillRect(0, leftUpY + height, screenWidth, screenHeight - leftUpY - height);
		g2d.fillRect(leftUpX + width, leftUpY, screenWidth - leftUpX - width, height);
		
		// 绘制宽高的位置
		if (leftUpY - 18 > 0){
			drawPromptString(g, String.format(" %d × %d", width, height), leftUpX, leftUpY, width, height, true);
		}else if (height + 18 > screenHeight) {
			drawPromptString(g, String.format(" %d × %d", width, height), leftUpX + 1, leftUpY + height - 19, width, height, false);
		}else{
			drawPromptString(g, String.format(" %d × %d", width, height), leftUpX, leftUpY+height, width, height, false);
		}
	}

	/**
	 * 绘制阴影背景提示文字 
	 */
	private void drawPromptString(Graphics g, String text, int x, int y, int width, int height, boolean isUp){
		Graphics2D g2d = (Graphics2D) g;
		//绘制文字提示信息
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) 0.7));
		g2d.setColor(Color.BLACK);
		g2d.fillRect(x, isUp ? y - 18 : y + 1, text.length() * 7 + 15, 18);
		g.setColor(Color.WHITE);
		g.setFont(new Font("宋体", 1, 12));
		g2d.drawString(text, x + 3, isUp ? y - 4 : y + 13);
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) 1));
		g.setColor(Tools.RECT_COLOR);// 还原色
	}
	
	/**
	 * Rectangle
	 */
	public Rectangle getRect() {
		return new Rectangle(leftUpX, leftUpY, rightDownX-leftUpX, rightDownY-leftUpY);
	}
	
	/**
	 * 只是判断了鼠标的状态
	 */
	public void mouseMoved(MouseEvent e) {
		if(!xuanDingMove){//如果选定区域结束
			int x = e.getX();
			int y = e.getY();
			
			//确定节点方向
			if( x >= lxd && x <= lxd+dot && y >= uyd-2 && y <= uyd+dot){
				direction = Direction.LEFT_UP;
			}else if( x >= lxd && x <= lxd+dot && y >= dyd && y <= dyd+dot){
				direction = Direction.LEFT_DOWN;
			}else if( x >= lxd && x <= lxd+dot && y >= cyd && y <= cyd+dot){
				direction = Direction.LEFT;
			}else if( x >= rxd && x <= rxd+dot && y >= uyd && y <= uyd+dot){
				direction = Direction.RIGHT_UP;
			}else if( x >= rxd && x <= rxd+dot && y >= dyd && y <= dyd+dot){
				direction = Direction.RIGHT_DOWN;
			}else if( x >= rxd && x <= rxd+dot && y >= cyd && y <= cyd+dot){
				direction = Direction.RIGHT;
			}else if( x >= cxd && x <= cxd+dot && y >= uyd && y <= uyd+dot){
				direction = Direction.UP;
			}else if( x >= cxd && x <= cxd+dot && y >= dyd && y <= dyd+dot){
				direction = Direction.DOWN;
			}else if( x > lxd+dot && x < rxd   && y > uyd+dot && y < dyd){
				direction = Direction.AT_CENTER;
			}else{
				direction = Direction.STOP;
			}
			
			//根据方向信息设置鼠标状态
			switch(direction){
				case LEFT_UP   : ps.setCursor(NW);break;
				case LEFT      : ps.setCursor(W);break;
				case LEFT_DOWN : ps.setCursor(SW);break;
				case RIGHT_UP  : ps.setCursor(NE);break;
				case RIGHT     : ps.setCursor(E);break;
				case RIGHT_DOWN: ps.setCursor(SE);break;
				case UP        : ps.setCursor(S);break;
				case DOWN      : ps.setCursor(S);break;
				default        : ps.setCursor(D);
			}
		}
	}
	
	
	// 设置位置
	public void mouseDragged(MouseEvent e) {
		if(mouseKeyCode == 1){
			if(xuanDingMove){
				rightDownX = e.getX();
				rightDownY = e.getY();
			}else{
				//根据拖动方向，改变两点位置信息
				int x = e.getX();
				int y = e.getY();
				
				switch(direction){
					case LEFT  : leftUpX = x; break;
					case RIGHT : rightDownX = x; break;
					case UP    : leftUpY = y; break;
					case DOWN  : rightDownY = y; break;
					case LEFT_UP:    leftUpX = x; leftUpY = y; break;
					case LEFT_DOWN:	 leftUpX = x; rightDownY = y; break;
					case RIGHT_UP:   rightDownX = x; leftUpY = y; break;
					case RIGHT_DOWN: rightDownX = x; rightDownY = y; break;
					case AT_CENTER ://移动整个矩形区域
						leftUpX = tdATemp.x + (x - mXTemp);
						leftUpY = tdATemp.y + (y - mYTemp);
						rightDownX = tdBTemp.x + (x - mXTemp);
						rightDownY = tdBTemp.y + (y - mYTemp);
						break;
					default: //默认是拖拽中
						rightDownX = e.getX();
						rightDownY = e.getY();
				}
				
				//验证选区是否越界
				if( leftUpX < 0) leftUpX = 0;
				if( leftUpY < 0) leftUpY = 0;
				if( rightDownX > screenWidth)  rightDownX = screenWidth;
				if( rightDownY > screenHeight) rightDownY = screenHeight;
			}
		}
		
		//判断矩形是否反向，相反就复位
		if( leftUpX > rightDownX ) rightDownX = leftUpX + 1;
		if( leftUpY > rightDownY ) rightDownY = leftUpY + 1;
	}
	
	public void mousePressed(MouseEvent e) {
		mouseKeyCode = e.getButton();//获取按键值，方便判断拖拽
		if(mouseKeyCode == 1){//左键
			if(xuanDingMove){//选定开始
				leftUpX = e.getX();//设置左边点X
				leftUpY = e.getY();//设置左边点Y
			}else{
				//移动矩形选区
				if(direction == Direction.AT_CENTER){
					ps.setCursor(M);
				}
				mXTemp  = e.getX();
				mYTemp  = e.getY();
				tdATemp = new Point(leftUpX, leftUpY);
				tdBTemp = new Point(rightDownX, rightDownY);
			}
		}
	}
	
	public void mouseReleased(MouseEvent e) {//鼠标按键弹起
		if(mouseKeyCode == 1){//左键
			if(xuanDingMove){
				xuanDingMove = !xuanDingMove;//结束选定
			}else{
				ps.setCursor(D);//设置鼠标默认状态
			}
			// 显示功能菜单
			if (leftUpX + 520 > screenWidth && leftUpY+height + 31 < screenHeight) {
				showMouseMenu(new Rectangle(screenWidth-522, leftUpY+height+10, 520, 30));
			}else if (leftUpY+height + 31 > screenHeight && leftUpX + 520 < screenWidth) {
				showMouseMenu(new Rectangle(leftUpX, leftUpY-40, 520, 30));
			}else if(leftUpX + 520 > screenWidth){
				showMouseMenu(new Rectangle(screenWidth-522, leftUpY-40, 520, 30));
			}else{
				showMouseMenu(null);
			}
		}
	}
	
	/**
	 * 显示功能菜单
	 */
	public void showMouseMenu(Rectangle r) {
		if(menu==null){
			initMenu();
		}
		if(r!=null){
			menu.setBounds(r);
		}else{
			menu.setBounds(leftUpX, leftUpY + height + 10, 520, 30);
		}
		ps.getLayeredPane().remove(menu);
		ps.getLayeredPane().add(menu, new Integer(Integer.MAX_VALUE));
		
	}
	
	private void initMenu(){
		menu=new JPanel();
		GridLayout grid=new GridLayout(1, 0);
		menu.setLayout(grid);
		Font f=new Font("华文楷体", 1, 15);
		JLabel label=new JLabel("麦穗", JLabel.CENTER);
		label.setToolTipText("麦穗题库录入截图工具 --Veasion");
		label.setForeground(new Color(237, 100, 20));
		label.setFont(f);
		JButton exit=new JButton("退出");
		JButton save=new JButton("保存");
		JButton fill=new JButton("适应设备");
		fill.setToolTipText("截图自适应设备（620~460）");
		JButton ocr=new JButton("文字识别");
		JButton complete=new JButton("完成");
		
		complete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ps.copyInShearPlate();// 复制到剪切板
			}
		});
		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ps.saveImageFile();
			}
		});
		exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ps.finishAndinitialization();
			}
		});
		fill.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ps.fillImageForDevice();
			}
		});
		ocr.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ps.ocrImage();
			}
		});
		menu.add(label);
		menu.add(exit);
		menu.add(save);
		menu.add(fill);
		menu.add(ocr);
		menu.add(complete);
	}
	
}
