package ext.sap.BOM;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

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
			List<String> msg = SendBOM2SAP.sendListBOM2SAP(ref);
			if (StringUtils.isNotBlank(msg.get(0))) {
				StringBuffer errorMsg = new StringBuffer();
				for (String err : msg) {
					errorMsg.append(err).append(System.lineSeparator());
				}
				formResult = new FormResult(FormProcessingStatus.FAILURE);
				formResult.addFeedbackMessage(new FeedbackMessage(FeedbackType.FAILURE, SessionHelper.getLocale(), null,
						null, new String[] { errorMsg.toString() }));
				return formResult;
			}
		} catch (Exception e) {
			formResult = new FormResult(FormProcessingStatus.FAILURE);
			formResult.addFeedbackMessage(new FeedbackMessage(FeedbackType.FAILURE, SessionHelper.getLocale(), null,
					null, new String[] { "发送BOM失败！", e.getMessage() }));
			e.printStackTrace();
			return formResult;
		}

		formResult = new FormResult(FormProcessingStatus.SUCCESS);
		formResult.addFeedbackMessage(new FeedbackMessage(FeedbackType.SUCCESS, SessionHelper.getLocale(), null, null,
				new String[] { "发送BOM成功！" }));
		return formResult;
	}

}
