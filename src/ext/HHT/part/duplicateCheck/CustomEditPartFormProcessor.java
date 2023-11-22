package ext.HHT.part.duplicateCheck;

import java.util.List;

import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.FormResult;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.windchill.enterprise.part.forms.EditPartFormProcessor;

import ext.HHT.part.duplicateCheck.service.BondedCheckService;
import ext.HHT.part.duplicateCheck.service.DuplicateCheckService;
import wt.util.WTException;

public class CustomEditPartFormProcessor extends EditPartFormProcessor {

	@Override
	public FormResult doOperation(NmCommandBean nmCommandBean, List<ObjectBean> list) throws WTException {
		System.out.println("----------------编辑物料保税校验----------------");
		String result = BondedCheckService.process(nmCommandBean);
		if (result.length() > 0) {
			throw new WTException(result);
		}
		System.out.println("----------------编辑物料查重（长描述）----------------");
		String result2 = DuplicateCheckService.process(nmCommandBean);
		if (result2.length() > 0) {
			throw new WTException(result2);
		}
		return super.doOperation(nmCommandBean, list);
	}
}
