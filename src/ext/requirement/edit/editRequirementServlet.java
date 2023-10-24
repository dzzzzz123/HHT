package ext.requirement.edit;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import ext.ait.util.CommonUtil;
import ext.ait.util.Result;
import wt.pom.Transaction;

public class editRequirementServlet implements Controller {

	@Override
	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		Transaction t = new Transaction();
		try {
			t.start();
			EditRequirement requirement = CommonUtil.getEntityFromJson(request, EditRequirement.class, "data");
			editRequirement(requirement);
			if (requirement.getDescription().length() > 0) {
				String sql = "UPDATE CUSTOMREQUIREMENT SET RICHTEXT = ? WHERE IDA2A2 = ?";
				String partId = requirement.getID();
				String postsJson = requirement.getDescription();
				CommonUtil.excuteUpdate(sql, partId, postsJson);
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
		for (String key : map.keySet()) {
			String value = map.get(key).toString();
		}
	}

}
