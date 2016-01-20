package y.readyTasks.OutputPolicies;

public class OutputPolicyFactory {
	public static OutputPolicy create(String parm) {
		
		if (parm.equals("none"))
			return new OutputPolicyNone();
		else if (parm.startsWith("comp")) {
			final String[] singles = parm.substring(4).split(",");
			final OutputPolicyComposite comp = new OutputPolicyComposite();
			for (String s : singles) {
				final OutputPolicy p = create(s);
				if (p != null)
					comp.add(p);
			}
			return comp;
		}
		
		// DEFAULT:
		return new OutputPolicyNone(); //null;
	}
}
