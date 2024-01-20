package ext.HHT.project.TrackHours;

import java.util.ArrayList;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormProcessorController;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.core.ui.resources.FeedbackType;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.projectmanagement.assignment.ResourceAssignment;
import com.ptc.projectmanagement.plan.Duration;
import com.ptc.projectmanagement.plan.PlanActivity;

import ext.HHT.Config;
import ext.HHT.project.TrackHours.entity.DoneEffort;
import wt.fc.PersistenceHelper;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

public class TrackHoursController implements FormProcessorController {

	@Override
	public FormResult execute(NmCommandBean nmCommandBean) throws WTException {
		Map<String, Object> paramMap = nmCommandBean.getParameterMap();
		String soid = "";
		String DoneEffort = "";
		String PercentWorkComplete = "";
		for (String key : paramMap.keySet()) {
			Object value = paramMap.get(key);
			String singleValue = value instanceof String[] ? ((String[]) value)[0] : value.toString();
			System.out.println("key: " + key + " Value: " + singleValue);
			if (key.equals("oid")) {
				soid = singleValue.strip();
			} else if (key.equals("CurrentDoneEffort")) {
				DoneEffort = singleValue.strip();
			} else if (key.equals("TotalPercentWorkComplete")) {
				PercentWorkComplete = singleValue.strip();
			}
		}

		changeResource(TrackHoursService.getResourceAssignment(soid), DoneEffort, PercentWorkComplete);
		FormResult result = new FormResult(FormProcessingStatus.SUCCESS);
		result.addFeedbackMessage(new FeedbackMessage(FeedbackType.SUCCESS, SessionHelper.getLocale(), null, null,
				new String[] { "跟踪工时设置成功!" }));
		return result;
	}

	// 修改资源工时信息
	public static void changeResource(ResourceAssignment resourceAssignment, String DoneEffort,
			String PercentWorkComplete) {
		try {
			DoneEffort doneEffort = new DoneEffort();
			Long SystemDoneEffort = (long) (Double.parseDouble(PercentWorkComplete) / 100
					* resourceAssignment.getTotalEffort().getMillis());
			Long RemainingDoneEffort = resourceAssignment.getTotalEffort().getMillis() - SystemDoneEffort;
			Duration duration = resourceAssignment.getDoneEffort();
			duration.setMillis(SystemDoneEffort);
			resourceAssignment.setDoneEffort(duration);
			Duration duration2 = resourceAssignment.getRemainingEffort();
			duration2.setMillis(RemainingDoneEffort);
			resourceAssignment.setRemainingEffort(duration2);
			String resOid = "OR:" + resourceAssignment.getPersistInfo().getObjectIdentifier().toString();
			String actOid = "OR:" + TrackHoursService.getPlanActivity(resourceAssignment).getPersistInfo()
					.getObjectIdentifier().toString();
			String proOid = "OR:" + TrackHoursService.getProject2(resourceAssignment).getPersistInfo()
					.getObjectIdentifier().toString();
			String userOid = "OR:"
					+ TrackHoursService.getWtUser(resourceAssignment).getPersistInfo().getObjectIdentifier().toString();
			doneEffort.setRESOURCEASSIGNMENTID(resOid);
			doneEffort.setPLANACTIVITYID(actOid);
			doneEffort.setPROJECTID(proOid);
			doneEffort.setDONEEFFORT(DoneEffort);
			doneEffort.setUSERID(userOid);
			int i = TrackHoursService.insertDoneEffort(doneEffort);
			resourceAssignment.setPercentWorkComplete(Double.valueOf(PercentWorkComplete));
			PersistenceHelper.manager.save(resourceAssignment);
			changePlanActivity(resourceAssignment);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (WTPropertyVetoException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}
	}

	// 修改资源工时信息
	public static void changePlanActivity(ResourceAssignment resourceAssignment) {
		try {
			PlanActivity planActivity = TrackHoursService.getPlanActivity(resourceAssignment);
			ArrayList<ResourceAssignment> list = TrackHoursService.getResourceAssignments(planActivity);
			String DoneEffortStr = "";
			double DoneEffort = 0d;
			double PercentWorkComplete = 0d;
			long SystemDoneEffort = 0L;
			long RemainingDoneEffort = 0L;
			long TotalDoneEffort = planActivity.getTotalEffort().getMillis();
			for (ResourceAssignment obj : list) {
				String tempDoneEffort = TrackHoursService.getDoneEffort(obj).split(" ")[0];
				Long tempSystemDoneEffort = obj.getDoneEffort().getMillis();
				if (StringUtils.isNotBlank(tempDoneEffort)) {
					DoneEffort += Double.parseDouble(tempDoneEffort);
				}
				SystemDoneEffort += tempSystemDoneEffort;
			}
			DoneEffortStr = (DoneEffort == Math.floor(DoneEffort) && !Double.isInfinite(DoneEffort))
					? String.valueOf((int) DoneEffort)
					: String.valueOf(DoneEffort);
			PercentWorkComplete = Double.valueOf((double) SystemDoneEffort / TotalDoneEffort * 100);
			RemainingDoneEffort = TotalDoneEffort - SystemDoneEffort;
			Duration duration = planActivity.getDoneEffort();
			duration.setMillis(SystemDoneEffort);
			planActivity.setDoneEffort(duration);
			Duration duration2 = planActivity.getRemainingEffort();
			duration2.setMillis(RemainingDoneEffort);
			planActivity.setRemainingEffort(duration2);
			Config.setHHT_DoneEffort(planActivity, DoneEffortStr + " 工时");
			planActivity.setPercentWorkComplete(PercentWorkComplete);
			PersistenceHelper.manager.save(planActivity);
		} catch (WTException e) {
			e.printStackTrace();
		} catch (WTPropertyVetoException e) {
			e.printStackTrace();
		}
	}

}
