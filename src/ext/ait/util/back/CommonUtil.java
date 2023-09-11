package ext.ait.util.back;

import java.beans.PropertyVetoException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.eclipse.ui.internal.activities.Persistence;

import com.ptc.core.lwc.server.LWCLocalizablePropertyValue;
import com.ptc.core.lwc.server.LWCTypeDefinition;
import com.ptc.core.meta.common.IdentifierFactory;
import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.core.meta.type.mgmt.common.TypeDefinitionDefaultView;
import com.ptc.core.meta.type.mgmt.server.impl.WTTypeDefinition;
import com.ptc.core.meta.type.mgmt.server.impl.WTTypeDefinitionMaster;
import com.ptc.windchill.enterprise.part.commands.PartDocServiceCommand;

import wt.change2.ChangeHelper2;
import wt.change2.WTChangeActivity2;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.content.ContentRoleType;
import wt.content.URLData;
import wt.doc.WTDocument;
import wt.doc.WTDocumentDependencyLink;
import wt.doc.WTDocumentMaster;
import wt.doc.WTDocumentMasterIdentity;
import wt.enterprise.RevisionControlled;
import wt.epm.EPMDocument;
import wt.epm.EPMDocumentMaster;
import wt.epm.EPMDocumentMasterIdentity;
import wt.epm.EPMDocumentType;
import wt.epm.structure.EPMDescribeLink;
import wt.epm.util.EPMSoftTypeServerUtilities;
import wt.fc.IdentityHelper;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.fc.WTObject;
import wt.folder.Folder;
import wt.folder.FolderHelper;
import wt.folder.FolderingInfo;
import wt.folder.SubFolder;
import wt.folder.SubFolderReference;
import wt.inf.container.OrgContainer;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerRef;
import wt.log4j.LogR;
import wt.org.WTPrincipal;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.part.WTPartDescribeLink;
import wt.part.WTPartHelper;
import wt.part.WTPartMaster;
import wt.part.WTPartMasterIdentity;
import wt.pds.StatementSpec;
import wt.query.QueryException;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.ServiceProviderHelper;
import wt.session.SessionHelper;
import wt.type.TypeDefinitionReference;
import wt.type.Typed;
import wt.type.TypedUtilityServiceHelper;
import wt.util.WTException;
import wt.vc.Iterated;
import wt.vc.Mastered;
import wt.vc.VersionControlHelper;
import wt.vc.VersionIdentifier;
import wt.vc.Versioned;
import wt.vc.config.ConfigHelper;
import wt.vc.config.ConfigSpec;
import wt.vc.config.LatestConfigSpec;
import wt.vc.wip.WorkInProgressHelper;
import wt.vc.wip.Workable;

public class CommonUtil {
	/**
	 * 转换中文格式，避免中文乱码
	 * 
	 * @param value
	 * @return
	 * @throws WTException
	 */
	private static ReferenceFactory factory = new ReferenceFactory();

	private static Logger LOGGER = LogR.getLogger(CommonUtil.class.getName());

	private static EPMDocumentType CADDRAWING = EPMDocumentType.toEPMDocumentType("CADDRAWING"); //

	public static String formatString(String value) throws WTException {
		try {
			if (value != null && value.trim().length() > 0) {
				byte[] tembyte = value.getBytes("gb2312");
				return new String(tembyte);
			} else {
				return value;
			}
		} catch (Exception e) {
			throw new WTException(e);
		}
	}

	/**
	 * 获取对象的文件夹路径
	 * 
	 * @param doc
	 * @return
	 */
	public static String getPath(RevisionControlled doc) {
		StringBuffer path = new StringBuffer();
		SubFolderReference ref = doc.getParentFolder();
		if (ref != null && ref.getObject() instanceof SubFolder) {
			SubFolder subFolder = (SubFolder) ref.getObject();
			getPath(path, subFolder);
		} else {
			path = new StringBuffer("/Default");
		}
		return path.toString();
	}

	/**
	 * 获取对象存储位置
	 * 
	 * @param fInfo
	 * @return
	 */
	public static String getFolderStr(FolderingInfo fInfo) {
		StringBuffer path = new StringBuffer();
		SubFolderReference ref = fInfo.getParentFolder();
		if (ref != null && ref.getObject() instanceof SubFolder) {
			SubFolder subFolder = (SubFolder) ref.getObject();
			getPath(path, subFolder);
		} else {
			path = new StringBuffer("/Default");
		}
		return path.toString();
	}

