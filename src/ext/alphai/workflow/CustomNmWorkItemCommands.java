package ext.alphai.workflow;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;

import com.ptc.core.components.forms.FormResult;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.user.NmUser;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.netmarkets.work.NmWorkItemCommands;

import ext.ait.util.PropertiesUtil;
import wt.fc.ReferenceFactory;
import wt.fc.WTReference;
import wt.project.Role;
import wt.util.WTException;
import wt.workflow.work.WfAssignedActivity;
import wt.workflow.work.WorkItem;

public class CustomNmWorkItemCommands extends NmWorkItemCommands {
	private static PropertiesUtil pUtil = PropertiesUtil.getInstance("config.properties");

	public static FormResult complete(NmCommandBean nmCommandBean) throws WTException {

		FormResult formResult = new FormResult();

		NmOid nmOid = nmCommandBean.getPageOid();
		System.out.println("J015_nmOid:" + nmOid);
		ReferenceFactory rf = new ReferenceFactory();
		WTReference reference = rf.getReference(nmOid.toString());
		System.out.println("J015_reference:" + reference.toString());
		WorkItem item = (WorkItem) reference.getObject();
		System.out.println("J015_item:" + item.toString());
		WfAssignedActivity waa = (WfAssignedActivity) item.getSource().getObject();
		System.out.println("J015_waa:" + waa.toString());
		String name = waa.getName();
		System.out.println("J015_name:" + name);

		String[] WfAssignedActivityName = pUtil.getValueByKey("WfAssignedActivityName").split(";");
		System.out.println("J015_WfAssignedActivityName:" + WfAssignedActivityName.toString());
		if (ArrayUtils.contains(WfAssignedActivityName, name)) {
			System.out.println("11111111111");
			completeName(nmCommandBean);
		}

		formResult = NmWorkItemCommands.complete(nmCommandBean);
		return formResult;

	}

	@SuppressWarnings("unchecked")
	private static void completeName(NmCommandBean nmCommandBean) throws WTException {
		System.out.println("J01_nmCommandBean:" + nmCommandBean.toString());
		NmOid nmOid = nmCommandBean.getPageOid();
		System.out.println("J02_nmOid:" + nmOid);

		Enumeration teamMembers = NmWorkItemCommands.service.getProjectMembers(nmOid);
		System.out.println("J03_teamMembers:" + teamMembers);

		while (teamMembers.hasMoreElements()) {
			NmUser member = (NmUser) teamMembers.nextElement();
			System.out.println("J04_member:" + member.getFullName());
		}

		ArrayList roleList = NmWorkItemCommands.service.getSetupParticipantsRoles(nmCommandBean, 0);
		System.out.println("J06_roleList:" + roleList);

		HashMap<Object, Object> roleMembersMap = NmWorkItemCommands.service.getRoleMembersMap(nmCommandBean);
		System.out.println("J06_roleMembersMap:" + roleMembersMap);

		List<String> list = new ArrayList();

		for (Map.Entry<Object, Object> entry : roleMembersMap.entrySet()) {
			Object key = entry.getKey();
			System.out.println("J05_key = " + key);

			Object value = entry.getValue();
			System.out.println("J05_value = " + value);

			for (Object object : roleList) {
				System.out.println("J06_role:" + object);

				if ((object.toString()).equals((key.toString()))) {
					System.out.println("J06_object = " + object);
					if ("[]".equals(value.toString())) {
						System.out.println("entry.getValue() = " + value);

						Role role = Role.toRole(key.toString());
						System.out.println("J05_role = " + role);
						String display = role.getDisplay(Locale.CHINA);
						System.out.println("J05_display = " + display);

						list.add(display);
						System.out.println("list:" + list);
					}

				}

			}
		}
		if (list != null && list.size() > 0) {
			throw new WTException("请到设置参与者标签为" + list.toString() + "角色添加相应参与者!");
		}
	}

}
