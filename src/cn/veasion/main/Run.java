package cn.veasion.main;

import javax.swing.JFrame;

public class Run {

	public static void main(String[] args) {
		VeasionFrame frame=new VeasionFrame();
		frame.setVisible(true);
		frame.setExtendedState(JFrame.ICONIFIED); //最小化
	}
}
