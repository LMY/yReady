package y.readyTasks.OutputPolicies;

import java.util.ArrayList;
import java.util.List;

import y.readyTasks.Entry;

class OutputPolicyComposite implements OutputPolicy {

	private List<OutputPolicy> policies = new ArrayList<OutputPolicy>();
	
	public void add(OutputPolicy p) { policies.add(p); }
	
	
	@Override
	public void notifyNew(Entry entry) {
		for (OutputPolicy p : policies)
			p.notifyNew(entry);
	}

	@Override
	public void notifyNew(List<Entry> entries) {
		for (OutputPolicy p : policies)
			p.notifyNew(entries);
	}

	@Override
	public void notifyExisting(Entry entry) {
		for (OutputPolicy p : policies)
			p.notifyExisting(entry);
	}

	@Override
	public void notifyExisting(List<Entry> entries) {
		for (OutputPolicy p : policies)
			p.notifyExisting(entries);
	}

	@Override
	public void notifyMessage(String type, String msg) {
		for (OutputPolicy p : policies)
			p.notifyMessage(type, msg);
	}
}
