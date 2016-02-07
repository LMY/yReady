package y.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import y.readyTasks.Task;
import y.readyTasks.TaskFactory;
import y.utils.GeneralProperties;
import y.utils.Utils;
import y.utils.UtilsSwing;

public class uiTaskCreator extends JFrame {
	private static final long serialVersionUID = -4085088336611418934L;
	
	private JComboBox<String> type;
	private JTextField url;
	private JTextField time;
	
	private MainWindow main;
	private GeneralProperties<String> config;
	
	public uiTaskCreator(MainWindow main, GeneralProperties<String> config) {
		this(main, config, "");
	}
	
	public uiTaskCreator(MainWindow main, GeneralProperties<String> config, String init_url) {
		super("Create new task");
		
		this.main = main;
		this.config = config;
		
		setLayout(new BorderLayout());
		
		final JPanel center = new JPanel();
		center.setLayout(new GridLayout(0,2));
		
		center.add(new JLabel(" type:"));
		type = new JComboBox<String>(new String[]{"subito", "kijiji", "insegreto"});
		center.add(type);
		
		center.add(new JLabel(" url:"));
		url = new JTextField();
		center.add(url);
		
		center.add(new JLabel(" time:"));
		time = new JTextField();
		center.add(time);
		
		
		final JPanel bottom = new JPanel();
		bottom.setLayout(new FlowLayout());
		bottom.add(UtilsSwing.createButton("Ok", e -> ok()));
		bottom.add(UtilsSwing.createButton("Cancel", e -> dispose()));
		
		add(center, BorderLayout.CENTER);
		add(bottom, BorderLayout.SOUTH);
		
		setPreferredSize(new Dimension(500, 200));
		pack();
		Utils.centerWindow(this);
		setVisible(true);
	}
	
	private void ok() {
		final Task t = TaskFactory.createTask(main, config, (String) type.getSelectedItem(), url.getText(), time.getText(), "none");
		if (t == null) {
			Utils.MessageBox("Error creating task. Check parameters", "ERROR");
			return;
		}
		
		main.newTask(t);
		dispose();
	}
}
