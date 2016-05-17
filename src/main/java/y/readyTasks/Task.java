package y.readyTasks;

import java.io.File;

import org.joda.time.DateTime;

import y.utils.GeneralProperties;
import y.utils.Notifier;

public abstract class Task implements Runnable {

	public final static String TASKS_FOLDER = "tasks" + File.separator;
	
	public final static String USER_AGENT = "Mozilla/5.0";
	
	private GeneralProperties<String> config;
	private String url;
	
	private long every;				// >0: loop forever. <=0: execute once. when set <=0, execute one more time then stop
	
	protected DateTime first;
	protected DateTime last;
	
	protected Notifier notifier;
		
	public Task(Notifier notifier, GeneralProperties<String> config, String url, long every) {
		super();
		this.notifier = notifier;
		this.config = config;
		this.url = url;
		this.every = every;
		
		first = null;
		last = null;
		
		// create folder
		final String folder = getFolder();
		try {
			if (!new File(folder).exists())
				new File(folder).mkdirs();
		}
		catch (Exception e) {}
	}
	
	final static String FILENAME_ENTRIES = "entries-debug.xml";
	
	
	public abstract String getType();
	protected abstract void go();
	
	public void run() {
		while (true) {
			go();

			if (every <= 0)
				break;
			else
				try { Thread.sleep(every * 1000); }
				catch (InterruptedException e) {}			
		}
	}
	
	public void runOnce() {
		final Task thisTask = this;
		
		final Thread newThread = new Thread() {
			public void run() {
				thisTask.go();
			}
		};
		newThread.start();		
	}
	
	
	public String getFolder() {
		return TASKS_FOLDER + url.replaceAll("[/:?=]", "-");
	}
	
	public String getUrl() {
		return url;
	}

	public DateTime getLast() {
		return last;
	}

	public DateTime getFirst() {
		return first;
	}

	public long getEvery() {
		return every;
	}

	public void setEvery(long every) {
		this.every = every;
	}
	
	public GeneralProperties<String> getConfig() {
		return config;
	}
	
	public String getOutputString() {
		// TODO Auto-generated method stub
		return null;
	}
}
