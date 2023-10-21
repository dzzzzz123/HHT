package ext.oa.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class util {
	public static String getJson() {
		ObjectMapper objectMapper = new ObjectMapper();

		Map<String, Object> rootMap = new HashMap<>();
		Map<String, Object> msgMap = new HashMap<>();
		Map<String, Object> oaMap = new HashMap<>();
		Map<String, Object> headMap = new HashMap<>();
		Map<String, Object> bodyMap = new HashMap<>();
		List<Map<String, Object>> formMapList = new ArrayList<>(); // 用于存储BOM_Body

		HashMap<String, String> endValueMap = new HashMap<String, String>() {
			{
				put("任务名称：", "设计审阅");
				put("流程发起人：", "Will Cui");
				put("任务发送时间：", "2023-10-17 14:55:55");
			}
		};

		rootMap.put("msg", msgMap);
		rootMap.put("agent_id", "2766238934");
		rootMap.put("userid_list", "226220332823676905");

		msgMap.put("oa", oaMap);
		msgMap.put("msgtype", "oa");

		oaMap.put("message_url",
				"http://uat.honghe-tech.com/Windchill/app/#ptc1/tcomp/infoPage?oid=OR%3Awt.workflow.work.WorkItem%3A465920&u8=1");
		oaMap.put("head", headMap);
		oaMap.put("body", bodyMap);
		oaMap.put("msgtype", "oa");

		headMap.put("bgcolor", "FFBBBBBB");
		headMap.put("text", "PLM");

		bodyMap.put("form", formMapList);
		bodyMap.put("title", "流程任务通知");

		for (String key : endValueMap.keySet()) {
			String value = endValueMap.get(key);
			Map<String, Object> tempMap = new HashMap<>();
			tempMap.put("key", key);
			tempMap.put("value", value);
			formMapList.add(tempMap);
		}

		try {
			return objectMapper.writeValueAsString(rootMap);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return "";
	}
}
