package ext.HHT.workFlowVerify;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import wt.org.WTPrincipal;
import wt.org.WTPrincipalReference;
import wt.project.Role;
import wt.team.Team;
import wt.team.TeamHelper;
import wt.util.WTException;
import wt.util.WTRuntimeException;
import wt.workflow.work.WfAssignedActivity;
import wt.workflow.work.WorkItem;

public class checkPersonnel {

	public static boolean process() {
		try {
			WorkItem wi = null;
			WfAssignedActivity wfAssignedActivity = (WfAssignedActivity) wi.getSource().getObject();
			System.out.println("wfAssignedActivity: " + wfAssignedActivity);
			Team team = (Team) wfAssignedActivity.getParentProcess().getTeamId().getObject();
			System.out.println("team: " + team.getName());
			HashMap map = TeamHelper.service.findAllParticipantsByRole(team);
			Iterator iterator = map.keySet().iterator();
			while (iterator.hasNext()) {
				Role role = (Role) iterator.next();
				System.out.println("role: " + role.getDisplay());
				List princRefs = (List) map.get(role);
				if (princRefs != null) {
					for (int i = 0; i < princRefs.size(); i++) {
						WTPrincipalReference principalReference = (WTPrincipalReference) princRefs.get(i);
						System.out.println("principalReference: " + principalReference.getName());
						System.out.println("principalReference: " + principalReference.getFullName());
						WTPrincipal principal = (WTPrincipal) principalReference.getObject();
						System.out.println("principal: " + principal.getName());

					}
				}
			}
			return false;
		} catch (WTRuntimeException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}
		return true;
	}
}
