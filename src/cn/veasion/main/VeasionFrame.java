package cn.veasion.main;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import com.melloware.jintellitype.HotkeyListener;
import com.melloware.jintellitype.JIntellitype;

import cn.veasion.main.TextSaveDialog.KeyValue;
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
	
	private VeasionFrame frame = null;
	
	public VeasionFrame(){
		
		frame=this;
		
		GridLayout gridLayout=new GridLayout(0, 2);
		this.setLayout(gridLayout);
		this.setTitle(StaticValue.name+"截图工具 --Veasion");
		this.setLocationRelativeTo(null);
		

		this.add(new JLabel(" 文字识别引擎："));
		JComboBox<String> ocrEngineComb=new JComboBox<>(new String[]{"百度云", "Face++"});
		ocrEngineComb.setSelectedIndex(StaticValue.ocrEngine);
		this.add(ocrEngineComb);
		
		
		this.add(new JLabel(" 文字识别模式："));
		JComboBox<String> ocrModelComb=new JComboBox<>(new String[]{"前台", "后台"});
		ocrModelComb.setSelectedIndex(StaticValue.ocrModel);
		this.add(ocrModelComb);
		
		
		this.add(new JLabel(" 文字识别结果展示："));
		JComboBox<String> ocrLocationComb=new JComboBox<>(new String[]{"自动", "左右", "上下"});
		ocrLocationComb.setSelectedIndex(StaticValue.ocrLocation);
		this.add(ocrLocationComb);
		
		
		this.add(new JLabel(" 文字识别结果排版："));
		JComboBox<String> ocrTypesettingComb=new JComboBox<>(new String[]{"等间距", "忽略垂直间距", "忽略水平间距", "缩小间距", "不排版"});
		ocrTypesettingComb.setSelectedIndex(StaticValue.ocrTypesetting);
		this.add(ocrTypesettingComb);
		
		
		this.add(new JLabel(" 自适应宽度："));
		JTextField deviceWidthTxt=new JTextField(String.valueOf(StaticValue.deviceWidth), 4);
		this.add(deviceWidthTxt);
		
		
		this.add(new JLabel(" 截图快捷键："));
		JPanel panel=new JPanel(new GridLayout(1, 0));
		JComboBox<String> key1Comb=new JComboBox<>(new String[]{"Shift", "Ctrl", "Win", "Alt"});
		key1Comb.setSelectedIndex(indexModKeyIndex());
		panel.add(key1Comb);
		panel.add(new JLabel("+", JLabel.CENTER));
		JTextField key2Txt=new JTextField(String.valueOf((char)StaticValue.printKey2));
		panel.add(key2Txt);
		this.add(panel);
		
		
		this.add(new JLabel(" 自适应背景："));
		JComboBox<String> deviceBgComb=new JComboBox<>(new String[]{"白色", "黑色"});
		deviceBgComb.setSelectedIndex(StaticValue.deviceBgColor==Color.black ? 1 : 0);
		this.add(deviceBgComb);
		
		
		this.add(new JLabel(" 截图操作后是否固定截图框："));
		JPanel radioPanel=new JPanel(new GridLayout(1, 0));
		JRadioButton yesRad=new JRadioButton("是", StaticValue.psFixed);
		JRadioButton noRad=new JRadioButton("否", !StaticValue.psFixed);
		ButtonGroup bg=new ButtonGroup();
		bg.add(yesRad);bg.add(noRad);
		radioPanel.add(yesRad);radioPanel.add(noRad);
		this.add(radioPanel);
		
		
		this.add(new JLabel(" 文字识别配置："));
		JPanel ocrConfigPanel=new JPanel(new GridLayout(1, 2));
		JButton baiduConfig=new JButton("百度云");
		JButton faceConfig=new JButton("Face++");
		ocrConfigPanel.add(baiduConfig);
		ocrConfigPanel.add(faceConfig);
		this.add(ocrConfigPanel);
		
		
		this.add(new JLabel(" 配置文件："));
		JTextField configTxt=new JTextField(ConfigUtil.configPath);
		configTxt.setEnabled(false);
		configTxt.setToolTipText(configTxt.getText());
		this.add(configTxt);
		
		
		JButton reset=new JButton("重置");
		JButton save=new JButton("保存");
		
		deviceWidthTxt.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				int k=e.getKeyCode();
				if (k != 8) {
					if ((k >= 48 && k <= 57) || (k >= 96 && k <= 105) || k == 8) {
						super.keyReleased(e);
					} else {
						deviceWidthTxt.setText(deviceWidthTxt.getText().replaceAll("\\D+", ""));
					}
					int v=VeaUtil.valueOfInt(deviceWidthTxt.getText(), 0);
					if (v <= 0 || v > 5000) {
						deviceWidthTxt.setText("630");
					}
				}
			}
		});
		
		key2Txt.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (key2Txt.getText().length() > 0) {
					key2Txt.setText(key2Txt.getText().toUpperCase().substring(key2Txt.getText().length()-1));
				}
			}
		});
		
		faceConfig.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Map<String, KeyValue> map=new LinkedHashMap<>();
				map.put("key", new KeyValue(" API Key：", StaticValue.faceApiKey));
				map.put("secret", new KeyValue(" API Secret：", StaticValue.faceApiSecret));
				TextSaveDialog dialog=new TextSaveDialog("Face++配置", 450, 140, map, frame);
				dialog.addSaveActionListener(new ActionListener() {
					@SuppressWarnings("deprecation")
					public void actionPerformed(ActionEvent e) {
						if (JOptionPane.showConfirmDialog(dialog, "请确认是否保存 ？", "提示",
								JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
							StaticValue.faceApiKey = dialog.getValue("key");
							StaticValue.faceApiSecret = dialog.getValue("secret");
							StaticValue.write();
							dialog.setVisible(false);
							dialog.disable();
						}
					}
				});
				if(VeaUtil.isNullEmpty(StaticValue.faceApiKey)){
					if (JOptionPane.showConfirmDialog(dialog, "您还没有Face++的文字识别key是否去注册并创建 ？（免费注册创建）", "温馨提示",
							JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
			            try {
			            	URI uri = new URI("https://console.faceplusplus.com.cn/register");
							Desktop.getDesktop().browse(uri);
						} catch (Exception e1) {
							JOptionPane.showConfirmDialog(dialog, "打开浏览器发生错误："+e1.getMessage(), "错误", JOptionPane.OK_CANCEL_OPTION);
						}
					}
				}
			}
		});
		
		baiduConfig.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Map<String, KeyValue> map=new LinkedHashMap<>();
				map.put("id", new KeyValue(" AppID：", StaticValue.baiduAppId));
				map.put("key", new KeyValue(" API Key：", StaticValue.baiduApiKey));
				map.put("secret", new KeyValue(" Secret Key：", StaticValue.baiduSecretKey));
				TextSaveDialog dialog=new TextSaveDialog("百度云配置", 450, 180, map, frame);
				dialog.addSaveActionListener(new ActionListener() {
					@SuppressWarnings("deprecation")
					public void actionPerformed(ActionEvent e) {
						if (JOptionPane.showConfirmDialog(dialog, "请确认是否保存 ？", "提示",
								JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
							StaticValue.baiduAppId = dialog.getValue("id");
							StaticValue.baiduApiKey = dialog.getValue("key");
							StaticValue.baiduSecretKey = dialog.getValue("secret");
							StaticValue.write();
							dialog.setVisible(false);
							dialog.disable();
						}
					}
				});
				if(VeaUtil.isNullEmpty(StaticValue.baiduApiKey)){
					if (JOptionPane.showConfirmDialog(dialog, "您还没有百度云的文字识别key是否去注册并创建 ？（免费注册创建）", "温馨提示",
							JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
						try {
			            	URI uri = new URI("https://login.bce.baidu.com/");
							Desktop.getDesktop().browse(uri);
						} catch (Exception e1) {
							JOptionPane.showConfirmDialog(dialog, "打开浏览器发生错误："+e1.getMessage(), "错误", JOptionPane.OK_CANCEL_OPTION);
						}
					}
				}
			}
		});
		
		reset.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				key1Comb.setSelectedIndex(1);
				ocrEngineComb.setSelectedIndex(0);
				ocrModelComb.setSelectedIndex(0);
				deviceBgComb.setSelectedIndex(0);
				ocrLocationComb.setSelectedIndex(0);
				ocrTypesettingComb.setSelectedIndex(0);
				yesRad.setSelected(true);
				key2Txt.setText("B");
				deviceWidthTxt.setText("630");
				save.doClick();
			}
		});
		
		save.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				StaticValue.printKey1=new int[]{JIntellitype.MOD_SHIFT, JIntellitype.MOD_CONTROL, JIntellitype.MOD_WIN, JIntellitype.MOD_ALT}[key1Comb.getSelectedIndex()];
				if (key2Txt.getText().length() > 0) {
					StaticValue.printKey2=(int)key2Txt.getText().toUpperCase().charAt(0);
				}else{
					key2Txt.setText("B");
					StaticValue.printKey2=(int)key2Txt.getText().toUpperCase().charAt(0);
				}
				StaticValue.ocrEngine=ocrEngineComb.getSelectedIndex();
				StaticValue.ocrModel=ocrModelComb.getSelectedIndex();
				StaticValue.ocrLocation=ocrLocationComb.getSelectedIndex();
				StaticValue.ocrTypesetting=ocrTypesettingComb.getSelectedIndex();
				StaticValue.deviceBgColor = deviceBgComb.getSelectedIndex() == 1 ? Color.black : Color.white;
				StaticValue.deviceWidth=VeaUtil.valueOfInt(deviceWidthTxt.getText(), 630);
				StaticValue.psFixed=yesRad.isSelected();
				StaticValue.write();
				registerHotKey(StaticValue.printKey1, StaticValue.printKey2);
			}
		});
		
		this.add(reset);
		this.add(save);
		
		this.setSize(360, 350);
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
