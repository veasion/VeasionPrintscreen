package cn.veasion.main;

import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;

/**
 * 文本保存对话框
 * 
 * @author zhuowei.luo
 */
public class TextSaveDialog extends JDialog {

	private static final long serialVersionUID = -3234012515583724540L;

	private Map<String, JTextField> txtMap;
	private TextSaveDialog dialog;
	private JButton save;

	public TextSaveDialog(String title, int width, int height, Map<String, KeyValue> map, Frame frame) {
		super(frame);
		dialog = this;
		this.setResizable(false);
		this.setLayout(new GridLayout(0, 2));
		this.setTitle(title);
		this.setSize(width, height);
		this.setLocationRelativeTo(null);
		txtMap = new LinkedHashMap<>();
		map.forEach((k, v) -> {
			this.add(new JLabel(v.getKey()));
			JTextField j = new JTextField(v.getValue());
			this.add(j);
			txtMap.put(k, j);
		});
		JButton cancel = new JButton("取消");
		this.add(cancel);
		save = new JButton("保存");
		this.add(save);
		this.setVisible(true);
		cancel.addActionListener(new ActionListener() {
			@Override
			@SuppressWarnings("deprecation")
			public void actionPerformed(ActionEvent e) {
				dialog.setVisible(false);
				dialog.disable();
			}
		});
	}

	public void addSaveActionListener(ActionListener saveL) {
		if (save != null && saveL != null) {
			save.addActionListener(saveL);
		}
	}

	public String getValue(String key) {
		return txtMap.get(key).getText();
	}

	public Map<String, JTextField> getTxtMap() {
		return txtMap;
	}

	public static class KeyValue {
		private String key;
		private String value;

		public KeyValue(String key, Object value) {
			this.key = key;
			if (value != null) {
				this.value = String.valueOf(value);
			} else {
				this.value = "";
			}
		}

		public String getKey() {
			return key;
		}

		public String getValue() {
			return value;
		}
	}
}
