package ext.ait.util.back;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Vector;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;

import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.util.beans.NmURLFactoryBean;
import com.ptc.netmarkets.util.misc.NetmarketURL;

import wt.access.AccessControlHelper;
import wt.access.AccessPermission;
import wt.access.AdHocAccessKey;
import wt.access.AdHocControlled;
import wt.change2.ChangeException2;
import wt.change2.ChangeHelper2;
import wt.change2.VersionableChangeItem;
import wt.change2.WTChangeActivity2;
import wt.change2.WTChangeOrder2;
import wt.change2.WTChangeReview;
import wt.doc.WTDocument;
import wt.enterprise.RevisionControlled;
import wt.fc.ObjectReference;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.fc.WTObject;
import wt.fc.WTReference;
import wt.httpgw.URLFactory;
import wt.log4j.LogR;
import wt.maturity.MaturityHelper;
import wt.maturity.Promotable;
import wt.maturity.PromotionNotice;
import wt.maturity.PromotionTarget;
import wt.org.WTPrincipalReference;
import wt.part.WTPart;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionHelper;
import wt.util.CollationKeyFactory;
import wt.util.SortedEnumeration;
import wt.util.WTException;
import wt.vc.baseline.BaselineHelper;
import wt.vc.baseline.ManagedBaseline;
import wt.workflow.definer.WfAssignedActivityTemplate;
import wt.workflow.definer.WfProcessTemplate;
import wt.workflow.engine.ProcessData;
import wt.workflow.engine.WfActivity;
import wt.workflow.engine.WfBlock;
import wt.workflow.engine.WfConnector;
import wt.workflow.engine.WfEngineHelper;
import wt.workflow.engine.WfExecutionObject;
import wt.workflow.engine.WfProcess;
import wt.workflow.engine.WfVotingEventAudit;
import wt.workflow.work.WfAssignedActivity;
import wt.workflow.work.WfAssignment;
import wt.workflow.work.WorkItem;
import wt.workflow.work.WorkItemLink;

public class WorkflowUtil {
	private static Logger LOGGER = LogR.getLogger(WorkflowUtil.class.getName());

	/**
	 * 获取升级流程中被评审的对象
	 * 
	 * @param pn
	 * @return
	 * @throws WTException
	 */
	public static ArrayList<WTObject> getTargerObjectByPromotionNotices(PromotionNotice pn) throws WTException {
		ArrayList<WTObject> list = new ArrayList<WTObject>();
		QueryResult qr = MaturityHelper.service.getPromotionTargets(pn, false);
		while (qr.hasMoreElements()) {
			PromotionTarget pt = (PromotionTarget) qr.nextElement();
			Promotable ptObj = pt.getPromotable();
			list.add((WTObject) ptObj);
		}
		return list;
	}

	/**
	 * 获取升级流程中被评审的所有部件
	 * 
	 * @param pn
	 * @return
	 * @throws WTException
	 */
	public static ArrayList<WTPart> getTargerPartByPromotionNotices(PromotionNotice pn) throws WTException {
		ArrayList<WTPart> list = new ArrayList<WTPart>();
		// LOGGER.debug("#### pn name --->"+pn.getName());
		QueryResult qr = MaturityHelper.service.getPromotionTargets(pn, false);
		while (qr.hasMoreElements()) {
			PromotionTarget pt = (PromotionTarget) qr.nextElement();
			Promotable ptObj = pt.getPromotable();
			if (ptObj instanceof WTPart) {
				WTPart part = (WTPart) ptObj;
				// LOGGER.debug("#### part name --->"+part.getNumber());
				list.add(part);
			}
		}
		return list;
	}

	/**
	 * 获取升级流程中被评审的文档
	 * 
	 * @param pn
	 * @return
	 * @throws WTException
	 */
	public static ArrayList<WTDocument> getDocByPromotionNotices(PromotionNotice pn) throws WTException {
		ArrayList<WTDocument> list = new ArrayList<WTDocument>();
		// LOGGER.debug("#### pn name --->"+pn.getName());
		QueryResult qr = MaturityHelper.service.getPromotionTargets(pn, false);
		while (qr.hasMoreElements()) {
			PromotionTarget pt = (PromotionTarget) qr.nextElement();
			Promotable ptObj = pt.getPromotable();
			if (ptObj instanceof WTDocument) {
				list.add((WTDocument) ptObj);
			}
		}
		return list;
	}

