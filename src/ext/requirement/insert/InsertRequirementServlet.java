package ext.requirement.insert;

import java.io.BufferedReader;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ext.ait.util.CommonUtil;
import ext.ait.util.PropertiesUtil;

public class InsertRequirementServlet implements Controller {

	private static PropertiesUtil properties = PropertiesUtil.getInstance("config.properties");

	@Override
	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// 获取传入的JSON数据
		BufferedReader reader = request.getReader();
		StringBuilder jsonInput = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			jsonInput.append(line);
		}

		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode rootNode = objectMapper.readTree(jsonInput.toString());
		JsonNode datasNode = rootNode.get("data");
		Requirement requirement = objectMapper.treeToValue(datasNode, Requirement.class);
		System.out.println("requirement: " + requirement);
		String sql = "INSERT INTO CUSTOMREQUIREMENT (IDA2A2, RICHTEXT) VALUES ( ? , ? )";
		String partId = createRequirement(requirement);
		String postsJson = objectMapper.writeValueAsString(requirement.getDescription());
		System.out.println("partId: " + partId);
		System.out.println("postsJson: " + postsJson);
		CommonUtil.excuteInsert(sql, partId, postsJson);

		// 将json转换为实体类
		// OfferingRequirement offeringRequirement = objectMapper.readValue(jsonData,
		// OfferingRequirement.class);

		// 在此处添加您的处理逻辑
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(ext.ait.util.Result.success().toString());
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
		System.out.println("offeringRequirementJson:" + offeringRequirementJson);
		String result = CommonUtil.requestInterface(url, username, password, offeringRequirementJson, "POST",
				new HashMap<String, String>() {
					{
						put("CSRF_NONCE", CommonUtil.getCSRF_NONCE(CSRFurl));
					}
				});
		System.out.println("result" + result);
		JsonNode rootNode = objectMapper.readTree(result);
		JsonNode id = rootNode.get("ID");
		return id.asText();
	}

}
