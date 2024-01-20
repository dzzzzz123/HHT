package ext.HHT.project;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.FormResult;
import com.ptc.netmarkets.project.processor.CreatePROPLProjectFormProcessor;
import com.ptc.netmarkets.util.beans.NmCommandBean;

import ext.HHT.project.service.GenerateNumber;
import wt.projmgmt.admin.Project2;
import wt.util.WTException;

public class CustomCreatePROPLProjectFormProcessor extends CreatePROPLProjectFormProcessor {

	@Override
	public FormResult postProcess(NmCommandBean nmCommandBean, List<ObjectBean> list) throws WTException {
		Project2 project = (list.size() > 0)
				? (list.get(0).getObject() instanceof Project2 ? (Project2) list.get(0).getObject() : null)
				: null;
		System.out.println("----------------新建项目时按照规则修改项目编号----------------");
		if (project != null) {
			String result = GenerateNumber.process2(project);
			if (StringUtils.isNotBlank(result)) {
				throw new WTException(result);
			}
		}
		return super.postProcess(nmCommandBean, list);
	}

}
