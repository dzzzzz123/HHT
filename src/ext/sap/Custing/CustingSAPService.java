package ext.sap.Custing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ext.ait.util.ClassificationUtil;
import ext.ait.util.CommonUtil;
import ext.ait.util.PartUtil;
import ext.ait.util.PropertiesUtil;
import ext.ait.util.VersionUtil;
import wt.part.WTPart;
import wt.part.WTPartUsageLink;
import wt.pom.WTConnection;

public class CustingSAPService {
	private static PropertiesUtil properties = PropertiesUtil.getInstance("config.properties");

	/**
	 * 从WTPart中获取需要的数据并组装为CustingEntity
	 * 
	 * @param WTPart part
	 * @return CustingEntity
	 */
	public static CustingEntity getBOMEntity(WTPart parent, WTPart part, WTConnection connection) {
		CustingEntity bom = new CustingEntity();
		bom.setNumber(part.getNumber());
		bom.setName(part.getName());
		bom.setVersion(VersionUtil.getVersion(part));
		String state = part.getState().getState().getDisplay();
		Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
		if (StringUtils.isNotBlank(state)) {
			Matcher m = p.matcher(state);
			if (m.find()) {
				bom.setStatus(state);
			} else {
				String newState = ClassificationUtil.getDisplayByInternal(state, connection);
				bom.setStatus(StringUtils.isNotBlank(newState) ? newState : state);
			}
		}
		@SuppressWarnings("static-access")
		String unit = part.getDefaultUnit().getDisplay();
		if (StringUtils.isNotBlank(unit)) {
			Matcher m = p.matcher(unit);
			if (m.find()) {
				bom.setUnit(unit);
			} else {
				String newUnit = ClassificationUtil.getDisplayByInternal(unit, connection);
				bom.setUnit(StringUtils.isNotBlank(newUnit) ? newUnit : unit);
			}
			if (unit.equals("each")) {
				bom.setUnit("每个(ea)");
			}
		}
		WTPartUsageLink link = PartUtil.getLinkByPart(parent, part);
		if (link != null && link.getQuantity() != null) {
			bom.setAmount(link.getQuantity().getAmount());
		}
		return bom;
	}

	public static String formatSapParam(List<String> list) {
		String json = "";
		Map<String, Object> request = new HashMap<>();
		List<Map<String, String>> params = new ArrayList<>();
		for (int i = 0; i < list.size(); i++) {
			Map<String, String> param = new HashMap<>();
			param.put("MATNR", list.get(i));
			params.add(param);
		}
		request.put("IT_ITEM", params);
		try {
			json = new ObjectMapper().writeValueAsString(request);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return json;
	}

	public static String getCustingFromSap(String param) {
		String result = CommonUtil.requestInterface(properties.getValueByKey("sap.custing.url"),
				properties.getValueByKey("sap.username"), properties.getValueByKey("sap.password"), param, "POST",
				null);
		return result;
	}

}
