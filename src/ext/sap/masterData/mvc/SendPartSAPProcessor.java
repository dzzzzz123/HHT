package ext.sap.masterData.mvc;

import java.util.List;

import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.DefaultObjectFormProcessor;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.core.ui.resources.FeedbackType;
import com.ptc.netmarkets.util.beans.NmCommandBean;

import ext.sap.masterData.PartSenderHelper;
import wt.fc.WTObject;
import wt.session.SessionHelper;
import wt.util.WTException;

public class SendPartSAPProcessor extends DefaultObjectFormProcessor {

	@Override
	public FormResult doOperation(NmCommandBean arg0, List<ObjectBean> arg1) throws WTException {
		WTObject ref = (WTObject) arg0.getPrimaryOid().getRef();
		FormResult formresult = null;

		try {
			PartSenderHelper.sendPartsSAP2(ref);
		} catch (Exception e) {
			formresult = new FormResult(FormProcessingStatus.FAILURE);
			formresult.addFeedbackMessage(new FeedbackMessage(FeedbackType.FAILURE, SessionHelper.getLocale(), null,
					null, new String[] { "设置失败！", e.getMessage() }));
			return formresult;
		}

		formresult = new FormResult(FormProcessingStatus.SUCCESS);
		formresult.addFeedbackMessage(new FeedbackMessage(FeedbackType.SUCCESS, SessionHelper.getLocale(), null, null,
				new String[] { "设置成功！" }));
		return formresult;
	}

}
