package ext.HHT.project.TrackHours;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import com.ptc.projectmanagement.assignment.ResourceAssignment;
import com.ptc.projectmanagement.assignment.resource.PlanResource;
import com.ptc.projectmanagement.plan.PlanActivity;

import ext.HHT.project.TrackHours.entity.DoneEffort;
import ext.HHT.project.TrackHours.entity.DoneEffortVO;
import ext.ait.util.CommonUtil;
import ext.ait.util.PersistenceUtil;
import wt.fc.ObjectReference;
import wt.fc.Persistable;
import wt.fc.ReferenceFactory;
import wt.inf.container.WTContainer;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.pom.WTConnection;
import wt.projmgmt.admin.Project2;

public class TrackHoursService {

	public static ResourceAssignment getResourceAssignment(String oid) {
		try {
			ReferenceFactory rf = new ReferenceFactory();
			return (ResourceAssignment) rf.getReference(oid).getObject();
		} catch (Exception e) {
			return null;
		}
	}

	public static PlanActivity getPlanActivity(ResourceAssignment resourceAssignment) {
		PlanActivity planActivity = null;
		ObjectReference reference = resourceAssignment.getParentReference();
		Persistable persistable = reference.getObject();
		if (persistable instanceof PlanActivity) {
			planActivity = (PlanActivity) persistable;
		}
		return planActivity;
	}

	public static ArrayList<ResourceAssignment> getResourceAssignments(PlanActivity planActivity) {
		ArrayList<ResourceAssignment> resultList = new ArrayList<>();
		WTConnection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		try {
			String sql = "SELECT IDA2A2 FROM RESOURCEASSIGNMENT WHERE IDA3B5 = ? ";
			String oid = String.valueOf(planActivity.getPersistInfo().getObjectIdentifier().getId());
			connection = CommonUtil.getWTConnection();
			statement = connection.prepareStatement(sql);
			statement.setString(1, oid);
			resultSet = statement.executeQuery();
			while (resultSet.next()) {
				String resourceAssignmentOid = resultSet.getString("IDA2A2");
				resourceAssignmentOid = "OR:com.ptc.projectmanagement.assignment.ResourceAssignment:"
						+ resourceAssignmentOid;
				ResourceAssignment resourceAssignment = (ResourceAssignment) PersistenceUtil
						.oid2Object(resourceAssignmentOid);
				resultList.add(resourceAssignment);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeResources(statement, resultSet);
		}
		return resultList;
	}

	public static String getDoneEffort(ResourceAssignment resourceAssignment) {
		String resOid = "OR:" + resourceAssignment.getPersistInfo().getObjectIdentifier().toString();
		ArrayList<DoneEffort> list = select(resOid);
		Double doubleDoneEffort = 0d;
		for (DoneEffort doneEffort : list) {
			doubleDoneEffort += Double.valueOf(doneEffort.getDONEEFFORT());
		}
		return String.valueOf(doubleDoneEffort) + " 工时";
	}

	public static ArrayList<DoneEffort> select(String resOid) {
		ArrayList<DoneEffort> list = new ArrayList<>();
		WTConnection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		try {
			String sql = "SELECT * FROM CUS_DONEEFFORT WHERE RESOURCEASSIGNMENTID = ? ";
			connection = CommonUtil.getWTConnection();
			statement = connection.prepareStatement(sql);
			statement.setString(1, resOid);
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
			closeResources(statement, resultSet);
		}
		return list;
	}

	public static int insertDoneEffort(DoneEffort doneEffort) {
		doneEffort.setIDA2A2(getSEQ());
		doneEffort.setTIME(getCurrentTimestamp());
		String sql = "INSERT INTO CUS_DONEEFFORT (RESOURCEASSIGNMENTID, DONEEFFORT, USERID, PLANACTIVITYID, PROJECTID, TIME, IDA2A2) VALUES ( ? , ? , ? , ? , ? , ? , ? )";
		int i = CommonUtil.excuteInsert(sql, doneEffort.getRESOURCEASSIGNMENTID(), doneEffort.getDONEEFFORT(),
				doneEffort.getUSERID(), doneEffort.getPLANACTIVITYID(), doneEffort.getPROJECTID(), doneEffort.getTIME(),
				doneEffort.getIDA2A2());
		return i;
	}

	public static String getSEQ() {
		try {
			String sql = "SELECT HHT_CUS_DONEEFFORT.NEXTVAL FROM DUAL";
			ResultSet resultSet = CommonUtil.excuteSelect(sql);
			while (resultSet.next()) {
				return resultSet.getString("NEXTVAL");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "0";
	}

	public static String getCurrentTimestamp() {
		Instant instant = Instant.now();
		ZoneId zoneId = ZoneId.of("Asia/Shanghai");
		long timestamp = instant.atZone(zoneId).toEpochSecond() * 1000;
		return String.valueOf(timestamp);
	}

	public static String parseTimestamp(String timestamp) {
		try {
			long timestampMillis = Long.parseLong(timestamp);
			Instant instant = Instant.ofEpochMilli(timestampMillis);
			ZoneId zoneId = ZoneId.of("Asia/Shanghai");
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(zoneId);
			return formatter.format(instant);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return "0";
		}
	}

	public static Project2 getProject2(ResourceAssignment resourceAssignment) {
		Project2 project = null;
		WTContainer wtContainer = resourceAssignment.getContainer();
		if (wtContainer instanceof Project2) {
			project = (Project2) wtContainer;
		}
		return project;
	}

	public static WTUser getWtUser(ResourceAssignment resourceAssignment) {
		WTUser user = null;
		PlanResource resource = (PlanResource) resourceAssignment.getResource();
		WTPrincipalReference reference = resource.getPrincipalRef();
		if (reference.getObject() instanceof WTUser) {
			user = (WTUser) reference.getObject();
		}
		return user;
	}

	public static DoneEffortVO getDoneEffortVO(ResourceAssignment resourceAssignment) {
		DoneEffortVO doneEffortVO = new DoneEffortVO();
		ArrayList<DoneEffort> list = select(
				"OR:" + resourceAssignment.getPersistInfo().getObjectIdentifier().toString());
		Double doubleDoneEffort = 0d;
		String strDoneEffort = "";
		for (DoneEffort doneEffort : list) {
			doubleDoneEffort += Double.valueOf(doneEffort.getDONEEFFORT());
		}
		strDoneEffort = String.valueOf(doubleDoneEffort);

		doneEffortVO.setPlanActivityName(TrackHoursService.getPlanActivity(resourceAssignment).getName());
		doneEffortVO.setUserName(TrackHoursService.getWtUser(resourceAssignment).getFullName());
		doneEffortVO.setPreviousDoneEffort(strDoneEffort);
		doneEffortVO.setPreviousPercentWorkComplete(String.valueOf(resourceAssignment.getPercentWorkComplete()));
		doneEffortVO.setCurrentTime(getCurrentTimestamp());
		return doneEffortVO;
	}

	public static void closeResources(PreparedStatement statement, ResultSet resultSet) {
		try {
			if (resultSet != null) {
				resultSet.close();
			}
			if (statement != null) {
				statement.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
