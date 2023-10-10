package ext.part.duplicateCheck;

import java.util.List;
import java.util.Map;

import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.FormResult;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.windchill.enterprise.part.forms.EditPartFormProcessor;

import ext.ait.util.PersistenceUtil;
import wt.part.WTPart;
import wt.util.WTException;

public class CustomEditPartFormProcessor extends EditPartFormProcessor {

	@Override
	public FormResult doOperation(NmCommandBean nmcommandbean, List<ObjectBean> list) throws WTException {
		System.out.println("----------------------测试CustomEditPartFormProcessor.doOperation是否正确执行");
		String oid = nmcommandbean.getTextParameter("oid");
		String currPartNum = "";
		WTPart currentPart = null;
		if ((!oid.equals("")) && (oid != null)) {
			currentPart = (WTPart) PersistenceUtil.oid2Object(oid);
			currPartNum = currentPart.getNumber();
			System.out.println("doOperation-----当前编辑的部件编号为：" + currPartNum);
		}
		Map<String, String> taMap = nmcommandbean.getTextArea();
		taMap.forEach((key, value) -> {
			System.out.println("doOperation-----key:" + key + " value:" + value);
		});
		return super.doOperation(nmcommandbean, list);
	}

	@Override
	public FormResult preProcess(NmCommandBean nmcommandbean, List<ObjectBean> list) throws WTException {
		System.out.println("----------------------测试CustomEditPartFormProcessor.preProcess是否正确执行");
		String oid = nmcommandbean.getTextParameter("oid");
		String currPartNum = "";
		WTPart currentPart = null;
		if ((!oid.equals("")) && (oid != null)) {
			currentPart = (WTPart) PersistenceUtil.oid2Object(oid);
			currPartNum = currentPart.getNumber();
			System.out.println("preProcess-----当前编辑的部件编号为：" + currPartNum);
		}
		Map<String, String> taMap = nmcommandbean.getTextArea();
		taMap.forEach((key, value) -> {
			System.out.println("preProcess-----key:" + key + " value:" + value);
		});

		return super.preProcess(nmcommandbean, list);
	}
}
