package ext.sap.BOM;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ext.ait.util.PartUtil;
import ext.ait.util.VersionUtil;
import wt.part.WTPart;
import wt.part.WTPartAlternateLink;
import wt.part.WTPartUsageLink;

public class Util {
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
			BOMBodyEntity entity = new BOMBodyEntity();
			entity.setName(part.getName());
			entity.setNumber(part.getNumber());
			entity.setVersion(VersionUtil.getVersion(part));
			entity.setUnit(PartUtil.getUnit(part));
			WTPartUsageLink link = PartUtil.getLinkByPart(wtPart, part);
			entity.setQuantity(link.getQuantity().toString());
			entity.setReferenceDesignatorRange(PartUtil.getPartUsesOccurrence(link));
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

	public static List<SubstituteEntity> getAlternates(WTPart wtPart) {
		List<SubstituteEntity> list = new ArrayList<>();
		List<WTPartAlternateLink> linkList = PartUtil.getWTPartAlternateLinks(wtPart);
		for (WTPartAlternateLink link : linkList) {

		}
		return list;
	}
}
