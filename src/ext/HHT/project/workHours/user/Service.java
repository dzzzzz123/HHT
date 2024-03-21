package ext.HHT.project.workHours.user;

import java.util.ArrayList;
import java.util.Enumeration;

import org.apache.commons.lang3.StringUtils;

import wt.org.OrganizationServicesHelper;
import wt.org.WTGroup;

public class Service {

	/**
	 * 获取所有描述中包含了 工时 的组
	 * 
	 * @return
	 */
	public static ArrayList<String> getGroups() {
		ArrayList<String> groups = new ArrayList<>();
		try {
			String[] services = wt.org.OrganizationServicesHelper.manager.getDirectoryServiceNames();
			wt.org.DirectoryContextProvider dcp = wt.org.OrganizationServicesHelper.manager
					.newDirectoryContextProvider(services, null);
			dcp.setSearchScope(dcp.getPrimaryService(), wt.org.OrganizationServicesHelper.ALL_CONTAINERS);
			Enumeration enums = OrganizationServicesHelper.manager.findLikeGroups("*", dcp);
//			Enumeration enums = OrganizationServicesHelper.manager.allGroups();
			while (enums.hasMoreElements()) {
				Object object = (Object) enums.nextElement();
				if (object instanceof WTGroup) {
					WTGroup group = (WTGroup) object;
					String description = group.getDescription();
					if (StringUtils.isNotBlank(description) && description.contains("工时")) {
						groups.add(group.getName());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return groups;
	}

}
