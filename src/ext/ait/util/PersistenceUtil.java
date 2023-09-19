package ext.ait.util;

import java.rmi.RemoteException;
import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;

import com.ptc.core.meta.common.impl.TypeIdentifierUtilityHelper;
import com.ptc.core.meta.type.mgmt.common.TypeDefinitionDefaultView;
import com.ptc.core.meta.type.mgmt.server.impl.WTTypeDefinition;
import com.ptc.netmarkets.model.NmOid;

import wt.doc.WTDocument;
import wt.doc.WTDocumentMaster;
import wt.doc.WTDocumentMasterIdentity;
import wt.epm.EPMDocument;
import wt.epm.EPMDocumentMaster;
import wt.epm.EPMDocumentMasterIdentity;
import wt.epm.util.EPMSoftTypeServerUtilities;
import wt.fc.IdentityHelper;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.fc.WTObject;
import wt.fc.collections.WTArrayList;
import wt.folder.Folder;
import wt.httpgw.URLFactory;
import wt.lifecycle.LifeCycleManaged;
import wt.lifecycle.State;
import wt.org.WTPrincipal;
import wt.org.WTPrincipalReference;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.part.WTPartMasterIdentity;
import wt.pds.StatementSpec;
import wt.query.QueryException;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.type.TypeDefinitionReference;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.vc.Mastered;
import wt.vc.VersionControlHelper;
import wt.vc.VersionIdentifier;
import wt.vc.Versioned;
import wt.vc.baseline.ManagedBaseline;
import wt.vc.config.LatestConfigSpec;
import wt.vc.wip.CheckoutLink;
import wt.vc.wip.WorkInProgressHelper;
import wt.vc.wip.Workable;

public class PersistenceUtil {

	/**
	 * @description 得到对象的自定义/显示名称？
	 * @param WTObject
	 * @return String
	 * @throws WTException
	 */
	public static String getTypeName(WTObject obj) {
		String typeDisplayName = null;
		TypeDefinitionReference ref = null;
		WTTypeDefinition definition = null;
		if (obj instanceof WTPart) {
			ref = ((WTPart) obj).getTypeDefinitionReference();
			try {
				@SuppressWarnings("deprecation")
				TypeDefinitionDefaultView view = EPMSoftTypeServerUtilities.getTypeDefinition(ref);
				definition = (WTTypeDefinition) PersistenceHelper.manager.refresh(view.getObjectID());
				typeDisplayName = definition.getDisplayNameKey(); // 类型的key
			} catch (WTException e) {
				e.printStackTrace();
			}
		}
		return typeDisplayName;
	}

