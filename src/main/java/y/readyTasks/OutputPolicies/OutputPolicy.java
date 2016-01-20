package y.readyTasks.OutputPolicies;

import java.util.List;

import y.readyTasks.Entry;

public interface OutputPolicy {
	public void notifyNew(Entry entry);
	public void notifyNew(List<Entry> entries);
	
	public void notifyExisting(Entry entry);
	public void notifyExisting(List<Entry> entries);
	
	public void notifyMessage(String type, String msg);
}
