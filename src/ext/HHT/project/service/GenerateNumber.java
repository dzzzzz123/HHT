package ext.HHT.project.service;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang3.StringUtils;

import ext.HHT.Config;
import ext.ait.util.CommonUtil;
import wt.projmgmt.admin.Project2;

public class GenerateNumber {

	/**
	 * 生成新项目编号并更新
	 * 
	 * @param project
	 * @return
	 */
	public static String process2(Project2 project) {
		String result = "";
		try {
			// String year = String.valueOf(project.getCreateTimestamp().getYear());
			// year = StringUtils.substring(year, year.length() - 2);
			String HHT_PojectYear = Config.getHHT_PojectYear(project);
			String sql = "SELECT PROJECTNUMBER FROM PMNUMBER WHERE YEAR = ?";
			ResultSet resultSet = CommonUtil.excuteSelect(sql, HHT_PojectYear);
			while (resultSet.next()) {
				String serialNumber = CommonUtil.addLead0(resultSet.getString("PROJECTNUMBER"), 3);
				HHT_PojectYear = HHT_PojectYear.substring(4);
				String newNumber = project.getCategory().toString().substring(4) + Config.getHHT_Classification(project)
						+ HHT_PojectYear + serialNumber + "-" + project.getBusinessUnit();
				Config.setHHT_ProjectNum(project, newNumber);
				// String sql2 = "UPDATE PROJECT2 SET PROJECTNUMBER = ? WHERE IDA2A2 = ? ";
				// int affectedRows = CommonUtil.excuteUpdate(sql2, newNumber,
				// String.valueOf(project.getPersistInfo().getObjectIdentifier().getId()));
				// System.out.println("affectedRows: " + affectedRows);
				String sqls = "UPDATE PMNUMBER SET PROJECTNUMBER=? where YEAR=?";
				CommonUtil.excuteUpdate(sqls, String.valueOf(Integer.parseInt(serialNumber) + 1), HHT_PojectYear);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 生成新项目编号并更新
	 * 
	 * @param project
	 * @return
	 */
	public static String process(Project2 project) {
		String result = "";
		try {
			// String year = String.valueOf(project.getCreateTimestamp().getYear());
			// year = StringUtils.substring(year, year.length() - 2);

			String HHT_PojectYear = Config.getHHT_PojectYear(project).substring(4);
			String sql = "SELECT PROJECTNUMBER FROM PMNUMBER WHERE YEAR = ?";
			ResultSet resultSet = CommonUtil.excuteSelect(sql, HHT_PojectYear);
			while (resultSet.next()) {
				String serialNumber = CommonUtil.addLead0(resultSet.getString("PROJECTNUMBER"), 3);
				String newNumber = project.getCategory().toString().substring(4) + Config.getHHT_Classification(project)
						+ HHT_PojectYear + serialNumber + "-" + project.getBusinessUnit();
				if (check(newNumber, Config.getHHT_ProjectNum(project))) {
					Config.setHHT_ProjectNum(project, newNumber);
					// String sql2 = "UPDATE PROJECT2 SET PROJECTNUMBER = ? WHERE IDA2A2 = ? ";
					// int affectedRows = CommonUtil.excuteUpdate(sql2, newNumber,
					// String.valueOf(project.getPersistInfo().getObjectIdentifier().getId()));
					// System.out.println("affectedRows: " + affectedRows);
					String sqls = "UPDATE PMNUMBER SET PROJECTNUMBER=? where YEAR=?";
					CommonUtil.excuteUpdate(sqls, String.valueOf(Integer.parseInt(serialNumber) + 1), HHT_PojectYear);
				} else {
					return "该项目编号已存在！";
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	private static boolean check(String oldNumber, String newNumber) {
		if (StringUtils.isBlank(newNumber) || StringUtils.isBlank(oldNumber)) {
			return true;
		}
		System.out.println("newNumber: " + newNumber);
		System.out.println("oldNumber: " + oldNumber);
		oldNumber = oldNumber.substring(0, oldNumber.length() - 8) + oldNumber.substring(oldNumber.length() - 5);
		newNumber = newNumber.substring(0, newNumber.length() - 8) + newNumber.substring(newNumber.length() - 5);
		System.out.println("newNumber: " + newNumber);
		System.out.println("oldNumber: " + oldNumber);
		if (StringUtils.equals(oldNumber, newNumber)) {
			return false;
		}
		return true;
	}

}
