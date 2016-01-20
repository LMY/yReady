package y.utils;

import java.util.List;

import y.readyTasks.Entry;
import y.readyTasks.OutputPolicies.OutputPolicy;

public class Notifier {
	
	private Notifiable tracker;
	private OutputPolicy policy;
	
	public Notifier(final Notifiable tracker, OutputPolicy policy) {
		this.tracker = tracker;
		this.policy = policy;
	}
	
	public Notifiable getTracker() {
		return tracker;
	}

//	public void setTracker(Notifiable tracker) {
//		this.tracker = tracker;
//	}

	public void notify_start() {
		tracker.notify_start(this);
		policy.notifyMessage("start", "");
	}
	
	public void notify_progress(int value) {
		tracker.notify_progress(this, value);
		policy.notifyMessage("progress", ""+value);
	}
	
	public void notify_message(final String message) {
		tracker.notify_message(this, message);
		policy.notifyMessage("msg", "message");
	}
	
	public void notify_abort() {
		tracker.notify_abort(this);
		policy.notifyMessage("abort", "");
	}
	
	public void notify_end() {
		tracker.notify_end(this);
		policy.notifyMessage("end", "");
	}
	
	public void notify_new(List<Entry> newentries) {
		tracker.notify_new(this, newentries);
		policy.notifyNew(newentries);
	}
}
