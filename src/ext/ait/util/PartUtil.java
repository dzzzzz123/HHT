package ext.ait.util;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.ptc.netmarkets.model.NmOid;
import com.ptc.windchill.cadx.common.WTPartUtilities;
import com.ptc.windchill.enterprise.workflow.WorkflowCommands;

import wt.configuration.TraceCode;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.epm.build.EPMBuildRule;
import wt.fc.Identified;
import wt.fc.ObjectReference;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.fc.collections.WTCollection;
import wt.fc.collections.WTHashSet;
import wt.folder.CabinetBased;
import wt.folder.FolderEntry;
import wt.folder.FolderHelper;
import wt.inf.container.WTContained;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerHelper;
import wt.inf.container.WTContainerRef;
import wt.lifecycle.LifeCycleState;
import wt.lifecycle.State;
import wt.maturity.MaturityHelper;
import wt.maturity.PromotionNotice;
import wt.method.RemoteAccess;
import wt.org.WTPrincipal;
import wt.part.PartType;
import wt.part.PartUsesOccurrence;
import wt.part.QuantityUnit;
import wt.part.Source;
import wt.part.WTPart;
import wt.part.WTPartAlternateLink;
import wt.part.WTPartConfigSpec;
import wt.part.WTPartHelper;
import wt.part.WTPartMaster;
import wt.part.WTPartMasterIdentity;
import wt.part.WTPartStandardConfigSpec;
import wt.part.WTPartSubstituteLink;
import wt.part.WTPartUsageLink;
import wt.pds.StatementSpec;
import wt.query.QueryException;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.vc.VersionControlHelper;
import wt.vc.config.LatestConfigSpec;
import wt.vc.struct.StructHelper;
import wt.vc.views.View;
import wt.vc.views.ViewException;
import wt.vc.views.ViewHelper;
import wt.vc.wip.WorkInProgressHelper;
import wt.vc.wip.Workable;
import wt.workflow.engine.WfProcess;
import wt.workflow.engine.WfState;

public class PartUtil implements RemoteAccess {

	/**
	 * 取得所有子部件的关联
	 * 
	 * @param WTPart
	 * @return Vector<WTPartUsageLink>
	 */
	@SuppressWarnings("unchecked")
	public static Vector<WTPartUsageLink> getSubPartUsagelinks(WTPart parentPart) throws WTException {
		Vector<WTPartUsageLink> partlist = new Vector<>();
		QueryResult subParts = WTPartHelper.service.getUsesWTPartMasters(parentPart);
		while (subParts.hasMoreElements()) {
			WTPartUsageLink usagelink = (WTPartUsageLink) subParts.nextElement();
			partlist.addElement(usagelink);
		}
		return partlist;
	}

