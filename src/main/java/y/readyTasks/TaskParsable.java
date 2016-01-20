package y.readyTasks;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;

import y.utils.GeneralProperties;
import y.utils.Notifier;
import y.utils.Utils;

public abstract class TaskParsable extends Task {

	private List<Entry> entries;
	
	public TaskParsable(Notifier notifier, GeneralProperties<String> config, String url, long every, List<Entry> entries) {
		super(notifier, config, url, every);
		
		this.entries = entries != null ? entries : TaskFactory.loadList(getFolder()+File.separator+FILENAME_ENTRIES, false);
	}
	
	public abstract String getContent(String url) throws Exception;
	public abstract List<Entry> parse(String content);
	
	public void go() {
		notifier.notify_start();
		
		final String url = getUrl();
		final String folder = getFolder();
		
		try {
			last = DateTime.now();
			if (first == null)
				first = last;
			
			final String content = getContent(url);
			Utils.saveText(folder + File.separator + "debug.html", content);
			
			if (content == null)
				throw new Exception("NULL content from "+url);
			if (content.isEmpty())
				throw new Exception("Empty content from "+url);
			
			final List<Entry> newentries = parse(content);
			TaskFactory.saveList(folder + File.separator + FILENAME_ENTRIES, newentries);
			if (newentries == null)
				throw new Exception("Could not parse "+url);
			
			analyze(newentries);
		}
		catch (Exception e) {
			notifier.notify_message(e.getMessage());
			notifier.notify_abort();
			return; // do not call notify_end()
		}
		
		notifier.notify_end();
	}
	
	public void analyze(List<Entry> newlist) {
		
		final List<Entry> newentries = new ArrayList<Entry>();
		
		for (Entry e : newlist)
			if (!entries.contains(e)) {
				newentries.add(e);
				entries.add(e);
			}
		
		if (newentries.size() > 0)
			notifier.notify_new(newentries);
	}
	
	public List<Entry> getEntries() {
		return entries;
	}
}
