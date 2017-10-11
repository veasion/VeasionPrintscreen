package cn.veasion.main;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.melloware.jintellitype.HotkeyListener;
import com.melloware.jintellitype.JIntellitype;

import cn.veasion.util.StaticValue;

/**
 * 截图工具
 * 
 * @author zhuowei.luo
 */
public class VeasionFrame extends JFrame{

	private static final long serialVersionUID = 1L;

	private static final int PrintscreenKey = 1;
	
	private Printscreen p=new Printscreen();
	
	public VeasionFrame(){
		this.setLayout(new GridLayout(1, 0));
		this.setTitle("麦穗截图工具 --Veasion");
		this.setLocationRelativeTo(null);
		JButton set=new JButton("参数设置");
		set.setToolTipText("点击设置截图参数..");
		set.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null, "待开发...", "温馨提示", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		this.add(set);
		this.setSize(400, 200);
		this.setPreferredSize(new Dimension(400, 20));
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// 添加全局键盘监听钩子
		//第一步：注册热键，第一个参数表示该热键的标识，第二个参数表示组合键，如果没有则为0，第三个参数为定义的主要热键
    	JIntellitype.getInstance().registerHotKey(PrintscreenKey, StaticValue.printKey1, StaticValue.printKey2);
    	
    	//第二步：添加热键监听器
        JIntellitype.getInstance().addHotKeyListener(new HotkeyListener() {
			@Override
			public void onHotKey(int markCode) {
				if(PrintscreenKey == markCode){
					p.start();
				}
			}
		});
	}
	
	
	
}
