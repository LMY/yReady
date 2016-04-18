package y.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import org.joda.time.DateTime;

import y.readyTasks.Entry;
import y.readyTasks.Task;
import y.readyTasks.TaskFactory;
import y.readyTasks.TaskParsable;
import y.utils.GeneralProperties;
import y.utils.Notifiable;
import y.utils.Notifier;
import y.utils.Utils;
import y.utils.UtilsSwing;
import y.utils.PopClickListener;

public class MainWindow extends JFrame implements Notifiable {
	private static final long serialVersionUID = -3035959296179302179L;

	private GeneralProperties<String> config;
	
	private JTable tableTasks;
	private JTable tableEntries;
	private TaskTableModel tasksModel;
	private EntryTableModel entriesModel;
	private final static String[] taskColumnNames = { "Type", "Url", "Every" };
	private final static String[] entryColumnNames = { "Url", "Name", "Category", "Spec", "Price", "Time" };
	private final static Class<?>[] taskColumnTypes = { String.class, String.class, Long.class };
	private final static Class<?>[] entryColumnTypes = { String.class, String.class, String.class, String.class, String.class, DateTime.class };

	
	public MainWindow(GeneralProperties<String> config) {
		super("yReady");
		this.config = config;
				
		addWindowListener(new WindowAdapter() {
		    public void windowClosing(WindowEvent e) {
		    	onClose();
		    	System.exit(0);
		    }
		});
		
		List<Task> tasks = null;

		try {
			tasks = TaskFactory.parseXML(TaskFactory.TASKS_FILENAME, this, config); 
		}
		catch (Exception e) {
			Utils.MessageBox("Error reading Task file: "+e.getMessage(), "ERROR");
			tasks = new ArrayList<Task>();
		}
		
		this.setLayout(new BorderLayout());
		
		tasksModel = new TaskTableModel(tasks);
		tableTasks = new JTable(tasksModel);
		tableTasks.addMouseListener(new PopClickListener(createPopup()));
		
		entriesModel = new EntryTableModel(new ArrayList<Entry>());
		tableEntries = new JTable(entriesModel);
		tableEntries.addMouseListener(new MouseAdapter() {
		    public void mouseClicked(MouseEvent evt) {
		        if (evt.getClickCount() == 2) {
		            final int index = tableEntries.rowAtPoint(evt.getPoint());
		            doubleClick(index);
		        }
		    }
		});
		
		tableTasks.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				try {
					entriesModel = new EntryTableModel(((TaskParsable)tasksModel.getSelected()).getEntries());
					tableEntries.setModel(entriesModel);
				}
				catch (Exception e) {
					entriesModel = new EntryTableModel(new ArrayList<Entry>());
					tableEntries.setModel(entriesModel);
				}
			}
		});
		
		final JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tableTasks, new JScrollPane(tableEntries));
		
		this.add(split, BorderLayout.CENTER);
		
		final JPanel down = new JPanel();
		down.setLayout(new GridLayout(0,1));
		
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

		CreateMenuBar();
		
		
		setPreferredSize(new Dimension(800, 600));

		pack();
		Utils.centerWindow(this);
		setVisible(true);
		
//		startAllTasks();	// called later, not in constructor
	}
	
	public void CreateMenuBar()
	{
		final JMenuBar menubar = new JMenuBar();

		final JMenu filemenu = menubar.add(new JMenu("File"));
		filemenu.add(UtilsSwing.createMenuitem("New...", ae -> buttonNew(), "control N"));

		filemenu.addSeparator();
		filemenu.add(UtilsSwing.createMenuitem("Quit", ae -> System.exit(0), "control Q"));

		setJMenuBar(menubar);
	}
	
	private JPopupMenu createPopup() {
		final JPopupMenu menu = new JPopupMenu();
		
		menu.add(UtilsSwing.createMenuitem("Edit...", ae -> buttonEdit()));
		menu.addSeparator();
		menu.add(UtilsSwing.createMenuitem("Copy", ae -> buttonCopy()));
		menu.addSeparator();
		menu.add(UtilsSwing.createMenuitem("New", ae -> buttonNew()));
		menu.add(UtilsSwing.createMenuitem("Paste", ae -> buttonPaste()));
		menu.addSeparator();
		menu.add(UtilsSwing.createMenuitem("Delete", ae -> buttonDel()));
	
		return menu;
	}

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
	    	if (tableTasks.getSelectedColumnCount() == 0)
	    		return null;
	    	
	    	return tasks.get(tableTasks.getSelectedRow());
	    }
	    
	    public List<Task> getSelectedTasks() {
	    	
	    	final int idx[] = tableTasks.getSelectedRows();

	    	final List<Task> ret = new ArrayList<Task>();
	    	for (int i : idx)
	    		ret.add(tasks.get(i));
	    	
	    	return ret;
	    }

		public void refreshData() {
			this.fireTableDataChanged();
		}
		
		public List<Task> getTasks() {
			return tasks;
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
	    
	    public List<Entry> getSelectedEntries() {
	    	
	    	final int idx[] = tableEntries.getSelectedRows();

	    	final List<Entry> ret = new ArrayList<Entry>();
	    	for (int i : idx)
	    		ret.add(entries.get(i));
	    	
	    	return ret;
	    }
	}

	
	public void startAllTasks() {
		final List<Task> tasks = tasksModel.getTasks();

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

	private void onClose() {
		try {
			final List<Task> tasks = tasksModel.getTasks();

			TaskFactory.saveXML(TaskFactory.TASKS_FILENAME, tasks);
		} catch (Exception e) {
			Utils.MessageBox("Error saving task list:\n"+e.toString(), "ERROR");
		}
	}
	
	public void newTask(Task t) {
		tasksModel.getTasks().add(t);
		tasksModel.refreshData();
	}
	
	
	private void buttonEdit() {
		// TODO Edit task
	}
	
	private void buttonCopy() {
		final List<Task> tasks = tasksModel.getSelectedTasks();
		if (tasks.size() == 1)
			Utils.clipboardCopy(tasks.get(0).getUrl());
	}
	
	private void buttonNew() {
		new uiTaskCreator(this, config);
	}
	
	private void buttonPaste() {
		new uiTaskCreator(this, config, Utils.clipboardPaste());
	}
	
	private void buttonDel() {
		final List<Task> tasks = tasksModel.getSelectedTasks();
		
		if (!Utils.MessageBoxYesNo(this, "Are you sure?\n"+tasks.size()+" tasks will be deleted.", "CONFIRM DELETE"))
			return;
		
		boolean reload = false;
		
		for (Task t : tasks)
			reload |= tasksModel.getTasks().remove(t);
		
		if (reload)
			tasksModel.refreshData();
	}
	
	private void doubleClick(int index) {
//		if (config.get(Boolean.class, "copyOnDoubleClick"))
//			buttonCopy();
//		else {
			final List<Entry> l = entriesModel.getSelectedEntries();
			if (l.size() != 1)
				return;

			Utils.clipboardCopy(l.get(0).getUrl());
//		}
	}
}
