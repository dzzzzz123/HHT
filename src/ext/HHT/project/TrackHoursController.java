package ext.HHT.project;

import java.util.Map;

import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormProcessorController;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.core.ui.resources.FeedbackType;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.projectmanagement.plan.PlanActivity;

import ext.HHT.Config;
import wt.fc.PersistenceHelper;
import wt.fc.ReferenceFactory;
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
			if (key.equals("oid")) {
				soid = singleValue;
			} else if (key.equals("DoneEffort")) {
				DoneEffort = singleValue;
			} else if (key.equals("PercentWorkComplete")) {
				PercentWorkComplete = singleValue;
			}
		}
		PlanActivity planActivity = processSoid(soid);
		try {
//			Duration duration = new Duration();
//			duration.setMillis(Long.valueOf((long) (Double.valueOf(DoneEffort) * 3600000)));
//			planActivity.setDoneEffort(duration);
			Config.setHHT_DoneEffort(planActivity, DoneEffort);
			planActivity.setPercentWorkComplete(Double.valueOf(PercentWorkComplete));
			PersistenceHelper.manager.save(planActivity);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (WTPropertyVetoException e) {
			e.printStackTrace();
		}
		FormResult result = new FormResult(FormProcessingStatus.SUCCESS);
		result.addFeedbackMessage(new FeedbackMessage(FeedbackType.SUCCESS, SessionHelper.getLocale(), null, null,
				new String[] { "跟踪工时成功!" }));
		return result;
	}

	public static PlanActivity processSoid(String oid) {
		try {
			ReferenceFactory rf = new ReferenceFactory();
			PlanActivity planActivity = (PlanActivity) rf.getReference(oid).getObject();
			return planActivity;
		} catch (Exception e) {
			return null;
		}
	}
}
