package ext.requirement.edit;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import ext.ait.util.CommonUtil;
import ext.ait.util.PartUtil;
import ext.ait.util.PropertiesUtil;
import ext.ait.util.Result;
import wt.fc.ReferenceFactory;
import wt.part.WTPart;
import wt.pom.Transaction;
import wt.util.WTException;
import wt.util.WTRuntimeException;

public class editRequirementServlet implements Controller {

	private static PropertiesUtil properties = PropertiesUtil.getInstance("config.properties");

	@Override
	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		Transaction t = new Transaction();
		try {
			t.start();
			List<EditRequirement> requirements = CommonUtil.getEntitiesFromRequest(request, EditRequirement.class,
					"dataMenth");
			EditRequirement requirement = requirements.get(0);
			editRequirement(requirement);
			if (StringUtils.isNotBlank(requirement.getDescription())) {
				String sql = "UPDATE CUSTOMREQUIREMENT SET RICHTEXT = ? WHERE IDA2A2 = ?";
				String partId = requirement.getID();
				String postsJson = requirement.getDescription();
				CommonUtil.excuteUpdate(sql, postsJson, partId);
			}
		} catch (Exception e) {
			t.rollback();
			e.printStackTrace();
		} finally {
			t.commit();
		}

		response.getWriter().write(Result.success().toString());
		return null;
	}

	private void editRequirement(EditRequirement requirement) {
		Map<String, Object> map = CommonUtil.entityToMap(requirement);
		ReferenceFactory rf = new ReferenceFactory();
		try {
			WTPart part = (WTPart) rf.getReference(map.get("ID").toString()).getObject();
			for (String key : map.keySet()) {
				Object checkValue = map.get(key);
				if (checkValue != null) {
					String value = map.get(key).toString();
					if (StringUtils.isBlank(value) || key.equalsIgnoreCase("ID")
							|| key.equalsIgnoreCase("Description")) {
						continue;
					} else if (key.equalsIgnoreCase("Number")) {
						PartUtil.changePartNumber(part, value);
					} else if (key.equalsIgnoreCase("Name")) {
						PartUtil.changePartName(part, value);
					} else {
						properties.setValueByKey(part, key, value);
					}
				}
			}
		} catch (WTRuntimeException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}
	}

}
