package ext.signature;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.ptc.windchill.enterprise.workflow.WfDataUtilitiesHelper;

import wt.change2.WTChangeActivity2;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.fc.ObjectIdentifier;
import wt.fc.ObjectReference;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.fc.WTObject;
import wt.fc.WTReference;
import wt.maturity.MaturityHelper;
import wt.maturity.PromotionNotice;
import wt.org.WTPrincipal;
import wt.query.CompositeWhereExpression;
import wt.query.LogicalOperator;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.query.TableColumn;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.workflow.engine.ProcessData;
import wt.workflow.engine.WfProcess;
import wt.workflow.engine.WfVotingEventAudit;
import wt.workflow.work.WfAssignedActivity;
import wt.workflow.work.WorkItem;

public class WorkFlowUtil {

	/***
	 * 流程反签，流程审批人、审批时间、备注等信息写到流程变量中
	 * 
	 * @description @param ObjectReference @param primaryBusinessObject @param
	 *              当前角色名称 @return @throws
	 */
	public static String saveReviewToVar(ObjectReference self, WTObject pbo, String roleName) {
		WTPrincipal currentUser = null;
//		Transaction transaction = new Transaction();
		try {
//			transaction.start();
			currentUser = SessionHelper.manager.getPrincipal();
			SessionHelper.manager.setAdministrator();
			System.out.println("Save reviewer for role :" + roleName);
			String values[] = getWorkFlowInfo(self);
			// 当前审核人员
			Persistable object = self.getObject();
			if (object instanceof WfProcess) {
				WfProcess wfprocess = (WfProcess) object;
				ProcessData context = wfprocess.getContext();
				Object value = context.getValue("reviewer");
				if (value != null && value instanceof String && !"".equalsIgnoreCase((String) (value))) {
					String oldValue = (String) value;
					JSONObject jsonObject = new JSONObject(oldValue);
					jsonObject.put(roleName, values);
//					wfprocess = (wt.workflow.engine.WfProcess) wt.workflow.engine.WfEngineServerHelper.lock(wfprocess);
					context.setValue("reviewer", jsonObject.toJSONString());
//					wfprocess.setContext(context);
//					wfprocess = (wt.workflow.engine.WfProcess) wt.fc.PersistenceHelper.manager.store(wfprocess);
//					transaction.commit();
					return jsonObject.toJSONString();
				} else {
					JSONObject jsonObject = new JSONObject();
					jsonObject.put(roleName, values);
//					wfprocess = (wt.workflow.engine.WfProcess) wt.workflow.engine.WfEngineServerHelper.lock(wfprocess);
					context.setValue("reviewer", jsonObject.toJSONString());
//					wfprocess.setContext(context);
//					wfprocess = (wt.workflow.engine.WfProcess) wt.fc.PersistenceHelper.manager.save(wfprocess);
//					transaction.commit();
					return jsonObject.toJSONString();
				}
			}
//			transaction.commit();
		} catch (Exception e) {
//			transaction.rollback();
			e.printStackTrace();
		} finally {
			if (currentUser != null) {
				try {
					SessionHelper.manager.setPrincipal(currentUser.getName());
				} catch (WTException e) {
					e.printStackTrace();
				}
			}
		}
		return "{}";
	}

