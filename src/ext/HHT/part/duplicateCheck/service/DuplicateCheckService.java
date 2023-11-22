package ext.HHT.part.duplicateCheck.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.ptc.netmarkets.util.beans.NmCommandBean;

import ext.HHT.part.duplicateCheck.Config;
import ext.ait.util.CommonUtil;
import ext.ait.util.PersistenceUtil;
import ext.ait.util.VersionUtil;
import ext.classification.service.ClassificationDescription;
import wt.fc.WTObject;
import wt.part.WTPart;
import wt.util.WTException;

public class DuplicateCheckService {

	public static String process(NmCommandBean nmCommandBean) {
		Map<String, Object> paramMap = nmCommandBean.getParameterMap();
		String classification = "";
		for (String key : paramMap.keySet()) {
			Object value = paramMap.get(key);
			String strValue = value instanceof String[] ? ((String[]) value)[0] : value.toString();
			if (key.contains(Config.getIBA_HHT_Classification()) && key.endsWith("textbox")) {
				classification = strValue;
			}
		}

		// 当物料分类存在时,根据物料长描述进行查重
		if (!classification.equals("")) {
			Map<String, String> map = getDescriptionByClass(classification);
			String result = ClassificationDescription.process(paramMap, classification);
			if (result.isBlank()) {
				return "当前分类 " + classification + " 在配置文件中不存在!";
			}
			for (String key : map.keySet()) {
				if (key.equals(result)) {
					String oid = map.get(key);
					WTPart part = (WTPart) PersistenceUtil.oid2Object(oid);
					if (VersionUtil.isLatestIterated(part)) {
						return "当前物料系统中已存在编号为 " + part.getNumber() + " 的物料与其相同！";
					}
				}
			}
		}

		return "";
	}

	/**
	 * 获取同一分类的所有长描述信息
	 * 
	 * @param classification 分类
	 * @return 长描述和part oid组成的结果集
	 */
	public static Map<String, String> getDescriptionByClass(String classification) {
		Map<String, String> map = new HashMap<>();

		String sql = "SELECT SV.VALUE,IDA3A4 FROM STRINGVALUE SV WHERE SV.IDA3A6 = ( SELECT SD.IDA2A2 FROM STRINGDEFINITION SD WHERE SD.NAME = ? ) AND SV.IDA3A4 IN ( SELECT SV2.IDA3A4 FROM STRINGVALUE SV2 WHERE SV2.IDA3A6 = (SELECT SD2.IDA2A2 FROM STRINGDEFINITION SD2 WHERE SD2.NAME = ? ) AND SV2.VALUE = ?)";
		ResultSet resultSet = CommonUtil.excuteSelect(sql, Config.getHHT_LongtDescription(),
				Config.getHHT_Classification(), classification);
		try {
			while (resultSet.next()) {
				map.put(resultSet.getString("VALUE"), resultSet.getString("IDA3A4"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return map;
	}

	public static String processInWF(WTObject obj) throws WTException {
		String result = ClassificationDescription.process(obj);
		try {
			if (result.length() > 0) {
				throw new WTException(result);
			}
		} catch (WTException e) {
			// TODO: handle exception
		}

		return result;
	}

}
