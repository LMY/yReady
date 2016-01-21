package y.readyTasks.OutputPolicies;

import java.util.ArrayList;
import java.util.List;

import y.readyTasks.Entry;
import y.utils.GMailer;

class OutputPolicyGmail implements OutputPolicy {
	
	private GMailer mailer;
	private String dest;
	
	public OutputPolicyGmail(String username, String password, String dest) {
		mailer = new GMailer(username, password);
		this.dest = dest;
	}
	
	public static String entryToHtml(Entry entry) {
		return "<a href=\""+entry.getUrl()+"\">"+
					entry.getTime() + " " +
					entry.getPrice() + " " +
					entry.getSpec() + " " +
					entry.getCategory() + " " +
					entry.getName() + " " +
					"</a>";
	}
	
	public void errorSend(Exception e, List<Entry> entries) {
		
	}
	
	
	@Override
	public boolean notifyNew(Entry entry) {
		try {
			mailer.sendMail(dest, "yReady: 1 new Entry", entryToHtml(entry));
			return true;
		}
		catch (Exception e) {
			final List<Entry> list = new ArrayList<Entry>();
			list.add(entry);
			errorSend(e, list);
			return false;
		}
	}

	@Override
	public boolean notifyNew(List<Entry> entries) {
		if (entries.isEmpty())
			return true;
		
		final String subject = "yReady: "+entries.size()+" new entries";
		
		String body = "";
		for (int i=0, imax=entries.size(); i<imax; i++) {
			body += entryToHtml(entries.get(i));
			if (i != imax-1)
				body += "<br />\n";
		}
		
		try {
			mailer.sendMail(dest, subject, body);
			return true;
		}
		catch (Exception e) {
			errorSend(e, entries);
			return false;
		}
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
		try {
			mailer.sendMail(dest, "yReady: message", "Message type: "+type+"<br /><br />\n"+msg);
			return true;
		}
		catch (Exception e) {
			errorSend(e, new ArrayList<Entry>());
			return false;
		}
	}
}
