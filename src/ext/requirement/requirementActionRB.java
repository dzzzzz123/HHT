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
	@RBEntry("ProjectRequirement infoPage")
	public static final String Requirement_INFO_PAGE = "ProjectRequirement.requirementInfo.description";
	@RBEntry("Edit ProjectRequirement")
	public static final String Requirement_EDIT_TOOLTIP = "ProjectRequirement.editRequirement.tooltip";
	@RBEntry("Edit ProjectRequirement")
	public static final String Requirement_EDIT_DESCRIPTION = "ProjectRequirement.editRequirement.description";
	@RBEntry("update.png")
	public static final String Requirement_EDIT_ICON = "ProjectRequirement.editRequirement.icon";
}
