package ext.requirement;

import wt.util.resource.RBEntry;
import wt.util.resource.RBUUID;
import wt.util.resource.WTListResourceBundle;

@RBUUID("ext.requirement.requirementActionRB")
public class requirementActionRB extends WTListResourceBundle {
	@RBEntry("New ProjectRequirement")
	public static final String Requirement_CREATE_TITLE = "ProjectRequirement.create.title";
	@RBEntry("New ProjectRequirement")
	public static final String Requirement_CREATE_TOOLTIP = "ProjectRequirement.create.tooltip";
	@RBEntry("New ProjectRequirement")
	public static final String Requirement_CREATE_DESCRIPTION = "ProjectRequirement.create.description";
	@RBEntry("add16x16.gif")
	public static final String Requirement_CREATE_ICON = "ProjectRequirement.create.icon";
}
