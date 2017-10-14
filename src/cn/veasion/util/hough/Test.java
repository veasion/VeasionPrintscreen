package cn.veasion.util.hough;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Test extends JFrame {
	
	private static final long serialVersionUID = 1L;
	
	private static final int rangeValue=3;
	
	public Test(String imagePath) {
		BufferedImage spriteImage=null;
		try {
			spriteImage = ImageIO.read(new File(imagePath));
			System.out.println(String.format("width: %d, height: %d", spriteImage.getWidth(), spriteImage.getHeight()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		List<Gerade> geraden=new Hough(spriteImage).lineRange();
		BufferedImage image = new BufferedImage(spriteImage.getWidth(), spriteImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
		image.getGraphics().drawImage(spriteImage, 0, 0, null);
		for (Gerade g : geraden) {
			g.draw(image.getGraphics());
		}
		
		double avgAngle=HoughUtils.avgAngle(geraden);
		System.out.println("\n平均角度："+avgAngle);
		double avgRangeAngle=HoughUtils.avgRangeAngle(geraden, rangeValue);
		System.out.println("\n统计范围求平均："+avgRangeAngle);
		
		this.setContentPane(new JPanel() {
			private static final long serialVersionUID = 1L;
			@Override
			public void paint(Graphics g) {
				super.paint(g);
				g.drawImage(image, 0, 0, null);
			}
		});
		this.setLocationRelativeTo(null);
		this.setSize(spriteImage.getWidth(), spriteImage.getHeight());
		this.setVisible(true);
	}
	
	public static void main(String[] args) {
		String imagePath="C:\\Users\\zhuowei.luo\\Desktop\\test.png";
		new Test(imagePath);
	}

}
