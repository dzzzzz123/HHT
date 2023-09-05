package ext.ait.util;

import wt.org.WTPrincipal;
import wt.org.WTUser;
import wt.session.SessionContext;
import wt.session.SessionHelper;
import wt.util.WTException;

/**
 * 用来切换管理员session工具
 * 
 *
 */
public class ChangeSession {
	private static WTUser previous = null;

	/**
	 * 切换到Administrator session
	 *
	 * @throws WTException
	 */
	public static void administratorSession() {
		try {
			previous = (WTUser) SessionHelper.manager.getPrincipal();
			WTPrincipal wtadministrator = SessionHelper.manager.getAdministrator();
			SessionContext.setEffectivePrincipal(wtadministrator);
			SessionHelper.manager.setAdministrator();
		} catch (WTException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 切换到原来的session
	 *
	 * @throws WTException
	 */
	public static void goPreviousSession() {
		if (previous != null) {
			try {
				SessionContext.setEffectivePrincipal(previous);
				SessionHelper.manager.setPrincipal(previous.getAuthenticationName());
			} catch (WTException e) {
				e.printStackTrace();
			}
		}
	}

}
