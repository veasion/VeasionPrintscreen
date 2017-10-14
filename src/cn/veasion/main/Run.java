package cn.veasion.main;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class Run {

	public static void main(String[] args) {
		VeasionFrame frame=new VeasionFrame();
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				if (JOptionPane.showConfirmDialog(frame, "程序退出后将不能截图，确认退出吗？", "温馨提示", JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE, null) == JOptionPane.YES_OPTION) {
					System.exit(0);
				}
			}
		});
		frame.setVisible(true);
		//最小化
		frame.setExtendedState(JFrame.ICONIFIED);
	}
	
}
