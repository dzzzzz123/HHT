package ext.HHT.filter;

import com.ptc.windchill.uwgm.common.container.OrganizationHelper;

import ext.HHT.Config;
import wt.inf.container.OrgContainer;
import wt.inf.container.PrincipalSpec;
import wt.inf.container.WTContainerHelper;
import wt.inf.container.WTContainerRef;
import wt.org.DirectoryContextProvider;
import wt.org.OrganizationServicesHelper;
import wt.org.WTGroup;
import wt.org.WTOrganization;

public class Service {
	public static WTGroup getGroup(String GroupName) {
		WTGroup foundGroup = null;
		try {
			WTOrganization org = OrganizationHelper.getOrganizationByName(Config.getORGName());
			OrgContainer orgContainer = WTContainerHelper.service.getOrgContainer(org);
			PrincipalSpec principalSpec = new PrincipalSpec(WTContainerRef.newWTContainerRef(orgContainer),
					WTGroup.class);
			principalSpec.setPerformLookup(false);
			principalSpec.setIncludeAllServices(true);
			DirectoryContextProvider[] directoryContextProviders = WTContainerHelper.service
					.getPublicContextProviders(principalSpec);
			foundGroup = OrganizationServicesHelper.manager.getGroup(GroupName, directoryContextProviders[0]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return foundGroup;
	}
}
