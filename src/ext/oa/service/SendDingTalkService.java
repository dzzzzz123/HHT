package ext.oa.service;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ext.ait.util.CommonUtil;
import ext.ait.util.PersistenceUtil;
import ext.ait.util.PropertiesUtil;
import ext.ait.util.WorkflowUtil;
import wt.fc.WTObject;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.workflow.engine.WfProcess;
import wt.workflow.work.WfAssignedActivity;
import wt.workflow.work.WorkItem;

public class SendDingTalkService {
	private static PropertiesUtil propertiesUtil = PropertiesUtil.getInstance("OAConfig.properties");

//	获取token
	public static String getToken() {
		String appkey = propertiesUtil.getValueByKey("appkey");
		String appsecret = propertiesUtil.getValueByKey("appsecret");
		String token_url = propertiesUtil.getValueByKey("token_url");
		String token = propertiesUtil.getValueByKey("token");
		System.out.println("token=" + token);
		if (StringUtils.isBlank(token)) {
			try {
				String url = token_url + "?appkey=" + URLEncoder.encode(appkey, "UTF-8") + "&appsecret="
						+ URLEncoder.encode(appsecret, "UTF-8");
				String result = CommonUtil.requestInterface(url, "", "", "", "GET", null);
				ObjectMapper objectMapper = new ObjectMapper();
				JsonNode rootNode = objectMapper.readTree(result);
				JsonNode access_token = rootNode.get("access_token");
				Map<String, String> map = new HashMap<>();
				map.put("token", access_token.asText());
				propertiesUtil.writeAll(map);
				token = access_token.asText();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return token;
	}

	public static void sendToDingtalk(WorkItem workitem) {
		String token = SendDingTalkService.getToken();
		String msg_url = propertiesUtil.getValueByKey("msg_url");
		try {
			String url = msg_url + "?access_token=" + URLEncoder.encode(token, "UTF-8");
			String json = SendDingTalkService.getDingtalkJson(workitem);

			ObjectMapper objectMapper = new ObjectMapper();
			String result = CommonUtil.requestInterface(url, "", "", json, "POST", null);
			JsonNode rootNode = objectMapper.readTree(result);
			JsonNode errcode = rootNode.get("errcode");
			String code = errcode.asText();
			// 判断token是否有效，如果失效，就重新获取token
			if (Integer.parseInt(code) != 0) {
				Map<String, String> map = new HashMap<>();
				map.put("token", "");
				propertiesUtil.writeAll(map);
				String newToken = SendDingTalkService.getToken();
				String new_url = msg_url + "?access_token=" + URLEncoder.encode(newToken, "UTF-8");
				CommonUtil.requestInterface(new_url, "", "", json, "POST", null);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static String getDingtalkJson(WorkItem workitem) {
		ObjectMapper objectMapper = new ObjectMapper();

		Map<String, Object> rootMap = new HashMap<>();

		Map<String, Object> msgMap = new HashMap<>();
		Map<String, Object> oaMap = new HashMap<>();
		Map<String, Object> headMap = new HashMap<>();
		Map<String, Object> bodyMap = new HashMap<>();
		String appid = propertiesUtil.getValueByKey("appid");
		try {
			List<Map<String, Object>> formMapList = new ArrayList<>();
			// 根据workitem获取WFProcess，流程创建者的名称，流程名称
			WTObject pbo = (WTObject) workitem.getPrimaryBusinessObject().getObject();
			WfProcess p = WorkflowUtil.getProcessByPbo(pbo);
			String creator = p.getCreator().getFullName();
			// System.out.println("========"+p.getCreator().getFullName());

			// 获取流程发送时间
			Calendar c = Calendar.getInstance();
			c.setTime(new Date());
			c.add(Calendar.HOUR_OF_DAY, 8);
			Date date = c.getTime();
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String showTime = format.format(date);
			// 根据workitem获取责任人
			WTPrincipalReference user = workitem.getOwnership().getOwner();// 用户
			WTUser tuser = (WTUser) user.getPrincipal();
			WfAssignedActivity assignedAct = (WfAssignedActivity) workitem.getSource().getObject();
			String owner = tuser.getName();
			// 将form中的key值和value值进行匹配储存
			HashMap<String, String> endValueMap = new HashMap<String, String>() {
				{
					put("任务名称：", assignedAct.getName());
					put("流程发起人：", creator);
					put("发送时间：", showTime);
				}
			};

			rootMap.put("msg", msgMap);
			rootMap.put("agent_id", appid);
			rootMap.put("userid_list", owner);

			msgMap.put("oa", oaMap);
			msgMap.put("msgtype", "oa");

			String plmUrl = URLEncoder.encode(PersistenceUtil.getPersUrl(workitem), "UTF-8");
			String msgUrl = "dingtalk://dingtalkclient/page/link?url=" + plmUrl + "&pc_slide=false";
			oaMap.put("pc_message_url", msgUrl);
			oaMap.put("head", headMap);
			oaMap.put("body", bodyMap);
			oaMap.put("msgtype", "oa");

			headMap.put("bgcolor", "FFBBBBBB");
			headMap.put("text", "PLM");

			bodyMap.put("form", formMapList);
			bodyMap.put("title", "流程任务通知");
			// 循环获取endValueMap中的key和value，并储存到formMapList中
			for (String key : endValueMap.keySet()) {
				String value = endValueMap.get(key);
				Map<String, Object> tempMap = new HashMap<>();
				tempMap.put("key", key);
				tempMap.put("value", value);
				formMapList.add(tempMap);
			}

			System.out.println("=============json=" + objectMapper.writeValueAsString(rootMap));
			return objectMapper.writeValueAsString(rootMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public static boolean isDuplicate(WorkItem item) {
		String oid = PersistenceUtil.object2Oid(item);
		String item_oid = propertiesUtil.getValueByKey("item_oid");
		if (StringUtils.isNotBlank(item_oid) && oid.equals(item_oid)) {
			return false;
		}
		Map<String, String> map = new HashMap<>();
		map.put("item_oid", oid);
		propertiesUtil.writeAll(map);
		return true;
	}
}
