package ext.HHT.project.workHours.user;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import ext.HHT.project.TrackHours.TrackHoursService;
import ext.HHT.project.TrackHours.entity.DoneEffort;
import ext.ait.util.CommonUtil;
import wt.fc.ReferenceFactory;
import wt.org.WTGroup;
import wt.org.WTUser;
import wt.pom.WTConnection;

public class Servlet implements Controller {

	@Override
	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String name = "";
		int condition = 1;
		try {
			// 从HttpServletRequest获取JSON数据
			BufferedReader reader = request.getReader();
			StringBuilder jsonInput = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				jsonInput.append(line);
			}
			System.out.println("-------从HttpServletRequest获取到的内容-------");
			System.out.println("json: " + jsonInput.toString());

			// 获取name和condition的值
			JSONObject jsonData = new JSONObject(jsonInput.toString());
//			jsonData = jsonData.getJSONObject("data");

			name = jsonData.getString("name");
			condition = Integer.valueOf(jsonData.getString("condition"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("name: " + name);
		System.out.println("condition: " + condition);
		ArrayList<String> userOidList = getUserList(name);
		userOidList.forEach(System.out::println);
		String[] times = getTimeInterval(condition);
		System.out.println("times: " + times[0] + " " + times[1]);
		ArrayList<DoneEffort> list = select(userOidList, times);
		list.forEach(System.out::println);
		ArrayList<VO> VOList = getVO(list, times);
		VOList.forEach(System.out::println);
		String json = CommonUtil.getJsonFromObject(VOList);
		System.out.println("json: " + json);
		response.setCharacterEncoding("utf-8");
		response.setContentType("appliction/json;charset=utf-8");
		PrintWriter out = response.getWriter();
		out.print(json);
		out.close();
		return null;
	}
//
//	/**
//	 * 输入组名和条件返回满足条件的结果集
//	 * 
//	 * @param name
//	 * @param condition 1/2/3/4 当天/当周/当月/当年
//	 * @return
//	 */
//	public static ArrayList<VO> getWorkHours(String name, int condition) {
//		ArrayList<String> userOidList = getUserList(name);
//		String[] times = getTimeInterval(condition);
//		ArrayList<DoneEffort> list = select(userOidList, times);
//		return getVO(list, times);
//	}

	/**
	 * 获取某个组的用户的oid列表
	 * 
	 * @param name
	 * @return
	 */
	public static ArrayList<String> getUserList(String name) {
		ArrayList<String> userListString = new ArrayList<>();
		try {
			WTGroup group = ext.HHT.filter.Service.getGroup(name);
			Enumeration GroupEnum = group.members();
			while (GroupEnum.hasMoreElements()) {
				Object object = (Object) GroupEnum.nextElement();
				if (object instanceof WTUser) {
					WTUser user = (WTUser) object;
					String userOid = "OR:" + user.getPersistInfo().getObjectIdentifier().toString();
					userListString.add(userOid);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return userListString;
	}

	/**
	 * 根据给出的条件获取当天/当周/当月/当年的前后时间戳
	 * 
	 * @param condition 1/2/3/4 当天/当周/当月/当年
	 * @return
	 */
	public static String[] getTimeInterval(int condition) {
		String[] result = new String[2];
		String currentTime = TrackHoursService.getCurrentTimestamp();
		Instant instant = Instant.ofEpochMilli(Long.valueOf(currentTime));
		ZoneId zoneId = ZoneId.of("Asia/Shanghai");
		LocalDateTime dateTime = LocalDateTime.ofInstant(instant, zoneId);
		switch (condition) {
		case 1:
			LocalDateTime startOfDay = dateTime.toLocalDate().atStartOfDay();
			result[0] = String.valueOf(startOfDay.atZone(zoneId).toInstant().toEpochMilli());
			LocalDateTime endOfDay = dateTime.toLocalDate().atTime(23, 59, 59, 999999999);
			result[1] = String.valueOf(endOfDay.atZone(zoneId).toInstant().toEpochMilli());
			break;
		case 2:
			LocalDateTime startOfWeek = dateTime.toLocalDate()
					.with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY)).atStartOfDay();
			result[0] = String.valueOf(startOfWeek.atZone(zoneId).toInstant().toEpochMilli());
			LocalDateTime endOfWeek = dateTime.toLocalDate()
					.with(TemporalAdjusters.nextOrSame(java.time.DayOfWeek.SUNDAY)).atTime(23, 59, 59, 999999999);
			result[1] = String.valueOf(endOfWeek.atZone(zoneId).toInstant().toEpochMilli());
			break;
		case 3:
			LocalDateTime startOfMonth = dateTime.with(TemporalAdjusters.firstDayOfMonth()).withHour(0).withMinute(0)
					.withSecond(0).withNano(0);
			result[0] = String.valueOf(startOfMonth.atZone(zoneId).toInstant().toEpochMilli());
			LocalDateTime endOfMonth = dateTime.with(TemporalAdjusters.lastDayOfMonth()).withHour(23).withMinute(59)
					.withSecond(59).withNano(999999999);
			result[1] = String.valueOf(endOfMonth.atZone(zoneId).toInstant().toEpochMilli());
			break;
		case 4:
			LocalDateTime startOfYear = dateTime.with(TemporalAdjusters.firstDayOfYear()).withHour(0).withMinute(0)
					.withSecond(0).withNano(0);
			result[0] = String.valueOf(startOfYear.atZone(zoneId).toInstant().toEpochMilli());
			LocalDateTime endOfYear = dateTime.with(TemporalAdjusters.lastDayOfYear()).withHour(23).withMinute(59)
					.withSecond(59).withNano(999999999);
			result[1] = String.valueOf(endOfYear.atZone(zoneId).toInstant().toEpochMilli());
			break;
		default:
			throw new IllegalArgumentException("Unexpected value: " + condition);
		}
		return result;
	}

	/**
	 * 根据用户oid和开始和结束条件获取的CUS_DONEEFFORT表中的数据
	 * 
	 * @param userOidList
	 * @param times
	 * @return
	 */
	public static ArrayList<DoneEffort> select(ArrayList<String> userOidList, String[] times) {
		ArrayList<DoneEffort> list = new ArrayList<>();
		WTConnection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		try {
			String sql = processSql(userOidList);
			connection = CommonUtil.getWTConnection();
			statement = connection.prepareStatement(sql);
			statement.setString(1, times[0]);
			statement.setString(2, times[1]);
			resultSet = statement.executeQuery();
			while (resultSet.next()) {
				DoneEffort doneEffort = new DoneEffort();
				doneEffort.setRESOURCEASSIGNMENTID(resultSet.getString("RESOURCEASSIGNMENTID"));
				doneEffort.setDONEEFFORT(resultSet.getString("DONEEFFORT"));
				doneEffort.setUSERID(resultSet.getString("USERID"));
				doneEffort.setPLANACTIVITYID(resultSet.getString("PLANACTIVITYID"));
				doneEffort.setPROJECTID(resultSet.getString("PROJECTID"));
				doneEffort.setTIME(resultSet.getString("TIME"));
				doneEffort.setIDA2A2(resultSet.getString("IDA2A2"));
				list.add(doneEffort);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			TrackHoursService.closeResources(statement, resultSet);
		}
		return list;
	}

	/**
	 * 根据用户oid拼接得到sql
	 * 
	 * @param userOidList
	 * @return
	 */
	private static String processSql(ArrayList<String> userOidList) {
//		String sql = "SELECT * FROM CUS_DONEEFFORT WHERE USERID IN ('OR:wt.org.WTUser:196422', 'OR:wt.org.WTUser:233344') AND TIME BETWEEN '1705137683000' AND '1705297597000'";
		String part1 = "SELECT * FROM CUS_DONEEFFORT WHERE USERID IN (";
		StringBuffer userCondition = new StringBuffer();
		String part2 = ") AND TIME BETWEEN ? AND ? ";
		for (int i = 0; i < userOidList.size(); i++) {
			if (i == userOidList.size() - 1) {
				userCondition.append("'").append(userOidList.get(i)).append("'");
			} else {
				userCondition.append("'").append(userOidList.get(i)).append("',");
			}
		}
		return part1 + userCondition.toString() + part2;
	}

	/**
	 * 获取试图层的信息
	 * 
	 * @param list
	 * @param times
	 * @return
	 */
	public static ArrayList<VO> getVO(ArrayList<DoneEffort> list, String[] times) {
		HashMap<String, Double> DoneEffortMap = new HashMap<String, Double>();
		ArrayList<VO> resultList = new ArrayList<>();
		for (DoneEffort doneEffort : list) {
			String userOid = doneEffort.getUSERID();
			Double workHour = Double.valueOf(doneEffort.getDONEEFFORT());
			if (DoneEffortMap.containsKey(doneEffort.getUSERID())) {
				Double PreviousWorkHours = DoneEffortMap.get(userOid);
				DoneEffortMap.put(userOid, workHour + PreviousWorkHours);
			} else {
				DoneEffortMap.put(userOid, workHour);
			}
		}
		for (Map.Entry<String, Double> entry : DoneEffortMap.entrySet()) {
			try {
				String oid = entry.getKey();
				Double workHours = entry.getValue();
				ReferenceFactory rf = new ReferenceFactory();
				WTUser user = (WTUser) rf.getReference(oid).getObject();
				VO vo = new VO();
				vo.setUserName(user.getName());
				vo.setUserFullName(user.getFullName());
				vo.setDoneEffort(String.valueOf(workHours));
				vo.setBeforeTime(parseTimestamp(times[0]));
				vo.setAfterTime(parseTimestamp(times[1]));
				resultList.add(vo);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return resultList;
	}

	public static String parseTimestamp(String timestamp) {
		try {
			long timestampMillis = Long.parseLong(timestamp);
			Instant instant = Instant.ofEpochMilli(timestampMillis);
			ZoneId zoneId = ZoneId.of("Asia/Shanghai");
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日").withZone(zoneId);
			return formatter.format(instant);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return "0";
		}
	}
}
