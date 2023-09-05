package ext.ait.util;

import java.util.Enumeration;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;

import com.ptc.core.lwc.server.LWCLocalizablePropertyValue;
import com.ptc.core.lwc.server.LWCTypeDefinition;
import com.ptc.core.meta.common.IdentifierFactory;
import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.core.meta.type.mgmt.server.impl.WTTypeDefinition;
import com.ptc.core.meta.type.mgmt.server.impl.WTTypeDefinitionMaster;

import wt.enterprise.RevisionControlled;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.folder.Folder;
import wt.folder.FolderHelper;
import wt.folder.FolderingInfo;
import wt.folder.SubFolder;
import wt.folder.SubFolderReference;
import wt.inf.container.OrgContainer;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerRef;
import wt.log4j.LogR;
import wt.org.OrganizationServicesHelper;
import wt.org.WTGroup;
import wt.org.WTPrincipal;
import wt.org.WTUser;
import wt.pds.StatementSpec;
import wt.query.QueryException;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.ServiceProviderHelper;
import wt.session.SessionHelper;
import wt.type.TypedUtilityServiceHelper;
import wt.util.WTException;

public class CommonUtil {

	private static Logger LOGGER = LogR.getLogger(CommonUtil.class.getName());

	/**
	 * 转换中文格式，避免中文乱码
	 * 
	 * @param value
	 * @return
	 * @throws WTException
	 */
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
	 * @param obj
	 * @return
	 */
	public static String getPath(RevisionControlled obj) {
		StringBuffer path = new StringBuffer();
		SubFolderReference ref = obj.getParentFolder();
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

	/**
	 * 用来递归获取文件夹完整路径的方法
	 * 
	 * @param path
	 * @param subFolder
	 */
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

	/**
	 * 根据用户id获取WTUser对象
	 * 
	 * @param id
	 * @return
	 * @throws WTException
	 */
	@SuppressWarnings("deprecation")
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
			throw new WTException(e);
		}
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
		WTTypeDefinition typeDef = getTypeDefinitionByName(name);
		if (typeDef.isLatestIteration()) {
			id = typeDef.getPersistInfo().getObjectIdentifier().getId();
		}
		LOGGER.debug("###[" + id + "] isInheritedDomain --->" + typeDef.isInheritedDomain() + " ;;;isUserAttributeable "
				+ typeDef.isUserAttributeable() + ";;;; isLatestIteration " + typeDef.isLatestIteration());
		return id;
	}

	/**
	 * 通过高级查询获取文档类型的对象
	 * 
	 * @param name
	 * @return
	 * @throws WTException
	 */
	@SuppressWarnings("deprecation")
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
	 * 通过类型的Key获取国际化的名称
	 * 
	 * @param key
	 * @return
	 * @throws WTException
	 */
	@SuppressWarnings("deprecation")
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

	/**
	 * 得到指定文件夹的对象，如果没有则创建该文件夹（尚不明晰，看上去并不那么好用）
	 * 
	 * @param strFolder
	 * @param wtContainer
	 * @return
	 * @throws WTException
	 */
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

	/**
	 * 根据对象类型和类型获取对象（暂不清楚）
	 * 
	 * @param queryClass
	 * @param type
	 * @return
	 * @throws Exception
	 */
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

	/**
	 * 根据容器的名称获取容器对象
	 * 
	 * @param containerName
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
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
	 * 根据组织名称获取组织对象
	 * 
	 * @param orgName
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
	 * 根据用户名称/用户全名获取用户对象
	 * 
	 * @param String
	 * @param boolean
	 * @return WTUser
	 */
	@SuppressWarnings("deprecation")
	public static WTUser getUserByName(String name, boolean IsFull) {
		String parm = IsFull ? WTUser.FULL_NAME : WTUser.NAME;
		if (StringUtils.isBlank(name)) {
			return null;
		}
		try {
			Enumeration enumUser = OrganizationServicesHelper.manager.findUser(parm, name.trim());
			while (enumUser.hasMoreElements()) {
				return (WTUser) enumUser.nextElement();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 根据组名获取组对象
	 * 
	 * @param String
	 * @return WTGroup
	 */
	public static WTGroup queryGroupByName(String groupName) {
		try {
			ChangeSession.administratorSession();
			if (StringUtils.isBlank(groupName)) {
				return null;
			}
			QuerySpec qs = new QuerySpec(WTGroup.class);
			SearchCondition sc = new SearchCondition(WTGroup.class, WTGroup.NAME, SearchCondition.EQUAL,
					groupName.trim());
			int[] index = { 0 };
			qs.appendWhere(sc, index);
			QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
			while (qr.hasMoreElements()) {
				WTGroup group = (WTGroup) qr.nextElement();
				return group;
			}
		} catch (WTException e) {
			e.printStackTrace();
		} finally {
			ChangeSession.goPreviousSession();
		}
		return null;
	}

}
