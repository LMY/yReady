package y.readyTasks.OutputPolicies;

import java.util.ArrayList;
import java.util.List;

import y.readyTasks.Entry;

class OutputPolicyComposite implements OutputPolicy {

	private List<OutputPolicy> policies = new ArrayList<OutputPolicy>();
	
	public void add(OutputPolicy p) { policies.add(p); }
	
	
	@Override
	public boolean notifyNew(Entry entry) {
		boolean ret = true;
		
		for (OutputPolicy p : policies)
			ret |= p.notifyNew(entry);
		
		return ret;
	}

	@Override
	public boolean notifyNew(List<Entry> entries) {
		boolean ret = true;
		
		for (OutputPolicy p : policies)
			ret |= p.notifyNew(entries);
		
		return ret;
	}

	@Override
	public boolean notifyExisting(Entry entry) {
		boolean ret = true;
		
		for (OutputPolicy p : policies)
			ret |= p.notifyExisting(entry);
		
		return ret;
	}

	@Override
	public boolean notifyExisting(List<Entry> entries) {
		boolean ret = true;
		
		for (OutputPolicy p : policies)
			ret |= p.notifyExisting(entries);
		
		return ret;
	}

	@Override
	public boolean notifyMessage(String type, String msg) {
		boolean ret = true;
		
		for (OutputPolicy p : policies)
			ret |= p.notifyMessage(type, msg);
		
		return ret;
	}
}
