package ext.HHT.project.service;

import java.sql.ResultSet;
import java.sql.SQLException;

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
	public static String process(Project2 project) {
		String result = "";
		try {
			// String year = String.valueOf(project.getCreateTimestamp().getYear());
			// year = StringUtils.substring(year, year.length() - 2);

			String HHT_PojectYear = Config.getHHT_PojectYear(project);
			String sql = "SELECT PROJECTNUMBER FROM PMNUMBER WHERE YEAR = ?";
			ResultSet resultSet = CommonUtil.excuteSelect(sql, HHT_PojectYear);
			while (resultSet.next()) {
				String serialNumber = CommonUtil.addLead0(resultSet.getString("PROJECTNUMBER"), 3);
				String newNumber = project.getCategory().toString() + Config.getHHT_Classification(project)
						+ HHT_PojectYear + serialNumber + "-" + project.getBusinessUnit();
				System.out.println("newNumber: " + newNumber);
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

}
