package ext.HHT.filter;

import com.ptc.core.ui.validation.DefaultSimpleValidationFilter;
import com.ptc.core.ui.validation.UIValidationCriteria;
import com.ptc.core.ui.validation.UIValidationKey;
import com.ptc.core.ui.validation.UIValidationStatus;

import ext.HHT.Config;
import wt.org.OrganizationServicesHelper;
import wt.org.WTGroup;
import wt.org.WTPrincipal;
import wt.session.SessionHelper;

public class SAPFilter extends DefaultSimpleValidationFilter {
	@Override
	public UIValidationStatus preValidateAction(UIValidationKey arg0, UIValidationCriteria arg1) {
		UIValidationStatus status = UIValidationStatus.HIDDEN;
		try {
			WTPrincipal currentUser = SessionHelper.manager.getPrincipal();
			WTGroup foundGroup = Service.getGroup(Config.getSAPDataGroup());
			boolean flag = OrganizationServicesHelper.manager.isMember(foundGroup, currentUser);
			if (flag) {
				status = UIValidationStatus.ENABLED;
			}
		} catch (Exception e) {
			e.printStackTrace();
			status = UIValidationStatus.HIDDEN;
		}
		return status;
	}
}