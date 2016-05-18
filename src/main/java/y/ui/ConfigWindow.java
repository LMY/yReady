package y.ui;

import java.awt.BorderLayout;

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
	
	public ConfigWindow(GeneralProperties<String> config) {
		super("Config");
		this.config = config;
		
		setLayout(new BorderLayout());
		
		model = new GeneralPropertiesTableModel();
		refresh();
		this.add(new JScrollPane(new JTable(model)), BorderLayout.CENTER);
		
		final JPanel south = new JPanel();
		south.add(UtilsSwing.createButton("Refresh",  ae -> refresh()));
		south.add(UtilsSwing.createButton("Save",  ae -> save()));
		this.add(south, BorderLayout.SOUTH);
		
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
