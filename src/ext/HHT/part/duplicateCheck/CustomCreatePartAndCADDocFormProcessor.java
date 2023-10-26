package ext.HHT.part.duplicateCheck;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.FormResult;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.windchill.enterprise.part.forms.CreatePartAndCADDocFormProcessor;

import ext.ait.util.CommonUtil;
import ext.ait.util.PersistenceUtil;
import ext.ait.util.PropertiesUtil;
import ext.classification.service.ClassificationDescription;
import wt.part.WTPart;
import wt.util.WTException;

public class CustomCreatePartAndCADDocFormProcessor extends CreatePartAndCADDocFormProcessor {

	private static PropertiesUtil pUtil = PropertiesUtil.getInstance("config.properties");

	@Override
	public FormResult doOperation(NmCommandBean nmCommandBean, List<ObjectBean> list) throws WTException {
		System.out.println("----------------物料查重（长描述）----------------");
		Map<String, Object> paramMap = nmCommandBean.getParameterMap();
		String classification = "";
		for (String key : paramMap.keySet()) {
			if (key.contains(pUtil.getValueByKey("iba.paramMap.HHT_Classification")) && key.endsWith("textbox")) {
				Object value = paramMap.get(key);
				classification = value instanceof String[] ? ((String[]) value)[0] : value.toString();
				break;
			}
		}
		Map<String, String> map = getDescriptionByClass(classification);
		String result = ClassificationDescription.process(paramMap, classification);
		for (String key : map.keySet()) {
			if (key.equals(result)) {
				String oid = map.get(key);
				WTPart part = (WTPart) PersistenceUtil.oid2Object(oid);
				throw new WTException("当前物料系统中已存在编号为 " + part.getNumber() + " 的物料！");
			}
		}
		return super.doOperation(nmCommandBean, list);
	}

	/**
	 * 获取同一分类的所有长描述信息
	 * 
	 * @param classification 分类
	 * @return 长描述和part oid组成的结果集
	 */
	public static Map<String, String> getDescriptionByClass(String classification) {
		Map<String, String> map = new HashMap<>();
		String des = pUtil.getValueByKey("iba.internal.HHT_LongtDescription");
		String classify = pUtil.getValueByKey("iba.internal.HHT_Classification");

		String sql = "SELECT SV.VALUE,IDA3A4 FROM STRINGVALUE SV WHERE SV.IDA3A6 = ( SELECT SD.IDA2A2 FROM STRINGDEFINITION SD WHERE SD.NAME = ? ) AND SV.IDA3A4 IN ( SELECT SV2.IDA3A4 FROM STRINGVALUE SV2 WHERE SV2.IDA3A6 = (SELECT SD2.IDA2A2 FROM STRINGDEFINITION SD2 WHERE SD2.NAME = ? ) AND SV2.VALUE = ?)";
		ResultSet resultSet = CommonUtil.excuteSelect(sql, des, classify, classification);
		try {
			while (resultSet.next()) {
				map.put(resultSet.getString("VALUE"), resultSet.getString("IDA3A4"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return map;
	}
}
