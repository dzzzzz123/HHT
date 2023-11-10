package ext.HHT.SRM.insert;

import java.util.ArrayList;
import java.util.List;

import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.DefaultObjectFormProcessor;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.core.ui.resources.FeedbackType;
import com.ptc.netmarkets.util.beans.NmCommandBean;

import ext.ait.util.CommonUtil;
import wt.doc.DocumentType;
import wt.doc.WTDocument;
import wt.fc.WTObject;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.util.WTInvalidParameterException;
import wt.util.WTPropertyVetoException;

/**
 * 在部件的编辑菜单中添加按钮
 * 
 * @author dz
 *
 */
public class Processor extends DefaultObjectFormProcessor {

	@Override
	public FormResult doOperation(NmCommandBean arg0, List<ObjectBean> arg1) throws WTException {
		WTObject ref = (WTObject) arg0.getPrimaryOid().getRef();
		FormResult formResult = null;

		try {
			process(ref);
		} catch (Exception e) {
			formResult = new FormResult(FormProcessingStatus.FAILURE);
			formResult.addFeedbackMessage(new FeedbackMessage(FeedbackType.FAILURE, SessionHelper.getLocale(), null,
					null, new String[] { "部件编号设置失败！", e.getMessage() }));
			e.printStackTrace();
			return formResult;
		}

		formResult = new FormResult(FormProcessingStatus.SUCCESS);
		formResult.addFeedbackMessage(new FeedbackMessage(FeedbackType.SUCCESS, SessionHelper.getLocale(), null, null,
				new String[] { "部件编号更新成功！" }));
		return formResult;

	}

	private void process(WTObject ref) {
		ArrayList<WTDocument> documents = (ArrayList<WTDocument>) CommonUtil.getListFromPBO(ref, WTDocument.class);
		for (WTDocument wtDocument : documents) {
			System.out.println("flag:" + wtDocument.getName());
			System.out.println("flag:" + wtDocument.getNumber());
			System.out.println("flag:" + wtDocument.getDocType().toString());
			try {
				wtDocument.setDocType(DocumentType.toDocumentType("HHT_TestReport"));
			} catch (WTInvalidParameterException e) {
				e.printStackTrace();
			} catch (WTPropertyVetoException e) {
				e.printStackTrace();
			}
		}
	}
}