	/**
	 * 通过流程模板中的Self变量，获取流程实例
	 * 
	 * @param self
	 * @return
	 */
	public static WfProcess getProcess(ObjectReference self) {
		Persistable persistable = self.getObject();
		if (persistable == null) {
			return null;
		}
		try {
			// 通过WorkItem获取流程
			if (persistable instanceof WorkItem) {
				persistable = ((WorkItem) persistable).getSource().getObject();
			}
			// 通过WfActivity获取流程
			if (persistable instanceof WfActivity) {
				persistable = ((WfActivity) persistable).getParentProcess();
			}
			// 通过WfConnector获取流程
			if (persistable instanceof WfConnector) {
				persistable = ((WfConnector) persistable).getParentProcessRef().getObject();
			}
			// 通过WfBlock获取流程
			if (persistable instanceof WfBlock) {
				persistable = ((WfBlock) persistable).getParentProcess();
			}
			// 转换成流程对象
			if (persistable instanceof WfProcess) {
				return (WfProcess) persistable;
			} else {
				return null;
			}
		} catch (Exception e) {
			LOGGER.debug("WFHepler.getProcess : error");
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 通过任务获取流程实例
	 * 
	 * @param wi
	 * @return
	 */
	public static WfProcess getProcess(WorkItem wi) {
		return (WfProcess) wi.getSource().getObject();
	}

	/**
	 * 保存流程变量
	 * 
	 * @param self
	 * @param key
	 * @param value
	 * @throws WTException
	 */
	public static void setProcessValue(ObjectReference self, String key, Object value) throws WTException {
		try {
			WfProcess process = getProcess(self);
			WfExecutionObject wfObject = (WfExecutionObject) PersistenceHelper.manager.refresh(process);
			ProcessData data = wfObject.getContext();
			// LOGGER.debug("属性["+key+"]原来的值为-->"+data.getValue(key));
			data.setValue(key, value);
			PersistenceHelper.manager.modify(wfObject);
			PersistenceHelper.manager.refresh(wfObject);
		} catch (Exception e) {
			LOGGER.error("设置流程变量" + key + "出错");
			e.printStackTrace();
			throw new WTException(e);
		}

	}

	/**
	 * 通过被评审对象获取流程对象
	 * 
	 * @param obj
	 * @return
	 * @throws WTException
	 */
	public static WfProcess getProcessByPbo(WTObject obj) {
		WfProcess process = null;
		try {
			String pboId = "";
			if (obj instanceof VersionableChangeItem) {
				pboId = "VR:" + obj.getClass().getName() + ":"
						+ ((VersionableChangeItem) obj).getIterationInfo().getBranchId();
				;
			} else if (obj instanceof RevisionControlled) {
				pboId = "VR:" + obj.getClass().getName() + ":"
						+ ((RevisionControlled) obj).getIterationInfo().getBranchId();
			} else if (obj instanceof PromotionNotice) {
				PromotionNotice pn = (PromotionNotice) obj;
				pboId = "OR:" + obj.getClass().getName() + ":" + pn.getPersistInfo().getObjectIdentifier().getId();
			} else {
				LOGGER.error("对象类型不兼容，不能获取流程");
				return null;
			}
			QuerySpec qs = new QuerySpec(WfProcess.class);
			qs.appendWhere(new SearchCondition(WfProcess.class, WfProcess.BUSINESS_OBJ_REFERENCE, SearchCondition.EQUAL,
					pboId));
			// LOGGER.debug("getProcessByPbo sql where -->"+qs.toString());
			QueryResult qr = PersistenceHelper.manager.find(qs);
			while (qr != null && qr.hasMoreElements()) {
				WfProcess tmpProcess = (WfProcess) qr.nextElement();
				if (process == null) {
					process = tmpProcess;
				} else {
					if (tmpProcess.getStartTime().after(process.getStartTime())) {
						process = tmpProcess;
					}
				}
			}
			if (process != null) {
				LOGGER.debug("[" + pboId + "]的最新流程为--->" + process.getName() + "|" + CommonUtil.object2Oid(process));
			} else {
				LOGGER.debug("[" + pboId + "]没有流程记录");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return process;
	}

	/**
	 * 设置活动变量的值
	 * 
	 * @param self
	 * @param key
	 * @param obj
	 * @throws WTException
	 */
	public static void setActivityValue(ObjectReference self, String key, Object obj) throws WTException {
		try {
			WfExecutionObject p = (WfExecutionObject) self.getObject();
			ProcessData data = p.getContext();
			// LOGGER.debug("属性["+key+"]原来的值为-->"+data.getValue(key));
			data.setValue(key, obj);
			PersistenceHelper.manager.modify(p);
			PersistenceHelper.manager.refresh(p);
		} catch (WTException e) {
			// TODO Auto-generated catch block
			LOGGER.error("设置流程变量" + key + "出错");
			e.printStackTrace();
			throw new WTException(e);
		}

	}

	/**
	 * 通过流程获取流程的所有任务
	 * 
	 * @param process
	 * @throws WTException
	 */
	public static ArrayList<WorkItem> getWorkItemByProcess(WfProcess process) {
		ArrayList<WorkItem> list = new ArrayList<WorkItem>();
		if (process == null) {
			return list;
		}
		try {
			QuerySpec qs = new QuerySpec();
			int wfAssignedActivityIndex = qs.addClassList(WfAssignedActivity.class, true);
			int workItemIndex = qs.addClassList(WorkItem.class, true);
			qs.appendWhere(new SearchCondition(WfAssignedActivity.class, "parentProcessRef.key.id",
					SearchCondition.EQUAL, process.getPersistInfo().getObjectIdentifier().getId()),
					wfAssignedActivityIndex);
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(WorkItem.class, "source.key.id", WfAssignedActivity.class,
					"thePersistInfo.theObjectIdentifier.id"), workItemIndex, wfAssignedActivityIndex);
			// LOGGER.debug("getWorkItemByProcess sql where -->"+qs.getWhere());
			QueryResult qr = PersistenceHelper.manager.find(qs);
			while (qr != null && qr.hasMoreElements()) {
				Object[] objs = (Object[]) qr.nextElement();
				Object obj = objs[1];
				if (obj instanceof WorkItem) {
					list.add((WorkItem) obj);
				}
			}
			// LOGGER.debug("process name :"+process.getName()+"获取的任务个数有---> "+list.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 获取流程中的所有活动
	 * 
	 * @param process
	 * @return
	 */
	public static ArrayList<WfAssignedActivity> getWfAssignedActivitys(WfProcess process) {
		ArrayList<WfAssignedActivity> list = new ArrayList<WfAssignedActivity>();
		try {
			QuerySpec qs = new QuerySpec(WfAssignedActivity.class);
			qs.appendWhere(new SearchCondition(WfAssignedActivity.class, "parentProcessRef.key.id",
					SearchCondition.EQUAL, process.getPersistInfo().getObjectIdentifier().getId()), new int[] { 0 });
			QueryResult qr = PersistenceHelper.manager.find(qs);
			while (qr.hasMoreElements()) {
				WfAssignedActivity act = (WfAssignedActivity) qr.nextElement();
				list.add(act);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 通过流程获取流程的所有任务
	 * 
	 * @param process
	 * @throws WTException
	 */
	public static ArrayList<WorkItem> getInProcessWorkItemByProcess(WfProcess process) throws WTException {
		ArrayList<WorkItem> list = new ArrayList<WorkItem>();
		if (process == null) {
			return list;
		}
		try {
			QuerySpec qs = new QuerySpec();
			int wfAssignedActivityIndex = qs.addClassList(WfAssignedActivity.class, true);
			int workItemIndex = qs.addClassList(WorkItem.class, true);
			qs.appendWhere(new SearchCondition(WfAssignedActivity.class, "parentProcessRef.key.id",
					SearchCondition.EQUAL, process.getPersistInfo().getObjectIdentifier().getId()),
					wfAssignedActivityIndex);
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(WorkItem.class, "source.key.id", WfAssignedActivity.class,
					"thePersistInfo.theObjectIdentifier.id"), workItemIndex, wfAssignedActivityIndex);
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(WorkItem.class, WorkItem.STATUS, SearchCondition.NOT_EQUAL, "COMPLETED"),
					workItemIndex);
			// LOGGER.debug("getWorkItemByProcess sql where -->"+qs.getWhere());
			QueryResult qr = PersistenceHelper.manager.find(qs);
			while (qr != null && qr.hasMoreElements()) {
				Object[] objs = (Object[]) qr.nextElement();
				Object obj = objs[1];
				if (obj instanceof WorkItem) {
					list.add((WorkItem) obj);
				}
			}
			// LOGGER.debug("process name :"+process.getName()+"获取的未完成任务个数有--->
			// "+list.size());
			return list;
		} catch (Exception e) {
			throw new WTException(e);
		}
	}

	/**
	 * 获取未完成的所有流程任务
	 * 
	 * @param process
	 * @throws WTException
	 */
	public static ArrayList<WorkItem> getUnfinishedWorkItem() {
		ArrayList<WorkItem> list = new ArrayList<WorkItem>();
		try {
			QuerySpec qs = new QuerySpec(WorkItem.class);
			qs.appendWhere(
					new SearchCondition(WorkItem.class, WorkItem.STATUS, SearchCondition.NOT_EQUAL, "COMPLETED"));
			// LOGGER.debug("getWorkItemByProcess sql where -->"+qs.getWhere());
			QueryResult qr = PersistenceHelper.manager.find(qs);
			while (qr != null && qr.hasMoreElements()) {
				Object obj = (Object) qr.nextElement();
				if (obj instanceof WorkItem) {
					list.add((WorkItem) obj);
				}
			}
			// LOGGER.debug("未完成的任务个数有---> "+list.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 获取任务的URL
	 * 
	 * @param wi
	 * @return
	 */
	public static String getWorkItemUrl(WorkItem wi) {
		String url = "";
		try {
			URLFactory factory = new URLFactory();
			String resource = "servlet/TypeBasedIncludeServlet";
			HashMap<String, String> map = new HashMap<String, String>();
			NmOid oid = new NmOid();
			oid.setOid(wi.getPersistInfo().getObjectIdentifier());
			map.put("oid", oid.toString());
			map.put("u8", "1");
			oid.setOid(wi.getPersistInfo().getObjectIdentifier());
			String ref1 = factory.getHREF(resource, map);
			if (ref1 != null && ref1.trim().length() > 0) {
				url = ref1;
			}
		} catch (Exception e) {
			LOGGER.error("获取任务[" + wi.toString() + "]URL失败");
			e.printStackTrace();
		}
		LOGGER.debug("获取任务[" + wi.toString() + "]URL为---->" + url);
		return url;
	}

	public static String getPersUrl(Persistable persistable) {
		String url = "";
		try {
			// http://plm.creolive.cn/Windchill/app/#ptc1/tcomp/infoPage?oid=OR%3Awt.maturity.PromotionNotice%3A2419390&u8=1

			URLFactory factory = new URLFactory();
			String resource = "app/#ptc1/tcomp/infoPage";
			HashMap<String, String> map = new HashMap<String, String>();
			NmOid oid = new NmOid();
			oid.setOid(persistable.getPersistInfo().getObjectIdentifier());
			map.put("oid", oid.toString());
			map.put("u8", "1");
			String ref1 = factory.getHREF(resource, map);
			if (ref1 != null && ref1.trim().length() > 0) {
				url = ref1;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return url;
	}

	public static String getWorkItemUrl(ObjectReference self) {
		String result = "";
		Persistable persistable = self.getObject();
		ArrayList<WorkItem> workItems = new ArrayList<WorkItem>();
		if (persistable == null) {
			return result;
		}
		if (persistable instanceof WfAssignedActivity) {
			LOGGER.debug("获取活动--------------->" + persistable.toString() + "的所有任务");
			workItems = getWorkItems((WfAssignedActivity) persistable);
			LOGGER.debug("获取到" + workItems.size() + "个");
			workItems = getActivityWorkItems((WfAssignedActivity) persistable);
			LOGGER.debug("获取到" + workItems.size() + "个");
		}
		LOGGER.debug("获取到" + workItems.size() + "个");
		try {
			for (WorkItem workItem : workItems) {
				LOGGER.debug("获取任务：" + workItem.toString() + workItem.getTaskURLPathInfo().toString());
				NmOid nmOid = NmOid.newNmOid(persistable.getPersistInfo().getObjectIdentifier());
				NmURLFactoryBean bean = new NmURLFactoryBean();
				bean.setRequestURI(NetmarketURL.BASEURL);
				result = result + ";" + NetmarketURL.buildURL(bean, "object", "view", nmOid, (HashMap) null);
				LOGGER.debug("WorkItemURL1:" + result);
			}
		} catch (WTException e) {
			LOGGER.error("获取任务[" + persistable.toString() + "]URL失败");
			e.printStackTrace();
		}
		LOGGER.debug("获取任务[" + persistable.toString() + "]URL为---->" + result);
		return result;
	}

	@SuppressWarnings("deprecation")
	public static WfVotingEventAudit getVoting(WorkItem wi) {
		WfVotingEventAudit wa = null;
		try {
			QuerySpec qs = new QuerySpec(WfVotingEventAudit.class);
			qs.appendWhere(new SearchCondition(WfVotingEventAudit.class, "theWorkItemReference.key.id",
					SearchCondition.EQUAL, wi.getPersistInfo().getObjectIdentifier().getId()));
			LOGGER.debug("getVoting sql where -->" + qs.toString());
			QueryResult qr = PersistenceHelper.manager.find(qs);
			while (qr != null && qr.hasMoreElements()) {
				wa = (WfVotingEventAudit) qr.nextElement();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return wa;
	}

	/**
	 * 获取升级流程中所有升级对象
	 * 
	 * @param pn
	 * @return
	 * @throws WTException
	 */
	public static ArrayList<Promotable> getPromotionNotice(PromotionNotice pn) throws WTException {
		ArrayList<Promotable> targetObjs = new ArrayList<Promotable>();
		try {
			QueryResult qr = MaturityHelper.service.getPromotionTargets(pn, false);
			while (qr.hasMoreElements()) {
				PromotionTarget pt = (PromotionTarget) qr.nextElement();
				Promotable ptObj = pt.getPromotable();
				targetObjs.add(ptObj);
			}
			return targetObjs;
		} catch (Exception e) {
			throw new WTException(e);
		}
	}

	/**
	 * 赋予用户流程编辑权限
	 */
	public static void giveAccessWorkflow(WfProcess process) {
		try {
			WTPrincipalReference ref = SessionHelper.manager.getPrincipalReference();
			Vector vector = new Vector();
			vector.add(AccessPermission.MODIFY);
			AdHocControlled adhoccontrolled = AccessControlHelper.manager.addPermissions((AdHocControlled) process, ref,
					vector, AdHocAccessKey.WNC_ACCESS_CONTROL);
		} catch (Exception e) {
			LOGGER.error(">>>>>>>>>>>>>>>>>赋予权限失败");
			e.printStackTrace();
		}
	}

	/**
	 * 收回用户流程编辑权限
	 */
	public static void removeAccessWorkflow(WfProcess process) {
		try {
			WTPrincipalReference ref = SessionHelper.manager.getPrincipalReference();
			Vector vector = new Vector();
			vector.add(AccessPermission.MODIFY);
			AdHocControlled adhoccontrolled = AccessControlHelper.manager.removePermissions((AdHocControlled) process,
					ref, vector, AdHocAccessKey.WNC_ACCESS_CONTROL);
		} catch (Exception e) {
			LOGGER.error(">>>>>>>>>>>>>>>>>收回权限失败");
			e.printStackTrace();
		}
	}

	/**
	 * 获取活动名称
	 * 
	 * @param wiOid
	 * @return
	 * @throws WTException
	 */
	public static String getActName(String wiOid) throws WTException {
		// LOGGER.debug("#### getActName by oid --->"+wiOid);
		String name = "";
		WorkItem wi = (WorkItem) CommonUtil.oid2Object(wiOid);
		WfAssignedActivity wfa = (WfAssignedActivity) wi.getSource().getObject();
		name = wfa.getName();
		// LOGGER.debug("#### getActName name--->"+name);
		return name;
	}

	/**
	 * 获取流程任务的路由选择
	 * 
	 * @param itemList
	 * @param actName
	 * @return
	 * @throws WTException
	 */
	public static String getEvent(ArrayList<WorkItem> itemList, String actName) {
		String voting = "";
		if (actName != null && actName.trim().length() > 0) {
			Timestamp ts = null;
			for (WorkItem item : itemList) {
				WfAssignedActivity waa = (WfAssignedActivity) item.getSource().getObject();
				if (actName.equals(waa.getName())) {
					if (item.isComplete()) {
						WfVotingEventAudit audit = WorkflowUtil.getVoting(item);
						if (audit != null) {
							if (voting.trim().length() == 0) {
								// 路由选择
								voting = audit.getEventList().toString();
								ts = item.getModifyTimestamp();
							} else {
								if (item.getModifyTimestamp().after(ts)) {
									ts = item.getModifyTimestamp();
									voting = audit.getEventList().toString();
								}
							}
						}
					}
				}
			}
		}
		if (voting.trim().length() > 0) {
			voting.replace("[", "");
			voting.replace("]", "");
		}
		// LOGGER.debug(" 活动["+actName+"] 路由选择为--->"+voting);
		return voting;
	}

	/**
	 * 获取流程任务的同一个活动下的所有任务
	 * 
	 * @param wi
	 * @return
	 */
	public static ArrayList<WorkItem> getWorkItems(WorkItem wi) {
		ArrayList<WorkItem> list = new ArrayList<WorkItem>();
		WfAssignedActivity waa = (WfAssignedActivity) wi.getSource().getObject();
		try {
			QuerySpec qs = new QuerySpec(WorkItem.class);
			qs.appendWhere(new SearchCondition(WorkItem.class, "source.key.id", SearchCondition.EQUAL,
					waa.getPersistInfo().getObjectIdentifier().getId()));
			QueryResult qr = PersistenceHelper.manager.find(qs);
			while (qr.hasMoreElements()) {
				WorkItem workItem = (WorkItem) qr.nextElement();
				list.add(workItem);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 获取同一个活动下的所有任务
	 * 
	 * @param wi
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static ArrayList<WorkItem> getWorkItems(WfAssignedActivity waa) {
		ArrayList<WorkItem> list = new ArrayList<WorkItem>();
		try {
			QuerySpec qs = new QuerySpec(WorkItem.class);
			qs.appendWhere(new SearchCondition(WorkItem.class, "source.key.id", SearchCondition.EQUAL,
					waa.getPersistInfo().getObjectIdentifier().getId()));
			LOGGER.debug(qs.getWhere());
			QueryResult qr = PersistenceHelper.manager.find(qs);
			while (qr.hasMoreElements()) {
				WorkItem workItem = (WorkItem) qr.nextElement();
				list.add(workItem);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public static ArrayList<WorkItem> getActivityWorkItems(WfAssignedActivity wfActivity) {
		ArrayList<WorkItem> list = new ArrayList<WorkItem>();
		try {
			Enumeration activityEnum = wfActivity.getAllAssignments();
			while (activityEnum.hasMoreElements()) {
				WfAssignment assignment = (WfAssignment) activityEnum.nextElement();
				LOGGER.debug(
						"getActivityWorkItems by getAllAssignments------------>" + assignment.getIdentity().toString());
				if (assignment != null) {
					QueryResult qr = PersistenceHelper.manager.navigate(assignment, WorkItemLink.WORK_ITEM_ROLE,
							WorkItemLink.class);
					while (qr.hasMoreElements()) {
						list.add((WorkItem) qr.nextElement());
					}
				}
			}

			Enumeration activityEnum1 = wfActivity.getAssignments();
			while (activityEnum1.hasMoreElements()) {
				WfAssignment assignment = (WfAssignment) activityEnum1.nextElement();
				LOGGER.debug(
						"getActivityWorkItems by getAssignments------------>" + assignment.getIdentity().toString());
				if (assignment != null) {
					QueryResult qr = PersistenceHelper.manager.navigate(assignment, "workItem", WorkItemLink.class);
					if (qr.size() > 0) {
						while (qr.hasMoreElements()) {
							list.add((WorkItem) qr.nextElement());
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.debug("getActivityWorkItems根据活动查询任务失败");
		}
		return list;
	}

	/**
	 * 获取流程模板中预设的活动参与者
	 * 
	 * @param template
	 * @return
	 */
	public static Vector getTemplateUsers(WfProcessTemplate template) {
		Vector vector = new Vector();
		/**
		 * 查询模板中所有预设的活动
		 */
		try {
			QuerySpec qs = new QuerySpec(WfAssignedActivityTemplate.class);
			qs.appendWhere(new SearchCondition(WfAssignedActivityTemplate.class, "parentTemplate.key.id",
					SearchCondition.EQUAL, template.getPersistInfo().getObjectIdentifier().getId()));
			QueryResult qr = PersistenceHelper.manager.find(qs);
			while (qr.hasMoreElements()) {
				WfAssignedActivityTemplate act = (WfAssignedActivityTemplate) qr.nextElement();
				Enumeration ens = act.getPrincipalRefs();
				while (ens.hasMoreElements()) {
					Object obj = ens.nextElement();
					// LOGGER.debug("从流程模板["+template.getName()+"]活动["+act.getName()+"]中获取到预设的参与者对象--->"+obj.toString());
					if (obj instanceof WTPrincipalReference) {
						WTPrincipalReference ref = (WTPrincipalReference) obj;
						vector.add(ref);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return vector;
	}

	/***
	 * 获取审阅单下的受影响部件、变更通知/变更任务的产生的对象、基线的部件
	 * 
	 * @param primaryBusinessObject
	 * @return
	 * @throws WTException
	 * @throws ChangeException2
	 */
	public static ArrayList<WTPart> getTargerParts(WTObject primaryBusinessObject) throws Exception {
		ArrayList<WTPart> list = new ArrayList<WTPart>();

		if (primaryBusinessObject instanceof WTChangeReview) {
			WTChangeReview review = (WTChangeReview) primaryBusinessObject;
			LOGGER.debug("getBomIntegrationResultInfo by review [" + review.getNumber() + "]");
			QueryResult qr = ChangeHelper2.service.getChangeables(review);
			while (qr.hasMoreElements()) {
				Object obj = qr.nextElement();
				if (obj instanceof WTPart) {
					WTPart part = (WTPart) obj;
					list.add(part);
				}
			}
		} else if (primaryBusinessObject instanceof WTChangeOrder2) {
			WTChangeOrder2 eco = (WTChangeOrder2) primaryBusinessObject;
			QueryResult qr = ChangeHelper2.service.getChangeablesAfter(eco);
			while (qr.hasMoreElements()) {
				Object obj = qr.nextElement();
				if (obj instanceof WTPart) {
					WTPart part = (WTPart) obj;
					list.add(part);
				}
			}
		} else if (primaryBusinessObject instanceof WTChangeActivity2) {
			WTChangeActivity2 eca = (WTChangeActivity2) primaryBusinessObject;
			QueryResult qr = ChangeHelper2.service.getChangeablesAfter(eca);
			while (qr.hasMoreElements()) {
				Object obj = qr.nextElement();
				if (obj instanceof WTPart) {
					WTPart part = (WTPart) obj;
					list.add(part);
				}
			}
		} else if (primaryBusinessObject instanceof ManagedBaseline) {
			ManagedBaseline baseLine = (ManagedBaseline) primaryBusinessObject;
			QueryResult qr = BaselineHelper.service.getBaselineItems(baseLine);
			while (qr.hasMoreElements()) {
				Object obj = qr.nextElement();
				if (obj instanceof WTPart) {
					WTPart part = (WTPart) obj;
					list.add(part);
				}
			}
		}

		return list;
	}

	/***
	 * 根据持久化对象获取流程实例
	 * 
	 * @param obj
	 * @return
	 * @throws Exception
	 */
	public static WfProcess getRelatedProcess(Persistable obj) throws Exception {
		WfProcess process = null;
		QueryResult qrProcs = null;
		qrProcs = WfEngineHelper.service.getAssociatedProcesses(obj, null, null);
		// 按时间排序,取最新一个流程实例
		CollationKeyFactory timeKeyFact = new CollationKeyFactory() {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");

			public String getCollationString(Object o) {
				if (!(o instanceof Persistable) || !PersistenceHelper.isPersistent(o))
					return "";
				return sdf.format(PersistenceHelper.getModifyStamp((Persistable) o));
			}
		};
		Enumeration enProcs = new SortedEnumeration(qrProcs, timeKeyFact, SortedEnumeration.DESCENDING);
		while (enProcs.hasMoreElements()) {
			process = (WfProcess) enProcs.nextElement();
			// 最新的非子进程（子进程的名称带有$符号
			if (process.getName().indexOf("$") == -1) {
				LOGGER.debug(" getRelatedProcess process=" + process.getName() + "  oid=" + process);
				break;
			}
		}
		return process;
	}

	/***
	 * 受影响对象
	 */
	public static final String AffectedObjects = "AffectedObjects";

	/***
	 * 产生的对象
	 */
	public static final String ResultingObjects = "ResultingObjects";

	/***
	 * 获取审阅/变更目标对象
	 * 
	 * @param <T>
	 * @param primaryBusinessObject 流程的primaryBusinessObject
	 * @param type                  GetTargerObjectUtil.AffectedObjects受影响对象/GetTargerObjectUtil.ResultingObjects产生的对象
	 * @param clazz                 WTPart.class/WTDocument.class
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static <T> ArrayList<T> getTargerObject(WTObject primaryBusinessObject, String type, Class<T> clazz)
			throws Exception {
		ArrayList<T> list = new ArrayList<T>();

		if (!type.equals(AffectedObjects) && !type.equals(ResultingObjects)) {
			LOGGER.debug("获取审阅/变更目标对象失败，只能指定为受影响对象或产生的对象");
			return list;
		}
		if (primaryBusinessObject instanceof WTChangeReview) {
			WTChangeReview review = (WTChangeReview) primaryBusinessObject;
			QueryResult qr = ChangeHelper2.service.getChangeables(review);
			while (qr.hasMoreElements()) {
				Object obj = qr.nextElement();
				if (clazz.getTypeName().equals(obj.getClass().getName())) {
					LOGGER.debug("当前的受影响对象类型为：" + obj.getClass().getName());
					T targerObj = (T) obj;
					list.add(targerObj);
				}
			}
		} else if (primaryBusinessObject instanceof WTChangeOrder2) {

			if (type.equals(AffectedObjects)) {
				WTChangeOrder2 eco = (WTChangeOrder2) primaryBusinessObject;
				QueryResult qr = ChangeHelper2.service.getChangeablesBefore(eco);
				while (qr.hasMoreElements()) {
					Object obj = qr.nextElement();
					if (clazz.getTypeName().equals(obj.getClass().getName())) {
						T targerObj = (T) obj;
						list.add(targerObj);
					}
				}
			} else if (type.equals(ResultingObjects)) {
				WTChangeOrder2 eco = (WTChangeOrder2) primaryBusinessObject;
				QueryResult qr = ChangeHelper2.service.getChangeablesAfter(eco);
				while (qr.hasMoreElements()) {
					Object obj = qr.nextElement();
					if (clazz.getTypeName().equals(obj.getClass().getName())) {
						T targerObj = (T) obj;
						list.add(targerObj);
					}
				}
			}

		} else if (primaryBusinessObject instanceof WTChangeActivity2) {

			if (type.equals(AffectedObjects)) {
				WTChangeActivity2 eca = (WTChangeActivity2) primaryBusinessObject;
				QueryResult qr = ChangeHelper2.service.getChangeablesBefore(eca);
				while (qr.hasMoreElements()) {
					Object obj = qr.nextElement();
					if (clazz.getTypeName().equals(obj.getClass().getName())) {
						T targerObj = (T) obj;
						list.add(targerObj);
					}
				}
			} else if (type.equals(ResultingObjects)) {
				WTChangeActivity2 eca = (WTChangeActivity2) primaryBusinessObject;
				QueryResult qr = ChangeHelper2.service.getChangeablesAfter(eca);
				while (qr.hasMoreElements()) {
					Object obj = qr.nextElement();
					if (clazz.getTypeName().equals(obj.getClass().getName())) {
						T targerObj = (T) obj;
						list.add(targerObj);
					}
				}
			}

		}
		return list;
	}

	/***
	 * 根据oid，返回持久化对象
	 * 
	 * @param oid
	 * @return
	 * @throws WTException
	 */
	public static Persistable getPersistableByOid(String oid) throws WTException {
		Persistable per = null;
		if (StringUtils.isNotEmpty(oid)) {
			ReferenceFactory referencefactory = new ReferenceFactory();
			WTReference wtreference = referencefactory.getReference(oid);
			per = wtreference.getObject();
		}
		return per;
	}
}
