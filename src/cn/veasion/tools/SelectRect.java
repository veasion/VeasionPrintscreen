package cn.veasion.tools;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import cn.veasion.main.Printscreen;

/**
 * 选中小矩形
 * 
 * @author zhuowei.luo
 */
public class SelectRect {

	private Printscreen ps;
	private Rect r;

	private boolean xuanDingMove=false;
	private boolean move=false;
	private int mouseKeyCode;
	
	private Rectangle current;
	private List<Operation> operations=new ArrayList<>();
	
	private int leftUpX;
	private int leftUpY;
	private int rightDownX;
	private int rightDownY;
	
	private Point start;
	
	private Rectangle moveStart;
	private Rectangle moveRectangle;
	private Operation moveOperation;
	
	public SelectRect(Printscreen ps, Rect r){
		this.ps=ps;
		this.r=r;
	}
	
	public void drawRect(Graphics g) {
		for (Operation operation : operations) {
			operation.draw(g);
		}
		if(!ps.isOverDrawImage()){
			if(move && moveOperation!=null){
				// 绘制图片移动过程
				moveOperation.draw(g);
			}
			g.setColor(Color.red);
			this.current=new Rectangle(leftUpX, leftUpY, rightDownX-leftUpX, rightDownY-leftUpY);
			g.drawRect(leftUpX, leftUpY, rightDownX-leftUpX, rightDownY-leftUpY);
		}
	}
	
	public Rectangle getRect() {
		return this.current==null ? new Rectangle(leftUpX, leftUpY, rightDownX-leftUpX, rightDownY-leftUpY) : this.current;
	}
	
	public List<Operation> getOperations(){
		return this.operations;
	}
	
	public void mousePressed(MouseEvent e) {
		mouseKeyCode = e.getButton();
		if(mouseKeyCode == 3){//右键
			if(!xuanDingMove){//选定开始
				xuanDingMove=true;
				leftUpX = e.getX();
				leftUpY = e.getY();
				rightDownX=e.getX();
				rightDownY=e.getY();
				start=new Point(e.getX(), e.getY());
				this.setSize();
			}
		}else if(mouseKeyCode == 1){//左键
			if(!move && current!=null){
				moveStart=new Rectangle(current);
				moveRectangle=new Rectangle(e.getX(), e.getY(), e.getX()-current.x, e.getY()-current.y);
				move=true;
			}
		}
	}
	
	public void mouseDragged(MouseEvent e) {
		if(mouseKeyCode == 3){//右键
			if(xuanDingMove){
				rightDownX=e.getX();
				rightDownY=e.getY();
				this.setSize();
			}
		}else if(mouseKeyCode == 1){//左键
			if(move){
				leftUpX=e.getX()-moveRectangle.width;
				leftUpY=e.getY()-moveRectangle.height;
				rightDownX=leftUpX+current.width;
				rightDownY=leftUpY+current.height;
				ps.setCursor(new Cursor(Cursor.MOVE_CURSOR));
				// 移动图片过程
				BufferedImage image=ps.getDrawImage();
				image=image.getSubimage(moveStart.x, moveStart.y, moveStart.width, moveStart.height);
				moveOperation=new Operation(new Rectangle(moveStart), new Rectangle(current), image);
			}
		}
	}
	
	public void mouseReleased(MouseEvent e) {
		if(mouseKeyCode == 3){//右键
			if(xuanDingMove){
				//结束选定
				xuanDingMove = false;
				start=null;
			}
		}else if(mouseKeyCode == 1){//左键
			if(move){
				//结束移动
				move = false;
				ps.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				BufferedImage image=ps.getDrawImage();
				image=image.getSubimage(moveStart.x, moveStart.y, moveStart.width, moveStart.height);
				operations.add(new Operation(new Rectangle(moveStart), new Rectangle(current), image));
				moveOperation=null;
				ps.repaint();// 重绘
			}
		}
	}
	
	public void keyReleased(KeyEvent e){
		if(e.getKeyCode() == 32){// 空格
			// 清空矩形
			if (current != null && current.width > 0 && current.height > 0) {
				operations.add(new Operation(new Rectangle(current)));
				// 重绘，这里不进行局部绘制
				ps.repaint();
			}
		}
	}
	
	private void setSize(){
		Rectangle re=r.getRect();
		if (leftUpX > re.x + re.width) {
			leftUpX = re.x + re.width;
		} else if (leftUpX < re.x) {
			leftUpX = re.x;
		}
		if (rightDownX > re.x + re.width) {
			rightDownX = re.x + re.width;
		} else if (rightDownX < re.x) {
			rightDownX = re.x;
		}
		if (leftUpY > re.y + re.height) {
			leftUpY = re.y + re.height;
		} else if (leftUpY < re.y) {
			leftUpY = re.y;
		}
		if (rightDownY > re.y + re.height) {
			rightDownY = re.y + re.height;
		} else if (rightDownY < re.y) {
			rightDownY = re.y;
		}
		
		// 上下左右都可以选拖
		if(start.x > rightDownX && start.y > rightDownY){
			leftUpX = rightDownX;
			rightDownX = start.x;
			leftUpY = rightDownY;
			rightDownY = start.y;
		}else if (start.x > rightDownX) {
			leftUpX = rightDownX;
			rightDownX = start.x;
		}else if (start.y > rightDownY) {
			leftUpY = rightDownY;
			rightDownY = start.y;
		}
	}
	
}