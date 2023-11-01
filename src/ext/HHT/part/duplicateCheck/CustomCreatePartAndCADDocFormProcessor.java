package ext.HHT.part.duplicateCheck;

import java.util.List;

import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.FormResult;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.windchill.enterprise.part.forms.CreatePartAndCADDocFormProcessor;

import wt.part.WTPart;
import wt.util.WTException;

public class CustomCreatePartAndCADDocFormProcessor extends CreatePartAndCADDocFormProcessor {

	@Override
	public FormResult doOperation(NmCommandBean nmCommandBean, List<ObjectBean> list) throws WTException {
		System.out.println("----------------创建物料查重（长描述）----------------");
		String result = DuplicateCheckHelper.process(nmCommandBean);
		if (result.length() > 0) {
			throw new WTException("当前物料系统中已存在编号为 " + result + " 的物料！");
		}
		return super.doOperation(nmCommandBean, list);
	}

	@Override
	public FormResult postProcess(NmCommandBean nmCommandBean, List<ObjectBean> list) throws WTException {
		// ObjectBean obean = list.size() > 0 ? list.get(0) : null;
		// Object obj = obean != null ? obean.getObject() : null;
		// WTPart part = obj instanceof WTPart ? (WTPart) obj : null;
		WTPart part = (list.size() > 0)
				? (list.get(0).getObject() instanceof WTPart ? (WTPart) list.get(0).getObject() : null)
				: null;
		System.out.println("----------------成品物料组，对应文件夹处理----------------");
		String result = FinishClassificationHelper.process(part);
		if (result.length() > 0) {
			throw new WTException(result);
		}
		return super.postProcess(nmCommandBean, list);
	}

}
