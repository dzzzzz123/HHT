package ext.requirement.info;

import java.sql.ResultSet;

import ext.ait.util.CommonUtil;
import ext.ait.util.PersistenceUtil;
import ext.ait.util.PropertiesUtil;
import wt.part.WTPart;

public class RequiremenrtInfoService {

	private static PropertiesUtil properties = PropertiesUtil.getInstance("config.properties");

	/**
	 * 根据部件的vr或者or来获取所对应需求的json
	 * 
	 * @param oid
	 * @return
	 */
	public static String getRequirementJsonByOid(String oid) {
		InfoRequirement infoRequirement = new InfoRequirement();
		WTPart requirement = (WTPart) PersistenceUtil.oid2Object(oid.split(":")[2]);

		infoRequirement.setName(requirement.getName());
		infoRequirement.setNumber(requirement.getNumber());
		infoRequirement.setHHTReqBelong(properties.getValueByKey(requirement, "iba.internal.HHT_ReqBelong"));
		infoRequirement.setHHTReqCategory(properties.getValueByKey(requirement, "iba.internal.HHT_ReqCategory"));
		infoRequirement.setHHTPriority(properties.getValueByKey(requirement, "iba.internal.HHT_Priority"));
		infoRequirement.setHHTReqSource(properties.getValueByKey(requirement, "iba.internal.HHT_ReqSource"));
		infoRequirement.setHHTipdReq(properties.getValueByKey(requirement, "iba.internal.HHT_ipdReq"));
		infoRequirement.setHHTReqGroup(properties.getValueByKey(requirement, "iba.internal.HHT_ReqGroup"));
		infoRequirement.setHHTCustomerRole(properties.getValueByKey(requirement, "iba.internal.HHT_CustomerRole"));
		infoRequirement
				.setHHTCustomerComment(properties.getValueByKey(requirement, "iba.internal.HHT_CustomerComment"));
		infoRequirement.setDescription(getDescriptionByID(oid));

		return CommonUtil.getJsonFromObject(infoRequirement);
	}

	/**
	 * 根据OID来获取富文本内容
	 * 
	 * @param oid
	 * @return
	 */
	public static String getDescriptionByID(String oid) {
		String description = "";
		String sql = "SELECT RICHTEXT FROM CUSTOMREQUIREMENT WHERE IDA2A2 = ?";
		try {
			ResultSet resultSet = CommonUtil.excuteSelect(sql, oid);
			while (resultSet.next()) {
				description = resultSet.getString("RICHTEXT");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return description;
	}
}
