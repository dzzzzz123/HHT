package ext.ait.util;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;

import wt.doc.WTDocument;
import wt.doc.WTDocumentMaster;
import wt.epm.EPMDocument;
import wt.fc.PersistenceHelper;
import wt.fc.ReferenceFactory;
import wt.folder.Folder;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.part.WTPart;
import wt.part.WTPartDescribeLink;
import wt.part.WTPartReferenceLink;
import wt.pom.PersistenceException;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.util.WTRuntimeException;
import wt.vc.wip.CheckoutLink;
import wt.vc.wip.WorkInProgressException;
import wt.vc.wip.WorkInProgressHelper;
import wt.vc.wip.Workable;
import wt.workflow.work.WorkItem;

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
		WTPart part = (WTPart) rf.getReference("OR:wt.part.WTPart:730302").getObject();
		EPMDocument epm = (EPMDocument) rf.getReference("OR:wt.epm.EPMDocument:633628").getObject();
		WTDocument doc = (WTDocument) rf.getReference("OR:wt.doc.WTDocument:566836").getObject();
		WorkItem wi = (WorkItem) rf.getReference("OR:wt.workflow.work.WorkItem:652084").getObject();
		String url = "http://uat.honghe-tech.com/Windchill/netmarkets/jsp/ext/requirement/1.html";
		String context = "Containers('OR:wt.pdmlink.PDMLinkProduct:125641')";
		String folder = "Folders('OR:wt.folder.SubFolder:134478')";
		String classString = "12021";
		String containerName = "H10-外购成品库";

		boolean flag = (boolean) invoke("createDocPartLink", RemoteTest15.class.getName(), null,
				new Class[] { WTPart.class, WTDocument.class, String.class }, new Object[] { part, doc, "Describe" });
		System.out.println("flag:" + flag);
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

	public static boolean createDocPartLink(WTPart part, WTDocument doc, String actionType) {
		try {
			switch (actionType) {
			case "Describe":
				// 创建说明方文档
				part = (WTPart) checkoutObj(part);
				WTPartDescribeLink describeLink = WTPartDescribeLink.newWTPartDescribeLink(part, doc);
				PersistenceHelper.manager.save(describeLink);
				checkinObj(part);
				break;
			case "Reference":
				// 创建参考文档
				part = (WTPart) checkoutObj(part);
				WTPartReferenceLink ref_link = WTPartReferenceLink.newWTPartReferenceLink(part,
						(WTDocumentMaster) doc.getMaster());
				PersistenceHelper.manager.save(ref_link);
				checkinObj(part);
				break;
			default:
				break;
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		return true;
	}

	public static Workable checkoutObj(Workable workable) {
		if (workable == null) {
			return null;
		}
		if (WorkInProgressHelper.isWorkingCopy(workable)) {
			return workable;
		}
		try {
			Folder folder = WorkInProgressHelper.service.getCheckoutFolder();
			CheckoutLink checkoutLink = WorkInProgressHelper.service.checkout(workable, folder, "AutoCheckOut");
			workable = checkoutLink.getWorkingCopy();
			if (!WorkInProgressHelper.isWorkingCopy(workable)) {
				workable = WorkInProgressHelper.service.workingCopyOf(workable);
			}
		} catch (WTException e) {
			e.printStackTrace();
		} catch (WTPropertyVetoException e) {
			e.printStackTrace();
		}
		return workable;
	}

	/**
	 * 检入对象
	 * 
	 * @param workable
	 * @return Workable
	 */
	public static Workable checkinObj(Workable workable) {
		try {
			if (workable == null) {
				return null;
			}
			workable = (WTPart) WorkInProgressHelper.service.checkin(workable, null);
		} catch (WorkInProgressException e) {
			e.printStackTrace();
		} catch (WTPropertyVetoException e) {
			e.printStackTrace();
		} catch (PersistenceException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}
		return workable;
	}
}
