package ext.ait.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ptc.windchill.enterprise.change2.commands.RelatedChangesQueryCommands;

import ext.sap.Config;
import ext.sap.BOM.BOMBodyEntity;
import ext.sap.BOM.BOMEntity;
import wt.change2.WTChangeOrder2;
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
		String stlan = getStlan(part);
		List<BOMBodyEntity> body = getBodyEntitiesByBOM(part);
		bom.setNumber(part.getNumber());
		bom.setName(part.getName());
		bom.setHHT_BasicQuantity(Config.getHHT_BasicQuantity(part));
		bom.setUnit(Config.getValue(part.getDefaultUnit().toString()));
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
			WTPartUsageLink link = PartUtil.getLinkByPart(wtPart, part);
			entity.setName(part.getName());
			entity.setNumber(part.getNumber());
			entity.setVersion(VersionUtil.getVersion(part));
			entity.setUnit(Config.getValue(part.getDefaultUnit().toString()));
			entity.setQuantity(String.valueOf(link.getQuantity().getAmount()));
			entity.setReferenceDesignatorRange(PartUtil.getPartUsesOccurrence(link));
			entity.setHHT_SubstituteGroup(Config.getHHT_SubstituteGroup(link));
			entity.setHHT_Priority(Config.getHHT_Priority(link));
			entity.setHHT_Strategies(Config.getHHT_Strategies(link));
			entity.setHHT_UsagePossibility(Config.getHHT_UsagePossibility(link));
			entity.setHHT_MatchGroup(Config.getHHT_MatchGroup(link));
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
	public static String getJsonByEntity(BOMEntity bomEnity) {
		ObjectMapper objectMapper = new ObjectMapper();
		List<Map<String, Object>> bomBodyList = new ArrayList<>(); // 用于存储BOM_Body

		Map<String, Object> rootMap = new HashMap<>();
		Map<String, Object> bomHeadMap = new HashMap<>();
		Map<String, Object> bomBodyMap = new HashMap<>();

		// 填入BOMHead的内容
		bomHeadMap.put("MATNR", bomEnity.getNumber());
		bomHeadMap.put("WERKS", bomEnity.getFactory());
		bomHeadMap.put("MAKTX", bomEnity.getName());
		bomHeadMap.put("BMENG", bomEnity.getHHT_BasicQuantity());
		bomHeadMap.put("BMEIN", bomEnity.getUnit());
		bomHeadMap.put("VERSI", bomEnity.getVersion());
		bomHeadMap.put("AENNR", bomEnity.getECNNumber());
		bomHeadMap.put("STLAN", bomEnity.getStlan());

		// 获取BOMbody的内容并填入map
		List<BOMBodyEntity> bomBodyEntities = bomEnity.getBOMBody();
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

}
