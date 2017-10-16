package cn.veasion.util.hough;

import java.awt.Color;
import java.awt.Graphics;

public class Gerade {
	private int x1;
	private int y1;
	private int x2;
	private int y2;
	
	private double angle;
	
	public Gerade(int alpha, int d, int width, int height) {
		int xAnfang = -width / 2;
		int xEnd = width / 2;
		if (alpha == 0) {
			alpha++;
		}
		int yStart = (int) ((d - Math.cos(alpha * (Math.PI / (double) 180)) * xAnfang)
				/ Math.sin(alpha * (Math.PI / (double) 180)));
		int yEnd = (int) ((d - Math.cos(alpha * (Math.PI / (double) 180)) * xEnd)
				/ Math.sin(alpha * (Math.PI / (double) 180)));

		// double steigung = ((double) yEnd - (double) yStart) / ((double) xAnfang - (double) xEnd);
		// System.out.println("steigung:" + steigung);
		
		this.x1=xAnfang - xAnfang;
		this.y1=yStart + height / 2;
		this.x2=xEnd + xEnd;
		this.y2=yEnd + height / 2;
		int x=Math.abs(x2-x1), y=Math.abs(y2-y1);
		angle=Math.acos(x/(Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2))))*180/Math.PI;
		if (y1 > y2) {
			angle = -angle;
		}
	}

	public void draw(Graphics g) {
		g.setColor(Color.RED);
		g.drawLine(x1, y1, x2, y2);
		System.out.println(String.format("g.drawLine(%d, %d, %d, %d)", x1, y1, x2, y2));
		System.out.println("角度："+angle);
	}
	
	/**
	 * 获取直线角度 
	 */
	public double getAngle() {
		return angle;
	}
	
}
