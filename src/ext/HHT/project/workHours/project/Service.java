package ext.HHT.project.workHours.project;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.ptc.projectmanagement.assignment.ResourceAssignment;
import com.ptc.projectmanagement.plan.Plan;
import com.ptc.projectmanagement.plan.PlanActivity;
import com.ptc.projectmanagement.plannable.PlannableHelper;

import ext.HHT.project.TrackHours.TrackHoursService;
import wt.fc.collections.WTCollection;
import wt.org.WTUser;

public class Service {

	public static List<Result> getResults(Plan plan) {
		List<Result> results = new ArrayList<>();
		List<PlanActivity> planActivitys = getPlanActivity(plan);
		ArrayList<ResourceAssignment> resourceAssignments = new ArrayList<>();
		for (PlanActivity planActivity : planActivitys) {
			resourceAssignments.addAll(TrackHoursService.getResourceAssignments(planActivity));
		}
		for (ResourceAssignment resourceAssignment : resourceAssignments) {
			Result result = getResult(resourceAssignment);
			if (result == null) {
				continue;
			}
			results.add(result);
		}
		return results;
	}

	public static List<PlanActivity> getPlanActivity(Plan plan) {
		List<PlanActivity> planActivity = new ArrayList<>();
		try {
			WTCollection children = PlannableHelper.service.getImmediateChildren(plan);
			for (Iterator iterator = children.persistableIterator(); iterator.hasNext();) {
				PlanActivity child = (PlanActivity) iterator.next();
				WTCollection children2 = PlannableHelper.service.getImmediateChildren(child);
				for (Iterator iterator2 = children2.persistableIterator(); iterator2.hasNext();) {
					PlanActivity child2 = (PlanActivity) iterator2.next();
					planActivity.add(child2);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return planActivity;
	}

	public static Result getResult(ResourceAssignment resourceAssignment) {
		Result result = new Result();
		result.setHHT_ActivityName(TrackHoursService.getPlanActivity(resourceAssignment).getName());
		WTUser user = TrackHoursService.getWtUser(resourceAssignment);
		if (user == null) {
			return null;
		}
		result.setHHT_UserName(user.getFullName());
		result.setHHT_StandardHours(resourceAssignment.getDoneEffort().toString() + " 工时");
		result.setHHT_ActualHours(TrackHoursService.getDoneEffort(resourceAssignment));
		result.setHHT_PercentWorkComplete(resourceAssignment.getPercentWorkComplete() + "%");
		return result;
	}
}
