package cn.veasion.main;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import com.melloware.jintellitype.HotkeyListener;
import com.melloware.jintellitype.JIntellitype;
import cn.veasion.util.StaticValue;
import cn.veasion.util.VeaUtil;

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
		this.setLayout(new GridLayout(0, 2));
		this.setTitle("麦穗截图工具 --Veasion");
		this.setLocationRelativeTo(null);
		this.add(new JLabel("文字识别模式："));
		JComboBox<String> cbx1=new JComboBox<>(new String[]{"前台", "后台"});
		cbx1.setSelectedIndex(StaticValue.ocrModel);
		this.add(cbx1);
		this.add(new JLabel("自适应宽度："));
		JTextField tf1=new JTextField(String.valueOf(StaticValue.deviceWidth), 4);
		tf1.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				int k=e.getKeyCode();
				if ((k >= 48 && k <= 57) || (k >= 96 && k <= 105) || k == 8) {
					super.keyReleased(e);
				} else {
					tf1.setText(tf1.getText().replaceAll("\\D+", ""));
					int v=VeaUtil.valueOfInt(tf1.getText(), 0);
					if (v <= 0 && v > 5000) {
						tf1.setText("730");
					}
				}
			}
		});
		this.add(tf1);
		this.add(new JLabel("截图快捷键："));
		JTextField tf2=new JTextField(" Ctrl + B");
		tf2.setEnabled(false);
		this.add(tf2);
		
		this.add(new JLabel("自适应背景："));
		JComboBox<String> cbx2=new JComboBox<>(new String[]{"白色", "黑色"});
		cbx2.setSelectedIndex(StaticValue.deviceBgColor==Color.black ? 1 : 0);
		this.add(cbx2);
		
		JButton b1=new JButton("重置");
		JButton b2=new JButton("保存");
		b1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cbx1.setSelectedIndex(0);
				cbx2.setSelectedIndex(0);
				tf1.setText("730");
				b2.doClick();
			}
		});
		b2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				StaticValue.ocrModel=cbx1.getSelectedIndex();
				StaticValue.deviceBgColor = cbx2.getSelectedIndex() == 1 ? Color.black : Color.white;
				StaticValue.deviceWidth=VeaUtil.valueOfInt(tf1.getText(), 730);
				StaticValue.write();
			}
		});
		this.add(b1);
		this.add(b2);
		
		this.setSize(300, 160);
		this.setResizable(false);
		
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
