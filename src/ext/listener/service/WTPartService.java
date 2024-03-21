package ext.listener.service;

import org.apache.commons.lang3.StringUtils;

import ext.ait.util.PersistenceUtil;
import ext.ait.util.VersionUtil;
import ext.listener.Config;
import wt.fc.ReferenceFactory;
import wt.inf.container.OrgContainer;
import wt.inf.container.WTContainerRef;
import wt.lifecycle.LifeCycleException;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.LifeCycleTemplate;
import wt.part.WTPart;
import wt.util.WTException;

public class WTPartService {

	/**
	 * 部件持久化之前的操作
	 * 
	 * @param part
	 * @throws WTException
	 */
	public static void process_POST_STORE(WTPart part) throws WTException {
		System.out.println("wtpart: " + part + " class: " + Config.getHHT_Classification(part) + " source: "
				+ part.getSource().toString() + " Version: " + VersionUtil.getVersion(part) + " SAP_Mark: "
				+ Config.getHHT_SapMark(part));
		String version = VersionUtil.getVersion(part);
		String SAPMark = Config.getHHT_SapMark(part);
		if (!PersistenceUtil.isCheckOut(part) && version.equals("A.1") && SAPMark.equals("X")) {
			System.out.println("Part: " + part.getName() + " Number: " + part.getNumber() + " 的SAP标识已去除!");
			Config.setHHT_SapMark(part, "");
		}
		if (Config.getHHT_Classification(part).startsWith("5") && part.getSource().toString().equals(Config.getBuy())) {
			String result = changeStateTemp(part);
			if (StringUtils.isNotBlank(result)) {
				throw new WTException(result);
			}
		}
	}

	/**
	 * 修改生命周期模板
	 * 
	 * @param part
	 * @return
	 */
	public static String changeStateTemp(WTPart part) {
		try {
			ReferenceFactory rf = new ReferenceFactory();
			OrgContainer orgContainer = (OrgContainer) rf.getReference(Config.getORGID()).getObject();
			WTContainerRef wtContainerRef = WTContainerRef.newWTContainerRef(orgContainer);
			LifeCycleTemplate template = LifeCycleHelper.service.getLifeCycleTemplate(Config.getLFName(),
					wtContainerRef);
			LifeCycleHelper.service.reassign(part, template.getLifeCycleTemplateReference());
			// 不需要重新设置部件子类型
			// TypeDefinitionReference tdr =
			// ClientTypedUtility.getTypeDefinitionReference(Config.getPESType());
			// part.setTypeDefinitionReference(tdr);
			// PersistenceServerHelper.manager.update(part);
			// LifeCycleHelper.setLifeCycle(part, template.getLifeCycleTemplateReference());
		} catch (LifeCycleException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}
		return null;
	}
}
