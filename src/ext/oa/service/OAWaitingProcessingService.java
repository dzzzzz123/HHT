package ext.oa.service;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ext.ait.util.CommonUtil;
import ext.ait.util.PersistenceUtil;
import ext.ait.util.PropertiesUtil;
import ext.oa.entity.OADeleteTaskEntity;
import ext.oa.entity.OAWaitingProcessingEntity;
import wt.fc.ReferenceFactory;
import wt.httpgw.URLFactory;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.workflow.work.WfAssignedActivity;
import wt.workflow.work.WorkItem;

public class OAWaitingProcessingService {
	private static PropertiesUtil propertiesUtil = PropertiesUtil.getInstance("OAConfig.properties");

	public static Set<String> workitem_set = null;
	public static Set<String> workitem_set_delete = null;
	public static Set<String> workitem_set_TurnTo = null;

	public static void sendTaskToOA(WorkItem workitem, ProcessStatus processStatus) {

		if (ProcessStatus.COMPLETED.toString().equals(processStatus.toString())) {
			sendDeleteTaskToOA(workitem);
			executionMethod(workitem, processStatus);
			return;
		}

//		if (workitem_set == null) {
//			workitem_set = new HashSet<String>();
//		}
//		String id = "" + workitem.getPersistInfo().getObjectIdentifier().getId();
//		if (workitem_set.size() > 200) {
//			workitem_set.clear();
//		}
//		WTPrincipalReference user = workitem.getOwnership().getOwner();// 用户
//		id = id + "_" + user.getName();
//		int count = workitem_set.size();
//		System.out.println("id>>>" + id);
//		workitem_set.add(id);
//		// 相同用户+任务监听重复的情况
//		if (workitem_set.size() == count) {
//			System.out.println("-----this workitem had send app:" + id);
//			return;
//		}
//		System.out.println("---------sendTaskToOA workitem:" + workitem);
//		sendDeleteTaskToOA(workitem);
		executionMethod(workitem, processStatus);

	}

