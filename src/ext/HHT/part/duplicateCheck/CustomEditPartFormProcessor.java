package ext.HHT.part.duplicateCheck;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.FormResult;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.windchill.enterprise.part.forms.EditPartFormProcessor;

import ext.HHT.part.duplicateCheck.service.BondedCheckService;
import ext.HHT.part.duplicateCheck.service.DuplicateCheckService;
import ext.HHT.part.duplicateCheck.service.MaterialNumberService;
import wt.part.WTPart;
import wt.util.WTException;

public class CustomEditPartFormProcessor extends EditPartFormProcessor {

	@Override
	public FormResult doOperation(NmCommandBean nmCommandBean, List<ObjectBean> list) throws WTException {
		System.out.println("----------------编辑物料保税校验----------------");
		String result = BondedCheckService.process(nmCommandBean);
		if (StringUtils.isNotBlank(result)) {
			throw new WTException(result);
		}
		System.out.println("----------------编辑物料查重（长描述）----------------");
		String result2 = DuplicateCheckService.process(nmCommandBean);
		if (StringUtils.isNotBlank(result2)) {
			throw new WTException(result2);
		}
		return super.doOperation(nmCommandBean, list);
	}

	@Override
	public FormResult postProcess(NmCommandBean nmCommandBean, List<ObjectBean> list) throws WTException {
		WTPart part = (list.size() > 0)
				? (list.get(0).getObject() instanceof WTPart ? (WTPart) list.get(0).getObject() : null)
				: null;
		System.out.println("----------------编辑成品物料生成成品物料正式编号----------------");
		String result3 = MaterialNumberService.process(part);
		if (StringUtils.isNotBlank(result3)) {
			throw new WTException(result3);
		}
		return super.postProcess(nmCommandBean, list);
	}

}
