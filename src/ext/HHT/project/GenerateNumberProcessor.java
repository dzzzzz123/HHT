package ext.HHT.project;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.DefaultObjectFormProcessor;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.core.ui.resources.FeedbackType;
import com.ptc.netmarkets.util.beans.NmCommandBean;

import ext.HHT.project.service.GenerateNumber;
import ext.ait.util.CommonUtil;
import wt.fc.WTObject;
import wt.projmgmt.admin.Project2;
import wt.session.SessionHelper;
import wt.util.WTException;

public class GenerateNumberProcessor extends DefaultObjectFormProcessor {

	@Override
	public FormResult doOperation(NmCommandBean arg0, List<ObjectBean> arg1) throws WTException {
		WTObject ref = (WTObject) arg0.getPrimaryOid().getRef();
		FormResult formresult = null;

		try {
			List<Project2> list = CommonUtil.getListFromPBO(ref, Project2.class);
			if (list.size() == 1) {
				String msg = GenerateNumber.process(list.get(0));
				if (StringUtils.isNotBlank(msg)) {
					formresult = new FormResult(FormProcessingStatus.FAILURE);
					formresult.addFeedbackMessage(new FeedbackMessage(FeedbackType.FAILURE, SessionHelper.getLocale(),
							null, null, new String[] { msg }));
					return formresult;
				}
			}
		} catch (Exception e) {
			formresult = new FormResult(FormProcessingStatus.FAILURE);
			formresult.addFeedbackMessage(new FeedbackMessage(FeedbackType.FAILURE, SessionHelper.getLocale(), null,
					null, new String[] { "生成项目编号失败，请联系管理员！", e.getMessage() }));
			e.printStackTrace();
			return formresult;
		}

		formresult = new FormResult(FormProcessingStatus.SUCCESS);
		formresult.addFeedbackMessage(new FeedbackMessage(FeedbackType.SUCCESS, SessionHelper.getLocale(), null, null,
				new String[] { "生成项目编号成功!" }));
		return formresult;

	}

}