	public static void executionMethod(WorkItem workitem, ProcessStatus processStatus) {
		String SYSCODE = propertiesUtil.getValueByKey("syscode");
		String WORKFLOWNAME = propertiesUtil.getValueByKey("workflowname");
		String NODENAME = propertiesUtil.getValueByKey("nodename");
		String NEWTASK_URL = propertiesUtil.getValueByKey("newTask_url");
		try {
			ReferenceFactory ref = new ReferenceFactory();
			WfAssignedActivity assignedAct = (WfAssignedActivity) workitem.getSource().getObject();

			if (assignedAct.getDeadline() != null) {

			}
			String taskName = assignedAct.getName();// 任务名称

			URLFactory factory = new URLFactory();
			String url = factory.getHREF("servlet/TypeBasedIncludeServlet") + "?oid="
					+ ref.getReferenceString(workitem);
			WTPrincipalReference user = workitem.getOwnership().getOwner();// 用户
			WTUser tuser = (WTUser) user.getPrincipal();
			System.out.println("----taskName:" + taskName + "--user:" + tuser.getName() + "--url:" + url);

			OAWaitingProcessingEntity pend = new OAWaitingProcessingEntity();

			System.out.println(workitem.getPersistInfo().getObjectIdentifier().getId());

			System.out.println(taskName);
			System.out.println(tuser.getName());
			System.out.println(PersistenceUtil.getPersUrl(workitem));
			pend.setSyscode(SYSCODE);
			pend.setFlowid(String.valueOf(workitem.getPersistInfo().getObjectIdentifier().getId()));
			pend.setRequestname(assignedAct.getParentProcess().getName());
			pend.setWorkflowname(WORKFLOWNAME);
			pend.setNodename(NODENAME);
			pend.setPcurl(PersistenceUtil.getPersUrl(workitem));
			pend.setAppurl(PersistenceUtil.getPersUrl(workitem));
			pend.setCreator(tuser.getName());

			Calendar c = Calendar.getInstance();
			c.setTime(new Date());
			c.add(Calendar.HOUR_OF_DAY, 8);
			Date date = c.getTime();
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String showTime = format.format(date);
			System.out.println(showTime);
			pend.setCreatedatetime(showTime);

			pend.setReceiver(tuser.getName());
			pend.setReceivedatetime(showTime);

			if (processStatus.toString().equals(ProcessStatus.COMPLETED.toString())) {
				pend.setIsremark("2");
				pend.setViewtype("1");
			} else {
				pend.setIsremark("0");
				pend.setViewtype("0");
			}
			long timestamp = System.currentTimeMillis();
			pend.setReceivets(String.valueOf(timestamp));
			Map<String, Object> map = convertToMap(pend);
			// OA接口
			System.out.println(map);
			ObjectMapper mapper = new ObjectMapper();
			System.out.println(mapper.writeValueAsString(map));

			System.out.println(sendNewTask(mapper.writeValueAsString(map), NEWTASK_URL));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void sendDeleteTaskToOA(WorkItem workitem) {
		String SYSCODE = propertiesUtil.getValueByKey("syscode");
		String DELETE_URL = propertiesUtil.getValueByKey("delete_url");
		try {

			if (workitem_set_delete == null) {
				workitem_set_delete = new HashSet<String>();
			}
			String id = "" + workitem.getPersistInfo().getObjectIdentifier().getId();
			if (workitem_set_delete.size() > 200) {
				workitem_set_delete.clear();
			}
			WTPrincipalReference user = workitem.getOwnership().getOwner();// 用户
			id = id + "_" + user.getName();
			int count = workitem_set_delete.size();
			System.out.println("id>>>" + id);
			workitem_set_delete.add(id);
			// 相同用户+任务监听重复的情况
			if (workitem_set_delete.size() == count) {
				System.out.println("-----this workitem had send app Delete:" + id);
				return;
			}
			System.out.println("---------sendDeleteToOA workitem:" + workitem);

			WTUser tuser = (WTUser) user.getPrincipal();
			String userid = tuser.getName();

			OADeleteTaskEntity oaDeleteTaskEntity = new OADeleteTaskEntity();

			oaDeleteTaskEntity.setFlowid(String.valueOf(workitem.getPersistInfo().getObjectIdentifier().getId()));
			oaDeleteTaskEntity.setSyscode(SYSCODE);
			oaDeleteTaskEntity.setUserid(userid);

			Map<String, Object> map = convertToMap(oaDeleteTaskEntity);
			ObjectMapper mapper = new ObjectMapper();
			System.out.println(mapper.writeValueAsString(map));
			System.out.println(sendNewTask(mapper.writeValueAsString(map), DELETE_URL));

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void sendTurnToTaskToOA(WorkItem workitem) {
		String SYSCODE = propertiesUtil.getValueByKey("syscode");
		String DELETE_URL = propertiesUtil.getValueByKey("delete_url");
		try {

			if (workitem_set_TurnTo == null) {
				workitem_set_TurnTo = new HashSet<String>();
			}
			String id = "" + workitem.getPersistInfo().getObjectIdentifier().getId();
			if (workitem_set_TurnTo.size() > 200) {
				workitem_set_TurnTo.clear();
			}
			WTPrincipalReference user = workitem.getOwnership().getOwner();// 用户
			id = id + "_" + user.getName();
			int count = workitem_set_TurnTo.size();
			System.out.println("id>>>" + id);
			workitem_set_TurnTo.add(id);
			// 相同用户+任务监听重复的情况
			if (workitem_set_TurnTo.size() == count) {
				System.out.println("-----this workitem had send app Turn:" + id);
				return;
			}
			System.out.println("---------sendTurnToOA workitem:" + workitem);

			WTUser tuser = (WTUser) user.getPrincipal();
			String userid = tuser.getName();

			OADeleteTaskEntity oaDeleteTaskEntity = new OADeleteTaskEntity();

			oaDeleteTaskEntity.setFlowid(String.valueOf(workitem.getPersistInfo().getObjectIdentifier().getId()));
			oaDeleteTaskEntity.setSyscode(SYSCODE);
			oaDeleteTaskEntity.setUserid(userid);

			Map<String, Object> map = convertToMap(oaDeleteTaskEntity);
			ObjectMapper mapper = new ObjectMapper();
			System.out.println(mapper.writeValueAsString(map));
			System.out.println(sendNewTask(mapper.writeValueAsString(map), DELETE_URL));

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static String sendNewTask(String paramJson, String url) {
		try {
			String result = CommonUtil.requestInterface(url, "", "", paramJson);
			System.out.println(result);
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode rootNode = objectMapper.readTree(result);
			String operResult = rootNode.get("operResult").asText();
			String message = rootNode.get("message").asText();
			if (operResult.equals("1")) {
				return "OK";
			} else {
				return message;
			}
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return "";
	}

	public static Map<String, Object> convertToMap(Object entity) {
		Map<String, Object> map = new HashMap<>();

		// 获取实体类的所有字段
		Field[] fields = entity.getClass().getDeclaredFields();
		for (Field field : fields) {
			field.setAccessible(true); // 设置字段为可访问

			String fieldName = field.getName();
			Object fieldValue;
			try {
				fieldValue = field.get(entity); // 获取字段的值
			} catch (IllegalAccessException e) {
				fieldValue = null;
			}

			map.put(fieldName, fieldValue);
		}

		return map;
	}

}
