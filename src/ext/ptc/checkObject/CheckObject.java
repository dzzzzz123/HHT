package ext.ptc.checkObject;

import java.util.ArrayList;

import org.apache.logging.log4j.Logger;

import wt.change2.ChangeHelper2;
import wt.change2.ChangeOrderIfc;
import wt.change2.WTChangeActivity2;
import wt.change2.WTChangeOrder2;
import wt.change2.WTChangeRequest2;
import wt.change2.WTChangeReview;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.fc.QueryResult;
import wt.fc.WTObject;
import wt.log4j.LogR;
import wt.maturity.MaturityHelper;
import wt.maturity.PromotionNotice;
import wt.part.WTPart;
import wt.util.WTException;
import wt.vc.wip.WorkInProgressHelper;
import wt.vc.wip.Workable;

public class CheckObject {
	private static Logger LOGGER = LogR.getLogger(ext.ptc.checkObject.CheckObject.class.getName());

	public static void isCheckOut(WTObject primaryBusinessObject) throws WTException {
		ArrayList<Object> list = new ArrayList<Object>();
		LOGGER.debug("isCheckOut : WTObject [" + primaryBusinessObject.toString() + "]");
		if (primaryBusinessObject instanceof WTChangeRequest2) {
			WTChangeRequest2 ecr = (WTChangeRequest2) primaryBusinessObject;
			LOGGER.debug("isCheckOut : WTChangeRequest2 [" + ecr.getNumber() + "]");
			list = ECUtil.getAffeectObjects(ecr);
		} else if (primaryBusinessObject instanceof WTChangeOrder2) {
			WTChangeOrder2 eco = (WTChangeOrder2) primaryBusinessObject;
			LOGGER.debug("isCheckOut  更改通告[" + eco.getNumber() + "]");
			list = ECUtil.getAffeectObject(eco);
			QueryResult qr = ChangeHelper2.service.getChangeablesAfter((ChangeOrderIfc) eco);
			while (qr.hasMoreElements()) {
				Object obj = qr.nextElement();
				list.add(obj);
			}
			ECUtil.getAffeectObject(eco);
		} else if (primaryBusinessObject instanceof PromotionNotice) {
			PromotionNotice pn = (PromotionNotice) primaryBusinessObject;
			LOGGER.debug("isCheckOut  升级请求[" + pn.getNumber() + "]");
			QueryResult qr = MaturityHelper.service.getPromotionTargets(pn);
			while (qr.hasMoreElements()) {
				Object obj = qr.nextElement();
				list.add(obj);
			}
		} else if (primaryBusinessObject instanceof WTChangeActivity2) {
			WTChangeActivity2 eca = (WTChangeActivity2) primaryBusinessObject;
			LOGGER.debug("isCheckOut  eca[" + eca.getNumber() + "]");
			ArrayList<Object> tempList1 = ECUtil.getChangeObject(eca);
			list = ECUtil.getAffeectObject(eca);
			list.addAll(tempList1);
		} else if (primaryBusinessObject instanceof WTChangeReview) {
			WTChangeReview review = (WTChangeReview) primaryBusinessObject;
			LOGGER.debug("isCheckOut  review[" + review.getNumber() + "]");
			list = ECUtil.getAffeectObject(review);
		} else if (primaryBusinessObject instanceof WTDocument) {
			WTDocument doc = (WTDocument) primaryBusinessObject;
			LOGGER.debug("isCheckOut  WTDocument[" + doc.getNumber() + "]");
			list.add(doc);
		} else if (primaryBusinessObject instanceof WTPart) {
			WTPart part = (WTPart) primaryBusinessObject;
			LOGGER.debug("isCheckOut  WTPart[" + part.getNumber() + "]");
			list.add(part);
		}

		boolean checkout = false;
		ArrayList<Object> checkOutObjs = new ArrayList<Object>();
		for (Object obj : list) {
			if (obj instanceof Workable) {
				Workable wa = (Workable) obj;
				if (WorkInProgressHelper.isCheckedOut(wa)) {
					checkout = true;
					checkOutObjs.add(wa);
				}
			}
		}
		StringBuffer buf = new StringBuffer();
		if (checkout) {
			buf.append("以下对象已经被检出，请检入后再提交任务：\n");
			for (Object obj : checkOutObjs) {
				if (obj instanceof WTDocument) {
					WTDocument doc = (WTDocument) obj;
					buf.append("文档[").append(doc.getNumber()).append("]\n");
					continue;
				}
				if (obj instanceof WTPart) {
					WTPart doc = (WTPart) obj;
					buf.append("部件[").append(doc.getNumber()).append("]\n");
					continue;
				}
				if (obj instanceof EPMDocument) {
					EPMDocument doc = (EPMDocument) obj;
					buf.append("图纸[").append(doc.getNumber()).append("]\n");
					continue;
				}
				buf.append("对象[").append(obj.toString()).append("]\n");
			}
			throw new WTException(buf.toString());
		}
	}
}