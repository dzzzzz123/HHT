package ext.sap.project;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ext.ait.util.CommonUtil;
import ext.sap.Config;
import wt.projmgmt.admin.Project2;
import wt.util.WTException;

public class SendProject2SAPService {

	/**
	 * 从WTPart中获取需要的数据并组装为BOMEntity
	 * 
	 * @param WTPart part
	 * @return BOMEntity
	 * @throws WTException
	 */
	public static ProjectEntity getProjectEntity(Project2 project) throws WTException {
		ProjectEntity projectEntity = new ProjectEntity();
		projectEntity.setProjectNumber(project.getProjectNumber());
		projectEntity.setProjectName(project.getName());
		projectEntity.setProjectCategory(getCategory(project.getCategory().getDisplay()));
		projectEntity.setProjectOwner(project.getOwner().getName());
		projectEntity.setProjectCreateStamp(
				new SimpleDateFormat("yyyyMMdd").format(new Date(project.getCreateTimestamp().getTime())));

		Timestamp endTime = project.getEstimatedEndDate();
		if (endTime == null) {
			throw new WTException();
		} else {
			projectEntity.setProjectEndStamp(new SimpleDateFormat("yyyyMMdd").format(new Date(endTime.getTime())));
		}
		projectEntity.setFactoryCode(project.getBusinessUnit());
		projectEntity.setProjectDescription(project.getDescription());
		projectEntity.setFinishFlag(project.getContainerTeamManagedInfo().getState().getDisplay());
		return projectEntity;
	}

	/**
	 * 从entity中解析获取SAP需要的json
	 * 
	 * @param entity
	 * @return
	 */
	public static String getJsonByEntity(ProjectEntity entity) {
		ObjectMapper objectMapper = new ObjectMapper();
		Map<String, Object> rootMap = new HashMap<>();
		Map<String, Object> nodeMap = new HashMap<>();

		nodeMap.put("PSPNR", entity.getProjectNumber());
		nodeMap.put("POST1", entity.getProjectName());
		nodeMap.put("PRART", entity.getProjectCategory());
		nodeMap.put("USR01", entity.getProjectOwner());
		nodeMap.put("PLFAZ", entity.getProjectCreateStamp());
		nodeMap.put("PLSEZ", entity.getProjectEndStamp());
		nodeMap.put("BUKRS", entity.getFactoryCode());
		nodeMap.put("POSID", entity.getProjectDescription());
		nodeMap.put("ZWJBS", entity.getFinishFlag());

		rootMap.put("IS_OBJECT", nodeMap);
		try {
			return objectMapper.writeValueAsString(rootMap);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return "";
	}

	public static String getCategory(String category) {
		switch (category) {
		case "A类":
			return "10";
		case "B类":
			return "20";
		case "C类":
			return "30";
		case "D类":
			return "40";
		default:
			return "10";
		}
	}

	/**
	 * 使用springframe的模板发送post请求传输数据给sap对应接口
	 * 
	 * @param json 发送给sap的json
	 * @return String 返回的结果集
	 */
	public static String sendProject2SAPUseUrl(String json) {
		String url = Config.getProjectUrl();
		String username = Config.getUsername();
		String password = Config.getPassword();
		return CommonUtil.requestInterface(url, username, password, json, "POST", null);
	}

	public static String getResultFromJson(String json) {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			JsonNode rootNode = objectMapper.readTree(json);
			JsonNode esMessgNode = rootNode.get("ES_MESSG");
			if (esMessgNode != null && esMessgNode.has("TYPE")) {
				String typeValue = esMessgNode.get("TYPE").asText();
				String msgValue = esMessgNode.get("MESSG").asText();
				if ("E".equals(typeValue)) {
					return "发送失败！" + msgValue;
				}
			} else {
				return "发送失败！SAP未给出错误信息!";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
