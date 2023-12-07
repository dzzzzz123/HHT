package ext.HHT.BOMDiff;

import java.util.ArrayList;
import java.util.List;

import ext.ait.util.CommonUtil;
import ext.ait.util.PartUtil;
import wt.fc.WTObject;
import wt.part.WTPart;

public class Service {

	public static void process(WTObject obj) {
		List<WTPart> list = CommonUtil.getListFromPBO(obj, WTPart.class);

	}

	public static void genExcel() {

	}

	public static List<Entity> getBOMDiff(WTPart partA, WTPart partB) {
		List<Entity> list = new ArrayList<>();
		List<WTPart> listA = PartUtil.getAllBomByPart(partA);
		List<WTPart> listB = PartUtil.getAllBomByPart(partB);

		return list;
	}
}
