
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;

/**
 * Swing中单选钮、下拉框、复选框的使用
 * 
 * @author pan_junbiao
 *
 */
public class temp {
	private JFrame frame;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					temp window = new temp();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public temp() {
		initialize();
	}

	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 270, 192);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		frame.setLocationRelativeTo(null); // 窗体居中

		JLabel label = new JLabel("类型：");
		label.setFont(new Font("宋体", Font.PLAIN, 14));
		label.setBounds(10, 24, 54, 15);
		frame.getContentPane().add(label);

		JLabel lblNewLabel = new JLabel("性别：");
		lblNewLabel.setFont(new Font("宋体", Font.PLAIN, 14));
		lblNewLabel.setBounds(10, 53, 44, 15);
		frame.getContentPane().add(lblNewLabel);

		JLabel label_1 = new JLabel("爱好：");
		label_1.setFont(new Font("宋体", Font.PLAIN, 14));
		label_1.setBounds(10, 78, 44, 15);
		frame.getContentPane().add(label_1);

		// 创建下拉框
		JComboBox comboBox = new JComboBox();
		// 绑定下拉框选项
		String[] strArray = { "学生", "军人", "工人" };
		for (String item : strArray) {
			comboBox.addItem(item);
		}

		comboBox.setFont(new Font("宋体", Font.PLAIN, 14));
		comboBox.setBounds(53, 20, 140, 23);
		frame.getContentPane().add(comboBox);

		// 创建单选钮
		JRadioButton rdoMan = new JRadioButton("男");
		JRadioButton rdoWoman = new JRadioButton("女");
		// 创建按钮组
		ButtonGroup group = new ButtonGroup();
		group.add(rdoMan);
		group.add(rdoWoman);
		// 设置默认选择
		rdoMan.setSelected(true);

		rdoMan.setFont(new Font("宋体", Font.PLAIN, 14));
		rdoMan.setBounds(48, 49, 54, 23);
		frame.getContentPane().add(rdoMan);

		rdoWoman.setFont(new Font("宋体", Font.PLAIN, 14));
		rdoWoman.setBounds(104, 49, 54, 23);
		frame.getContentPane().add(rdoWoman);

		JCheckBox ckbA = new JCheckBox("足球");
		ckbA.setFont(new Font("宋体", Font.PLAIN, 14));
		ckbA.setBounds(48, 74, 54, 23);
		frame.getContentPane().add(ckbA);

		JCheckBox ckbB = new JCheckBox("篮球");
		ckbB.setFont(new Font("宋体", Font.PLAIN, 14));
		ckbB.setBounds(104, 74, 54, 23);
		frame.getContentPane().add(ckbB);

		JCheckBox ckbC = new JCheckBox("羽毛球");
		ckbC.setFont(new Font("宋体", Font.PLAIN, 14));
		ckbC.setBounds(163, 74, 71, 23);
		frame.getContentPane().add(ckbC);

		JButton btnSubmit = new JButton("确定");
		btnSubmit.setFont(new Font("宋体", Font.PLAIN, 14));
		btnSubmit.setBounds(10, 115, 93, 23);
		frame.getContentPane().add(btnSubmit);

		JButton btnReset = new JButton("重置");
		btnReset.setFont(new Font("宋体", Font.PLAIN, 14));
		btnReset.setBounds(141, 115, 93, 23);
		frame.getContentPane().add(btnReset);

		// 重置按钮事件
		btnSubmit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String msg = "";

				// 获取下拉框选择
				String typeStr = comboBox.getSelectedItem().toString();
				msg = "类型：" + typeStr;
				JOptionPane.showMessageDialog(null, msg, "提示", JOptionPane.INFORMATION_MESSAGE);

				// 获取单选钮选项
				String sexStr = rdoMan.isSelected() ? "男" : "女";
				msg = "性别：" + sexStr;
				JOptionPane.showMessageDialog(null, msg, "提示", JOptionPane.INFORMATION_MESSAGE);

				// 获取复选框选项
				msg = "爱好：";
				if (ckbA.isSelected()) {
					msg += ckbA.getText() + ";";
				}

				if (ckbB.isSelected()) {
					msg += ckbB.getText() + ";";
				}

				if (ckbC.isSelected()) {
					msg += ckbC.getText() + ";";
				}
				JOptionPane.showMessageDialog(null, msg, "提示", JOptionPane.INFORMATION_MESSAGE);

			}
		});

		// 重置按钮事件
		btnReset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				comboBox.setSelectedIndex(0);
				rdoMan.setSelected(true);
				ckbA.setSelected(false);
				ckbB.setSelected(false);
				ckbC.setSelected(false);
			}
		});
	}
}