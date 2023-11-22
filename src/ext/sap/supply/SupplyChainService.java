package ext.sap.supply;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import ext.ait.util.CommonUtil;
import ext.ait.util.WorkflowUtil;
import ext.sap.Config;
import ext.sap.supply.Entity.IT_MRP2;
import wt.fc.ReferenceFactory;
import wt.fc.WTObject;
import wt.part.WTPart;
import wt.util.WTException;
import wt.util.WTRuntimeException;
import wt.workflow.engine.WfProcess;
import wt.workflow.work.WorkItem;

public class SupplyChainService {

	/**
	 * 获取访问供应链信息单条数据
	 * 
	 * @param jsonInput { "I_MATNR": "230820014A" }
	 * @return
	 */
	public static Entity requestSupplyChain(String jsonInput) {
		String url = Config.getSupplyUrl();
		String username = Config.getUsername();
		String password = Config.getPassword();
		String jsonStr = CommonUtil.requestInterface(url, username, password, jsonInput.toString(), "POST", null);
		Entity entity = new Entity();
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode rootNode = objectMapper.readTree(jsonStr.toString());
			rootNode = cleanJsonNode(rootNode.get("IS_MRP1"));
			entity = objectMapper.treeToValue(rootNode, Entity.class);
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return entity;
	}

	/**
	 * 提供给jsp页面使用的方法，使用workitem的id来直接获取其对应的部件，然后获取其对应的对应供应链信息
	 * 
	 * @param workItemID
	 * @return
	 */
	public static List<Entity> requestSupplyChainList(String workItemID) {
		List<Entity> result = new ArrayList<>();
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
	 * 从wi到wf获取流程变量数据
	 * 
	 * @return
	 */
	public static List<Entity> getVar(String workItemID) {
		List<Entity> result = new ArrayList<>();
		try {
			String json = "";
			ReferenceFactory rf = new ReferenceFactory();
			WorkItem workItem = (WorkItem) rf.getReference(workItemID).getObject();
			WTObject pbo = WorkflowUtil.getPBOByWorkItem(workItem);
			WfProcess process = WorkflowUtil.getProcessByPbo(pbo);
			HashMap<String, Object> map = WorkflowUtil.getRefinedVarsByWf(process);
			for (Entry<String, Object> entry : map.entrySet()) {
				String key = entry.getKey();
				Object value = entry.getValue();
				if (Config.getValue("").equals(key)) {
					json = String.valueOf(value);
					result = CommonUtil.getEntitiesFromJson(json, Entity.class, "");
				}
			}
		} catch (WTRuntimeException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 给流程变量中的属性设置一个变量值
	 * 
	 * @param pbo
	 * @return
	 */
	public static String setVar(WTObject pbo) {
		ArrayList<WTPart> parts = (ArrayList<WTPart>) CommonUtil.getListFromPBO(pbo, WTPart.class);
		List<Entity> entities = new ArrayList<>();
		for (WTPart part : parts) {
			entities.add(requestSupplyChain("{ \"I_MATNR\": \"" + part.getNumber() + "\" }"));
		}
		return CommonUtil.getJsonFromObject(entities);
	}

	/**
	 * 获取工厂分类的数据
	 * 
	 * @param entities
	 * @param flag     2000/2100
	 * @return List<IT_MRP2>
	 */
	public static List<IT_MRP2> getIT_MRP2(List<Entity> entities, String flag) {
		List<IT_MRP2> result = new ArrayList<>();
		for (Entity entity : entities) {
			for (IT_MRP2 subEntity : entity.getIT_MRP2()) {
				if (flag.equals(subEntity.getFactory())) {
					result.add(subEntity);
				}
			}
		}
		return result;
	}

	/**
	 * 清洗数据，删除原始数据中小数点后的内容,如000，000-
	 * 
	 * @param jsonNode
	 * @return JsonNode
	 */
	private static JsonNode cleanJsonNode(JsonNode jsonNode) {
		ObjectMapper objectMapper = new ObjectMapper();
		if (jsonNode.isObject()) {
			ObjectNode cleanedObjectNode = objectMapper.createObjectNode();
			jsonNode.fields().forEachRemaining(entry -> {
				String key = entry.getKey();
				JsonNode value = entry.getValue();
				if (value.isTextual()) {
					// 使用正则表达式去除小数点后的 "000" 或 "000-"，并去除前后空格
					String cleanedValue = value.asText().replaceAll("\\s*-|\\s*\\.\\d*00\\s*", "").trim();
					cleanedObjectNode.put(key, cleanedValue);
				} else {
					cleanedObjectNode.set(key, cleanJsonNode(value));
				}
			});
			return cleanedObjectNode;
		} else if (jsonNode.isArray()) {
			ArrayNode cleanedArrayNode = objectMapper.createArrayNode();
			jsonNode.elements().forEachRemaining(element -> cleanedArrayNode.add(cleanJsonNode(element)));
			return cleanedArrayNode;
		}
		// 如果不是对象或数组，直接返回原始值
		return jsonNode;
	}

}
