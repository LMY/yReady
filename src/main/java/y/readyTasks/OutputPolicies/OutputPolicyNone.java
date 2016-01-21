package y.readyTasks.OutputPolicies;

import java.util.List;

import y.readyTasks.Entry;

class OutputPolicyNone implements OutputPolicy {
	@Override
	public boolean notifyNew(Entry entry) {
		return true;
	}

	@Override
	public boolean notifyNew(List<Entry> entries) {
		return true;
	}

	@Override
	public boolean notifyExisting(Entry entry) {
		return true;
	}

	@Override
	public boolean notifyExisting(List<Entry> entries) {
		return true;
	}

	@Override
	public boolean notifyMessage(String type, String msg) {
		return true;
	}
}
