package cn.veasion.util.hough;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Hough {

	private static final int edgeFilter = 30;
	
	private BufferedImage spriteImage;
	private BufferedImage farbAenderungsImage;

	private HoughSpace houghSpace;
	
	private HoughUtils eckenErkennungsWerkzeug = new HoughUtils();
	
	public Hough(BufferedImage spriteImage){
		this.spriteImage=spriteImage;
	}
	
	public ArrayList<Gerade> lineRange(){
		// ErkenneFarbaenderung
		farbAenderungsImage = eckenErkennungsWerkzeug.performFarbaenderungsErkennungRGB(spriteImage);
		// FilterEdge
		farbAenderungsImage = eckenErkennungsWerkzeug.filterSprite(farbAenderungsImage, edgeFilter);
		// CreateHoughSpace
		houghSpace = eckenErkennungsWerkzeug.calcHoughSpace(farbAenderungsImage);
		// RecreateLines
		return houghSpace.returnMaxima();
	}
	
}
