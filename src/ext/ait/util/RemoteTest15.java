package ext.ait.util;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.sql.ResultSet;
import java.util.HashSet;
import java.util.Set;

import wt.dataops.containermove.ContainerMoveHelper;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.fc.collections.WTCollection;
import wt.fc.collections.WTValuedHashMap;
import wt.fc.collections.WTValuedMap;
import wt.folder.Folder;
import wt.folder.FolderHelper;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerRef;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.org.WTPrincipal;
import wt.part.WTPart;
import wt.query.QueryException;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.util.WTRuntimeException;

/**
 * 由于Windchill系统重启过慢，所以这个类是用来进行远程方法调用的类 这个方法所调用
 * 这里在main方法中调用反射方法，然后调用windchill的RemoteMethodServer来执行反射中的方法
 * 
 * 使用前提：被调用的方法必须实现 wt.method.RemoteAccess, java.io.Serializable这两个接口
 * 
 * 如何调用其他方法： 这里调用了同级目录下VersionUtil类中的方法getVersion 这个方法是用来获取部件的版本
 * 对于入参这里使用ReferenceFactory来获取系统中真实存在的部件对象
 * 
 * invoke方法是用来调用远程方法的真正执行方法， 这里有5个参数 分别为
 * 
 * MethodName 被调用方法的方法名, className 被调用方法的类名称 instance 执行方法的对象这里为null cla
 * 传入反射方法变量的类（这里的类型必须是所调用方法对应的类型，不能是任何子类已实现接口等） obj 传入反射方法变量的对象
 * 
 * @author dz
 *
 */
public class RemoteTest15 implements RemoteAccess, Serializable {

	private static final long serialVersionUID = 1L;

	public static void main(String[] args) throws WTRuntimeException, WTException {
		ReferenceFactory rf = new ReferenceFactory();
		WTPart part = (WTPart) rf.getReference("OR:wt.part.WTPart:619554").getObject();
		String url = "http://uat.honghe-tech.com/Windchill/netmarkets/jsp/ext/requirement/1.html";
		String context = "Containers('OR:wt.pdmlink.PDMLinkProduct:125641')";
		String folder = "Folders('OR:wt.folder.SubFolder:134478')";
		String classString = "12021";
		String containerName = "H10-外购成品库";
		String set = (String) invoke("mainn", RemoteTest15.class.getName(), null,
				new Class[] { WTPart.class, String.class }, new Object[] { part, containerName });
	}

	public static Object invoke(String methodName, String className, Object instance, Class[] cla, Object[] obj) {
		RemoteMethodServer rms = RemoteMethodServer.getDefault();
		rms.setUserName("wcadmin");
		rms.setPassword("wcadmin");
		try {
			return rms.invoke(methodName, className, instance, cla, obj);
		} catch (RemoteException e) {
			e.printStackTrace();
			return null;
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void mainn(WTPart part, String containerName) {
		WTContainer container = getContainer(containerName);
		Folder folder = getFolder("60129_样机", container);
		System.out.println("container:" + container.getName());
		System.out.println("folder:" + folder.getName());
		moveObj2Folder(part, folder);
	}

	public static Set<String> getContainerFolders(String containerName) {
		Set<String> foldersSet = new HashSet<>();
		WTContainer container = ContainerUtil.getContainer(containerName);
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

	public static void moveObj2Folder(Persistable obj, Folder folder) {
		try {
			WTValuedMap objFolderMap = new WTValuedHashMap(1);
			objFolderMap.put(obj, folder);
			WTCollection col = ContainerMoveHelper.service.moveAllVersions(objFolderMap);
		} catch (WTException e) {
			e.printStackTrace();
		}
	}

}
