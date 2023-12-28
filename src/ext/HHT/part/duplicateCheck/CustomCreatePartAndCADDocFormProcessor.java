package ext.HHT.part.duplicateCheck;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.FormResult;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.windchill.enterprise.part.forms.CreatePartAndCADDocFormProcessor;

import ext.HHT.Config;
import ext.HHT.part.duplicateCheck.service.BondedCheckService;
import ext.HHT.part.duplicateCheck.service.DistrbutePartService;
import ext.HHT.part.duplicateCheck.service.DuplicateCheckService;
import ext.HHT.part.duplicateCheck.service.MaterialNumberService;
import ext.classification.service.ClassificationNumber;
import wt.part.WTPart;
import wt.util.WTException;

public class CustomCreatePartAndCADDocFormProcessor extends CreatePartAndCADDocFormProcessor {

	@Override
	public FormResult doOperation(NmCommandBean nmCommandBean, List<ObjectBean> list) throws WTException {
		System.out.println("----------------创建物料保税校验----------------");
		String result = BondedCheckService.process(nmCommandBean);
		if (StringUtils.isNotBlank(result)) {
			throw new WTException(result);
		}
		System.out.println("----------------创建物料查重（长描述）----------------");
		String result2 = DuplicateCheckService.process(nmCommandBean);
		if (StringUtils.isNotBlank(result2)) {
			throw new WTException(result2);
		}

		return super.doOperation(nmCommandBean, list);
	}

	@Override
	public FormResult postProcess(NmCommandBean nmCommandBean, List<ObjectBean> list) throws WTException {
		// 怕有人看不懂
		// ObjectBean obean = list.size() > 0 ? list.get(0) : null;
		// Object obj = obean != null ? obean.getObject() : null;
		// WTPart part = obj instanceof WTPart ? (WTPart) obj : null;
		WTPart part = (list.size() > 0)
				? (list.get(0).getObject() instanceof WTPart ? (WTPart) list.get(0).getObject() : null)
				: null;
		System.out.println("----------------物料存放到对应文件夹 处理----------------");
		String result = DistrbutePartService.process(part);
		if (StringUtils.isNotBlank(result)) {
			throw new WTException(result);
		}
		System.out.println("----------------创建成品物料时更新成品正式编号----------------");
		String result2 = MaterialNumberService.process(part);
		if (StringUtils.isNotBlank(result2)) {
			throw new WTException(result2);
		}
		System.out.println("----------------创建物料生成物料编号----------------");
		if (Config.getHHT_Classification(part).startsWith("5")) {
			String result3 = ClassificationNumber.process(part);
			if (StringUtils.isNotBlank(result3)) {
				throw new WTException(result3);
			}
		}
		return super.postProcess(nmCommandBean, list);
	}

}
