package ext.HHT.project.TrackHours;

import com.ptc.core.businessfield.server.businessObject.BusinessAlgorithm;
import com.ptc.core.businessfield.server.businessObject.BusinessAlgorithmContext;
import com.ptc.core.businessfield.server.businessObject.BusinessObject;
import com.ptc.projectmanagement.assignment.ResourceAssignment;

import wt.fc.Persistable;
import wt.fc.WTReference;
import wt.util.WTException;

public class CustomBusinessAlgorithm implements BusinessAlgorithm {

	@Override
	public Object execute(BusinessAlgorithmContext context, Object[] args) {
		String result = "";
		System.out.println("execute CustomBusinessAlgorithm");
		try {
			BusinessObject businessObject = context.getCurrentBusinessObject();
			WTReference wtReference = businessObject.getWTReference();
			if (wtReference != null) {
				Persistable persistable = wtReference.getObject();
				if (persistable != null && persistable instanceof ResourceAssignment) {
					ResourceAssignment resourceAssignment = (ResourceAssignment) persistable;
					result = TrackHoursService.getDoneEffort(resourceAssignment);
				}
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public Object getSampleValue() {
		// TODO Auto-generated method stub
		return null;
	}

}
