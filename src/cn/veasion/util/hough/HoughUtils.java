package cn.veasion.util.hough;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class HoughUtils {

	public BufferedImage performFarbaenderungsErkennungRGB(BufferedImage sprite) {
		BufferedImage returnImage = new BufferedImage(sprite.getWidth(), sprite.getHeight(),
				BufferedImage.TYPE_INT_ARGB);
		for (int y = 1; y < sprite.getHeight() - 1; y++) {
			for (int x = 1; x < sprite.getWidth() - 1; x++) {
				Color ownColor = new Color(sprite.getRGB(x, y));

				// calc overal difference
				int totalColorDiff = 0;
				totalColorDiff += getColorDiff(x, y - 1, sprite, ownColor);
				totalColorDiff += getColorDiff(x - 1, y, sprite, ownColor);
				totalColorDiff = totalColorDiff / 2;

				// recalc in 0-255
				int newRGB = totalColorDiff / 3;

				Color returnColor = new Color(newRGB, newRGB, newRGB);

				returnImage.setRGB(x, y, returnColor.getRGB());
			}
		}
		return returnImage;
	}

	public BufferedImage performFarbaenderungsErkennungContrast(BufferedImage sprite) {
		BufferedImage returnImage = new BufferedImage(sprite.getWidth(), sprite.getHeight(),
				BufferedImage.TYPE_INT_ARGB);
		for (int y = 1; y < sprite.getHeight() - 1; y++) {
			for (int x = 1; x < sprite.getWidth() - 1; x++) {
				Color ownColor = new Color(sprite.getRGB(x, y));
				// calc overal difference
				int totalColorDiff = 0;
				totalColorDiff += getColorDiff(x, y - 1, sprite, ownColor);
				totalColorDiff += getColorDiff(x - 1, y, sprite, ownColor);
				totalColorDiff = totalColorDiff / 2;

				// recalc in 0-255
				int newRGB = totalColorDiff / 3;

				Color returnColor = new Color(newRGB, newRGB, newRGB);

				returnImage.setRGB(x, y, returnColor.getRGB());

			}
		}
		return returnImage;
	}

	/**
	 * Sets color depending on value of edgeFilter either to white or to black.
	 * 
	 * @param sprite
	 */
	public BufferedImage filterSprite(BufferedImage sprite, int edgeFilter) {
		for (int y = 1; y < sprite.getHeight() - 1; y++) {
			for (int x = 1; x < sprite.getWidth() - 1; x++) {
				if (new Color(sprite.getRGB(x, y)).getRed() > edgeFilter) {
					sprite.setRGB(x, y, new Color(0, 0, 0).getRGB());
				} else {
					sprite.setRGB(x, y, new Color(255, 255, 255).getRGB());
				}
			}
		}
		return sprite;
	}

	private int getColorDiff(int x, int y, BufferedImage image, Color ownColor) {
		Color compareColor = new Color(image.getRGB(x, y));
		int rDiff = Math.abs(ownColor.getRed() - compareColor.getRed());
		int gDiff = Math.abs(ownColor.getGreen() - compareColor.getGreen());
		int bDiff = Math.abs(ownColor.getBlue() - compareColor.getBlue());

		return rDiff + gDiff + bDiff;
	}

	public HoughSpace calcHoughSpace(BufferedImage sprite) {
		long start = System.currentTimeMillis();

		int maxDis = (int) Math.sqrt(Math.pow(sprite.getWidth() / 2, 2) + Math.pow(sprite.getHeight() / 2, 2));

		int xOffset = sprite.getWidth() / 2;
		int yOffset = sprite.getHeight() / 2;

		// System.out.println("maxDis:" + maxDis);
		int[][] houghSpace = new int[maxDis * 2][180];

		int maxD = 0;
		int minD = 0;

		double PiUmrechnungsfaktor = Math.PI / 180;
		for (int y = 1; y < sprite.getHeight() - 1; y++) {
			for (int x = 1; x < sprite.getWidth() - 1; x++) {
				if (new Color(sprite.getRGB(x, y)).getRed() == 0) {
					for (int i = 0; i < 180; i++) {
						int d = (int) (Math.cos(i * PiUmrechnungsfaktor) * (x - xOffset)
								+ Math.sin(i * PiUmrechnungsfaktor) * (y - yOffset));
						houghSpace[d + maxDis][i]++;
						if (d > maxD) {
							maxD = d;
						}
						if (d < minD) {
							minD = d;
						}
					}
				}
			}
		}

		long end = System.currentTimeMillis();

		// System.out.println("maxD: " + maxD);
		// System.out.println("minD: " + minD);
		System.out.println("Hough Time: " + (end - start) + "ms");
		
		return new HoughSpace(houghSpace, sprite.getWidth(), sprite.getHeight());
	}
	
	/**
	 * 除去最大和最小求平均角度 
	 */
	public static double avgAngle(List<Gerade> angles) {
		double min = angles.stream().mapToDouble((v) -> v.getAngle()).min().orElse(0);
		double max = angles.stream().mapToDouble((v) -> v.getAngle()).max().orElse(0);
		return angles.stream()
				.filter((v) -> v.getAngle() != min && v.getAngle() != max)
				.mapToDouble((v) -> v.getAngle()).average().orElse(0);
	}
	
	/**
	 * 统计范围求平均
	 */
	public static double avgRangeAngle(List<Gerade> angles, int rangeValue) {
		List<List<Double>> ranges = new ArrayList<>();
		double start = angles.get(0).getAngle();
		List<Double> range = new ArrayList<>();
		ranges.add(range);
		//System.out.println(angles);
		angles.sort((v1, v2) -> v1.getAngle() == v2.getAngle() ? 0 : v1.getAngle() > v2.getAngle() ? 1 : -1);
		for (Gerade angle : angles) {
			if (angle.getAngle() > start + rangeValue) {
				range = new ArrayList<>();
				ranges.add(range);
				start = angle.getAngle();
			} else {
				range.add(angle.getAngle());
			}
		}
		int maxSize = ranges.stream().map((v) -> v.size()).mapToInt((v) -> v.intValue()).max().getAsInt();
		return ranges.stream().filter((v) -> v.size() == maxSize)
				.map((v) -> v.stream().mapToDouble((v1) -> v1.doubleValue()).average().orElse(0))
				.mapToDouble((v) -> v.doubleValue()).average().orElse(0);
	}
	
}
