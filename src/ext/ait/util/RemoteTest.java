package ext.ait.util;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;

import wt.enterprise.RevisionControlled;
import wt.fc.ReferenceFactory;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.part.WTPart;
import wt.util.WTException;
import wt.util.WTRuntimeException;

/**
 * 由于Windchill系统重启过慢，所以这个类是用来进行远程方法调用的类 这个方法所调用
 * 这里在main方法中调用反射方法，然后调用windchill的RemoteMethodServer来执行反射中的方法
 * 
 * 使用前提：被调用的方法必须实现 wt.method.RemoteAccess, java.io.Serializable这两个接口
 * 
 * 如何调用其他方法：
 * 这里调用了同级目录下VersionUtil类中的方法getVersion 这个方法是用来获取部件的版本
 * 对于入参这里使用ReferenceFactory来获取系统中真实存在的部件对象
 * 
 * invoke方法是用来调用远程方法的真正执行方法， 这里有5个参数 分别为
 * 
 * MethodName 被调用方法的方法名,
 * className 被调用方法的类名称
 * instance 执行方法的对象这里为null
 * cla 传入反射方法变量的类（这里的类型必须是所调用方法对应的类型，不能是任何子类已实现接口等）
 * obj 传入反射方法变量的对象
 * 
 * @author dz
 *
 */
public class RemoteTest implements RemoteAccess, Serializable {

	private static final long serialVersionUID = 1L;

	public static void main(String[] args) throws WTRuntimeException, WTException {
		ReferenceFactory rf = new ReferenceFactory();
		WTPart part = (WTPart) rf.getReference("OR:wt.part.WTPart:435620").getObject();
		String version = (String) invoke("getVersion", VersionUtil.class.getName(), null,
				new Class[] { RevisionControlled.class }, new Object[] { part });
		System.out.println("version：" + version);
	}

	//	public static void main(String[] args) throws WTRuntimeException, WTException {
	//		ReferenceFactory rf = new ReferenceFactory();
	//		WTPart part = (WTPart) rf.getReference("OR:wt.part.WTPart:435620").getObject();
	//		ArrayList<String> list = new ArrayList<String>() {
	//			{
	//				add("ProductName");
	//				add("ModelPrefix");
	//				add("ProductModel");
	//				add("Touc_Hscreen_Type");
	//				add("Manufacturing");
	//			}
	//		};
	//		list.forEach(str -> {
	//			String displayName = (String) invoke("getEnumDisplay", Util.class.getName(), null,
	//					new Class[] { WTPart.class, String.class }, new Object[] { part, str });
	//			System.out.println("displayName：" + displayName);
	//		});
	//	}

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

	public static String getVersion(RevisionControlled revisionControlled) {
		return revisionControlled.getVersionInfo().getIdentifier().getValue() + "."
				+ revisionControlled.getIterationInfo().getIdentifier().getValue();
	}
}
