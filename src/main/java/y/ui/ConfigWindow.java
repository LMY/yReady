package y.ui;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import y.utils.GeneralProperties;
import y.utils.Utils;
import y.utils.UtilsSwing;

public class ConfigWindow extends JFrame {

	private static final long serialVersionUID = 6023209663964137397L;
	
	
	private GeneralProperties<String> config;
	private GeneralPropertiesTableModel model;
	private JTable table;
	
	
	
	public ConfigWindow(GeneralProperties<String> config) {
		super("Config");
		this.config = config;
		
		setLayout(new BorderLayout());
		
		final JPanel center = new JPanel();
//		center.setLayout(new GridLayout(0,2));
		model = new GeneralPropertiesTableModel();
		refresh();
		table = new JTable(model);
		center.add(new JScrollPane(table));
		this.add(center, BorderLayout.CENTER);
		
		final JPanel south = new JPanel();
		center.setLayout(new GridLayout(0,2));
		this.add(south, BorderLayout.SOUTH);
		
		south.add(UtilsSwing.createButton("Refresh",  ae -> refresh()));
		south.add(UtilsSwing.createButton("Save",  ae -> save()));
		
		this.pack();
		Utils.centerWindow(this);
		this.setVisible(true);
	}
	
	
	private void save() {
		model.save(config);
	}
	
	private void refresh() {
		model.refresh(config);
	}
}
