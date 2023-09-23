package ext.signature;

import org.apache.commons.lang3.StringUtils;

import com.ptc.core.ui.validation.DefaultUIComponentValidator;
import com.ptc.core.ui.validation.UIComponentValidator;
import com.ptc.core.ui.validation.UIValidationCriteria;
import com.ptc.core.ui.validation.UIValidationKey;
import com.ptc.core.ui.validation.UIValidationStatus;

import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.inf.container.OrgContainer;
import wt.inf.container.WTContainer;
import wt.org.OrganizationServicesHelper;
import wt.org.WTGroup;
import wt.org.WTPrincipal;
import wt.query.QueryException;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionHelper;
import wt.util.WTException;

/**
 * 批量下载PDF权限控制，只允许“资料管理员”组和管理员下载
 * @author samuel @2019-3-17
 *
 */
public class BatchDownloadZipPDFValidator extends DefaultUIComponentValidator implements UIComponentValidator{

	@Override
	public UIValidationStatus preValidateAction(UIValidationKey uivalidationkey, UIValidationCriteria uivalidationcriteria) {
		UIValidationStatus status = null;
		WTPrincipal principal = null;
		String groupName = "资料管理员";
		try {
			principal = SessionHelper.manager.getPrincipal();//获取当前用户
			OrgContainer orgContainer =getOrgContainer("sinoboom");//获取组织
//			OrgContainer orgContainer =getOrgContainer("Demo Organization");//获取组织
			if(orgContainer == null){
				return UIValidationStatus.HIDDEN;
			}
			WTContainer container = orgContainer.getContainer();//站点
			//组织管理员或站点管理员
			if(container.getAdministrators().isMember(principal) || orgContainer.getAdministrators().isMember(principal)){
					status =  UIValidationStatus.ENABLED;
			}else {
				boolean isGroupMember = isGroupMember(orgContainer, groupName, principal);
				if(isGroupMember){
					status =  UIValidationStatus.ENABLED;
				}else {
					status =  UIValidationStatus.HIDDEN;
				}
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		
		return status;
	}
	/**
	 * 获取当前组织
	 * 
	 * @return
	 */
	public static OrgContainer getOrgContainer(String orgName) {
		try {
			QuerySpec queryspec = new QuerySpec(OrgContainer.class);
			QueryResult qr = PersistenceHelper.manager.find(queryspec);
			while (qr.hasMoreElements()) {
				OrgContainer org = (OrgContainer) qr.nextElement();
				if (StringUtils.equalsIgnoreCase(orgName, org.getName())) {
					return org;
				}
			}
		} catch (QueryException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 获取判断当前用户是否在指定WTGroup中
	 * 
	 * @return
	 */
	public static boolean isGroupMember(OrgContainer orgContainer,
			String groupName, WTPrincipal principal) {
		WTGroup group = null;
		try {
			QuerySpec queryspec = new QuerySpec(WTGroup.class);
			queryspec.appendWhere(new SearchCondition(WTGroup.class,
					"containerReference.key", "=", orgContainer
							.getPersistInfo().getObjectIdentifier()),
					new int[] {});
			queryspec.appendAnd();
			queryspec.appendWhere(new SearchCondition(WTGroup.class,
					WTGroup.NAME, "=", groupName), new int[] {});
			QueryResult qr = PersistenceHelper.manager.find(queryspec);
			if (qr.hasMoreElements()) {
				group = (WTGroup) qr.nextElement();
			}
			return OrganizationServicesHelper.manager
					.isMember(group, principal);
		} catch (QueryException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}
		return false;
	}

	
}