	private static void getPath(StringBuffer path, SubFolder subFolder) {
		path.insert(0, subFolder.getName()).insert(0, "/");
		SubFolderReference ref = subFolder.getParentFolder();
		if (ref != null && ref.getObject() instanceof SubFolder) {
			SubFolder sub = (SubFolder) ref.getObject();
			getPath(path, sub);
		} else {
			path.insert(0, "/Default");
		}
	}

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
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 修改文档、物料、图纸的编码
	 * 
	 * @param mast
	 * @param number
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
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 获取文档的主要内容
	 * 
	 * @author wide at 2016-6-16
	 * @param wtdocument
	 * @return
	 * @throws WTException
	 * @throws PropertyVetoException
	 */
	public static ArrayList<ApplicationData> getSecondaryContent(ContentHolder holder) {
		ContentHolder contentHolder = null;
		ArrayList<ApplicationData> dataList = new ArrayList<ApplicationData>();
		try {
			contentHolder = ContentHelper.service.getContents(holder);
			QueryResult qr = ContentHelper.service.getContentsByRole(contentHolder, ContentRoleType.SECONDARY);
			while (qr.hasMoreElements()) {
				Object obj = qr.nextElement();
				if (obj instanceof URLData) {

				}
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return dataList;
	}

	public static Map<String, String> toMap(String url) {
		Map<String, String> map = new HashMap<String, String>();
		if (url != null && url.indexOf("?") > -1 && url.indexOf("=") > -1) {
			url = url.substring(url.indexOf("?") + 1, url.length());
			String[] arrTemp = url.split("&");
			for (String str : arrTemp) {
				String[] qs = str.split("=");
				map.put(qs[0], qs[1]);
			}
		}
		return map;
	}

	/**
	 * 判断字符串是否在list当中
	 * 
	 * @param list
	 * @param str
	 * @return
	 */
	public static boolean hasInList(ArrayList<String> list, String str) {
		boolean result = false;
		if (str == null || str.trim().length() == 0) {
			return result;
		}
		for (String s : list) {
			if (s != null && s.equals(str)) {
				result = true;
				break;
			}
		}
		return result;
	}

	public static BigDecimal doubleToBigDecimal(double d) {
		String doubleStr = String.valueOf(d);
		if (doubleStr.indexOf(".") != -1) {
			int pointLen = doubleStr.replaceAll("\\d+\\.", "").length(); // 取得小数点后的数字的位数
			pointLen = pointLen > 16 ? 16 : pointLen; // double最大有效小数点后的位数为16
			double pow = Math.pow(10, pointLen);
			long tmp = (long) (d * pow);
			return new BigDecimal(tmp).divide(new BigDecimal(pow));
		}
		return new BigDecimal(d);
	}

	public static BigDecimal stringToBigDecimal(String doubleStr) {
		double d = Double.parseDouble(doubleStr);
		if (doubleStr.indexOf(".") != -1) {
			int pointLen = doubleStr.replaceAll("\\d+\\.", "").length(); // 取得小数点后的数字的位数
			pointLen = pointLen > 16 ? 16 : pointLen; // double最大有效小数点后的位数为16
			double pow = Math.pow(10, pointLen);
			long tmp = (long) (d * pow);
			return new BigDecimal(tmp).divide(new BigDecimal(pow));
		}
		return new BigDecimal(d);
	}

	/**
	 * 获取对象的版本，如A.1
	 * 
	 * @param revisionControlled
	 * @return
	 */
	public static String getVersion(RevisionControlled revisionControlled) {
		return revisionControlled.getVersionInfo().getIdentifier().getValue() + "."
				+ revisionControlled.getIterationInfo().getIdentifier().getValue();
	}

	public static String object2Oid(WTObject obj) {
		return "OR:" + obj.getClass().getName() + ":" + obj.getPersistInfo().getObjectIdentifier().getId();
	}

	public static WTObject oid2Object(String oid) throws WTException {
		return (WTObject) factory.getReference(oid).getObject();

	}

	/**
	 * 只适用于版序为大写字母的版本 当当前版本为A是，返回其本身
	 * 
	 * @param version
	 * @return
	 */
	public static String getLastVersion(String version) {
		int i = Integer.parseInt(version, 36);
		if (i > 10) {
			i--;
			Long l = (long) i;
			String next = Long.toString(l, 36).toUpperCase();
			return next;
		} else {
			return version;
		}

	}

	/**
	 * 根据当前日期返回不同形式的字符串形式。
	 * 
	 * @param date Date对象。
	 * @return 如果与当前时间所在年月日相同，则返回"HH:mm:ss"形式，否则返回"yyyy-MM-dd HH:mm:ss"。
	 */
	public static String getCurrentDay2String() {
		Date d = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
		// 时:分:秒:毫秒
		return sdf.format(d);
	}

	/**
	 * 根据当前日期返回不同形式的字符串形式。
	 * 
	 * @param date Date对象。
	 * @return 如果与当前时间所在年月日相同，则返回"HH:mm:ss"形式，否则返回"yyyy-MM-dd HH:mm:ss"。
	 */
	public static String getTime2String(Timestamp ts) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
		// 时:分:秒:毫秒
		return sdf.format(ts);
	}

	/**
	 * 根据当前日期返回不同形式的字符串形式。
	 * 
	 * @param date Date对象。
	 * @return 如果与当前时间所在年月日相同，则返回"HH:mm:ss"形式，否则返回"yyyy-MM-dd HH:mm:ss"。
	 */
	public static String getTime2String(Timestamp ts, String formatStr) {
		SimpleDateFormat sdf = new SimpleDateFormat(formatStr);
		sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
		// 时:分:秒:毫秒
		return sdf.format(ts);
	}

	/**
	 * 获取当前日期
	 * 
	 * @return "yyyy-MM-dd"
	 */
	public static String getNowTime() {
		/* 获取当前时间 */
		DateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
		format1.setTimeZone(TimeZone.getTimeZone("GMT+8"));
		return format1.format(new Date());

	}

	public static String getDateKey() {
		/* 获取当前时间 */
		DateFormat format1 = new SimpleDateFormat("yyMMdd");
		format1.setTimeZone(TimeZone.getTimeZone("GMT+8"));
		return format1.format(new Date());
	}

	public static String getDateString() {
		/* 获取当前时间 */
		DateFormat format1 = new SimpleDateFormat("yyyyMMdd");
		format1.setTimeZone(TimeZone.getTimeZone("GMT+8"));
		return format1.format(new Date());
	}

	public static String getTimeString() {
		/* 获取当前时间 */
		DateFormat format1 = new SimpleDateFormat("yyyyMMddHHmmss");
		format1.setTimeZone(TimeZone.getTimeZone("GMT+8"));
		return format1.format(new Date());
	}

	/**
	 * 根据当前日期返回不同形式的数字。
	 * 
	 * @param date Date对象。
	 * @return 如果与当前时间所在年月日相同，则返回"HH:mm:ss"形式，否则返回"yyyy-MM-dd HH:mm:ss"。
	 */
	public static Integer getTime2Int(Timestamp ts) {
		int d = ts.getSeconds() + ts.getMinutes() * 60 * +ts.getHours() * 60 * 60 + ts.getDate() * 60 * 60 * 24
				+ ts.getMonth() * 60 * 60 * 24 * 30 + ts.getYear() * 60 * 60 * 24 * 30 * 12;
		return d;
	}

	/**
	 * 对比是否为最新版本
	 * 
	 * @param interated
	 * @return
	 * @throws WTException
	 */
	public static boolean isLatestIterated(Iterated interated) throws WTException {

		Iterated localIterated = null;
		boolean bool = false;
		Mastered m = null;
		LatestConfigSpec localLatestConfigSpec = new LatestConfigSpec();

		QueryResult localQueryResult = ConfigHelper.service.filteredIterationsOf(interated.getMaster(),
				localLatestConfigSpec);
		if ((localQueryResult != null) && (localQueryResult.size() <= 0)) {
			ConfigSpec localConfigSpec = ConfigHelper.service.getDefaultConfigSpecFor(WTPartMaster.class);
			localQueryResult = ConfigHelper.service.filteredIterationsOf(interated.getMaster(), localConfigSpec);
		}

		while ((localQueryResult.hasMoreElements()) && (!bool)) {
			localIterated = (Iterated) localQueryResult.nextElement();
			bool = localIterated.isLatestIteration();
		}
		LOGGER.debug("    the latest iteration=" + localIterated.getIdentity());
		if (localIterated.equals(interated)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 获取最新小版本
	 * 
	 * @param interated
	 * @return
	 * @throws WTException
	 */
	public static Iterated getLatestInterated(Mastered master) throws WTException {

		Iterated localIterated = null;
		LatestConfigSpec localLatestConfigSpec = new LatestConfigSpec();

		QueryResult localQueryResult = ConfigHelper.service.filteredIterationsOf(master, localLatestConfigSpec);
		if ((localQueryResult != null) && (localQueryResult.size() <= 0)) {
			ConfigSpec localConfigSpec = ConfigHelper.service.getDefaultConfigSpecFor(WTPartMaster.class);
			localQueryResult = ConfigHelper.service.filteredIterationsOf(master, localConfigSpec);
		}

		while ((localQueryResult.hasMoreElements())) {
			Iterated localIterated1 = (Iterated) localQueryResult.nextElement();
			if (localIterated1.isLatestIteration()) {
				localIterated = localIterated1;
			}
		}
		// LOGGER.debug(" the latest iteration=" + localIterated.getIdentity());
		return localIterated;
	}

	/**
	 * 获取上一个大版本的最新小版本，如果没有上一个大版本，则返回当前版本的最新小版本
	 * 
	 * @param part
	 * @return
	 */
	public static RevisionControlled getLastBigOne(RevisionControlled revisionControlled) {
		RevisionControlled last = null;
		try {
			last = (RevisionControlled) VersionControlHelper.service.predecessorOf(revisionControlled);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return last;
	}

	/**
	 * 获取上一个大版本的最新小版本，如果没有上一个大版本，则返回当前版本的最新小版本
	 * 
	 * @param part
	 * @return
	 */
	public static RevisionControlled getLasterBigOne(RevisionControlled revisionControlled) {
		RevisionControlled last = null;
		try {
			if ("A".equals(revisionControlled.getVersionInfo().getIdentifier().getValue())) {
				return (RevisionControlled) getLatestInterated(revisionControlled.getMaster());
			}
			last = (RevisionControlled) VersionControlHelper.service.predecessorOf(revisionControlled);
			if (!last.getVersionInfo().getIdentifier().getValue()
					.equals(revisionControlled.getVersionInfo().getIdentifier().getValue())) {
				return last;
			} else {
				last = getLasterBigOne(last);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return last;
	}

	/**
	 * 根据用户id获取WTUser对象
	 * 
	 * @throws WTException
	 */

	public static WTUser getUserById(String id) throws WTException {
		WTUser user = null;
		try {
			if (id != null && id.trim().length() > 0) {
				QuerySpec qs = new QuerySpec(WTUser.class);
				SearchCondition sc1 = new SearchCondition(WTUser.class, WTUser.NAME, SearchCondition.EQUAL, id);
				SearchCondition sc2 = new SearchCondition(WTUser.class, WTUser.FULL_NAME, SearchCondition.EQUAL, id);
				qs.appendWhere(sc1);
				qs.appendOr();
				qs.appendWhere(sc2);
				LOGGER.debug("searchUsers sql where --->" + qs.getWhere());
				QueryResult qr = new QueryResult();
				qr = PersistenceHelper.manager.find(qs);
				while (qr.hasMoreElements()) {
					user = (WTUser) qr.nextElement();
				}
			}
			return user;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new WTException(e);
		}
	}

	public static boolean isLetter(char c) {
		boolean isLetter = false;
		int asc = (int) c;
		if (64 < asc && c < 91) {
			isLetter = true;
		}
		return isLetter;
	}

	/**
	 * 通过高级查询获取文档类型的ID
	 * 
	 * @param name
	 * @return
	 * @throws WTException
	 */
	public static long getTypeDefinitionIdByName(String name) throws WTException {
		long id = 0;
		QuerySpec qs = new QuerySpec();
		int typeDefine = qs.appendClassList(WTTypeDefinition.class, true);
		int typeDefineMaster = qs.appendClassList(WTTypeDefinitionMaster.class, false);
		qs.setAdvancedQueryEnabled(true);
		SearchCondition typebyMaster = new SearchCondition(WTTypeDefinition.class, "masterReference.key.id",
				WTTypeDefinitionMaster.class, "thePersistInfo.theObjectIdentifier.id");
		qs.appendWhere(typebyMaster, new int[] { typeDefine, typeDefineMaster });
		qs.appendAnd();
		SearchCondition typeMasterName = new SearchCondition(WTTypeDefinitionMaster.class, "displayNameKey", "=", name);
		qs.appendWhere(typeMasterName, typeDefineMaster);
		QueryResult qr = PersistenceHelper.manager.find(qs);
		while (qr.hasMoreElements()) {
			Object[] objs = (Object[]) qr.nextElement();
			LOGGER.debug("###getTypeDefinitionIdByName### class name --->" + objs[0].getClass().getName());
			if (objs[0] instanceof WTTypeDefinition) {
				WTTypeDefinition typeDef = (WTTypeDefinition) objs[0];
				if (typeDef.isLatestIteration()) {
					id = typeDef.getPersistInfo().getObjectIdentifier().getId();
				}
				LOGGER.debug("###[" + typeDef.getPersistInfo().getObjectIdentifier().getId()
						+ "] isInheritedDomain --->" + typeDef.isInheritedDomain() + " ;;;isUserAttributeable "
						+ typeDef.isUserAttributeable() + ";;;; isLatestIteration " + typeDef.isLatestIteration());
			}

		}
		LOGGER.debug("############### getTypeDefinitionIdByName ##### sql --->" + qs.getWhere());
		return id;
	}

	/**
	 * 通过高级查询获取文档类型的ID
	 * 
	 * @param name
	 * @return
	 * @throws WTException
	 */
	public static WTTypeDefinition getTypeDefinitionByName(String name) throws WTException {
		WTTypeDefinition type = null;
		QuerySpec qs = new QuerySpec();
		int typeDefine = qs.appendClassList(WTTypeDefinition.class, true);
		int typeDefineMaster = qs.appendClassList(WTTypeDefinitionMaster.class, false);
		qs.setAdvancedQueryEnabled(true);
		SearchCondition typebyMaster = new SearchCondition(WTTypeDefinition.class, "masterReference.key.id",
				WTTypeDefinitionMaster.class, "thePersistInfo.theObjectIdentifier.id");
		qs.appendWhere(typebyMaster, new int[] { typeDefine, typeDefineMaster });
		qs.appendAnd();
		SearchCondition typeMasterName = new SearchCondition(WTTypeDefinitionMaster.class, "displayNameKey", "=", name);
		qs.appendWhere(typeMasterName, typeDefineMaster);
		QueryResult qr = PersistenceHelper.manager.find(qs);
		while (qr.hasMoreElements()) {
			Object[] objs = (Object[]) qr.nextElement();
			if (objs[0] instanceof WTTypeDefinition) {
				WTTypeDefinition typeDef = (WTTypeDefinition) objs[0];
				if (typeDef.isLatestIteration()) {
					type = typeDef;
				}

			}

		}
		return type;
	}

	/**
	 * 获取文档所描述的部件
	 * 
	 * @param doc
	 * @return
	 * @throws WTException
	 */
	public static ArrayList<WTPart> getDescriptionParts(WTDocument doc) throws WTException {
		ArrayList<WTPart> partlist = new ArrayList<WTPart>();
		try {
			ArrayList<String> numberlist = new ArrayList<String>();
			QuerySpec qs = new QuerySpec(WTPartDescribeLink.class);
			qs.appendWhere(new SearchCondition(WTPartDescribeLink.class, "roleBObjectRef.key.id", SearchCondition.EQUAL,
					doc.getPersistInfo().getObjectIdentifier().getId()));
			QueryResult qr = PersistenceHelper.manager.find(qs);
			while (qr.hasMoreElements()) {
				WTPartDescribeLink link = (WTPartDescribeLink) qr.nextElement();
				WTPart part = link.getDescribes();
				if (numberlist.contains(part.getNumber())) {
					continue;
				} else {
					LOGGER.debug("文档[" + doc.getNumber() + "]描述的部件为--->" + part.getNumber());
					partlist.add(part);
					numberlist.add(part.getNumber());
				}

			}
		} catch (Exception e) {
			throw new WTException(e);
		}

		return partlist;
	}

	/**
	 * 获取变更的部件
	 * 
	 * @param eca
	 * @return
	 * @throws WTException
	 */
	public static ArrayList<WTPart> getChangeParts(WTChangeActivity2 eca) throws WTException {
		ArrayList<WTPart> parts = new ArrayList<WTPart>();
		try {
			QueryResult aqr = ChangeHelper2.service.getChangeablesAfter(eca);
			while (aqr.hasMoreElements()) {
				Object obj = aqr.nextElement();
				if (obj instanceof WTPart) {
					WTPart part = (WTPart) obj;
					parts.add(part);
				}
			}
			return parts;
		} catch (Exception e) {
			throw new WTException(e);
		}
	}

	/**
	 * 获取参考文档
	 * 
	 * @param doc
	 * @return
	 * @throws WTException
	 */
	public static ArrayList<WTDocument> getDepDoc(WTDocument doc) throws WTException {
		ArrayList<WTDocument> doclist = new ArrayList<WTDocument>();
		try {
			QuerySpec qs = new QuerySpec(WTDocumentDependencyLink.class);
			qs.appendWhere(new SearchCondition(WTDocumentDependencyLink.class, "roleAObjectRef.key.id",
					SearchCondition.EQUAL, doc.getPersistInfo().getObjectIdentifier().getId()));
			QueryResult qr = PersistenceHelper.manager.find(qs);
			while (qr.hasMoreElements()) {
				WTDocumentDependencyLink link = (WTDocumentDependencyLink) qr.nextElement();
				WTDocument depDoc = (WTDocument) link.getRoleBObject();
				LOGGER.debug("文档[" + doc.getNumber() + "]参考文档为--->" + depDoc.getNumber());
				doclist.add(depDoc);
			}
		} catch (Exception e) {
			throw new WTException(e);
		}

		return doclist;
	}

	/**
	 * 获取被参考文档
	 * 
	 * @param doc
	 * @return
	 * @throws WTException
	 */
	public static ArrayList<WTDocument> getDepByDoc(WTDocument doc) throws WTException {
		ArrayList<WTDocument> doclist = new ArrayList<WTDocument>();
		try {
			QuerySpec qs = new QuerySpec(WTDocumentDependencyLink.class);
			qs.appendWhere(new SearchCondition(WTDocumentDependencyLink.class, "rolBeObjectRef.key.id",
					SearchCondition.EQUAL, doc.getPersistInfo().getObjectIdentifier().getId()));
			QueryResult qr = PersistenceHelper.manager.find(qs);
			while (qr.hasMoreElements()) {
				WTDocumentDependencyLink link = (WTDocumentDependencyLink) qr.nextElement();
				WTDocument depByDoc = (WTDocument) link.getRoleAObject();
				LOGGER.debug("文档[" + doc.getNumber() + "]被参考文档为--->" + depByDoc.getNumber());
				doclist.add(depByDoc);
			}
		} catch (Exception e) {
			throw new WTException(e);
		}

		return doclist;
	}

	/**
	 * @description 得到对象的自定义类型的内部名称
	 * @param obj
	 * @return String
	 * @throws WTException
	 */
	public static String getTypeName(Typed obj) {
		String typeDisplayName = null;
		try {
			WTTypeDefinition definition = null;
			Typed type = (Typed) obj;
			TypeDefinitionReference ref = type.getTypeDefinitionReference();
			TypeDefinitionDefaultView view = EPMSoftTypeServerUtilities.getTypeDefinition(ref);
			definition = (WTTypeDefinition) PersistenceHelper.manager.refresh(view.getObjectID());
			typeDisplayName = definition.getName(); // 类型的显示名称
//			System.out.println(typeDisplayName+"----------");
		} catch (WTException e) {
			e.printStackTrace();
		}
		return typeDisplayName;
	}

	/**
	 * 通过类型的Key获取国际化的名称
	 * 
	 * @param key
	 * @return
	 * @throws WTException
	 */
	public static String getTypeDisplayName(String key) throws WTException {
		String typeDisplayName = "";
		try {
			LWCTypeDefinition lwcType = null;
			QuerySpec lwcSpec = new QuerySpec(LWCTypeDefinition.class);
			lwcSpec.appendSearchCondition(
					new SearchCondition(LWCTypeDefinition.class, LWCTypeDefinition.NAME, SearchCondition.EQUAL, key));
			QueryResult qr = PersistenceHelper.manager.find(lwcSpec);
			while (qr.hasMoreElements()) {
				lwcType = (LWCTypeDefinition) qr.nextElement();
			}
			if (lwcType != null) {
				/**
				 * LWCLocalizablePropertyValue记录所有的国际化字段
				 */
				QuerySpec valueSpec = new QuerySpec(LWCLocalizablePropertyValue.class);
				valueSpec.appendSearchCondition(
						new SearchCondition(LWCLocalizablePropertyValue.class, "contextReference.key.id",
								SearchCondition.EQUAL, lwcType.getPersistInfo().getObjectIdentifier().getId()));
				valueSpec.appendAnd();
				valueSpec.appendSearchCondition(
						new SearchCondition(LWCLocalizablePropertyValue.class, "holderReference.key.id",
								SearchCondition.EQUAL, lwcType.getPersistInfo().getObjectIdentifier().getId()));
				QueryResult vqr = PersistenceHelper.manager.find(valueSpec);
				while (vqr.hasMoreElements()) {
					LWCLocalizablePropertyValue value = (LWCLocalizablePropertyValue) vqr.nextElement();
					typeDisplayName = value.getValue(Locale.CHINA);
					if (typeDisplayName == null || typeDisplayName.trim().length() == 0) {
						typeDisplayName = value.getValue();
					}
				}
			}
			return typeDisplayName;
		} catch (Exception e) {
			throw new WTException(e);
		}
	}

	public static boolean isCheckout(Workable wa) {
		boolean checkOut = false;
		try {
			checkOut = WorkInProgressHelper.isCheckedOut(wa);
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return checkOut;
	}

	public static boolean isnew(RevisionControlled revisionControlled) {
		boolean isnew = false;
		String bigVersion = revisionControlled.getVersionInfo().getIdentifier().getValue();
		if (bigVersion != null && bigVersion.trim().length() > 0 && bigVersion.equals("A")) {
			isnew = true;
		}
		return isnew;
	}

	/**
	 * 给map数据进行排序
	 * 
	 * @param map      : 需排序的数据集
	 * @param indexKey ： map中需要排序的字段名，如果为空，则已大的Map的Key进行排序
	 * @return
	 */
	public static ArrayList<String> sortPlaceNumber(HashMap<String, HashMap<String, String>> map, String indexKey) {
		LOGGER.debug("传入需要排序的Map数据有[" + map.size() + "]个");
		ArrayList<String> keyList = new ArrayList<String>();
		TreeMap<String, String> tm = new TreeMap<String, String>();
		if (indexKey != null && indexKey.trim().length() > 0) {
			Iterator<String> keys = map.keySet().iterator();
			while (keys.hasNext()) {
				String key = keys.next();
				HashMap<String, String> childMap = map.get(key);
				String indexValue = childMap.get(indexKey);
				if (indexValue == null || indexValue.trim().length() == 0) {
					indexValue = "0";
				}
				tm.put(indexValue, indexValue);
			}
			Iterator<String> its = tm.keySet().iterator();
			while (its.hasNext()) {
				String key = its.next();
				Iterator<String> sKeys = map.keySet().iterator();
				while (sKeys.hasNext()) {
					String sKey = sKeys.next();
					HashMap<String, String> childMap = map.get(sKey);
					String indexValue = childMap.get(indexKey);
					if (indexValue == null || indexValue.trim().length() == 0) {
						indexValue = "0";
					}
					if (indexValue.equals(key)) {
						keyList.add(sKey);
					}
				}
			}
		} else {
			Iterator<String> keys = map.keySet().iterator();
			while (keys.hasNext()) {
				String key = keys.next();
				tm.put(key, key);
			}
			Iterator<String> its = tm.keySet().iterator();
			while (its.hasNext()) {
				String key = its.next();
				keyList.add(key);

			}
		}
		LOGGER.debug("排序后的返回的Map数据有[" + keyList.size() + "]个");
		return keyList;
	}

	/**
	 * Object转成指定的类型
	 * 
	 * @param obj
	 * @param type
	 * @param <T>
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T convert(Object obj, Class<T> type) {
		if (obj != null) {
			if (type.equals(Integer.class) || type.equals(int.class)) {
				return (T) new Integer(obj.toString());
			} else if (type.equals(Long.class) || type.equals(long.class)) {
				return (T) new Long(obj.toString());
			} else if (type.equals(Boolean.class) || type.equals(boolean.class)) {
				return (T) new Boolean(obj.toString());
			} else if (type.equals(Short.class) || type.equals(short.class)) {
				return (T) new Short(obj.toString());
			} else if (type.equals(Float.class) || type.equals(float.class)) {
				return (T) new Float(obj.toString());
			} else if (type.equals(Double.class) || type.equals(double.class)) {
				return (T) new Double(obj.toString());
			} else if (type.equals(Byte.class) || type.equals(byte.class)) {
				return (T) new Byte(obj.toString());
			} else if (type.equals(Character.class) || type.equals(char.class)) {
				return (T) new Character(obj.toString().charAt(0));
			} else if (type.equals(String.class)) {
				return (T) obj;
			} else if (type.equals(BigDecimal.class)) {
				return (T) new BigDecimal(obj.toString());
			} else if (type.equals(LocalDateTime.class)) {
				// DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd
				// HH:mm:ss");
				return (T) LocalDateTime.parse(obj.toString());
			} else if (type.equals(Date.class)) {
				try {
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
					return (T) formatter.parse(obj.toString());
				} catch (ParseException e) {
					throw new RuntimeException(e.getMessage());
				}
			} else {
				return null;
			}
		} else {
			if (type.equals(int.class)) {
				return (T) new Integer(0);
			} else if (type.equals(long.class)) {
				return (T) new Long(0L);
			} else if (type.equals(boolean.class)) {
				return (T) new Boolean(false);
			} else if (type.equals(short.class)) {
				return (T) new Short("0");
			} else if (type.equals(float.class)) {
				return (T) new Float(0.0);
			} else if (type.equals(double.class)) {
				return (T) new Double(0.0);
			} else if (type.equals(byte.class)) {
				return (T) new Byte("0");
			} else if (type.equals(char.class)) {
				return (T) new Character('\u0000');
			} else {
				return null;
			}
		}
	}

	public static Folder getFolder(String strFolder, WTContainer wtContainer) throws WTException {
		WTPrincipal curUser = SessionHelper.manager.getPrincipal();
		SessionHelper.manager.setAdministrator();
		Folder folder = null;
		String subPath = "Default/" + strFolder;
		WTContainerRef ref = WTContainerRef.newWTContainerRef(wtContainer);
		try {
			folder = FolderHelper.service.getFolder(subPath, ref);
		} catch (WTException e) {
			folder = FolderHelper.service.createSubFolder(subPath, ref);
		} finally {
			SessionHelper.manager.setPrincipal(curUser.getName());
		}
		return folder;
	}

	public static QueryResult findObjectByType(Class queryClass, String type) throws Exception {
		IdentifierFactory identifier_factory = (IdentifierFactory) ServiceProviderHelper
				.getService(IdentifierFactory.class, "logical");
		TypeIdentifier tid = (TypeIdentifier) identifier_factory.get(type);
		QuerySpec qs = new QuerySpec(queryClass);
		int idx = qs.addClassList(queryClass, true);
		SearchCondition sc = TypedUtilityServiceHelper.service.getSearchCondition(tid, true);
		qs.appendWhere(sc, new int[] { idx });
		System.out.println(queryClass);
		QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
		return qr;
	}

	public static WTContainer getContainer(String containerName) throws Exception {
		QuerySpec qs = new QuerySpec(WTContainer.class);
		SearchCondition sc = new SearchCondition(WTContainer.class, WTContainer.NAME, "=", containerName);
		qs.appendWhere(sc);
		QueryResult qr = PersistenceHelper.manager.find(qs);
		while (qr.hasMoreElements()) {
			WTContainer container = (WTContainer) qr.nextElement();
			return container;
		}
		return null;
	}

	/**
	 * 获取最新版本的文档
	 */
	public static WTDocument getDoc(String number) {
		try {
			if (StringUtils.isBlank(number)) {
				return null;
			}
			QuerySpec qs = new QuerySpec(WTDocument.class);
			qs.appendWhere(
					new SearchCondition(WTDocument.class, WTDocument.NUMBER, SearchCondition.EQUAL, number.trim()));
			QueryResult qr = PersistenceHelper.manager.find(qs);
			qr = new LatestConfigSpec().process(qr);
			if (qr.hasMoreElements()) {
				WTDocument doc = (WTDocument) qr.nextElement();
				return doc;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

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

	public static Object getObjByOR(Class queryClass, String or) {
		try {
			if (StringUtils.isBlank(or)) {
				return null;
			}
			QuerySpec qs = new QuerySpec(queryClass);
			qs.appendWhere(new SearchCondition(queryClass, "thePersistInfo.theObjectIdentifier.id",
					SearchCondition.EQUAL, Long.valueOf(or)));
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
	 * 获取最新版本的文档
	 */
	public static WTPart getPartByNumber(String number) {
		try {
			if (StringUtils.isBlank(number)) {
				return null;
			}
			QuerySpec qs = new QuerySpec(WTPart.class);
			qs.appendWhere(new SearchCondition(WTPart.class, WTPart.NUMBER, SearchCondition.EQUAL, number.trim()));
			QueryResult qr = PersistenceHelper.manager.find(qs);
			qr = new LatestConfigSpec().process(qr);
			if (qr.hasMoreElements()) {
				WTPart part = (WTPart) qr.nextElement();
				return part;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getObjType(Persistence obj) {

		return "";

	}

	/**
	 * 获取当前组织
	 * 
	 * @return
	 */
	public static OrgContainer getOrgContainer(String orgName) {
		try {
			QuerySpec queryspec = new QuerySpec(OrgContainer.class);
			QueryResult qr = PersistenceHelper.manager.find(queryspec);
			while (qr.hasMoreElements()) {
				OrgContainer org = (OrgContainer) qr.nextElement();
				if (StringUtils.equalsIgnoreCase(orgName, org.getName())) {
					return org;
				}
			}
		} catch (QueryException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}
		return null;
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
	 * 根据3D获取2D drawing
	 *
	 * @param cad
	 * @throws Exception
	 */
	public static List<Persistable> get2DDrawingByWTPart(WTPart part, EPMDocumentType eType) throws Exception {
		List<Persistable> result = new ArrayList<Persistable>();
		QueryResult qr = null;
		EPMDocument doc = null;
		try {
			qr = PartDocServiceCommand.getAssociatedCADDocuments(part);// CAD文档
			while (qr.hasMoreElements()) {
				Object obj = qr.nextElement();
				if (obj instanceof EPMDocument) {
					doc = (EPMDocument) obj;
					EPMDocumentType docType = doc.getDocType();
//					EPMAuthoringAppType appType = doc.getAuthoringApplication();
					// && doc.getNumber().contains(part.getNumber())
					if (CADDRAWING.equals(docType)) {
						System.out.println("005-batch download pdf : get2DDrawingByWTPart " + doc.getNumber());
						result.add(doc);
					}
				}
			}

		} catch (WTException e) {
			e.printStackTrace();
			throw e;
		}
		return result;
	}

	/**
	 * 由于可能不存在cad文档的情况 所以根据wtpart获取说明方文档
	 *
	 * @param wtpart
	 * @throws Exception
	 */
	public static List<Persistable> getDescriptedByWTPart(WTPart part) throws Exception {
		List<Persistable> result = new ArrayList<Persistable>();
		QueryResult qr = null;
		WTDocument doc = null;
		try {
			qr = WTPartHelper.service.getDescribedByDocuments(part);// 说明方文档
			while (qr.hasMoreElements()) {
				Object obj = qr.nextElement();
				if (obj instanceof WTDocument) {
					doc = (WTDocument) obj;
					result.add(doc);
				}
			}
		} catch (WTException e) {
			e.printStackTrace();
			throw e;
		}
		return result;
	}

	public static ArrayList<EPMDescribeLink> getRelatedObjectFromEPMDescribeLink(Long number) {
		ArrayList<EPMDescribeLink> linkList = new ArrayList<>();
		try {
			QuerySpec qs = new QuerySpec(EPMDescribeLink.class);
			qs.appendWhere(
					new SearchCondition(EPMDescribeLink.class, "roleBObjectRef.key.id", SearchCondition.EQUAL, number));
			QueryResult qr = PersistenceHelper.manager.find(qs);
			if (qr.hasMoreElements()) {
				EPMDescribeLink epmDescribeLink = (EPMDescribeLink) qr.nextElement();
				linkList.add(epmDescribeLink);
				return linkList;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
