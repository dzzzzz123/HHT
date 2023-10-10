package ext.part.duplicateCheck;

import java.util.List;
import java.util.Map;

import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.FormResult;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.windchill.enterprise.part.forms.CreatePartAndCADDocFormProcessor;

import wt.util.WTException;

public class CustomCreatePartAndCADDocFormProcessor extends CreatePartAndCADDocFormProcessor {

	@Override
	public FormResult doOperation(NmCommandBean nmcommandbean, List<ObjectBean> list) throws WTException {
		System.out.println("----------------------测试CustomCreatePartAndCADDocFormProcessor.doOperation是否正确执行");
		Map<String, String> taMap = nmcommandbean.getTextArea();
		taMap.forEach((key, value) -> {
			System.out.println("doOperation TextArea-----key:" + key + " value:" + value);
		});
		return super.doOperation(nmcommandbean, list);
	}

	@Override
	public FormResult preProcess(NmCommandBean nmcommandbean, List<ObjectBean> list) throws WTException {
		System.out.println("----------------------测试CustomCreatePartAndCADDocFormProcessor.preProcess是否正确执行");
		Map<String, String> taMap = nmcommandbean.getTextArea();
		taMap.forEach((key, value) -> {
			System.out.println("preProcess TextArea-----key:" + key + " value:" + value);
		});
		return super.preProcess(nmcommandbean, list);
	}

}
