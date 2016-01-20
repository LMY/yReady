package y.utils;

import java.util.List;

import y.readyTasks.Entry;

public abstract class Notifier implements Runnable {
	
	private Notifiable tracker;
	
	public Notifier(final Notifiable tracker) {
		this.tracker = tracker;
	}
	
	public Notifiable getTracker() {
		return tracker;
	}

//	public void setTracker(Notifiable tracker) {
//		this.tracker = tracker;
//	}

	public void notify_start() {
		tracker.notify_start(this);
	}
	
	public void notify_progress(int value) {
		tracker.notify_progress(this, value);
	}
	
	public void notify_message(final String message) {
		tracker.notify_message(this, message);
	}
	
	public void notify_abort() {
		tracker.notify_abort(this);
	}
	
	public void notify_end() {
		tracker.notify_end(this);
	}
	
	public void notify_new(List<Entry> newentries) {
		tracker.notify_new(this, newentries);
	}

	public abstract void run();
}
