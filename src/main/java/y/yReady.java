package y;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import org.joda.time.DateTime;

import y.readyTasks.Entry;
import y.readyTasks.Task;
import y.readyTasks.TaskFactory;
import y.readyTasks.TaskParsable;
import y.utils.GeneralProperties;
import y.utils.GeneralPropertiesExporter;
import y.utils.Notifiable;
import y.utils.Notifier;
import y.utils.Utils;


public class yReady extends JFrame implements Notifiable {
	private static final long serialVersionUID = -1018051318360484373L;
	public static final String ReleaseDate = "2015-12-02";
	public static final String VersionString = "1.32" + " (" + ReleaseDate + ")"; 
	
	public static final String CONFIG_FILENAME = "yReady.conf";
	
	
	public static void main(String args[])
	{
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception exception) {
			Utils.MessageBox(null, "Invalid look and feel.", "ERROR");
		}
		
		Locale.setDefault(Locale.US);
		NumberFormat.getInstance(Locale.US).setGroupingUsed(false);
		
		GeneralProperties<String> config = null;
		try {
			config = GeneralPropertiesExporter.read(CONFIG_FILENAME, p -> { return p; });
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		setGlobalProxy(config);
		final yReady main = new yReady(config);
		main.startAllTasks();
	}
	
	private static void setGlobalProxy(GeneralProperties<String> config) {
		final String proxyHost = config.get(String.class, "proxy_host");
		final Integer proxyPort = config.get(Integer.class, "proxy_port");
		
		final String authUser = config.get(String.class, "proxy_username");
		final String authPassword = config.get(String.class, "proxy_password");

		if (proxyHost != null && !proxyHost.isEmpty())
			System.setProperty("http.proxyHost", proxyHost);
		if (proxyPort != null && proxyPort != 0)
			System.setProperty("http.proxyPort", ""+proxyPort);
		
		
		if (authUser != null && authPassword != null) {
			Authenticator.setDefault(new Authenticator() {
				public PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(authUser, authPassword.toCharArray());
				}
			});
	
			System.setProperty("http.proxyUser", authUser);
			System.setProperty("http.proxyPassword", authPassword);
		}
	}
	
	
	@SuppressWarnings("unused")
	private GeneralProperties<String> config;
	
	private List<Task> tasks = null;

	private JTable tableTasks;
	private JTable tableEntries;
	private TaskTableModel tasksModel;
	private EntryTableModel entriesModel;
	private final static String[] taskColumnNames = { "Type", "Url", "Every" };
	private final static String[] entryColumnNames = { "Url", "Name", "Category", "Spec", "Price", "Time" };
	private final static Class<?>[] taskColumnTypes = { String.class, String.class, Long.class };
	private final static Class<?>[] entryColumnTypes = { String.class, String.class, String.class, String.class, String.class, DateTime.class };

	
	private class TaskTableModel extends AbstractTableModel
	{
		private static final long serialVersionUID = -4916482284819856646L;
		private List<Task> tasks;
		
		public TaskTableModel(List<Task> tasks)
		{
			this.tasks = tasks;
		}
		
		@Override
		public int getColumnCount() {
			return 2;
		}

		@Override
		public int getRowCount() {
			return tasks.size();
		}

		@Override
		public Object getValueAt(int row, int column) {
			try {
				final Task t = tasks.get(row);
				switch (column) {
				case 0: return t.getType();
				case 1: return t.getUrl();
				case 2: return t.getEvery();
				}
			}
			catch (Exception e) {}

			return null;
		}
		
		@Override
	    public void setValueAt(Object value, int row, int col)
	    {
			final Task t = tasks.get(row);
			if (col == 0)
				; // not editable
			else if (col == 1)
				; // not editable
			else if (col == 2)
				t.setEvery(Long.parseLong((String) value));
	    }
		
	    public String getColumnName(int column) {
	        return taskColumnNames[column];
	    }
	    		
	    @Override
	    public Class<? extends Object> getColumnClass(int column) {
	    	return taskColumnTypes[column];
	    }
	    
	    @Override
	    public boolean isCellEditable(int row, int col) {
	    	return col==2;
	    }
	    
	    public Task getSelected() {
	    	return tasks.get(tableTasks.getSelectedRow());
	    }
	}
	
	private class EntryTableModel extends AbstractTableModel
	{
		private static final long serialVersionUID = -4916482284819856646L;
		private List<Entry> entries;
		
		public EntryTableModel(List<Entry> entries)
		{
			this.entries = entries;
		}
		
		@Override
		public int getColumnCount() {
			return entryColumnNames.length;
		}

		@Override
		public int getRowCount() {
			return entries.size();
		}

		@Override
		public Object getValueAt(int row, int column) {
			try {
				final Entry t = entries.get(row);
				switch (column) {
				case 0: return t.getUrl();
				case 1: return t.getName();
				case 2: return t.getCategory();
				case 3: return t.getSpec();
				case 4: return t.getPrice();
				case 5: return t.getTime();
				}
			}
			catch (Exception e) {}

			return null;
		}
		
		@Override
	    public void setValueAt(Object value, int row, int col)
	    {
//			final Entry t = entries.get(row);
	    }
		
	    public String getColumnName(int column) {
	        return entryColumnNames[column];
	    }
	    		
	    @Override
	    public Class<? extends Object> getColumnClass(int column) {
	    	return entryColumnTypes[column];
	    }
	    
	    @Override
	    public boolean isCellEditable(int row, int col) {
	    	return false;
	    }
	    
//	    public Entry getSelected() {
//	    	return entries.get(tableEntries.getSelectedRow());
//	    }
	}

	
	
	public yReady(GeneralProperties<String> config) {
		this.config = config;
				
		addWindowListener(new WindowAdapter() {
		    public void windowClosing(WindowEvent e) {
//		    	onClose();
		    	System.exit(0);
		    }
		});
		
		try {
			tasks = TaskFactory.parse(TaskFactory.TASKS_FILENAME, this, config); 
		}
		catch (Exception e) {
			Utils.MessageBox("Error reading Task file: "+e.getMessage(), "ERROR");
			tasks = new ArrayList<Task>();
		}
		
		
		this.setLayout(new BorderLayout());
		
		
		final JPanel center = new JPanel();
		center.setLayout(new GridLayout(0,2));
		
		tasksModel = new TaskTableModel(tasks);
		tableTasks = new JTable(tasksModel);
		
		entriesModel = new EntryTableModel(new ArrayList<Entry>());
		tableEntries = new JTable(entriesModel);
		
		tableTasks.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				try {
					tableEntries.setModel(new EntryTableModel(((TaskParsable)tasksModel.getSelected()).getEntries()));
				}
				catch (Exception e) {
					tableEntries.setModel(new EntryTableModel(new ArrayList<Entry>()));
				}
			}
		});
		
		
		center.add(tableTasks);
		center.add(new JScrollPane(tableEntries));
		
		this.add(center, BorderLayout.CENTER);
		
		
		
		final JPanel down = new JPanel();
		down.setLayout(new GridLayout(0,2));
		
		final JButton check = new JButton("Check");
		check.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try { tasksModel.getSelected().go(); }
				catch (Exception ex) {
					Utils.MessageBox(ex.getMessage(), "ERROR");
				}
			}
		});
		down.add(check);
		
		
		this.add(down, BorderLayout.SOUTH);

		setPreferredSize(new Dimension(800, 600));

		pack();
		Utils.centerWindow(this);
		setVisible(true);
		
//		startAllTasks();	// called later, not in constructor
	}
	
	public void startAllTasks() {
		for (final Task t : tasks)
			new Thread(t).start();
	}
	
	@Override
	public void notify_start(Notifier who) {
	}

	@Override
	public void notify_progress(Notifier who, int value) {
	}

	@Override
	public void notify_abort(Notifier who) {
	}

	@Override
	public void notify_message(Notifier who, String message) {
		Utils.MessageBox(message, "Message");
	}
 
	@Override
	public void notify_end(Notifier who) {
	}

	@Override
	public void notify_new(Notifier who, List<Entry> newentries) {
		
	}
}
