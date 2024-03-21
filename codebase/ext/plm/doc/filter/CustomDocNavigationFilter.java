package ext.plm.doc.filter;

import wt.access.AccessControlHelper;
import wt.access.AccessPermission;
import wt.change2.WTChangeActivity2;
import wt.doc.WTDocument;
import wt.fc.Persistable;
import wt.org.WTPrincipal;
import wt.session.SessionHelper;
import wt.util.WTException;

import com.ptc.core.ui.validation.DefaultSimpleValidationFilter;
import com.ptc.core.ui.validation.UIValidationCriteria;
import com.ptc.core.ui.validation.UIValidationKey;
import com.ptc.core.ui.validation.UIValidationStatus;

public class CustomDocNavigationFilter extends DefaultSimpleValidationFilter {
	
	public UIValidationStatus preValidateAction(UIValidationKey validationKey, UIValidationCriteria uivalidationcriteria) {
		try {
			Persistable object = null;
			if(uivalidationcriteria.getContextObject()!=null){
				object = uivalidationcriteria.getContextObject().getObject();
			}
			WTPrincipal principal = SessionHelper.manager.getPrincipal(); // 获取当前用户
			//对文档有编辑权限
			if(object instanceof WTDocument){
				boolean hasAccess = AccessControlHelper.manager.hasAccess(principal,uivalidationcriteria.getPageObject().getObject(),AccessPermission.MODIFY);
				if(hasAccess){
					WTDocument wtDoc = (WTDocument)object;
					boolean hadSupp = ext.plm.supplier.SupplierHelper.isSupplierDoc(wtDoc);
					if(hadSupp){
						return UIValidationStatus.ENABLED;
					}
				}
			}else if(object instanceof WTChangeActivity2){//生成更改通知单
				if("createECAReport".equalsIgnoreCase( validationKey.getComponentID())){
					boolean hasAccess = AccessControlHelper.manager.hasAccess(principal,uivalidationcriteria.getPageObject().getObject(),AccessPermission.MODIFY);
					if(hasAccess){
						return UIValidationStatus.ENABLED;
					}
				}
				
			}
			
		} catch (WTException e) {
			e.printStackTrace();
		}
		return UIValidationStatus.HIDDEN;
	}
	
	
	
}
