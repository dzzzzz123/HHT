package ext.ptc.checkObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.logging.log4j.Logger;

import wt.change2.ChangeActivityIfc;
import wt.change2.ChangeHelper2;
import wt.change2.ChangeOrderIfc;
import wt.change2.ChangeRecord2;
import wt.change2.ChangeRequestIfc;
import wt.change2.ChangeReviewIfc;
import wt.change2.WTChangeActivity2;
import wt.change2.WTChangeOrder2;
import wt.change2.WTChangeRequest2;
import wt.change2.WTChangeReview;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.LifeCycleManaged;
import wt.lifecycle.State;
import wt.lifecycle.Transition;
import wt.log4j.LogR;
import wt.part.WTPart;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.query.WhereExpression;
import wt.util.WTException;
import wt.vc.Versionable;

public class ECUtil {
	private static Logger LOGGER = LogR.getLogger(ext.ptc.checkObject.ECUtil.class.getName());

	@SuppressWarnings("deprecation")
	public static ArrayList<WTPart> getChangePart(WTChangeActivity2 eca) throws WTException {
		ArrayList<WTPart> parts = new ArrayList<>();
		try {
			QuerySpec qs = new QuerySpec(ChangeRecord2.class);
			qs.appendWhere((WhereExpression) new SearchCondition(ChangeRecord2.class, "roleAObjectRef.key.branchId",
					"=", eca.getBranchIdentifier()));
			LOGGER.debug("查找变更后的对象SQL条件--->" + qs.getWhere());
			QueryResult qr = PersistenceHelper.manager.find(qs);
			while (qr.hasMoreElements()) {
				Object obj = qr.nextElement();
				if (obj instanceof ChangeRecord2) {
					ChangeRecord2 record = (ChangeRecord2) obj;
					Persistable p = record.getRoleBObject();
					if (p instanceof WTPart) {
						WTPart part = (WTPart) p;
						Transition trans = record.getTargetTransition();
						String changeType = "";
						if (trans != null)
							changeType = trans.toString();
						if (changeType.trim().length() == 0 || changeType.equals("CHANGE"))
							parts.add(part);
					}
				}
			}
			return parts;
		} catch (Exception e) {
			throw new WTException(e);
		}
	}

	@SuppressWarnings("deprecation")
	public static ArrayList<WTPart> getObsEPart(WTChangeActivity2 eca) throws WTException {
		ArrayList<WTPart> parts = new ArrayList<>();
		try {
			QuerySpec qs = new QuerySpec(ChangeRecord2.class);
			qs.appendWhere((WhereExpression) new SearchCondition(ChangeRecord2.class, "roleAObjectRef.key.branchId",
					"=", eca.getBranchIdentifier()));
			LOGGER.debug("查找变更后的对象SQL条件--->" + qs.getWhere());
			QueryResult qr = PersistenceHelper.manager.find(qs);
			while (qr.hasMoreElements()) {
				Object obj = qr.nextElement();
				if (obj instanceof ChangeRecord2) {
					ChangeRecord2 record = (ChangeRecord2) obj;
					Persistable p = record.getRoleBObject();
					if (p instanceof WTPart) {
						WTPart part = (WTPart) p;
						Transition trans = record.getTargetTransition();
						String changeType = "";
						if (trans != null)
							changeType = record.getTargetTransition().toString();
						if (changeType.trim().length() > 0 && !changeType.equals("CHANGE"))
							parts.add(part);
					}
				}
			}
			return parts;
		} catch (Exception e) {
			throw new WTException(e);
		}
	}

	public static ArrayList<WTPart> getBeforeParts(WTChangeActivity2 eca) throws WTException {
		ArrayList<WTPart> beforeParts = new ArrayList<>();
		try {
			QueryResult qr = ChangeHelper2.service.getChangeablesBefore((ChangeActivityIfc) eca);
			while (qr.hasMoreElements()) {
				Object obj = qr.nextElement();
				if (obj instanceof WTPart)
					beforeParts.add((WTPart) obj);
			}
		} catch (Exception e) {
			throw new WTException(e);
		}
		return beforeParts;
	}

	public static ArrayList<Object> getBeforeObject(WTChangeActivity2 eca) throws WTException {
		ArrayList<Object> beforeObjs = new ArrayList();
		try {
			QueryResult qr = ChangeHelper2.service.getChangeablesBefore((ChangeActivityIfc) eca);
			while (qr.hasMoreElements()) {
				Object obj = qr.nextElement();
				beforeObjs.add(obj);
			}
		} catch (Exception e) {
			throw new WTException(e);
		}
		return beforeObjs;
	}

	public static ArrayList<Object> getChangeObject(WTChangeActivity2 eca) throws WTException {
		ArrayList<Object> objects = new ArrayList();
		try {
			QueryResult qr = ChangeHelper2.service.getChangeablesAfter((ChangeActivityIfc) eca);
			while (qr.hasMoreElements()) {
				Object obj = qr.nextElement();
				objects.add(obj);
			}
		} catch (Exception e) {
			throw new WTException(e);
		}
		return objects;
	}

