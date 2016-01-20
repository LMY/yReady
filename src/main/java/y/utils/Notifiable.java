package y.utils;

import java.util.List;

import y.readyTasks.Entry;

public interface Notifiable {
	public void notify_start(final Notifier who);
	
	public void notify_progress(final Notifier who, int value);
	public void notify_abort(final Notifier who);
	public void notify_message(final Notifier who, final String message);
	
	public void notify_end(final Notifier who);
	
	public void notify_new(final Notifier who, List<Entry> newentries);
}
