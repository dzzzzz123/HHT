package ext.sap.BOM;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ptc.windchill.enterprise.change2.commands.RelatedChangesQueryCommands;

import ext.ait.util.CommonUtil;
import ext.ait.util.PartUtil;
import ext.ait.util.PropertiesUtil;
import ext.ait.util.VersionUtil;
import wt.change2.WTChangeOrder2;
import wt.fc.collections.WTCollection;
import wt.part.WTPart;
import wt.part.WTPartUsageLink;
import wt.util.WTException;

public class SendBOM2SAPService {

	private static PropertiesUtil properties = PropertiesUtil.getInstance("config.properties");

	/**
	 * 从WTPart中获取需要的数据并组装为BOMEntity
	 * 
	 * @param WTPart part
	 * @return BOMEntity
	 */
	public static BOMEntity getBOMEntity(WTPart part) {
		BOMEntity bom = new BOMEntity();
		String stlan = getStlan(part);
		List<BOMBodyEntity> body = getBodyEntitiesByBOM(part);
		bom.setNumber(part.getNumber());
		bom.setName(part.getName());
		bom.setHHT_BasicQuantity(properties.getValueByKey(part, "iba.internal.HHT_BasicQuantity"));
		bom.setUnit(part.getDefaultUnit().getDisplay());
		bom.setVersion(VersionUtil.getVersion(part));
		bom.setFactory(part.getViewName());
		String ECNnum = "";
		try {
			WTCollection conllection = RelatedChangesQueryCommands.getRelatedResultingChangeNotices(part);
			for (Object object : conllection) {
				if (object instanceof WTChangeOrder2) {
					WTChangeOrder2 wtChangeOrder2 = (WTChangeOrder2) object;
					ECNnum += wtChangeOrder2.getNumber();
				}
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		bom.setECNNumber(ECNnum);
		bom.setStlan(stlan);
		bom.setBOMBody(body);
		return bom;
	}

	/**
	 * 从BOM中获取信息填充到BOMBody中
	 * 
	 * @param WTPart wtPart
	 * @return List<BOMBodyEntity>
	 */
	public static List<BOMBodyEntity> getBodyEntitiesByBOM(WTPart wtPart) {
		List<BOMBodyEntity> list = new ArrayList<>();
		List<WTPart> partList = PartUtil.getBomByPart(wtPart);
		for (WTPart part : partList) {
			SendBOM2SAP.sendListBOM2SAP(part);
			BOMBodyEntity entity = new BOMBodyEntity();
			entity.setName(part.getName());
			entity.setNumber(part.getNumber());
			entity.setVersion(VersionUtil.getVersion(part));
			entity.setUnit(part.getDefaultUnit().getDisplay());
			WTPartUsageLink link = PartUtil.getLinkByPart(wtPart, part);
			entity.setQuantity(String.valueOf(link.getQuantity().getAmount()));
			entity.setReferenceDesignatorRange(PartUtil.getPartUsesOccurrence(link));

			String HHT_SubstituteGroup = properties.getValueByKey(link, "iba.internal.HHT_SubstituteGroup");
			String HHT_Priority = properties.getValueByKey(link, "iba.internal.HHT_Priority");
			String HHT_Strategies = properties.getValueByKey(link, "iba.internal.HHT_Strategies");
			String HHT_UsagePossibility = properties.getValueByKey(link, "iba.internal.HHT_UsagePossibility");
			String HHT_MatchGroup = properties.getValueByKey(link, "iba.internal.HHT_MatchGroup");
			entity.setHHT_SubstituteGroup(HHT_SubstituteGroup);
			entity.setHHT_Priority(HHT_Priority);
			entity.setHHT_Strategies(HHT_Strategies);
			entity.setHHT_UsagePossibility(HHT_UsagePossibility);
			entity.setHHT_MatchGroup(HHT_MatchGroup);
			list.add(entity);
		}
		return list;
	}

	/**
	 * 获取bom的stlan属性
	 * 
	 * @param WTPart wtPart
	 * @return String
	 */
	public static String getStlan(WTPart wtPart) {
		String number = wtPart.getNumber();
		WTPart designPart = PartUtil.getWTPartByNumberAndView(number, "Design");
		if (designPart == null) {
			return "2";
		} else {
			String state = designPart.getState().toString();
			Set<String> set = new HashSet<>(Arrays.asList("PVT", "MP"));
			return set.contains(state) ? "1" : "2";
		}
	}

	/**
	 * 从BOM的实体对象中解析出SAP需要的json字符串
	 * @param bomEnrity bom的实体类
	 * @return 解析得到的json
	 */
	public static String getJsonByEntity(BOMEntity bomEnrity) {
		ObjectMapper objectMapper = new ObjectMapper();
		List<Map<String, Object>> bomBodyList = new ArrayList<>(); // 用于存储BOM_Body

		Map<String, Object> rootMap = new HashMap<>();
		Map<String, Object> bomHeadMap = new HashMap<>();
		Map<String, Object> bomBodyMap = new HashMap<>();

		// 填入BOMHead的内容
		bomHeadMap.put("MATNR", bomEnrity.getNumber());
		bomHeadMap.put("WERKS", bomEnrity.getFactory());
		bomHeadMap.put("MAKTX", bomEnrity.getName());
		bomHeadMap.put("BMENG", bomEnrity.getHHT_BasicQuantity());
		bomHeadMap.put("BMEIN", bomEnrity.getUnit());
		bomHeadMap.put("VERSI", bomEnrity.getVersion());
		bomHeadMap.put("AENNR", bomEnrity.getECNNumber());
		bomHeadMap.put("STLAN", bomEnrity.getStlan());

		// 获取BOMbody的内容并填入map
		List<BOMBodyEntity> bomBodyEntities = bomEnrity.getBOMBody();
		bomBodyEntities.forEach(bomBodyEntity -> {

			bomBodyMap.put("IDNRK", bomBodyEntity.getNumber());
			bomBodyMap.put("MAKTX", bomBodyEntity.getName());
			bomBodyMap.put("MENGE", bomBodyEntity.getQuantity());
			bomBodyMap.put("MEINS", bomBodyEntity.getUnit());
			bomBodyMap.put("POTXT", bomBodyEntity.getReferenceDesignatorRange());

			// 获取每个部件的替代信息并填入map
			bomBodyMap.put("ALPGR", bomBodyEntity.getHHT_SubstituteGroup());
			bomBodyMap.put("ALPRF", bomBodyEntity.getHHT_Priority());
			bomBodyMap.put("ALPST", bomBodyEntity.getHHT_Strategies());
			bomBodyMap.put("EWAHR", bomBodyEntity.getHHT_UsagePossibility());
			bomBodyMap.put("EWAHR", bomBodyEntity.getHHT_UsagePossibility());

			bomBodyList.add(bomBodyMap);
		});
		rootMap.put("IS_MAST", bomHeadMap);
		rootMap.put("IT_STPO", bomBodyList);
		try {
			return objectMapper.writeValueAsString(rootMap);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 将BOM的json发送给SAP
	 * @param json BOM的json
	 * @return 得到的返回信息
	 */
	public static String SendBOM2SAPUseUrl(String json) {
		String url = properties.getValueByKey("sap.url");
		String username = properties.getValueByKey("sap.username");
		String password = properties.getValueByKey("sap.password");

		return CommonUtil.requestInterface(url, username, password, json);

	}

}
