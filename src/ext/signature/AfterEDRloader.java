package ext.signature;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.json.JSONObject;

import wt.change2.ChangeHelper2;
import wt.change2.Changeable2;
import wt.change2.WTChangeActivity2;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.epm.EPMDocumentType;
import wt.fc.ObjectReference;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.fc.collections.WTArrayList;
import wt.fc.collections.WTCollection;
import wt.filter.NavigationCriteria;
import wt.maturity.MaturityHelper;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.representation.Representable;
import wt.representation.Representation;
import wt.util.WTException;
import wt.workflow.engine.WfProcess;

/**
 * 监听新建表示法后，重新获取流程签名信息，然后签署到PDF
 * 
 * @author luoxiaomin
 * @since 2019-5-4
 */
public class AfterEDRloader {
	@SuppressWarnings("deprecation")
	public static String[] resignPDF(Representable repable, Persistable object, Representation rep,
			NavigationCriteria docCriteria, NavigationCriteria partCriteria, int structureType) {

		String[] ret = { "AfterEDRloader starts: Checking if object is EPMDocument or WTDocument" };
		System.out.println("002-resignPDF() START");
		if (repable instanceof EPMDocument) {
			EPMDocument cadDoc = (EPMDocument) repable;
			System.out.println("002-resignPDF : EPMDocument=" + cadDoc.getNumber());
			EPMDocumentType docType = cadDoc.getDocType();
			System.out.println("002-resignPDF: doctype :" + docType);
			if (!"CADDRAWING".equalsIgnoreCase(docType.toString())) {
				System.out.println("002-resignPDF:The Type of EPMDocument is not CADDRAWING ,so return.");
				return ret;// 只有绘图才更新文件名称
			}
			try {
				System.out.println("002-resignPDF : get process Promotion or ECA");
				WfProcess self = getWfProcess(cadDoc);
				if (self != null) {
					HashMap<String, Vector<String[]>> signInfo = WorkFlowUtil.getSignInfo(self);
					PDFSign.signPDFVisualization(cadDoc, signInfo, "PDF图纸");
				} else {
					System.out.println("002-resignPDF : no process ");
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("002-resignPDF ERROR:" + e.getMessage());

			}

		} else if (repable instanceof WTDocument) {
			WTDocument doc = (WTDocument) repable;
			System.out.println("002-resignPDF : WTDocument=" + doc.getNumber());
			try {
				System.out.println("002-resignPDF : get process Promotion or ECA");
				WfProcess self = getWfProcess(doc);
				if (self != null) {
					System.out.println("002-resignPDF : get signature information from workflow-"
							+ self.getTemplate().getName() + "->" + self.getName() + "->" + self.getEndTime());
					HashMap<String, Vector<String[]>> signInfo = WorkFlowUtil.getSignInfo(self);
					System.out.println("002-resignPDF : signature information from process var [reviewer]: "
							+ new JSONObject(signInfo).toJSONString());
					PDFSign.signPDFVisualization(doc, signInfo, "PDF文档");
					// 2020-5-31 解决pdf表示法无法在creo view直接打开的问题，方案是重置一下生命周期状态.
					// --2023-11-27qqqqqqqqqqqq取消重置生命周期
//					WTList list = new WTArrayList();
//					list.add(doc);
//					LifeCycleTemplate lifeCycleTemplate = LifeCycleHelper.service.getLifeCycleTemplate(doc);
//					WTContainerRef containerReference = doc.getContainerReference();
//					State state = doc.getLifeCycleState();
//					LifeCycleHelper.service.reassign(list, lifeCycleTemplate.getLifeCycleTemplateReference(),
//							containerReference, state);
					/*
					 * if(signInfo != null && ! signInfo.isEmpty()) {
					 * PDFSign.signPDFVisualization(doc, signInfo, "PDF文档"); }else { System.out.
					 * println("002-resignPDF : no signature information from process var : reviewer "
					 * ); }
					 */
				} else {
					System.out.println("002-resignPDF : no process ");
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("002-resignPDF ERROR:" + e.getMessage());
			}
		}
		System.out.println("002-resignPDF() END");
		return ret;

	}

	/***
	 * 
	 * @description @param @return @throws
	 */
	private static WfProcess getSignProcess(List<WfProcess> processList) {
		WfProcess wfprocess = null;
		if (processList == null || processList.size() == 0) {
			return null;
		}
		for (WfProcess wfProcess2 : processList) {
			//
//			WfState state = wfProcess2.getState();
			/*
			 * if( !WfState.CLOSED_COMPLETED_EXECUTED.equals(state)) { continue; }
			 */
			if (wfprocess != null) {
				Timestamp startTime2 = wfProcess2.getStartTime();
				Timestamp startTime = wfprocess.getStartTime();
				if (startTime2 != null && startTime != null && startTime2.after(startTime)) {
					wfprocess = wfProcess2;
				}
			} else {
				wfprocess = wfProcess2;
			}
		}

		return wfprocess;
	}

	public static WfProcess getWfProcess(Persistable persistable) throws WTException {
		String str = null;
		ReferenceFactory localReferenceFactory = new ReferenceFactory();
		str = localReferenceFactory.getReferenceString(persistable);
		QuerySpec querySpec = new QuerySpec(WfProcess.class);

		// 高级流程
		querySpec.appendWhere(new SearchCondition(WfProcess.class, "businessObjReference", "=", str), 0);

		// 升级流程
		WTCollection list = new WTArrayList();
		list.add(persistable);
		WTCollection promotionNotices = MaturityHelper.service.getPromotionNotices(list);
		if (promotionNotices != null && promotionNotices.size() > 0) {
			Iterator iterator = promotionNotices.iterator();
			while (iterator.hasNext()) {
				Object next = iterator.next();
				Persistable pn = ((ObjectReference) next).getObject();
				str = localReferenceFactory.getReferenceString(pn);
				querySpec.appendOr();
				querySpec.appendWhere(new SearchCondition(WfProcess.class, "businessObjReference", "=", str), 0);
			}
		}
		// ECA

		QueryResult implementedChangeActivities = ChangeHelper2.service
				.getImplementedChangeActivities((Changeable2) persistable);

		while (implementedChangeActivities.hasMoreElements()) {
			WTChangeActivity2 eca = (WTChangeActivity2) implementedChangeActivities.nextElement();
			str = localReferenceFactory.getReferenceString(eca);
			querySpec.appendOr();
			querySpec.appendWhere(new SearchCondition(WfProcess.class, "businessObjReference", "=", str), 0);
		}
		// 按开始时间降序排序
		querySpec.appendOrderBy(WfProcess.class, "startTime", true);
		QueryResult result = PersistenceHelper.manager.find(querySpec);
		WfProcess wfprocess = null;
		List<WfProcess> processList = new ArrayList<WfProcess>();
		int index = 0;
		while (result.hasMoreElements()) {
			wfprocess = (WfProcess) result.nextElement();
			System.out.println("002-resignPDF : " + (++index) + "======" + wfprocess.getTemplate().getName() + "->"
					+ wfprocess.getName() + "->" + wfprocess.getEndTime());
			processList.add(wfprocess);
		}
		return getSignProcess(processList);
	}
}