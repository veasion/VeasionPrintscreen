package cn.veasion.util;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class Operation {
	
	private Rectangle src;
	private Rectangle desc;

	private BufferedImage image;
	
	public Operation(Rectangle src) {
		this.src = src;
	}

	public Operation(Rectangle src, Rectangle desc, BufferedImage image) {
		this.src = src;
		this.desc = desc;
		this.image=image;
	}

	public void draw(Graphics g) {
		g.setColor(StaticValue.deviceBgColor);
		g.fillRect((int) src.getX(), (int) src.getY(), (int) src.getWidth(), (int) src.getHeight());
		if (image != null) {
			g.drawImage(image, (int)desc.getX(), (int)desc.getY(), (int)desc.getWidth(), (int)desc.getHeight(), null);
		}else if(desc != null){
			g.fillRect((int) desc.getX(), (int) desc.getY(), (int) desc.getWidth(), (int) desc.getHeight());
		}
	}

	public Rectangle getSrc() {
		return src;
	}

	public void setSrc(Rectangle src) {
		this.src = src;
	}

	public Rectangle getDesc() {
		return desc;
	}

	public void setDesc(Rectangle desc) {
		this.desc = desc;
	}
}
