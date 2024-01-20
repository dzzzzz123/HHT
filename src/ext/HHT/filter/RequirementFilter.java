package ext.HHT.filter;

import com.ptc.core.ui.validation.DefaultSimpleValidationFilter;
import com.ptc.core.ui.validation.UIValidationCriteria;
import com.ptc.core.ui.validation.UIValidationKey;
import com.ptc.core.ui.validation.UIValidationStatus;

import ext.HHT.Config;
import ext.ait.util.PersistenceUtil;
import wt.fc.Persistable;
import wt.fc.WTReference;
import wt.part.WTPart;

public class RequirementFilter extends DefaultSimpleValidationFilter {

	@Override
	public UIValidationStatus preValidateAction(UIValidationKey arg0, UIValidationCriteria criteria) {
		UIValidationStatus status = UIValidationStatus.HIDDEN;
		try {
			WTReference reference = criteria.getContextObject();
			if (reference != null) {
				Persistable persistable = reference.getObject();
				if (persistable instanceof WTPart) {
					WTPart part = (WTPart) persistable;
					String subType = PersistenceUtil.getSubTypeInternal(part);
					if (Config.getReqType().contains(subType)) {
						status = UIValidationStatus.ENABLED;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			status = UIValidationStatus.HIDDEN;
		}
		return status;
	}
}
