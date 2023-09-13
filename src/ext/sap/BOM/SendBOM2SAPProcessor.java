package ext.sap.BOM;

import java.util.List;

import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.DefaultObjectFormProcessor;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.core.ui.resources.FeedbackType;
import com.ptc.netmarkets.util.beans.NmCommandBean;

import wt.fc.WTObject;
import wt.session.SessionHelper;
import wt.util.WTException;

/**
 * 在BOM的操作菜单中显示的按钮所对应的Processor
 * 
 * @author dz
 */
public class SendBOM2SAPProcessor extends DefaultObjectFormProcessor {

	@Override
	public FormResult doOperation(NmCommandBean arg0, List<ObjectBean> arg1) throws WTException {
		WTObject ref = (WTObject) arg0.getPrimaryOid().getRef();
		FormResult formResult = null;

		try {
			SendBOM2SAP.sendListBOM2SAP(ref);
		} catch (Exception e) {
			formResult = new FormResult(FormProcessingStatus.FAILURE);
			formResult.addFeedbackMessage(new FeedbackMessage(FeedbackType.FAILURE, SessionHelper.getLocale(), null,
					null, new String[] { "设置失败！", e.getMessage() }));
			return formResult;
		}

		formResult = new FormResult(FormProcessingStatus.SUCCESS);
		formResult.addFeedbackMessage(new FeedbackMessage(FeedbackType.SUCCESS, SessionHelper.getLocale(), null, null,
				new String[] { "设置成功！" }));
		return formResult;
	}

}