	/***
	 * 获取流程中所有变量
	 * 
	 * @description @param ObjectReference @param
	 *              primaryBusinessObject @return @throws
	 */
	public static String getReview(WfProcess wfprocess) {
		try {
//			Persistable object = self.getObject();
//			WfProcess wfprocess = (WfProcess) object;
			Object reviewer = wfprocess.getContext().getValue("reviewer");
			System.out.println("Reviewer info:" + reviewer);
			return reviewer != null ? reviewer.toString() : "{}";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public static HashMap<String, Vector<String[]>> getSignInfo(Object self) {
		// 获取签名信息
		HashMap<String, Vector<String[]>> signInfo = new HashMap<String, Vector<String[]>>();
		try {
			String reviewer = "{}";
			if (self instanceof ObjectReference) {
				reviewer = getReview((WfProcess) ((ObjectReference) self).getObject());
			} else if (self instanceof WfProcess) {
				reviewer = getReview((WfProcess) self);
			}
			if (StringUtils.isBlank(reviewer)) {
				return signInfo;
			}
//			reviewer="{\"图纸校对\":[\"administrator\",\"2019.1.1\",\"test\",\"test\"]}";
			JSONObject json = new JSONObject(reviewer);
			Iterator keys = json.keys();
			while (keys.hasNext()) {
				Object next = keys.next();
				String role = String.valueOf(next);
				Object object = json.get(role);
				Vector<String[]> v = new Vector<String[]>();
				if (object instanceof JSONArray) {
					JSONArray array = (JSONArray) object;
					int length = array.length();
					String[] sign = new String[length];
					for (int i = 0; i < array.length(); i++) {
						sign[i] = array.getString(i);
					}
					v.add(sign);
				}
				signInfo.put(role, v);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return signInfo;
	}

	/**
	 * 只适用于高级文档工作流和升级流程中文档表示法pdf执行签名
	 * 
	 * @param self
	 * @param pbo
	 */
	public static void signDocumentPDF(ObjectReference self, WTObject pbo) {
		try {
			HashMap<String, Vector<String[]>> signInfo = getSignInfo(self);
			System.out.println("Sign Info :" + new JSONObject(signInfo).toJSONString());
			if (pbo instanceof WTDocument) {
				// 获取文档的pdf表示法，签名，更新表示法
				/*
				 * // 增加受控章打印信息 String info[] = new String[4]; info[0] = "CONTROLLED"; info[1] =
				 * "2018-11-14"; info[2] = "CONTROLLED"; info[3] = ""; Vector<String[]> v = new
				 * Vector<String[]>(); v.add(info); signInfo.put("受控章", v);
				 */
				// PDF文档 signature.properties中关于文档类型的配置
				PDFSign.signPDFVisualization(pbo, signInfo, "PDF文档");
			} else {
				if (pbo instanceof PromotionNotice) {
					PromotionNotice pn = (PromotionNotice) pbo;
					// 获取流程的多个对象
					QueryResult qr = MaturityHelper.service.getPromotionTargets(pn);
					while (qr.hasMoreElements()) {
						Object obj = qr.nextElement();
						if (obj instanceof WTDocument) {
							// PDF文档 signature.properties中关于文档类型的配置
							PDFSign.signPDFVisualization((WTDocument) obj, signInfo, "PDF文档");
						}
					}
				} else if (pbo instanceof WTChangeActivity2) {
					WTChangeActivity2 eca = (WTChangeActivity2) pbo;
					wt.fc.QueryResult changeables = wt.change2.ChangeHelper2.service.getChangeablesAfter(eca);
					if (changeables != null) {
						while (changeables.hasMoreElements()) {
							Object localObject = changeables.nextElement();
							if (localObject instanceof WTDocument) {
								PDFSign.signPDFVisualization((WTDocument) localObject, signInfo, "PDF文档");
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 适用于升级流程和变更流程签署2D表示法pdf执行签名
	 * 
	 * @param self
	 * @param pbo
	 */
	public static void signCADPDF(ObjectReference self, WTObject pbo) {
		try {
			// 获取签名信息
			HashMap<String, Vector<String[]>> signInfo = getSignInfo(self);
			System.out.println("Sign Info :" + new JSONObject(signInfo).toJSONString());
			if (pbo instanceof PromotionNotice) {
				PromotionNotice pn = (PromotionNotice) pbo;
				// 获取流程的多个对象
				QueryResult qr = MaturityHelper.service.getPromotionTargets(pn);
				while (qr.hasMoreElements()) {
					Object obj = qr.nextElement();
					if (obj instanceof EPMDocument) {
						PDFSign.signPDFVisualization((EPMDocument) obj, signInfo, "PDF图纸");
					}
				}
			} else if (pbo instanceof WTChangeActivity2) {
				WTChangeActivity2 eca = (WTChangeActivity2) pbo;
				wt.fc.QueryResult changeables = wt.change2.ChangeHelper2.service.getChangeablesAfter(eca);
				if (changeables != null) {
					while (changeables.hasMoreElements()) {
						Object localObject = changeables.nextElement();
						if (localObject instanceof EPMDocument) {
							PDFSign.signPDFVisualization((EPMDocument) localObject, signInfo, "PDF图纸");
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/***
	 * 根据self得到当前节点的审批人 审批时间 审批备注
	 * 
	 * @description @param @return @throws
	 */
	public static String[] getWorkFlowInfo(ObjectReference self) {
		String info[] = new String[4];
		try {
			// WfActivity wfactivity = (WfActivity) self.getObject();
			ReferenceFactory referencefactory = new ReferenceFactory();

			WTReference reference = referencefactory.getReference(self.getObjectId().toString());

			if (reference.getObject() != null) {

				if (reference.getObject() instanceof WfProcess) {

					WfProcess wfprocess = (WfProcess) reference.getObject();
					WorkItem workItem = getWorkItem(wfprocess);
					WfVotingEventAudit wfvoting = WfDataUtilitiesHelper.getMatchingEventAudit(workItem);

					if (wfvoting != null) {

						// info[0] = wfvoting.getUserRef() != null ? wfvoting.getUserRef().getName() :
						// "";
						info[0] = wfvoting.getUserRef() != null ? wfvoting.getUserRef().getFullName() : "";

						String date = SignatureHelper.DATEFORMATE.format(wfvoting.getTimestamp());
						info[1] = date;
						// info[2] = wfvoting.getUserRef().getName();
						info[2] = wfvoting.getUserRef().getFullName();
						info[3] = wfvoting.getUserComment();
					}

				} else if (reference.getObject() instanceof WfAssignedActivity) {

					WfAssignedActivity assActivity = (WfAssignedActivity) reference.getObject();

					QuerySpec queryspec = new QuerySpec(WorkItem.class);

					WTReference wtreference = referencefactory.getReference((WTObject) assActivity);
					String oid = referencefactory.getReferenceString(wtreference);
					System.out.println("oid----" + oid);

					SearchCondition searchCondition = new SearchCondition(WorkItem.class, "source.key", "=",
							new ObjectIdentifier(oid));

					queryspec.appendWhere(searchCondition);

					QueryResult queryresult = PersistenceServerHelper.manager.query(queryspec);

					while (queryresult.hasMoreElements()) {
						Object obj = queryresult.nextElement();

						WorkItem workItem = (WorkItem) obj;

						WfVotingEventAudit wfvoting = WfDataUtilitiesHelper.getMatchingEventAudit(workItem);

						if (wfvoting != null) {
							info[0] = wfvoting.getUserRef() != null ? wfvoting.getUserRef().getFullName() : "";
							String date = SignatureHelper.DATEFORMATE.format(wfvoting.getTimestamp());
							info[1] = date;
							info[2] = wfvoting.getUserRef().getName();
							info[3] = wfvoting.getUserComment();
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return info;
	}

	public static WorkItem getWorkItem(WfProcess wfprocess) {
		WorkItem workItem = null;
		try {
			QuerySpec queryspec = new QuerySpec();
			// int i = queryspec.appendClassList(WfAssignedActivity.class,
			// false);
			// queryspec.appendSelectAttribute("thePersistInfo.theObjectIdentifier.id",
			// i, false);
			// queryspec.appendWhere(new
			// SearchCondition(WfAssignedActivity.class, "parentProcessRef.key",
			// "=", getOid(wfprocess)));

			int a = queryspec.appendClassList(WfAssignedActivity.class, false);
			int b = queryspec.appendClassList(WorkItem.class, true);
			queryspec.setAdvancedQueryEnabled(true);
			String[] aliases = new String[2];
			aliases[0] = queryspec.getFromClause().getAliasAt(a);
			aliases[1] = queryspec.getFromClause().getAliasAt(b);

			TableColumn tc1 = new TableColumn(aliases[0], "IDA2A2");
			TableColumn tc2 = new TableColumn(aliases[1], "IDA3A4");

			CompositeWhereExpression andExpression = new CompositeWhereExpression(LogicalOperator.AND);
			andExpression.append(
					new SearchCondition(WfAssignedActivity.class, "parentProcessRef.key", "=", getOid(wfprocess)));

			andExpression.append(new SearchCondition(tc1, "=", tc2));

			queryspec.appendWhere(andExpression, null);

			// ------------------------QuerySpec queryspec = new QuerySpec();
			queryspec.appendOrderBy(WorkItem.class, "thePersistInfo.updateStamp", true);

			QueryResult qr = PersistenceHelper.manager.find(queryspec);
			while (qr.hasMoreElements()) {
				Object[] obj = (Object[]) qr.nextElement();
				if (obj[0] instanceof WorkItem) {
					workItem = (WorkItem) obj[0];
					return workItem;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return workItem;
	}

	private static ObjectIdentifier getOid(Object obj) {
		if (obj == null)
			return null;
		if (obj instanceof ObjectReference)
			return (ObjectIdentifier) ((ObjectReference) obj).getKey();
		else
			return PersistenceHelper.getObjectIdentifier((Persistable) obj);
	}
}