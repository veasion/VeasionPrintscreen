package cn.veasion.util;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
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

	private boolean xuanDingMove=true;
	private int mouseKeyCode;
	
	private Rectangle temp;
	private List<Rectangle> rectangles=new ArrayList<>();
	
	private int leftUpX;
	private int leftUpY;
	private int rightDownX;
	private int rightDownY;
	
	public SelectRect(Printscreen ps, Rect r){
		this.ps=ps;
		this.r=r;
	}
	
	public void drawRect(Graphics g) {
		g.setColor(Color.red);
		this.temp=this.getRect();
		g.drawRect(leftUpX, leftUpY, rightDownX-leftUpX, rightDownY-leftUpY);
		for (Rectangle re : rectangles) {
			g.setColor(StaticValue.deviceBgColor);
			g.fillRect((int)re.getX(), (int)re.getY(), (int)re.getWidth(), (int)re.getHeight());
		}
	}
	
	public Rectangle getRect() {
		return new Rectangle(leftUpX, leftUpY, rightDownX-leftUpX, rightDownY-leftUpY);
	}
	
	public List<Rectangle> getRectangles(){
		return this.rectangles;
	}
	
	public void mousePressed(MouseEvent e) {
		mouseKeyCode = e.getButton();
		if(mouseKeyCode == 3){//右键
			if(xuanDingMove){//选定开始
				leftUpX = e.getX();
				leftUpY = e.getY();
				this.setSize();
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
		}
	}
	
	public void mouseReleased(MouseEvent e) {
		if(mouseKeyCode == 3){//右键
			if(xuanDingMove){
				//结束选
				xuanDingMove = !xuanDingMove;
			}
		}
	}
	
	public void keyReleased(KeyEvent e){
		if(e.getKeyCode() == 32){// 空格
			// 清空矩形
			if (temp != null && temp.getWidth() > 0 && temp.getHeight() > 0) {
				rectangles.add(new Rectangle(temp));
				// 重绘，这里不进行局部绘制
				ps.repaint();
			}
		}
	}
	
	private void setSize(){
		Rectangle re=r.getRect();
		if (leftUpX > re.getX() + re.getWidth()) {
			leftUpX = (int) (re.getX() + re.getWidth());
		} else if (leftUpX < re.getX()) {
			leftUpX = (int) re.getX();
		}
		if (rightDownX > re.getX() + re.getWidth()) {
			rightDownX = (int) (re.getX() + re.getWidth());
		} else if (rightDownX < re.getX()) {
			rightDownX = (int) re.getX();
		}
		if (leftUpY > re.getY() + re.getHeight()) {
			leftUpY = (int) (re.getY() + re.getHeight());
		} else if (leftUpY < re.getY()) {
			leftUpY = (int) re.getY();
		}
		if (rightDownY > re.getY() + re.getHeight()) {
			rightDownY = (int) (re.getY() + re.getHeight());
		} else if (rightDownY < re.getY()) {
			rightDownY = (int) re.getY();
		}
		if(leftUpX > rightDownX) rightDownX = leftUpX + 1;
		if(leftUpY > rightDownY) rightDownY = leftUpY + 1;
	}
	
}
