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
	public static String process(Project2 project) {
		String result = "";
		try {
			String category = project.getCategory().toString();
			String HHT_Classification = Config.getHHT_Classification(project);
			String year = String.valueOf(project.getCreateTimestamp().getYear());
			year = StringUtils.substring(year, year.length() - 2);
			String serialNumber = "";
			String sql = "SELECT HHT_PROJECT.NEXTVAL FROM DUAL";
			ResultSet resultSet = CommonUtil.excuteSelect(sql);
			while (resultSet.next()) {
				serialNumber = resultSet.getString(1);
			}
			serialNumber = CommonUtil.addLead0(serialNumber, 3);
			String facotry = project.getBusinessUnit();
			String newNumber = category + HHT_Classification + year + serialNumber + "-" + facotry;
			Config.setHHT_ProjectNum(project, newNumber);
//			String sql2 = "UPDATE PROJECT2 SET PROJECTNUMBER = ? WHERE IDA2A2 = ? ";
//			int affectedRows = CommonUtil.excuteUpdate(sql2, newNumber,
//					String.valueOf(project.getPersistInfo().getObjectIdentifier().getId()));
//			System.out.println("affectedRows: " + affectedRows);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

}
