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
	
	private Task oldTask;
	private MainWindow main;
	private GeneralProperties<String> config;
	
	public uiTaskCreator(MainWindow main, GeneralProperties<String> config) {
		this(main, config, null);
	}
	
	public uiTaskCreator(MainWindow main, GeneralProperties<String> config, Task oldTask) {
		super("Create new task");
		
		this.main = main;
		this.config = config;
		this.oldTask = oldTask;
		
		setLayout(new BorderLayout());
		
		final JPanel center = new JPanel();
		center.setLayout(new GridLayout(0,2));
		
		center.add(new JLabel(" type:"));
		type = new JComboBox<String>(types);
		center.add(type);
		if (oldTask != null)
			type.setSelectedItem(oldTask.getType());
		
		center.add(new JLabel(" url:"));
		url = new JTextField(oldTask == null? "" : oldTask.getUrl());
		center.add(url);
		
		center.add(new JLabel(" time:"));
		time = new JTextField(oldTask == null? "300" : ""+oldTask.getEvery());
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
	
	final static String[] types = new String[]{"subito", "kijiji", "insegreto"};
	
	public void setUrl(String t) { 
		url.setText(t);
		
		for (String k : types)
			if (t.contains(k)) {
				type.setSelectedItem(k);
				return;
			}
	}
	
	private void ok() {
		final Task t = TaskFactory.createTask(main, config, (String) type.getSelectedItem(), url.getText(), time.getText(), "none");
		if (t == null) {
			Utils.MessageBox("Error creating task. Check parameters", "ERROR");
			return;
		}
		
		if (oldTask == null)
			main.newTask(t);
		else
			main.modifyTask(oldTask, t);
		
		dispose();
	}
}
