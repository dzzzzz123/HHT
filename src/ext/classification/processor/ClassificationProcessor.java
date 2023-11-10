package ext.classification.processor;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.DefaultObjectFormProcessor;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.core.ui.resources.FeedbackType;
import com.ptc.netmarkets.util.beans.NmCommandBean;

import ext.classification.ClassificationHelper;
import wt.fc.WTObject;
import wt.session.SessionHelper;
import wt.util.WTException;

/**
 * 在部件的编辑菜单中添加按钮
 * 
 * @author dz
 *
 */
public class ClassificationProcessor extends DefaultObjectFormProcessor {

	@Override
	public FormResult doOperation(NmCommandBean arg0, List<ObjectBean> arg1) throws WTException {
		WTObject ref = (WTObject) arg0.getPrimaryOid().getRef();
		FormResult formResult = null;
		List<String> errorList = new ArrayList<>();
		String[] result = new String[] {};

		try {
			errorList = ClassificationHelper.classify(ref, "");
			result = errorList.toArray(new String[errorList.size()]);
		} catch (Exception e) {
			formResult = new FormResult(FormProcessingStatus.FAILURE);
			formResult.addFeedbackMessage(new FeedbackMessage(FeedbackType.FAILURE, SessionHelper.getLocale(), null,
					null, new String[] { "更新失败！", e.getMessage() }));
			e.printStackTrace();
			return formResult;
		}

		if (result.length > 0 && StringUtils.isNotBlank(result[0])) {
			formResult = new FormResult(FormProcessingStatus.FAILURE);
			formResult.addFeedbackMessage(
					new FeedbackMessage(FeedbackType.FAILURE, SessionHelper.getLocale(), null, null, result));
			return formResult;
		} else {
			formResult = new FormResult(FormProcessingStatus.SUCCESS);
			formResult.addFeedbackMessage(new FeedbackMessage(FeedbackType.SUCCESS, SessionHelper.getLocale(), null,
					null, new String[] { "更新成功！" }));
			return formResult;
		}
	}
}
