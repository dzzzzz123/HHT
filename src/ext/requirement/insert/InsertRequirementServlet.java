package ext.requirement.insert;

import java.io.BufferedReader;
import java.nio.charset.Charset;
import java.util.Collections;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ext.ait.util.CommonUtil;

public class InsertRequirementServlet implements Controller {

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
		JsonNode datasNode = rootNode.get("datas");
		Requirement requirement = objectMapper.treeToValue(datasNode, Requirement.class);

		String sql = "INSERT INTO CUSTOMREQUIREMENT (IDA2A2, RICHTEXT) VALUES ( ? , ? )";
		String partId = createRequirement(requirement);
		String postsJson = objectMapper.writeValueAsString(requirement.getDescription());
		CommonUtil.excuteInsert(sql, partId, postsJson);

		// 将json转换为实体类
		//	OfferingRequirement offeringRequirement = objectMapper.readValue(jsonData, OfferingRequirement.class);

		// 在此处添加您的处理逻辑
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(ext.ait.util.Result.success().toString());
		return null;
	}

	// 调用Windchill REST API创建一个需求部件，并返回其中的id
	public String createRequirement(Requirement requirement) throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		String url = "http://tplm.honghe-tech.com:80/Windchill/servlet/odata/v5/ProdMgmt/Parts";
		OfferingRequirement offeringRequirement = new OfferingRequirement();
		offeringRequirement.setNumber(requirement.getNumber());
		offeringRequirement.setName(requirement.getName());
		offeringRequirement.setOdataType("#PTC.ProdMgmt.com.honghe_tech.HHTOfferingRequirement");
		offeringRequirement.setContextOdataBind(requirement.getContext());
		offeringRequirement.setFolderOdataBind(requirement.getFolder());
		offeringRequirement.setDescription(requirement.getDescription());
		// 将实体类转换为 JSON
		String offeringRequirementJson = objectMapper.writeValueAsString(offeringRequirement);
		System.out.println("offeringRequirementJson" + offeringRequirementJson);
		String result = createPart(url, "wcadmin", "wcadmin", offeringRequirementJson);
		System.out.println("result" + result);
		JsonNode rootNode = objectMapper.readTree(result);
		JsonNode id = rootNode.get("ID");
		return id.asText();
	}

	private String createPart(String url, String username, String password, String json) {
		// 自定义请求头
		RestTemplate restTemplate = new RestTemplate();
		if (StringUtils.isNotBlank(password) && StringUtils.isNotBlank(username)) {
			restTemplate.getInterceptors().add(new BasicAuthenticationInterceptor(username, password));
		}
		restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(Charset.forName("utf-8")));
		HttpHeaders headers = new HttpHeaders();
		headers.add("CSRF_NONCE", getCSRF_NONCE());
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAcceptCharset(Collections.singletonList(Charset.forName("utf-8")));
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

		// 参数
		HttpEntity<String> entity = new HttpEntity<String>(json, headers);
		ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

		if (responseEntity == null) {
			return null;
		}

		return responseEntity.getBody().toString();
	}

	// 获取CSRF_NONCE（token）
	public String getCSRF_NONCE() {
		String url = "http://tplm.honghe-tech.com/Windchill/servlet/odata/PTC/GetCSRFToken()";
		String result = CommonUtil.requestInterface(url, "wcadmin", "wcadmin", "", "GET");
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			JsonNode rootNode = objectMapper.readTree(result);
			JsonNode esMessgNode = rootNode.get("NonceValue");
			return esMessgNode.asText();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return "";
	}
}
