package y.readyTasks.OutputPolicies;

public class OutputPolicyFactory {
	
	final static String COMMAND_NONE = "none";
	final static String COMMAND_COMPOSITE = "comp";
	final static String COMMAND_GMAIL = "gmail";
	
	public static OutputPolicy create(String parm) {
		
		if (parm.equals(COMMAND_NONE))
			return new OutputPolicyNone();
		else if (parm.startsWith(COMMAND_COMPOSITE)) {
			final String[] singles = parm.substring(COMMAND_COMPOSITE.length()).split(",");
			final OutputPolicyComposite comp = new OutputPolicyComposite();
			for (String s : singles) {
				final OutputPolicy p = create(s);
				if (p != null)
					comp.add(p);
			}
			return comp;
		}
		else if (parm.startsWith(COMMAND_GMAIL)) {
			final String[] singles = parm.substring(COMMAND_GMAIL.length()).split("\\s");
			try {
				return new OutputPolicyGmail(singles[0], singles[1], singles[2]);
			}
			catch (Exception e) {
				// fall through to default return
			}
		}
		
		// DEFAULT:
		return new OutputPolicyNone(); //null;
	}
}