	/**
	 * 获取子类型 内部名称
	 * 
	 * @param per 持久化对象
	 * @return String
	 * @throws Exception
	 */
	public static String getSubTypeInternal(Persistable per) {
		try {
			String type = TypeIdentifierUtilityHelper.service.getTypeIdentifier(per).toString();
			String[] typeArray = type.split("\\|");
			if (typeArray.length > 0) {
				return typeArray[typeArray.length - 1];
			} else {
				return "";
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 检出对象
	 * 
	 * @param Workable
	 * @return Workable
	 * @throws WTException
	 */
	public static Workable checkoutPart(Workable workable) throws WTException {
		if (workable == null) {
			return null;
		}
		if (WorkInProgressHelper.isWorkingCopy(workable)) {
			return workable;
		}
		Folder folder = WorkInProgressHelper.service.getCheckoutFolder();
		try {
			CheckoutLink checkoutLink = WorkInProgressHelper.service.checkout(workable, folder, "AutoCheckOut");
			workable = checkoutLink.getWorkingCopy();
		} catch (WTPropertyVetoException ex) {
			ex.printStackTrace();
		}
		if (!WorkInProgressHelper.isWorkingCopy(workable)) {
			workable = WorkInProgressHelper.service.workingCopyOf(workable);
		}
		return workable;
	}

	/**
	 * 判断对象是否处于检出状态
	 * 
	 * @param Workable
	 * @return boolean
	 */
	public static boolean isCheckOut(Workable workable) {
		try {
			return WorkInProgressHelper.isCheckedOut(workable);
		} catch (WTException e) {

		}
		return false;
	}

	/**
	 * 修改几乎所有存在于Windchill中对象的状态
	 * 
	 * @param Object
	 * @param Object
	 * @return String
	 * @throws WTPropertyVetoException
	 * @throws WTException
	 */
	public static String changeObjectState(Object obj, Object objState) throws WTPropertyVetoException, WTException {
		String message = "";
		State newState = null;
		WTPrincipal currentUser = null;
		try {
			currentUser = SessionHelper.manager.getPrincipal();
			SessionHelper.manager.setAdministrator();
			if (objState instanceof State) {
				newState = (State) objState;
			} else if (objState instanceof String) {
				newState = State.toState(objState + "");
			}

			if (obj instanceof WTPart) {
				WTPart part = (WTPart) obj;
				part = (WTPart) wt.lifecycle.LifeCycleHelper.service
						.setLifeCycleState((wt.lifecycle.LifeCycleManaged) part, newState);
				PersistenceHelper.manager.refresh(part);

			} else if (obj instanceof EPMDocument) {
				EPMDocument epmDocument = (EPMDocument) obj;
				epmDocument = (EPMDocument) wt.lifecycle.LifeCycleHelper.service
						.setLifeCycleState((wt.lifecycle.LifeCycleManaged) epmDocument, newState);
				PersistenceHelper.manager.refresh(epmDocument);

			} else if (obj instanceof WTDocument) {
				WTDocument wtDocument = (WTDocument) obj;
				wtDocument = (WTDocument) wt.lifecycle.LifeCycleHelper.service
						.setLifeCycleState((wt.lifecycle.LifeCycleManaged) wtDocument, newState);
				PersistenceHelper.manager.refresh(wtDocument);

			} else if (obj instanceof ManagedBaseline) {
				ManagedBaseline managedBaseline = (ManagedBaseline) obj;
				managedBaseline = (ManagedBaseline) wt.lifecycle.LifeCycleHelper.service
						.setLifeCycleState((wt.lifecycle.LifeCycleManaged) managedBaseline, newState);
				PersistenceHelper.manager.refresh(managedBaseline);

			} else if (obj instanceof LifeCycleManaged) {
				LifeCycleManaged lifeCycleManaged = (LifeCycleManaged) obj;
				lifeCycleManaged = (LifeCycleManaged) wt.lifecycle.LifeCycleHelper.service
						.setLifeCycleState((wt.lifecycle.LifeCycleManaged) lifeCycleManaged, newState);
				PersistenceHelper.manager.refresh(lifeCycleManaged);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (currentUser != null) {
				SessionHelper.manager.setPrincipal(currentUser.getName());
			}
		}
		return message;
	}

	/**
	 * 更改部件/文档/图纸/其他对象的名称
	 * 
	 * @param Mastered
	 * @param String
	 */
	public static void changeName(Mastered mast, String name) {
		WTPrincipal user = null;
		try {
			user = SessionHelper.getPrincipal();
			SessionHelper.manager.setAdministrator();
			if (mast instanceof WTDocumentMaster) {
				WTDocumentMaster master = (WTDocumentMaster) mast;
				master = (WTDocumentMaster) PersistenceHelper.manager.refresh(master);
				WTDocumentMasterIdentity identity = (WTDocumentMasterIdentity) master.getIdentificationObject();
				identity.setName(name);
				master = (WTDocumentMaster) IdentityHelper.service.changeIdentity(master, identity);
				PersistenceHelper.manager.save(master);
			} else if (mast instanceof WTPartMaster) {
				WTPartMaster master = (WTPartMaster) mast;
				master = (WTPartMaster) PersistenceHelper.manager.refresh(master);
				WTPartMasterIdentity identity = (WTPartMasterIdentity) master.getIdentificationObject();
				identity.setName(name);
				master = (WTPartMaster) IdentityHelper.service.changeIdentity(master, identity);
				PersistenceHelper.manager.save(master);
			} else if (mast instanceof EPMDocumentMaster) {
				EPMDocumentMaster master = (EPMDocumentMaster) mast;
				master = (EPMDocumentMaster) PersistenceHelper.manager.refresh(master);
				EPMDocumentMasterIdentity identity = (EPMDocumentMasterIdentity) master.getIdentificationObject();
				identity.setName(name);
				master = (EPMDocumentMaster) IdentityHelper.service.changeIdentity(master, identity);
				PersistenceHelper.manager.save(master);
			}
			SessionHelper.manager.setPrincipal(user.getName());
			user = null;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (user != null) {
				try {
					SessionHelper.manager.setPrincipal(user.getName());
				} catch (WTException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 修改文档、物料、图纸等其他对象的编号
	 * 
	 * @param Mastered
	 * @param String
	 */
	public static void changeNumber(Mastered mast, String number) {
		WTPrincipal user = null;
		try {
			user = SessionHelper.getPrincipal();
			SessionHelper.manager.setAdministrator();
			if (mast instanceof WTDocumentMaster) {
				WTDocumentMaster master = (WTDocumentMaster) mast;
				master = (WTDocumentMaster) PersistenceHelper.manager.refresh(master);
				WTDocumentMasterIdentity identity = (WTDocumentMasterIdentity) master.getIdentificationObject();
				identity.setNumber(number);
				master = (WTDocumentMaster) IdentityHelper.service.changeIdentity(master, identity);
				PersistenceHelper.manager.save(master);
			} else if (mast instanceof WTPartMaster) {
				WTPartMaster master = (WTPartMaster) mast;
				master = (WTPartMaster) PersistenceHelper.manager.refresh(master);
				WTPartMasterIdentity identity = (WTPartMasterIdentity) master.getIdentificationObject();
				identity.setNumber(number);
				master = (WTPartMaster) IdentityHelper.service.changeIdentity(master, identity);
				PersistenceHelper.manager.save(master);
			} else if (mast instanceof EPMDocumentMaster) {
				EPMDocumentMaster master = (EPMDocumentMaster) mast;
				master = (EPMDocumentMaster) PersistenceHelper.manager.refresh(master);
				EPMDocumentMasterIdentity identity = (EPMDocumentMasterIdentity) master.getIdentificationObject();
				identity.setNumber(number);
				master = (EPMDocumentMaster) IdentityHelper.service.changeIdentity(master, identity);
				PersistenceHelper.manager.save(master);
			}
			SessionHelper.manager.setPrincipal(user.getName());
			user = null;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (user != null) {
				try {
					SessionHelper.manager.setPrincipal(user.getName());
				} catch (WTException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 获取对象的oid
	 * 
	 * @param WTObject
	 * @return String
	 */
	public static String object2Oid(WTObject obj) {
		String className = obj.getClass().getName();
		String oid = String.valueOf(obj.getPersistInfo().getObjectIdentifier().getId());
		return "OR:" + className + ":" + oid;
	}

	/**
	 * 获取oid所对应的对象
	 * 
	 * @param String
	 * @return WTObject
	 * @throws WTException
	 */
	public static WTObject oid2Object(String oid) throws WTException {
		ReferenceFactory factory = new ReferenceFactory();
		Persistable persistable = factory.getReference(oid).getObject();
		return (WTObject) persistable;
	}

	/**
	 * 根据VR获取对应版本的对象
	 * 
	 * @param Class
	 * @param String
	 * @return Object
	 */
	@SuppressWarnings("deprecation")
	public static Object getObjByVR(Class queryClass, String vr) {
		try {
			if (StringUtils.isBlank(vr)) {
				return null;
			}
			QuerySpec qs = new QuerySpec(queryClass);
			qs.appendWhere(
					new SearchCondition(queryClass, "iterationInfo.branchId", SearchCondition.EQUAL, Long.valueOf(vr)));
			QueryResult qr = PersistenceHelper.manager.find(qs);
			qr = new LatestConfigSpec().process(qr);
			if (qr.hasMoreElements()) {
				return qr.nextElement();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 根据编号获取对象
	 * 
	 * @param String
	 * @param Class
	 * @return Object
	 */
	@SuppressWarnings("deprecation")
	public static Object getObjByNumber(String number, Class queryClass) {
		try {
			if (StringUtils.isBlank(number)) {
				return null;
			}
			QuerySpec qs = new QuerySpec(queryClass);
			qs.appendWhere(new SearchCondition(queryClass, "master>number", SearchCondition.EQUAL, number.trim()));
			QueryResult qr = PersistenceHelper.manager.find(qs);
			qr = new LatestConfigSpec().process(qr);
			if (qr.hasMoreElements()) {
				return qr.nextElement();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取已持久化对象对应的URL
	 * 
	 * @param Persistable
	 * @return String
	 */
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

	/**
	 * 根据对象的number找到最新版本的对象
	 *
	 * @author gongke
	 * @param number    要查询的对象的编号
	 * @param thisClass class对象
	 * @return 由number标识的最新版本对象
	 */
	public static Persistable getLatestPersistableByNumber(String number, Class thisClass) {
		Persistable persistable = null;
		try {
			int[] index = { 0 };
			QuerySpec qs = new QuerySpec(thisClass);
			String attribute = (String) thisClass.getField("NUMBER").get(thisClass);
			qs.appendWhere(new SearchCondition(thisClass, attribute, SearchCondition.EQUAL, number), index);
			QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
			LatestConfigSpec configSpec = new LatestConfigSpec();
			qr = configSpec.process(qr);
			if (qr != null && qr.hasMoreElements()) {
				persistable = (Persistable) qr.nextElement();
			}
		} catch (QueryException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
		return persistable;
	}

	/**
	 * 根据对象的number verision找到最新版本的对象
	 *
	 * @author gongke
	 * @param number    要查询的对象的编号
	 * @param thisClass class对象
	 * @return 由number标识的最新版本对象
	 */
	public static Persistable getLatestPersistableByNumberAndVersion(String number, String version, Class thisClass) {
		Persistable persistable = null;
		try {
			int[] index = { 0 };
			QuerySpec qs = new QuerySpec(thisClass);
			String attribute = (String) thisClass.getField("NUMBER").get(thisClass);
			qs.appendWhere(new SearchCondition(thisClass, attribute, SearchCondition.EQUAL, number), index);
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(thisClass,
					Versioned.VERSION_IDENTIFIER + "." + VersionIdentifier.VERSIONID, SearchCondition.EQUAL, version),
					index);
			QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
			LatestConfigSpec configSpec = new LatestConfigSpec();
			qr = configSpec.process(qr);
			if (qr != null && qr.hasMoreElements()) {
				persistable = (Persistable) qr.nextElement();
			}
		} catch (QueryException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
		return persistable;
	}

	/**
	 * 判断一个列表中是否存在某个持久对象
	 * 
	 * @param WTArrayList
	 * @param Persistable
	 * @return boolean
	 * @throws WTException
	 */
	public static boolean containsPersistable(WTArrayList list, Persistable destination) throws WTException {
		boolean flag = false;
		if (list != null && list.size() > 0) {
			if (list.contains(destination)) {
				flag = true;
			} else {
				int size = list.size();
				for (int i = 0; i < size; ++i) {
					flag = PersistenceHelper.isEquivalent(list.getPersistable(i), destination);
				}
			}
		} else {
			flag = false;
		}
		return flag;
	}

	/**
	 * 合并两个WTArrayList
	 * 
	 * @param WTArrayList
	 * @param WTArrayList
	 * @return WTArrayList
	 */
	public static WTArrayList mergeList(WTArrayList wtList1, WTArrayList wtList2) {
		if (wtList1 == null && wtList2 == null) {
			return new WTArrayList();
		} else if (wtList1 != null && wtList2 == null) {
			return wtList1;
		} else if (wtList1 == null && wtList2 != null) {
			return wtList2;
		} else {
			int size = wtList2.size();

			for (int i = 0; i < size; ++i) {
				try {
					Persistable persistable = wtList2.getPersistable(i);
					boolean flag = containsPersistable(wtList1, persistable);
					if (!flag) {
						wtList1.add(persistable);
					}
				} catch (WTException e) {
					e.printStackTrace();
				}
			}

			return wtList1;
		}
	}

	/**
	 * 给有版本的对象设置创作者
	 * 
	 * @param Versioned
	 * @param WTPrincipalReference
	 * @throws WTException
	 */
	public static void setCreator(Versioned newVer, WTPrincipalReference principalRef) throws WTException {
		boolean flag = SessionServerHelper.manager.setAccessEnforced(false);

		try {
			VersionControlHelper.assignIterationCreator(newVer, principalRef);
		} catch (WTPropertyVetoException e) {
			throw new WTException(e);
		} finally {
			SessionServerHelper.manager.setAccessEnforced(flag);
		}
	}

	/**
	 * 给有版本的对象设置修改者
	 * 
	 * @param Versioned
	 * @param WTPrincipalReference
	 * @throws WTException
	 */
	public static void setModifier(Versioned newVer, WTPrincipalReference principalRef) throws WTException {
		boolean flag = SessionServerHelper.manager.setAccessEnforced(false);

		try {
			VersionControlHelper.setIterationModifier(newVer, principalRef);
		} catch (WTPropertyVetoException var8) {
			throw new WTException(var8);
		} finally {
			SessionServerHelper.manager.setAccessEnforced(flag);
		}
	}

	/**
	 * 获取现在的用户姓名
	 * 
	 * @return String
	 */
	public static String getCurrentPrincipalName() {
		try {
			WTPrincipal principal = SessionHelper.getPrincipal();
			if (principal != null) {
				return principal.getName();
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		return "";
	}
}
