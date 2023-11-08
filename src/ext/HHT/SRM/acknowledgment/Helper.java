package ext.HHT.SRM.acknowledgment;

import java.util.ArrayList;
import java.util.List;

import ext.ait.util.CommonUtil;
import wt.fc.WTObject;
import wt.part.WTPart;

public class Helper {

	public static List<String> process(WTObject pbo) throws Exception {
		List<WTPart> list = CommonUtil.getListFromPBO(pbo, WTPart.class);
		List<String> msg = new ArrayList<>();
		for (WTPart part : list) {
			msg.add(Service.process(part));
		}
		return msg;
	}
}
