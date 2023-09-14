package ext.sap.BOM;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ext.ait.util.PartUtil;
import ext.ait.util.PropertiesUtil;
import ext.ait.util.VersionUtil;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.part.WTPartSubstituteLink;
import wt.part.WTPartUsageLink;

public class Util {
	private static PropertiesUtil properties = PropertiesUtil.getInstance("config.properties");

	/**
	 * 校验部件是否为bom
	 * 
	 * @param WTPart wtPart
	 * @return boolean
	 */
	public static boolean verifyBOM(WTPart wtPart) {
		List<WTPart> list = PartUtil.getBomByPart(wtPart);
		return list.size() > 0 ? true : false;
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
			List<SubstituteEntity> substituteList = getAlternates(link, part);
			entity.setSubstitute(substituteList);
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
		String state = designPart.getState().toString();
		Set<String> set = new HashSet<>(Arrays.asList("PVT", "MP"));
		return set.contains(state) ? "1" : "2";
	}

	/**
	 * 获取部件的替代料信息并放到实体类中
	 * 
	 * @param WTPart wtPart
	 * @return List<SubstituteEntity>
	 */
	public static List<SubstituteEntity> getAlternates(WTPartUsageLink usageLink, WTPart wtPart) {
		System.out.println(usageLink.getPersistInfo().getObjectIdentifier().getId());
		System.out.println(wtPart.getPersistInfo().getObjectIdentifier().getId());
		List<SubstituteEntity> list = new ArrayList<>();
		List<WTPartSubstituteLink> linkList = PartUtil.getWTPartSubstituteLinks(usageLink);

		for (WTPartSubstituteLink link : linkList) {
			SubstituteEntity substituteEntity = new SubstituteEntity();
			WTPartMaster master = (WTPartMaster) link.getRoleBObject();
			WTPart substitutePart = PartUtil.getWTPartByNumber(master.getNumber());
			substituteEntity.setNumber(substitutePart.getNumber());
			substituteEntity.setUnit(substitutePart.getDefaultUnit().getDisplay());
			substituteEntity.setQuantity(String.valueOf(link.getQuantity().getAmount()));
			String HHT_Priority = properties.getStr(link, "iba.trans.HHT_Priority");
			String HHT_Strategies = properties.getStr(link, "iba.trans.HHT_Strategies");
			String HHT_UsagePossibility = properties.getStr(link, "iba.trans.HHT_UsagePossibility");
			String HHT_MatchGroup = properties.getStr(link, "iba.trans.HHT_MatchGroup");
			substituteEntity.setHHT_Priority(HHT_Priority);
			substituteEntity.setHHT_Strategies(HHT_Strategies);
			substituteEntity.setHHT_UsagePossibility(HHT_UsagePossibility);
			substituteEntity.setHHT_MatchGroup(HHT_MatchGroup);
			list.add(substituteEntity);
		}
		return list;
	}
}
