package ext.sap.supply;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ext.ait.util.CommonUtil;
import ext.ait.util.Result;
import ext.ait.util.WorkflowUtil;
import ext.sap.Config;
import wt.fc.ReferenceFactory;
import wt.fc.WTObject;
import wt.part.WTPart;
import wt.util.WTException;
import wt.util.WTRuntimeException;
import wt.workflow.work.WorkItem;

public class SupplyChainServlet implements Controller {

	@Override
	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");

		try {
			BufferedReader reader = request.getReader();
			StringBuilder jsonInput = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				jsonInput.append(line);
			}

			String result = requestSupplyChain(jsonInput.toString());
			response.getWriter().write(result.toString());
		} catch (IOException e) {
			e.printStackTrace();
			response.getWriter().write(Result.error().toString());
		}
		return null;
	}

	/**
	 * 获取访问供应链信息单条数据
	 * 
	 * @param jsonInput { "I_MATNR": "230820014A" }
	 * @return
	 */
	public static String requestSupplyChain(String jsonInput) {
		String url = Config.getSupplyUrl();
		String username = Config.getUsername();
		String password = Config.getPassword();
		String jsonStr = CommonUtil.requestInterface(url, username, password, jsonInput.toString(), "POST", null);
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode jsonNode = objectMapper.readTree(jsonStr);
			cleanJsonNode(jsonNode);
			return objectMapper.writeValueAsString(jsonNode);
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return jsonStr;
	}

	/**
	 * 提供给jsp页面使用的方法，使用workitem的id来直接获取其对应的部件，然后获取其对应的对应供应链信息
	 * 
	 * @param workItemID
	 * @return
	 */
	public static List<String> requestSupplyChainList(String workItemID) {
		List<String> result = new ArrayList<>();
		try {
			ReferenceFactory rf = new ReferenceFactory();
			WorkItem workItem = (WorkItem) rf.getReference(workItemID).getObject();
			WTObject pbo = WorkflowUtil.getPBOByWorkItem(workItem);
			ArrayList<WTPart> parts = WorkflowUtil.getTargerObject(pbo, "AffectedObjects", WTPart.class);
			parts.forEach(part -> {
				result.add(requestSupplyChain("{ \"I_MATNR\": \"" + part.getNumber() + "\" }"));
			});
		} catch (WTRuntimeException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 清洗数据，删除原始数据中小数点后的内容,如000，000-
	 * 
	 * @param jsonNode
	 */
	private static void cleanJsonNode(JsonNode jsonNode) {
		if (jsonNode.isObject()) {
			jsonNode.fields().forEachRemaining(entry -> {
				String key = entry.getKey();
				JsonNode value = entry.getValue();
				if (value.isTextual()) {
					// 使用正则表达式去除小数点后的 "000" 或 "000-"，并去除前后空格
					String cleanedValue = value.asText().replaceAll("\\s*-|\\s*\\.\\d*00\\s*", "").trim();
					((com.fasterxml.jackson.databind.node.ObjectNode) jsonNode).put(key, cleanedValue);
				} else {
					cleanJsonNode(value);
				}
			});
		} else if (jsonNode.isArray()) {
			jsonNode.elements().forEachRemaining(element -> cleanJsonNode(element));
		}
	}

}