	/**
	 * 根据编号查询part
	 * 
	 * @param String
	 * @return WTPart
	 */
	@SuppressWarnings("deprecation")
	public static WTPart getWTPartByNumber(String number) {
		WTPart result = null;
		QueryResult qr = null;
		try {
			QuerySpec qs = new QuerySpec(WTPart.class);
			SearchCondition scnumber = new SearchCondition(WTPart.class, wt.part.WTPart.NUMBER, SearchCondition.EQUAL,
					number.toUpperCase());
			qs.appendSearchCondition(scnumber);
			qs.appendAnd();
			SearchCondition sclatest = VersionControlHelper.getSearchCondition(wt.part.WTPart.class, true);
			qs.appendSearchCondition(sclatest);
			qr = PersistenceHelper.manager.find(qs);
			LatestConfigSpec cfg = new LatestConfigSpec();
			QueryResult qr1 = cfg.process(qr);
			if (qr1 != null && qr1.hasMoreElements()) {
				result = (WTPart) qr1.nextElement();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 不递归获取部件所有的子部件
	 * 
	 * @param WTPart
	 * @return List<WTPart>
	 */
	public static List<WTPart> getBomByPart(WTPart ProductPart) {
		WTPart sPart = null;
		QueryResult qr2 = null;
		List<WTPart> list = new ArrayList<WTPart>();
		try {
			QueryResult qr = WTPartHelper.service.getUsesWTPartMasters(ProductPart);
			while (qr.hasMoreElements()) {
				WTPartUsageLink usageLink = (WTPartUsageLink) qr.nextElement();
				qr2 = VersionControlHelper.service.allVersionsOf(usageLink.getUses());
				if (qr2.hasMoreElements()) {
					sPart = (WTPart) qr2.nextElement();
					list.add(sPart);
				}
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 修改部件编号
	 * 
	 * @param WTPart
	 * @param String
	 */
	public static void changePartNumber(WTPart part, String newPartNumber) {
		try {
			part = (WTPart) PersistenceHelper.manager.refresh(part);
			Identified identified = (Identified) part.getMaster();
			WTPartMasterIdentity partIdentity = (WTPartMasterIdentity) identified.getIdentificationObject();
			partIdentity.setNumber(newPartNumber);
			identified = wt.fc.IdentityHelper.service.changeIdentity(identified, partIdentity);
			part = (WTPart) PersistenceHelper.manager.refresh(part);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 修改部件名称
	 * 
	 * @param WTPart
	 * @param String
	 */
	public static void changePartName(WTPart part, String newName) {
		try {
			part = (WTPart) PersistenceHelper.manager.refresh(part);
			Identified identified = (Identified) part.getMaster();
			WTPartMasterIdentity partIdentity = (WTPartMasterIdentity) identified.getIdentificationObject();
			partIdentity.setName(newName);
			identified = wt.fc.IdentityHelper.service.changeIdentity(identified, partIdentity);
			part = (WTPart) PersistenceHelper.manager.refresh(part);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取原样位号
	 * 
	 * @param WTPartUsageLink
	 * @return String
	 * @throws WTException
	 */
	@SuppressWarnings({ "unchecked", "deprecation" })
	public static String getPartUsesOccurrence(WTPartUsageLink useagelink) {
		String occurrenceStr = "";
		List listWTPart2 = new ArrayList();
		long linkid = PersistenceHelper.getObjectIdentifier(useagelink).getId();
		try {
			QuerySpec qs = new QuerySpec(PartUsesOccurrence.class);
			qs.appendWhere(new SearchCondition(PartUsesOccurrence.class, "linkReference.key.id", SearchCondition.EQUAL,
					linkid));
			QueryResult qr = PersistenceHelper.manager.find(qs);
			while (qr.hasMoreElements()) {
				PartUsesOccurrence occurrence = (PartUsesOccurrence) qr.nextElement();
				listWTPart2.add(occurrence.getName());
			}
			for (int i = 0; i < listWTPart2.size(); i++) {
				if (listWTPart2.get(i) != null) {
					String ocName = listWTPart2.get(i).toString();
					if (i == 0) {
						occurrenceStr = ocName;
					} else {
						occurrenceStr = occurrenceStr + "," + ocName;
					}
				}
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		return sortPlaceNumber(occurrenceStr);
	}

	// 对位号排序
	public static String sortPlaceNumber(String numberStr) {
		TreeMap<String, String> tm = new TreeMap<>();
		String[] s = numberStr.split(",");
		for (int i = 0; i < s.length; i++) {
			tm.put(s[i], s[i]);
		}
		Iterator it = tm.keySet().iterator();
		String number = "";
		int a = 0;
		while (it.hasNext()) {
			if (a == 0) {
				number = (String) it.next();
			} else {
				number = number + "," + (String) it.next();
			}
			a++;
		}
		return number;
	}

	/**
	 * 根据部件获取全局替代料的关联关系
	 * 
	 * @param WTPart
	 * @return List<WTPartAlternateLink>
	 */
	public static List<WTPartAlternateLink> getWTPartAlternateLinks(WTPart wtpart) {
		if (wtpart == null) {
			return null;
		}
		List<WTPartAlternateLink> list = new ArrayList<WTPartAlternateLink>();
		long masterId = PersistenceHelper.getObjectIdentifier(wtpart.getMaster()).getId();
		int[] index = { 0 };
		try {
			QuerySpec qs = new QuerySpec(WTPartAlternateLink.class);
			qs.appendWhere(new SearchCondition(WTPartAlternateLink.class, "roleAObjectRef.key.id",
					SearchCondition.EQUAL, masterId), index);
			QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
			while (qr.hasMoreElements()) {
				WTPartAlternateLink sLink = (WTPartAlternateLink) qr.nextElement();
				list.add(sLink);
			}
		} catch (QueryException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 根据部件于获取局部替代料的关联关系
	 * 
	 * @param WTPart part
	 * @return List<WTPartSubstituteLink>
	 */
	public static List<WTPartSubstituteLink> getWTPartSubstituteLinks(WTPartUsageLink usageLink) {
		if (usageLink == null) {
			return null;
		}
		List<WTPartSubstituteLink> list = new ArrayList<WTPartSubstituteLink>();
		long masterId = PersistenceHelper.getObjectIdentifier(usageLink).getId();
		int[] index = { 0 };
		try {
			QuerySpec qs = new QuerySpec(WTPartSubstituteLink.class);
			qs.appendWhere(new SearchCondition(WTPartSubstituteLink.class, "roleAObjectRef.key.id",
					SearchCondition.EQUAL, masterId), index);
			QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
			while (qr.hasMoreElements()) {
				WTPartSubstituteLink sLink = (WTPartSubstituteLink) qr.nextElement();
				list.add(sLink);
			}
		} catch (QueryException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 修改部件生命周期状态
	 * 
	 * @param WTPart
	 * @param String
	 * @return String
	 * @throws WTPropertyVetoException
	 * @throws WTException
	 */
	public static String changePartState(WTPart part, String state) throws WTPropertyVetoException, WTException {
		String message = "";
		WTPrincipal currentUser = null;
		try {
			currentUser = SessionHelper.manager.getPrincipal();
			SessionHelper.manager.setAdministrator();

			part = (WTPart) wt.lifecycle.LifeCycleHelper.service.setLifeCycleState((wt.lifecycle.LifeCycleManaged) part,
					State.toState(state));

			PersistenceHelper.manager.refresh(part);
		} catch (Exception e) {
			e.printStackTrace();
			throw new WTException(e);
		} finally {
			if (currentUser != null) {
				SessionHelper.manager.setPrincipal(currentUser.getName());
			}
		}
		return message;
	}

	/**
	 * 修改部件状态（直接使用State进行修改）
	 * 
	 * @param WTPart
	 * @param State
	 * @return String
	 * @throws WTPropertyVetoException
	 * @throws WTException
	 */
	public static String changePartState(WTPart part, State state) throws WTPropertyVetoException, WTException {
		String message = "";
		WTPrincipal currentUser = null;
		try {
			currentUser = SessionHelper.manager.getPrincipal();
			SessionHelper.manager.setAdministrator();

			part = (WTPart) wt.lifecycle.LifeCycleHelper.service.setLifeCycleState((wt.lifecycle.LifeCycleManaged) part,
					state);

			PersistenceHelper.manager.refresh(part);
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
	 * 通过说明文档找到部件
	 * 
	 * @param WTDocument
	 * @return List<WTPart>
	 */
	public static List<WTPart> getPartByDescribesDoc(WTDocument doc) {
		QueryResult qr = null;
		WTPart part = null;
		List<WTPart> list = new ArrayList<WTPart>();
		try {
			qr = WTPartHelper.service.getDescribesWTParts(doc);// 说明文档
			while (qr.hasMoreElements()) {
				Object obj = qr.nextElement();
				if (obj instanceof WTPart) {
					part = (WTPart) obj;
					list.add(part);
				}
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 将CAD文档关联到部件上
	 * 
	 * @param WTPart
	 * @param EPMDocument
	 * @param int
	 */
	public static void addCAD2WTPart(WTPart part, EPMDocument cad, int linkType) {
		try {
			EPMBuildRule newEPMBuildRule = EPMBuildRule.newEPMBuildRule(cad, part, linkType);
			PersistenceServerHelper.manager.insert(newEPMBuildRule);
		} catch (WTException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取参考文档关联的部件
	 * 
	 * @param WTDocument
	 * @return List<WTPart>
	 */
	public static List<WTPart> getPartByRefrenceDoc(WTDocument doc) {
		QueryResult qr = null;
		WTPart part = null;
		List<WTPart> list = new ArrayList<WTPart>();
		try {
			qr = StructHelper.service.navigateReferencedBy(doc.getMaster());// 参考关系
			while (qr.hasMoreElements()) {
				Object obj = qr.nextElement();
				if (obj instanceof WTPart) {
					part = (WTPart) obj;
					list.add(part);
				}
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 获取部件间的关联关系
	 * 
	 * @param WTPart
	 * @param WTPart
	 * @return WTPartUsageLink
	 */
	public static WTPartUsageLink getLinkByPart(WTPart fatherPart, WTPart sonPart) {
		try {
			QueryResult qr = WTPartHelper.service.getUsesWTPartMasters(fatherPart);
			while (qr.hasMoreElements()) {
				WTPartUsageLink link = (WTPartUsageLink) qr.nextElement();
				WTPart part = getBomPartBylink(link);
				if (part.getNumber().equals(sonPart.getNumber())) {
					return link;
				}
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 根据部件间的关联关系获取子部件
	 * 
	 * @param WTPartUsageLink
	 * @return WTPart
	 */
	public static WTPart getBomPartBylink(WTPartUsageLink link) {
		try {
			QueryResult qr = VersionControlHelper.service.allVersionsOf(link.getUses());
			if (qr.hasMoreElements()) {
				WTPart part = (WTPart) qr.nextElement();
				return part;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取部件与其所有子部件间的关联关系
	 * 
	 * @param WTPart
	 * @return List<WTPartUsageLink>
	 */
	public static List<WTPartUsageLink> getLinksByPart(WTPart fatherPart) {
		System.out.println("getNumber" + fatherPart.getNumber());
		List<WTPartUsageLink> list = new ArrayList<WTPartUsageLink>();
		try {
			QueryResult qr = WTPartHelper.service.getUsesWTPartMasters(fatherPart);
			while (qr.hasMoreElements()) {
				WTPartUsageLink link = (WTPartUsageLink) qr.nextElement();
				list.add(link);
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		System.out.println("listsize" + list.size());
		return list;
	}

	/**
	 * 获取部件的子部件和数量关系
	 * 
	 * @param WTPart
	 * @param List<Object[]>
	 * @return List<Object[]>
	 */
	public static List<Object[]> getAllBomByParentPart(WTPart parentPart, List<Object[]> list) {
		WTPart sPart = null;
		QueryResult qr2 = null;
		try {
			QueryResult qr = WTPartHelper.service.getUsesWTPartMasters(parentPart);
			while (qr.hasMoreElements()) {
				WTPartUsageLink usageLink = (WTPartUsageLink) qr.nextElement();
				qr2 = VersionControlHelper.service.allVersionsOf(usageLink.getUses());
				if (qr2.hasMoreElements()) {
					sPart = (WTPart) qr2.nextElement();
					boolean found = false;
					for (Object[] old : list) {
						WTPart object = (WTPart) old[0];
						String number = object.getNumber();
						if (sPart.getNumber().equalsIgnoreCase(number)) {
							old[1] = (Double) old[1] + usageLink.getQuantity().getAmount();
							found = true;
							break;
						}
					}
					if (!found) {
						list.add(new Object[] { sPart, usageLink.getQuantity().getAmount() });
					}
					getAllBomByParentPart(sPart, list);
				}
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 递归获取部件的所有子部件
	 *
	 * @param WTPart
	 * @return List<WTPart>
	 */
	public static List<WTPart> getAllBomByPart(WTPart part) {
		List<WTPart> localList = new ArrayList<>(); // 局部列表用于递归操作的累积结果

		WTPart sPart = null;
		QueryResult qr2 = null;
		try {
			QueryResult qr = WTPartHelper.service.getUsesWTPartMasters(part);
			while (qr.hasMoreElements()) {
				WTPartUsageLink usageLink = (WTPartUsageLink) qr.nextElement();
				qr2 = VersionControlHelper.service.allVersionsOf(usageLink.getUses());
				if (qr2.hasMoreElements()) {
					sPart = (WTPart) qr2.nextElement();
					localList.add(sPart);

					// 递归调用，将结果合并到局部列表
					localList.addAll(getAllBomByPart(sPart));
				}
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		return localList; // 返回局部列表
	}

	/**
	 * 检入部件
	 * 
	 * @param WTPart
	 * @param String
	 * @return WTPart
	 * @throws WTException
	 */
	public static WTPart checkinPart(WTPart partParentPrevious, String comment) throws WTException {
		WTPart part = null;
		if (partParentPrevious != null) {
			try {
				wt.org.WTPrincipal wtprincipal = SessionHelper.manager.getPrincipal();
				if (WorkInProgressHelper.isCheckedOut((Workable) partParentPrevious, wtprincipal)) {
					if (!WorkInProgressHelper.isWorkingCopy((Workable) partParentPrevious)) {
						partParentPrevious = (WTPart) WorkInProgressHelper.service
								.workingCopyOf((Workable) partParentPrevious);
					}
					part = (WTPart) WorkInProgressHelper.service.checkin((Workable) partParentPrevious, comment);
				}
			} catch (WTPropertyVetoException ex) {
				ex.printStackTrace();
			}
		}
		return part;
	}

	/**
	 * 根据替代料获取原部件
	 * 
	 * @param WTPart
	 * @return List<WTPart>
	 * @throws WTException
	 */
	public static List<WTPart> getPart(WTPart part) throws WTException {
		List<WTPart> list = new ArrayList<WTPart>();
		if (part == null) {
			return list;
		}
		long linkid = PersistenceHelper.getObjectIdentifier(part.getMaster()).getId();

		int[] index = { 0 };
		QuerySpec qs = new QuerySpec(WTPartSubstituteLink.class);
		qs.appendWhere(
				new SearchCondition(WTPartSubstituteLink.class, "roleBObjectRef.key.id", SearchCondition.EQUAL, linkid),
				index);
		QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
		while (qr.hasMoreElements()) {
			WTPartSubstituteLink sLink = (WTPartSubstituteLink) qr.nextElement();
			WTPartUsageLink ulink = sLink.getSubstituteFor();
			QueryResult qr2 = VersionControlHelper.service.allVersionsOf(ulink.getUses());
			if (qr2.hasMoreElements()) {
				WTPart part2 = (WTPart) qr2.nextElement();
				list.add(part2);
			}
		}
		return list;
	}

	/**
	 * 根据子部件获取所有父部件之间的关联
	 * 
	 * @param WTPart
	 * @return List<WTPartUsageLink>
	 */
	@SuppressWarnings("deprecation")
	public static List<WTPartUsageLink> getBomLinkByChildPart(WTPart childPart) {
		List<WTPartUsageLink> list = new ArrayList<WTPartUsageLink>();
		try {
			if (childPart != null) {
				QuerySpec queryspec = new QuerySpec(WTPartUsageLink.class);
				queryspec.appendWhere(new SearchCondition(WTPartUsageLink.class, "roleBObjectRef.key", "=",
						PersistenceHelper.getObjectIdentifier((WTPartMaster) childPart.getMaster())));
				QueryResult qr = PersistenceHelper.manager.find((StatementSpec) queryspec);
				while (qr.hasMoreElements()) {
					WTPartUsageLink usageLink = (WTPartUsageLink) qr.nextElement();
					list.add(usageLink);
				}
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 通过父部件和子部件获取部件之间使用关系
	 * 
	 * @param fatherPart
	 * @param childPart
	 * @return
	 */
	public static List<WTPartUsageLink> getWtPartUsageLink(WTPart fatherPart, WTPart childPart) {
		List<WTPartUsageLink> list = new ArrayList<WTPartUsageLink>();
		try {
			if (childPart != null) {
				QuerySpec queryspec = new QuerySpec(WTPartUsageLink.class);
				queryspec.appendWhere(new SearchCondition(WTPartUsageLink.class, "roleAObjectRef.key", "=",
						PersistenceHelper.getObjectIdentifier(fatherPart)));
				queryspec.appendAnd();
				queryspec.appendWhere(new SearchCondition(WTPartUsageLink.class, "roleBObjectRef.key", "=",
						PersistenceHelper.getObjectIdentifier((WTPartMaster) childPart.getMaster())));
				QueryResult qr = PersistenceHelper.manager.find((StatementSpec) queryspec);
				while (qr.hasMoreElements()) {
					WTPartUsageLink usageLink = (WTPartUsageLink) qr.nextElement();
					list.add(usageLink);
				}
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 复制一个新部件（名称与编号不同）
	 * 
	 * @param WTPart
	 * @param String
	 * @return WTPart
	 */
	@SuppressWarnings("deprecation")
	public static WTPart copyPart(WTPart part, String partName) {
		WTPart newPart = null;
		try {
			String number = part.getNumber();
			LifeCycleState state = part.getState();
			Source source = part.getSource();
			PartType partType = part.getPartType(); // 装配模式
			boolean endItem = part.isEndItem(); // 成品
			QuantityUnit defaultUnit = part.getDefaultUnit();// 单位
			boolean hidePartInStructure = part.getHidePartInStructure();// 收集部件
			TraceCode defaultTraceCode = part.getDefaultTraceCode(); // 默认追踪代码
			String location = FolderHelper.getLocation((CabinetBased) part);
			WTContainer container2 = WTContainerHelper.getContainer((WTContained) part);
			WTContainerRef containerRef = WTContainerRef.newWTContainerRef(container2);

			String newNumber = number.substring(0, number.length() - 1);
			String suffix = number.substring(number.length() - 1);

			if ("0".equals(suffix)) {
				newNumber += "1";
			} else if ("1".equals(suffix)) {
				newNumber += "0";
			}

			newPart = WTPart.newWTPart();
			newPart.setName(partName);
			newPart.setNumber(newNumber);
			newPart.setState(state);
			newPart.setSource(source);
			newPart.setPartType(partType);
			newPart.setEndItem(endItem);
			newPart.setHidePartInStructure(hidePartInStructure);
			newPart.setDefaultUnit(defaultUnit);
			newPart.setDefaultTraceCode(defaultTraceCode);
			newPart.setView(part.getView());
			FolderHelper.assignLocation((FolderEntry) newPart, location, containerRef);
			wt.fc.PersistenceHelper.manager.save(newPart);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return newPart;
	}

	/**
	 * 校验部件是否正在其他工作流中运行（直接抛出异常？太奇怪了）
	 * 
	 * @param List<WTPart>
	 * @throws WTException
	 */
	@SuppressWarnings("deprecation")
	public static void checkOtherWorkflow(List<WTPart> list) throws WTException {
		for (WTPart wtPart : list) {
			WTCollection localList = new WTHashSet();
			localList.add(wtPart);
			WTCollection promotionNotices = MaturityHelper.getService().getPromotionNotices(localList);
			for (Object object : promotionNotices) {
				if (object instanceof ObjectReference) {
					ObjectReference pn = (ObjectReference) object;
					Persistable object2 = pn.getObject();
					if (object2 instanceof PromotionNotice) {
						PromotionNotice promotion = (PromotionNotice) object2;
						QueryResult localQueryResult = WorkflowCommands.getRoutingHistory(new NmOid(promotion));
						while (localQueryResult.hasMoreElements()) {
							Object nextElement = localQueryResult.nextElement();
							if (nextElement instanceof WfProcess) {
								WfProcess process = (WfProcess) nextElement;
								WfState state = process.getState();
								if (WfState.OPEN_RUNNING.equals(state)) {// 正在运行
									String number = promotion.getNumber();
									String name = process.getName();
									String partNumber = wtPart.getNumber();
									String partName = wtPart.getName();
									throw new WTException("物料编码为" + partNumber + "的物料【" + partName + "】正在运行另一个升级请求："
											+ number + "," + name + "。请勿重复提交升级请求流程。");
								}
							}
						}
					}
				}
			}
		}
	}

	/**
	 * 修改部件的单位
	 * 
	 * @param WTPart
	 * @param String
	 * @throws WTException
	 */
	public static void replaceUnit(WTPart part, String unit) throws WTException {

		if (part == null || StringUtils.isBlank(unit)) {
			return;
		}
		QuantityUnit defaultUnit = part.getDefaultUnit();
		QuantityUnit quantityUnit = QuantityUnit.toQuantityUnit(unit);

		if (defaultUnit.equals(quantityUnit)) {
			return;
		}

		try {
			Identified identified = (Identified) part.getMaster();
			WTPartMaster master = (WTPartMaster) part.getMaster();
			master.setDefaultUnit(quantityUnit);
			WTPartMasterIdentity partIdentity = (WTPartMasterIdentity) identified.getIdentificationObject();
			identified = wt.fc.IdentityHelper.service.changeIdentity(master, partIdentity);
		} catch (WTException e) {
			e.printStackTrace();
		} catch (WTPropertyVetoException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 根据url获取对应的wtpart对象
	 * http://plmtest.sinoboom.com.cn/Windchill/app/#ptc1/tcomp/infoPage?u8=1&oid=OR%3Awt.part.WTPart%3A804374507
	 * http://plmtest.sinoboom.com.cn/Windchill/app/#ptc1/tcomp/infoPage?oid=OR:wt.part.WTPart:1099247509
	 * 
	 * @param String
	 * @return WTPart
	 */
	public static WTPart getPartFromURL(String url) {
		try {
			Pattern pattern = Pattern.compile("(?<=WTPart)(%3A|:)([0-9]+)");
			String oid = "";
			Matcher matcher = pattern.matcher(url);
			if (matcher.find()) {
				oid = matcher.group(2);
			}
			WTPart part = (WTPart) PersistenceUtil.oid2Object(oid);
			return part;
		} catch (Exception e) {
			System.out.println("系统未找到url指定的部件");
			return null;
		}
	}

	/**
	 * 根据给定的视图和编号获取指定视图的部件
	 * 
	 * @param String number
	 * @param String param Design/Manufacturing/
	 * @return WTPart
	 * @throws QueryException
	 * @throws WTException
	 */
	public static WTPart getWTPartByNumberAndView(String number, String param) {
		View view;
		try {
			view = ViewHelper.service.getView(param);
			WTPartConfigSpec designConfigSpec = WTPartConfigSpec
					.newWTPartConfigSpec(WTPartStandardConfigSpec.newWTPartStandardConfigSpec(view, (State) null));
			WTPart viewPart = WTPartUtilities.getWTPart(number, designConfigSpec);
			if (viewPart != null && StringUtils.equalsIgnoreCase(param, viewPart.getViewName())) {
				return viewPart;
			}
		} catch (ViewException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 根据部件的VR查找到部件的OR
	 * 
	 * @param VR VR:wt.part.WTPart:569305
	 * @return OR OR:wt.part.WTPart:569306
	 */
	public static String getORbyVR(String VR) {
		VR = VR.split(":")[2];
		String sql = "SELECT IDA2A2 FROM WTPART WHERE BRANCHIDITERATIONINFO = ?";
		String OR = "";
		try {
			ResultSet resultSet = CommonUtil.excuteSelect(sql, VR);
			while (resultSet.next()) {
				OR = "OR:wt.part.WTPart:" + resultSet.getString("IDA2A2");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return OR;
	}

	/**
	 * 通过递归找到部件的BOM最上层部件
	 * 
	 * @param part
	 * @return List<WTPart>
	 */
	public static List<WTPart> getTopParentParts(WTPart part) {
		List<WTPart> list = new ArrayList<WTPart>();
		try {
			if (part != null) {
				QueryResult qr = WTPartHelper.service.getUsedByWTParts(part.getMaster());

				if (!qr.hasMoreElements()) {
					list.add(part);
				} else {
					while (qr.hasMoreElements()) {
						WTPart childPart = (WTPart) qr.nextElement();
						list.addAll(getTopParentParts(childPart));
					}
				}
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		return list;
	}

}
