package ext.HHT.part.duplicateCheck;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.FormResult;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.windchill.enterprise.part.forms.CreatePartAndCADDocFormProcessor;

import ext.ait.util.CommonUtil;
import ext.ait.util.PropertiesUtil;
import wt.part.WTPart;
import wt.util.WTException;

public class CustomCreatePartAndCADDocFormProcessor extends CreatePartAndCADDocFormProcessor {

	private static PropertiesUtil pUtil = PropertiesUtil.getInstance("config.properties");

	@Override
	public FormResult doOperation(NmCommandBean nmcommandbean, List<ObjectBean> list) throws WTException {
		System.out.println("----------------------测试CustomCreatePartAndCADDocFormProcessor.doOperation是否正确执行");
		Map<String, String> taMap = nmcommandbean.getTextArea();
		taMap.forEach((key, value) -> {
			System.out.println("doOperation TextArea-----key:" + key + " value:" + value);
		});
		Map<String, Object> comMap = nmcommandbean.getComboBox();
		comMap.forEach((key, value) -> {
			System.out.println("preProcess ComboBox-----key:" + key + " value:" + value.toString());
		});
		Object obj = list.get(0);
		System.out.println("obj" + obj);
		if ((obj != null) && (obj instanceof ObjectBean)) {
			ObjectBean ob = (ObjectBean) obj;
			obj = ob.getObject();
			WTPart part = obj instanceof WTPart ? (WTPart) obj : null;
			System.out.println("part" + part);

		}
		return super.doOperation(nmcommandbean, list);
	}

	@Override
	public FormResult preProcess(NmCommandBean nmcommandbean, List<ObjectBean> list) throws WTException {
		System.out.println("----------------------测试CustomCreatePartAndCADDocFormProcessor.preProcess是否正确执行");
		Map<String, String> taMap = nmcommandbean.getTextArea();
		taMap.forEach((key, value) -> {
			System.out.println("preProcess TextArea-----key:" + key + " value:" + value);
		});
		Map<String, Object> comMap = nmcommandbean.getComboBox();
		comMap.forEach((key, value) -> {
			System.out.println("preProcess ComboBox-----key:" + key + " value:" + value.toString());
		});
		return super.preProcess(nmcommandbean, list);
	}

	public static List<String> getAllDescription() {
		List<String> list = new ArrayList<>();
		String des = pUtil.getValueByKey("iba.internal.HHT_LongtDescription");
		String sql = "SELECT VALUE FROM STRINGVALUE WHERE IDA3A6 = (SELECT IDA2A2 FROM STRINGDEFINITION WHERE NAME = ?)";
		ResultSet resultSet = CommonUtil.excuteSelect(sql, des);
		try {
			while (resultSet.next()) {
				list.add(resultSet.getString("VALUE"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}
}
