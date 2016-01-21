package y.readyTasks.OutputPolicies;

import java.util.List;

import y.readyTasks.Entry;

public interface OutputPolicy {
	public boolean notifyNew(Entry entry);
	public boolean notifyNew(List<Entry> entries);
	
	public boolean notifyExisting(Entry entry);
	public boolean notifyExisting(List<Entry> entries);
	
	public boolean notifyMessage(String type, String msg);
}
