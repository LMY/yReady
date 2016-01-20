package y.readyTasks.OutputPolicies;

import java.util.List;

import y.readyTasks.Entry;

class OutputPolicyNone implements OutputPolicy {
	@Override
	public void notifyNew(Entry entry) {
	}

	@Override
	public void notifyNew(List<Entry> entries) {
	}

	@Override
	public void notifyExisting(Entry entry) {
	}

	@Override
	public void notifyExisting(List<Entry> entries) {
	}

	@Override
	public void notifyMessage(String type, String msg) {
	}
}
