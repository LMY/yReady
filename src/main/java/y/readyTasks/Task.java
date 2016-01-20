package y.readyTasks;

import java.io.File;

import org.joda.time.DateTime;

import y.utils.GeneralProperties;
import y.utils.Notifiable;
import y.utils.Notifier;

public abstract class Task extends Notifier {

	public final static String TASKS_FOLDER = "tasks" + File.separator;
	
	public final static String USER_AGENT = "Mozilla/5.0";
	
	private GeneralProperties<String> config;
	private String url;
	
	private long every;				// >0: loop forever. <=0: execute once. when set <=0, execute one more time then stop
	
	protected DateTime first;
	protected DateTime last;
		
	public Task(Notifiable tracker, GeneralProperties<String> config, String url, long every) {
		super(tracker);
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
	
	final static String FILENAME_ENTRIES = TASKS_FOLDER + "entries-debug.xml";
	
	
	public abstract String getType();
	public abstract void go();
	
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
	
	public String getFolder() {
		return TASKS_FOLDER + url.replaceAll("[/:?]", "-");
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
}