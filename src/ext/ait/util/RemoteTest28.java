package ext.ait.util;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;

import wt.fc.ReferenceFactory;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.util.WTException;
import wt.util.WTRuntimeException;
import wt.workflow.engine.WfProcess;
import wt.workflow.work.WfAssignedActivity;
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
public class RemoteTest28 implements RemoteAccess, Serializable {

	private static final long serialVersionUID = 1L;

	public static void main(String[] args) throws WTRuntimeException, WTException {
		ReferenceFactory rf = new ReferenceFactory();
//		WTDocument doc = (WTDocument) rf.getReference("OR:wt.doc.WTDocument:1796110").getObject();
//		WTPart part = (WTPart) rf.getReference("OR:wt.part.WTPart:1939296").getObject();
//		EPMDocument epm = (EPMDocument) rf.getReference("OR:wt.epm.EPMDocument:1833683").getObject();
//		WTChangeRequest2 ecr = (WTChangeRequest2) rf.getReference("OR:wt.change2.WTChangeRequest2:2113629").getObject();
		WorkItem wi = (WorkItem) rf.getReference("OR:wt.workflow.work.WorkItem:2538761").getObject();
//		PlanActivity planActivity = (PlanActivity) rf
//				.getReference("OR:com.ptc.projectmanagement.plan.PlanActivity:849396").getObject();
		String flag = (String) invoke("process", RemoteTest28.class.getName(), null, new Class[] { WorkItem.class },
				new Object[] { wi });
		System.out.println("flag: " + flag);
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

	public static String process(WorkItem wi) {
		try {
			WfAssignedActivity wfAssignedActivity = (WfAssignedActivity) wi.getSource().getObject();
			WfProcess wfProcess = wfAssignedActivity.getParentProcess();
			String value = (String) wfProcess.getContext().getValue("erpMsg");
			return value;
		} catch (Exception e) {
			return null;
		}
	}
}
