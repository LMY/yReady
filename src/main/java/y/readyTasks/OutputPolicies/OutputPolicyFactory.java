package y.readyTasks.OutputPolicies;

import y.utils.GeneralProperties;

public class OutputPolicyFactory {
	
	final static String COMMAND_NONE = "none";
	final static String COMMAND_COMPOSITE = "comp";
	final static String COMMAND_GMAIL = "mail";
	
	public static OutputPolicy create(GeneralProperties<String> config, String parm) {
		
		if (parm.equals(COMMAND_NONE))
			return new OutputPolicyNone();
		else if (parm.startsWith(COMMAND_COMPOSITE)) {
			final String[] singles = parm.substring(COMMAND_COMPOSITE.length()).split(",");
			final OutputPolicyComposite comp = new OutputPolicyComposite();
			for (String s : singles) {
				final OutputPolicy p = create(config, s);
				if (p != null)
					comp.add(p);
			}
			return comp;
		}
		else if (parm.startsWith(COMMAND_GMAIL)) {
			final String[] singles = parm.substring(COMMAND_GMAIL.length()).split("\\s");
			
			if (singles.length == 0)
				return new OutputPolicyGmail(config.get(String.class, "mailusername"),
												config.get(String.class, "mailpassword"),
												config.get(String.class, "mailto"));
			else if (singles.length == 1)
				return new OutputPolicyGmail(config.get(String.class, "mailusername"),
						config.get(String.class, "mailpassword"),
						singles[0]);
			else if (singles.length == 2)
				return new OutputPolicyGmail(singles[0],
						config.get(String.class, "mailpassword"),
						singles[1]);
			else
				return new OutputPolicyGmail(singles[0], singles[1], singles[2]);
		}
		
		// DEFAULT:
		return
				new OutputPolicyNone(); //null;
	}
}
