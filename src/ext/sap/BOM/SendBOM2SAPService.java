package ext.sap.BOM;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ptc.windchill.enterprise.change2.commands.RelatedChangesQueryCommands;

import ext.ait.util.CommonUtil;
import ext.ait.util.PartUtil;
import ext.ait.util.VersionUtil;
import ext.sap.Config;
import wt.change2.WTChangeOrder2;
import wt.fc.ObjectReference;
import wt.fc.Persistable;
import wt.fc.collections.WTCollection;
import wt.part.WTPart;
import wt.part.WTPartUsageLink;
import wt.util.WTException;

public class SendBOM2SAPService {

	/**
	 * 从WTPart中获取需要的数据并组装为BOMEntity
	 * 
	 * @param WTPart part
	 * @return BOMEntity
	 */
	public static BOMEntity getBOMEntity(WTPart part) {
		BOMEntity bom = new BOMEntity();
		List<BOMBodyEntity> body = getBodyEntitiesByBOM(part);
		bom.setNumber(part.getNumber());
		bom.setName(part.getName());
		bom.setHHT_BasicQuantity(Config.getHHT_BasicQuantity(part));
		bom.setUnit(Config.getValue(part.getDefaultUnit().toString()));
		bom.setVersion(StringUtils.substring(VersionUtil.getVersion(part), 0, 1));
		bom.setFactory(part.getViewName());
		bom.setECNNumber(geECNNumber(part));
//		bom.setStlan(getStlan(part));
		bom.setStlan("1");
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
		int substituteGroup = 65;
		List<BOMBodyEntity> list = new ArrayList<>();
		List<WTPart> partList = PartUtil.getBomByPart(wtPart);
		for (WTPart part : partList) {
//			SendBOM2SAP.sendListBOM2SAP(part);
			BOMBodyEntity entity = new BOMBodyEntity();
			WTPartUsageLink link = PartUtil.getLinkByPart(wtPart, part);
			List<BOMBodyEntity> substitutes = getSubstitutesEntity(part, wtPart);
			if (substitutes.size() > 0) {
				String substituteGroupStr = String.valueOf((char) substituteGroup);
				for (BOMBodyEntity subEntity : substitutes) {
					subEntity.setHHT_SubstituteGroup(substituteGroupStr);
					list.add(subEntity);
				}
				entity.setHHT_SubstituteGroup(substituteGroupStr);
				entity.setHHT_Strategies("1");
				entity.setHHT_UsagePossibility("100");
				substituteGroup++;
			} else {
				entity.setHHT_SubstituteGroup(Config.getHHT_SubstituteGroup(link));
				entity.setHHT_Strategies("");
				entity.setHHT_UsagePossibility("");
			}
			entity.setName(part.getName());
			entity.setNumber(part.getNumber());
			entity.setVersion(StringUtils.substring(VersionUtil.getVersion(part), 0, 1));
			entity.setUnit(Config.getValue(part.getDefaultUnit().toString()));
			entity.setQuantity(String.valueOf(link.getQuantity().getAmount()));
			entity.setReferenceDesignatorRange(PartUtil.getPartUsesOccurrence(link));
			entity.setHHT_Priority(Config.getHHT_Priority(link));

			entity.setHHT_MatchGroup(Config.getHHT_MatchGroup(link));
			list.add(entity);
		}
		return list;
	}

	/**
	 * 获取替代部件实体类
	 * 
	 * @param childPart
	 * @param fatherPart
	 * @return
	 */
	public static List<BOMBodyEntity> getSubstitutesEntity(WTPart childPart, WTPart fatherPart) {
		List<WTPart> parts = PartUtil.getSubstitutesParts(childPart, fatherPart);
		List<BOMBodyEntity> list = new ArrayList<>();
		for (WTPart part : parts) {
			BOMBodyEntity entity = new BOMBodyEntity();
			entity.setName(part.getName());
			entity.setNumber(part.getNumber());
			entity.setVersion(StringUtils.substring(VersionUtil.getVersion(part), 0, 1));
			entity.setUnit(Config.getValue(part.getDefaultUnit().toString()));
			entity.setHHT_Strategies("1");
			entity.setHHT_UsagePossibility("0");
			entity.setQuantity("1");
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
	public static String getStlan(WTPart part) {
		WTPart designPart = PartUtil.getWTPartByNumberAndView(part.getNumber(), "Design");
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
	 * 
	 * @param bomEnrity bom的实体类
	 * @return 解析得到的json
	 */
	public static String getJsonByEntity(BOMEntity bomEnrity) {
		ObjectMapper objectMapper = new ObjectMapper();
		List<Map<String, Object>> bomBodyList = new ArrayList<>(); // 用于存储BOM_Body

		Map<String, Object> rootMap = new HashMap<>();
		Map<String, Object> bomHeadMap = new HashMap<>();

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
			Map<String, Object> bomBodyMap = new HashMap<>();
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
	 * 
	 * @param json BOM的json
	 * @return 得到的返回信息
	 */
	public static String SendBOM2SAPUseUrl(String json) {
		String url = Config.getBOMUrl();
		String username = Config.getUsername();
		String password = Config.getPassword();

		return CommonUtil.requestInterface(url, username, password, json, "POST", null);

	}

	public static String getResultFromJson(String json) {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			JsonNode rootNode = objectMapper.readTree(json);
			JsonNode esMessgNode = rootNode.get("ES_MESSG");
			if (esMessgNode != null && esMessgNode.has("TYPE")) {
				String typeValue = esMessgNode.get("TYPE").asText();
				String msgValue = esMessgNode.get("MESSG").asText();
				if ("E".equals(typeValue)) {
					return "发送失败！" + msgValue;
				}
			} else {
				return "发送失败！SAP未给出错误信息!";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取部件相关联的ECN编号
	 * 
	 * @param part
	 * @return
	 */
	public static String geECNNumber(WTPart part) {
		try {
			WTCollection conllection = RelatedChangesQueryCommands.getRelatedResultingChangeNotices(part);
			for (Object object : conllection) {
				if (object instanceof ObjectReference) {
					ObjectReference pn = (ObjectReference) object;
					Persistable object2 = pn.getObject();
					if (object2 instanceof WTChangeOrder2) {
						WTChangeOrder2 wtChangeOrder2 = (WTChangeOrder2) object2;
						return wtChangeOrder2.getNumber();
					}
				}
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		return "";
	}
}
