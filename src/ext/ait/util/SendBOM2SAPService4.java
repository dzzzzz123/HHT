package ext.ait.util;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
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
import wt.fc.ReferenceFactory;
import wt.fc.WTObject;
import wt.fc.collections.WTCollection;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.part.WTPart;
import wt.part.WTPartUsageLink;
import wt.util.WTException;
import wt.util.WTRuntimeException;

public class SendBOM2SAPService4 implements RemoteAccess, Serializable {

	private static final long serialVersionUID = 1L;

	public static void main(String[] args) throws WTRuntimeException, WTException {
		ReferenceFactory rf = new ReferenceFactory();
		WTPart part = (WTPart) rf.getReference("OR:wt.part.WTPart:1310780").getObject();
		List<String> flag = (List<String>) invoke("sendListBOM2SAP", SendBOM2SAPService4.class.getName(), null,
				new Class[] { WTObject.class }, new Object[] { (WTObject) part });
		flag.forEach(System.out::println);
	}

	public static Object invoke(String methodName, String className, Object instance, Class[] cla, Object[] obj) {
		RemoteMethodServer rms = RemoteMethodServer.getDefault();
		rms.setUserName("wcadmin");
		rms.setPassword("wcadmin");
		try {
			return rms.invoke(methodName, className, instance, cla, obj);
		} catch (RemoteException e) {
			e.printStackTrace();
			return null;
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static List<String> sendListBOM2SAP(WTObject obj) {
		List<WTPart> list = CommonUtil.getListFromPBO(obj, WTPart.class);
		List<WTPart> listFiltered = new ArrayList<>();
		List<String> msg = new ArrayList<>();
		// 过滤部件，判断是否为BOM
		list.forEach(part -> {
			if (PartUtil.getBomByPart(part).size() > 0) {
				listFiltered.add(part);
			}
		});
		// 从部件中获取BOM实体类，并逐个发送给SAP
		listFiltered.forEach(part -> {
			BOMEntity bomEntity = getBOMEntity(part);
			String json = getJsonByEntity(bomEntity);
			String result = SendBOM2SAPUseUrl(json);
			msg.add(getResultFromJson(result));
		});
		return msg;
	}

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
		bom.setECNNumber(geECNNumber(part));
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
			System.out.println("getBodyEntitiesByBOM --- part: " + part.getNumber());
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
			sendListBOM2SAP(part);
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
		for (BOMBodyEntity bomBodyEntity : bomBodyEntities) {
			Map<String, Object> bomBodyMap = new HashMap<>();
			System.out.println("bomBodyEntity.getNumber()" + bomBodyEntity.getNumber());
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
		}

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

	public static String geECNNumber(WTPart part) {
		try {
			WTCollection conllection = RelatedChangesQueryCommands.getRelatedResultingChangeNotices(part);
			for (Object object : conllection) {
				if (object instanceof WTChangeOrder2) {
					WTChangeOrder2 wtChangeOrder2 = (WTChangeOrder2) object;
					return wtChangeOrder2.getNumber();
				}
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		return "";
	}
}
