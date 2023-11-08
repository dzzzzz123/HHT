package ext.ait.util;

import java.sql.ResultSet;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import wt.dataops.containermove.ContainerMoveHelper;
import wt.enterprise.RevisionControlled;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.collections.WTCollection;
import wt.fc.collections.WTValuedHashMap;
import wt.fc.collections.WTValuedMap;
import wt.folder.Folder;
import wt.folder.FolderHelper;
import wt.folder.FolderingInfo;
import wt.folder.SubFolder;
import wt.folder.SubFolderReference;
import wt.inf.container.OrgContainer;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerRef;
import wt.method.RemoteAccess;
import wt.org.WTPrincipal;
import wt.part.WTPart;
import wt.query.QueryException;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionHelper;
import wt.util.WTException;

public class ContainerUtil implements RemoteAccess {
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
	 * 得到指定文件夹的对象，如果没有则创建该文件夹
	 * 
	 * @param strFolder
	 * @param wtContainer
	 * @return
	 * @throws WTException
	 */
	public static Folder getFolder(String strFolder, WTContainer wtContainer) {
		try {
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
		} catch (WTException e1) {
			e1.printStackTrace();
		}
		return null;
	}

	/**
	 * 根据容器的名称获取容器对象
	 * 
	 * @param containerName
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	public static WTContainer getContainer(String containerName) {
		try {
			QuerySpec qs = new QuerySpec(WTContainer.class);
			SearchCondition sc = new SearchCondition(WTContainer.class, WTContainer.NAME, "=", containerName);
			qs.appendWhere(sc);
			QueryResult qr = PersistenceHelper.manager.find(qs);
			while (qr.hasMoreElements()) {
				WTContainer container = (WTContainer) qr.nextElement();
				return container;
			}
		} catch (QueryException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
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
	 * 获取container下所有文件夹名称
	 * 
	 * @param containerName container名称
	 * @return container下所有文件夹名称
	 */
	public static Set<String> getContainerFolders(String containerName) {
		Set<String> foldersSet = new HashSet<>();
		WTContainer container = getContainer(containerName);
		String oid = String.valueOf(container.getPersistInfo().getObjectIdentifier().getId());
		String sql = "SELECT NAME FROM SUBFOLDER WHERE IDA3CONTAINERREFERENCE = ?";
		try {
			ResultSet resultSet = CommonUtil.excuteSelect(sql, oid);
			while (resultSet.next()) {
				foldersSet.add(resultSet.getString("NAME"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return foldersSet;
	}

	/**
	 * 将某个持久化对象移动到指定的文件夹
	 * 
	 * @param obj    持久化对象
	 * @param folder 文件夹对象
	 */
	public static void moveObj2Folder(Persistable obj, Folder folder) {
		try {
			WTValuedMap objFolderMap = new WTValuedHashMap(1);
			objFolderMap.put(obj, folder);
			WTCollection col = ContainerMoveHelper.service.moveAllVersions(objFolderMap);
		} catch (WTException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 将某个持久化对象移动到指定库下的文件夹中
	 * 
	 * @param part       持久化对象
	 * @param library    库名称
	 * @param folderName 文件夹名称
	 */
	public static void moveObj2FolderWithContainer(WTPart part, String library, String folderName) {
		WTContainer container = ContainerUtil.getContainer(library);
		Folder toFolder = ContainerUtil.getFolder(folderName, container);
		ContainerUtil.moveObj2Folder(part, toFolder);
	}

}
