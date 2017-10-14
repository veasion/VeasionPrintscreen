package cn.veasion.util.hough;

import java.util.ArrayList;

public class HoughSpace {

	public static final double geradenErkennungsFaktor = 0.6;

	private int[][] houghSpace;

	private int maxValue;
	
	private int maxD;
	private int maxAlpha;
	
	private int width;
	private int height;

	public HoughSpace(int[][] houghSpace, int width, int height) {
		this.width=width;
		this.height=height;
		this.houghSpace = houghSpace;
		maxD = houghSpace.length / 2;
		maxAlpha = houghSpace[0].length;
		maxValue = 0;
		for (int d = 0; d < houghSpace.length; d++) {
			for (int alpha = 0; alpha < houghSpace[0].length; alpha++) {
				if (houghSpace[d][alpha] > maxValue) {
					maxValue = houghSpace[d][alpha];
					// System.out.println(maxValue+"，"+d + " " + alpha + "°");
				}
			}
		}
		System.out.println("maxValue: " + maxValue);
	}

	/**
	 * d strechtes from -maxD to maxD;
	 * 
	 * @param d
	 * @param alpha
	 * @return
	 */
	public int getValue(int d, int alpha) {
		return houghSpace[d + maxD][alpha];
	}
	
	public ArrayList<Gerade> returnMaxima() {
		ArrayList<Gerade> list = new ArrayList<>();

		for (int d = -maxD; d < maxD; d++) {
			for (int alpha = 0; alpha < maxAlpha; alpha++) {
				if (houghSpace[d + maxD][alpha] > (double) maxValue * (double) geradenErkennungsFaktor) {
					list.add(new Gerade(alpha, d, width, height));
				}
			}
		}

		return list;
	}

	public int getMaxD() {
		return maxD;
	}

	public int getMaxAlpha() {
		return maxAlpha;
	}

	public int getMaxValue() {
		return maxValue;
	}

}