	public static ArrayList<WTPart> getChangeParts(WTChangeRequest2 ecr) throws WTException {
		ArrayList<WTPart> ChangeParts = new ArrayList<>();
		try {
			QueryResult qr = ChangeHelper2.service.getChangeables((ChangeRequestIfc) ecr);
			while (qr.hasMoreElements()) {
				Object obj = qr.nextElement();
				if (obj instanceof WTPart)
					ChangeParts.add((WTPart) obj);
			}
		} catch (Exception e) {
			throw new WTException(e);
		}
		return ChangeParts;
	}

	public static ArrayList<Object> getAffeectObjects(WTChangeRequest2 ecr) throws WTException {
		ArrayList<Object> ChangeParts = new ArrayList();
		try {
			QueryResult qr = ChangeHelper2.service.getChangeables((ChangeRequestIfc) ecr);
			while (qr.hasMoreElements()) {
				Object obj = qr.nextElement();
				ChangeParts.add(obj);
			}
		} catch (Exception e) {
			throw new WTException(e);
		}
		return ChangeParts;
	}

	public static ArrayList<WTPart> getChangeParts(WTChangeOrder2 eco) throws WTException {
		ArrayList<WTPart> ChangeParts = new ArrayList<>();
		try {
			QueryResult qr = ChangeHelper2.service.getChangeablesBefore((ChangeOrderIfc) eco);
			while (qr.hasMoreElements()) {
				Object obj = qr.nextElement();
				if (obj instanceof WTPart)
					ChangeParts.add((WTPart) obj);
			}
		} catch (Exception e) {
			throw new WTException(e);
		}
		return ChangeParts;
	}

	public static ArrayList<Object> getAffeectObject(WTChangeOrder2 eco) throws WTException {
		ArrayList<Object> ChangeParts = new ArrayList();
		try {
			QueryResult qr = ChangeHelper2.service.getChangeablesBefore((ChangeOrderIfc) eco);
			while (qr.hasMoreElements()) {
				Object obj = qr.nextElement();
				ChangeParts.add(obj);
			}
		} catch (Exception e) {
			throw new WTException(e);
		}
		return ChangeParts;
	}

	public static ArrayList<Object> getAffeectObject(WTChangeActivity2 eca) throws WTException {
		ArrayList<Object> ChangeParts = new ArrayList();
		try {
			QueryResult qr = ChangeHelper2.service.getChangeablesBefore((ChangeActivityIfc) eca);
			while (qr.hasMoreElements()) {
				Object obj = qr.nextElement();
				ChangeParts.add(obj);
			}
		} catch (Exception e) {
			throw new WTException(e);
		}
		return ChangeParts;
	}

	public static ArrayList<Object> getAffeectObject(WTChangeReview changeReview) throws WTException {
		ArrayList<Object> ChangeParts = new ArrayList();
		try {
			QueryResult qr = ChangeHelper2.service.getChangeables((ChangeReviewIfc) changeReview);
			while (qr.hasMoreElements())
				ChangeParts.add(qr.nextElement());
		} catch (Exception e) {
			throw new WTException(e);
		}
		return ChangeParts;
	}

	public static ArrayList<Object> getNewObject(WTChangeActivity2 eca) throws WTException {
		ArrayList<Object> newObject = new ArrayList();
		ArrayList<Object> afterObjects = getChangeObject(eca);
		ArrayList<Object> beforeObjects = getBeforeObject(eca);
		HashMap<String, Versionable> afterMap = new HashMap<>();
		for (Object obj : afterObjects) {
			if (obj instanceof Versionable) {
				Versionable v = (Versionable) obj;
				afterMap.put(v.getMaster().toString(), v);
			}
		}
		HashMap<String, Versionable> beforeMap = new HashMap<>();
		for (Object obj : beforeObjects) {
			if (obj instanceof Versionable) {
				Versionable v = (Versionable) obj;
				beforeMap.put(v.getMaster().toString(), v);
			}
		}
		Iterator<String> keys = afterMap.keySet().iterator();
		while (keys.hasNext()) {
			String key = keys.next();
			Versionable v = afterMap.get(key);
			if (!beforeMap.containsKey(key))
				newObject.add(v);
		}
		return newObject;
	}

	public static ArrayList<WTPart> getNewParts(WTChangeActivity2 eca) throws WTException {
		ArrayList<WTPart> newParts = new ArrayList<>();
		ArrayList<WTPart> afterParts = getChangePart(eca);
		ArrayList<WTPart> beforeParts = getBeforeParts(eca);
		HashMap<String, WTPart> afterMap = new HashMap<>();
		for (WTPart part : afterParts)
			afterMap.put(part.getNumber(), part);
		HashMap<String, WTPart> beforeMap = new HashMap<>();
		for (WTPart part : beforeParts)
			beforeMap.put(part.getNumber(), part);
		Iterator<String> keys = afterMap.keySet().iterator();
		while (keys.hasNext()) {
			String key = keys.next();
			WTPart part = afterMap.get(key);
			if (!beforeMap.containsKey(key))
				newParts.add(part);
		}
		return newParts;
	}

	public static boolean changeAfterPartState(WTChangeActivity2 eca, String state) {
		boolean result = true;
		try {
			ArrayList<WTPart> parts = getChangePart(eca);
			for (WTPart part : parts)
				LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged) part, State.toState(state));
		} catch (Exception e) {
			result = false;
			e.printStackTrace();
		}
		return result;
	}
}