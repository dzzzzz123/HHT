package ext.requirement.insert;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ext.ait.util.CommonUtil;
import ext.ait.util.PropertiesUtil;
import ext.ait.util.Result;
import wt.pom.Transaction;

public class InsertRequirementServlet implements Controller {

	private static PropertiesUtil properties = PropertiesUtil.getInstance("config.properties");

	@Override
	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		Transaction t = new Transaction();
		try {
			t.start();
			List<Requirement> requirements = CommonUtil.getEntitiesFromRequest(request, Requirement.class, "data");
			Requirement requirement = requirements.get(0);
			String sql = "INSERT INTO CUSTOMREQUIREMENT (IDA2A2, RICHTEXT) VALUES ( ? , ? )";
			String partId = createRequirement(requirement);
			String postsJson = requirement.getDescription();
			CommonUtil.excuteInsert(sql, partId, postsJson);
		} catch (Exception e) {
			t.rollback();
			e.printStackTrace();
		} finally {
			t.commit();
		}

		response.getWriter().write(Result.success().toString());
		return null;
	}

	// 调用Windchill REST API创建一个需求部件，并返回其中的id
	public String createRequirement(Requirement requirement) throws JsonProcessingException {
		String url = properties.getValueByKey("create.parts.url");
		String username = properties.getValueByKey("windchill.username");
		String password = properties.getValueByKey("windchill.password");
		String requirementSoftType = properties.getValueByKey("requirement.softType");
		String CSRFurl = properties.getValueByKey("CSRF.url");

		ObjectMapper objectMapper = new ObjectMapper();
		OfferingRequirement offeringRequirement = new OfferingRequirement();
		offeringRequirement.setName(requirement.getName());
		offeringRequirement.setOdataType(requirementSoftType);
		offeringRequirement.setContextOdataBind(requirement.getContext());
		offeringRequirement.setFolderOdataBind(requirement.getFolder());
		offeringRequirement.setHHTReqBelong(requirement.getHHT_ReqBelong());
		HashMap<String, String> map = new HashMap<>();
		map.put("Value", requirement.getHHT_ReqCategory());
		offeringRequirement.setHHTReqCategory(map);
		offeringRequirement.setHHTPriority(requirement.getHHT_Priority());
		offeringRequirement.setHHTReqSource(requirement.getHHT_ReqSource());
		offeringRequirement.setHHTipdReq(requirement.getHHT_ipdReq());
		offeringRequirement.setHHTCustomerRole(requirement.getHHT_CustomerRole());
		offeringRequirement.setHHTCustomerComment(requirement.getHHT_CustomerComment());

		// 将实体类转换为 JSON
		String offeringRequirementJson = objectMapper.writeValueAsString(offeringRequirement);
		String result = CommonUtil.requestInterface(url, username, password, offeringRequirementJson, "POST",
				new HashMap<String, String>() {
					{
						put("CSRF_NONCE", CommonUtil.getCSRF_NONCE(CSRFurl));
					}
				});
		JsonNode rootNode = objectMapper.readTree(result);
		JsonNode id = rootNode.get("ID");
		return id.asText();
	}

}
