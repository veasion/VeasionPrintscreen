package cn.veasion.main;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import com.melloware.jintellitype.HotkeyListener;
import com.melloware.jintellitype.JIntellitype;

import cn.veasion.util.ConfigUtil;
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
	
	private HotkeyListener hotkey;
	
	private Printscreen p=new Printscreen();
	
	public VeasionFrame(){
		this.setLayout(new GridLayout(0, 2));
		this.setTitle(StaticValue.name+"截图工具 --Veasion");
		this.setLocationRelativeTo(null);
		this.add(new JLabel("文字识别引擎："));
		JComboBox<String> cbx=new JComboBox<>(new String[]{"Face++", "百度云"});
		cbx.setSelectedIndex(StaticValue.ocrEngine);
		this.add(cbx);
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
				if (k != 8) {
					if ((k >= 48 && k <= 57) || (k >= 96 && k <= 105) || k == 8) {
						super.keyReleased(e);
					} else {
						tf1.setText(tf1.getText().replaceAll("\\D+", ""));
					}
					int v=VeaUtil.valueOfInt(tf1.getText(), 0);
					if (v <= 0 || v > 5000) {
						tf1.setText("630");
					}
				}
			}
		});
		this.add(tf1);
		this.add(new JLabel("截图快捷键："));
		
		JPanel jp=new JPanel(new GridLayout(1, 0));
		JComboBox<String> cboKey=new JComboBox<>(new String[]{"Shift", "Ctrl", "Win", "Alt"});
		cboKey.setSelectedIndex(indexModKeyIndex());
		jp.add(cboKey);
		jp.add(new JLabel("+", JLabel.CENTER));
		JTextField jkey=new JTextField(String.valueOf((char)StaticValue.printKey2));
		jkey.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (jkey.getText().length() > 0) {
					jkey.setText(jkey.getText().toUpperCase().substring(jkey.getText().length()-1));
				}
			}
		});
		jp.add(jkey);
		this.add(jp);
		this.add(new JLabel("自适应背景："));
		JComboBox<String> cbx2=new JComboBox<>(new String[]{"白色", "黑色"});
		cbx2.setSelectedIndex(StaticValue.deviceBgColor==Color.black ? 1 : 0);
		this.add(cbx2);
		this.add(new JLabel("截图操作后是否固定截图框："));
		JPanel jp2=new JPanel(new GridLayout(1, 0));
		JRadioButton r1=new JRadioButton("是", StaticValue.psFixed);
		JRadioButton r2=new JRadioButton("否", !StaticValue.psFixed);
		ButtonGroup bg=new ButtonGroup();
		bg.add(r1);bg.add(r2);
		jp2.add(r1);jp2.add(r2);
		this.add(jp2);
		this.add(new JLabel("配置文件："));
		JTextField jtf=new JTextField(ConfigUtil.configPath);
		jtf.setEnabled(false);
		jtf.setToolTipText(jtf.getText());
		this.add(jtf);
		JButton b1=new JButton("重置");
		JButton b2=new JButton("保存");
		b1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cboKey.setSelectedIndex(1);
				cbx.setSelectedIndex(0);
				cbx1.setSelectedIndex(0);
				cbx2.setSelectedIndex(0);
				r2.setSelected(true);
				jkey.setText("B");
				tf1.setText("630");
				b2.doClick();
			}
		});
		b2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				StaticValue.printKey1=new int[]{JIntellitype.MOD_SHIFT, JIntellitype.MOD_CONTROL, JIntellitype.MOD_WIN, JIntellitype.MOD_ALT}[cboKey.getSelectedIndex()];
				if (jkey.getText().length() > 0) {
					StaticValue.printKey2=(int)jkey.getText().toUpperCase().charAt(0);
				}else{
					jkey.setText("B");
					StaticValue.printKey2=(int)jkey.getText().toUpperCase().charAt(0);
				}
				StaticValue.ocrEngine=cbx.getSelectedIndex();
				StaticValue.ocrModel=cbx1.getSelectedIndex();
				StaticValue.deviceBgColor = cbx2.getSelectedIndex() == 1 ? Color.black : Color.white;
				StaticValue.deviceWidth=VeaUtil.valueOfInt(tf1.getText(), 630);
				StaticValue.psFixed=r1.isSelected();
				StaticValue.write();
				registerHotKey(StaticValue.printKey1, StaticValue.printKey2);
			}
		});
		this.add(b1);
		this.add(b2);
		
		this.setSize(340, 200);
		this.setResizable(false);
		
		registerHotKey(StaticValue.printKey1, StaticValue.printKey2);
	}
	
	/**
	 * 添加/重置 全局键盘监听钩子 
	 */
	private void registerHotKey(int key1, int key2){
		// 添加全局键盘监听钩子
		if (hotkey != null) {
			JIntellitype.getInstance().unregisterHotKey(PrintscreenKey);
			JIntellitype.getInstance().removeHotKeyListener(hotkey);
		}
		
		// 注册热键，第一个参数表示该热键的标识，第二个参数表示组合键，如果没有则为0，第三个参数为定义的主要热键
    	JIntellitype.getInstance().registerHotKey(PrintscreenKey, key1, key2);
    	
    	hotkey=new HotkeyListener() {
			@Override
			public void onHotKey(int markCode) {
				if(PrintscreenKey == markCode){
					p.start();
				}
			}
		};
		
    	//第二步：添加热键监听器
        JIntellitype.getInstance().addHotKeyListener(hotkey);
	}
	
	private int indexModKeyIndex() {
		switch (StaticValue.printKey1) {
			case JIntellitype.MOD_SHIFT:
				return 0;
			case JIntellitype.MOD_CONTROL:
				return 1;
			case JIntellitype.MOD_WIN:
				return 2;
			case JIntellitype.MOD_ALT:
				return 3;
			default:
				return 1;
		}
	}
	
}
