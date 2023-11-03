package ext.ait.util;

import java.beans.PropertyVetoException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.ptc.windchill.enterprise.part.commands.AssociationLinkObject;
import com.ptc.windchill.enterprise.part.commands.PartDocServiceCommand;

import wt.change2.ChangeException2;
import wt.change2.ChangeHelper2;
import wt.change2.WTChangeActivity2;
import wt.change2.WTChangeOrder2;
import wt.change2.WTChangeReview;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.content.ContentItem;
import wt.content.FormatContentHolder;
import wt.epm.EPMDocument;
import wt.epm.EPMDocumentType;
import wt.fc.Identified;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.fc.WTObject;
import wt.maturity.MaturityHelper;
import wt.maturity.PromotionNotice;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.org.WTPrincipal;
import wt.part.WTPart;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.util.WTRuntimeException;
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
public class RemoteTest27 implements RemoteAccess, Serializable {

	private static final long serialVersionUID = 1L;

	public static void main(String[] args) throws WTRuntimeException, WTException {
		ReferenceFactory rf = new ReferenceFactory();
		WTPart part = (WTPart) rf.getReference("OR:wt.part.WTPart:634413").getObject();
		EPMDocument epm = (EPMDocument) rf.getReference("OR:wt.epm.EPMDocument:633628").getObject();
		WorkItem wi = (WorkItem) rf.getReference("OR:wt.workflow.work.WorkItem:652084").getObject();
		String url = "http://uat.honghe-tech.com/Windchill/netmarkets/jsp/ext/requirement/1.html";
		String context = "Containers('OR:wt.pdmlink.PDMLinkProduct:125641')";
		String folder = "Folders('OR:wt.folder.SubFolder:134478')";
		String classString = "12021";
		String containerName = "H10-外购成品库";

		ArrayList<WTPart> list = (ArrayList<WTPart>) invoke("getPartNumberByWI", RemoteTest27.class.getName(), null,
				new Class[] { WorkItem.class }, new Object[] { wi });
		list.forEach(System.out::println);

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

	public static ArrayList<WTPart> getPartNumberByWI(WorkItem workItem) {
		ArrayList<WTPart> result = new ArrayList<>();
		try {
			WTObject pbo = WorkflowUtil.getPBOByWorkItem(workItem);
			result = getTargerObject(pbo, "AffectedObjects", WTPart.class);
		} catch (WTRuntimeException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static <T> ArrayList<T> getTargerObject(WTObject primaryBusinessObject, String type, Class<T> clazz) {
		ArrayList<T> list = new ArrayList<T>();

		if (!type.equals("AffectedObjects") && !type.equals("ResultingObjects")) {
			System.out.println("获取审阅/变更目标对象失败，只能指定为受影响对象或产生的对象");
			return list;
		}

		try {
			if (primaryBusinessObject instanceof WTChangeReview) {
				WTChangeReview review = (WTChangeReview) primaryBusinessObject;
				QueryResult qr = ChangeHelper2.service.getChangeables(review);
				list = getListFromQR(qr, clazz);
			} else if (primaryBusinessObject instanceof WTChangeOrder2) {

				if (type.equals("AffectedObjects")) {
					WTChangeOrder2 eco = (WTChangeOrder2) primaryBusinessObject;
					QueryResult qr = ChangeHelper2.service.getChangeablesBefore(eco);
					list = getListFromQR(qr, clazz);
				} else if (type.equals("ResultingObjects")) {
					WTChangeOrder2 eco = (WTChangeOrder2) primaryBusinessObject;
					QueryResult qr = ChangeHelper2.service.getChangeablesAfter(eco);
					list = getListFromQR(qr, clazz);
				}

			} else if (primaryBusinessObject instanceof WTChangeActivity2) {

				if (type.equals("AffectedObjects")) {
					WTChangeActivity2 eca = (WTChangeActivity2) primaryBusinessObject;
					QueryResult qr = ChangeHelper2.service.getChangeablesBefore(eca);
					list = getListFromQR(qr, clazz);
				} else if (type.equals("ResultingObjects")) {
					WTChangeActivity2 eca = (WTChangeActivity2) primaryBusinessObject;
					QueryResult qr = ChangeHelper2.service.getChangeablesAfter(eca);
					list = getListFromQR(qr, clazz);
				}

			} else if (primaryBusinessObject instanceof PromotionNotice) {
				PromotionNotice pn = (PromotionNotice) primaryBusinessObject;
				QueryResult qr = MaturityHelper.service.getPromotionTargets(pn);
				list = getListFromQR(qr, clazz);
			}
		} catch (ChangeException2 e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}
		return list;
	}

	public static <T> ArrayList<T> getListFromQR(QueryResult qr, Class<T> clazz) {
		ArrayList<T> list = new ArrayList<T>();
		while (qr.hasMoreElements()) {
			Object obj = qr.nextElement();
			if (clazz.isInstance(obj)) {
				T castedObj = clazz.cast(obj);
				list.add(castedObj);
			}
		}
		return list;
	}

	private static EPMDocumentType CADCOMPONENT = EPMDocumentType.toEPMDocumentType("CADCOMPONENT"); //

	private static EPMDocumentType CADASSEMBLY = EPMDocumentType.toEPMDocumentType("CADASSEMBLY"); //

	private static EPMDocumentType CADDRAWING = EPMDocumentType.toEPMDocumentType("CADDRAWING"); //

	public static void renameCADFromWTPart(WTPart part) throws Exception {
		List<EPMDocument> list = getEPMDocumentByPart(part);

		String new3DNumber = getSerial3DNumber(part);
		String new2DNumber = new3DNumber + "_D";
		String new3DName = part.getName();
		String new2DName = part.getName();
		String new3DFileName = new3DNumber + ".prt";
		String new2DFileName = new3DNumber + ".drw";

		for (EPMDocument epm : list) {
			EPMDocumentType epmType = epm.getDocType();
			System.out.println("epmType:" + epmType);
			if ((CADCOMPONENT.equals(epmType) || CADASSEMBLY.equals(epmType))) {
				reEPMNameNumber(epm, new3DNumber, new3DName, new3DFileName);
			} else if (CADDRAWING.equals(epmType)) {
				reEPMNameNumber(epm, new2DNumber, new2DName, new2DFileName);
			}
		}
	}

	private static String getSerial3DNumber(WTPart part) {
		String serialNumber = "";
		String classification = ClassificationUtil.getClassificationInternal(part, "HHT_Classification");
		try {
			String sql = "SELECT HHT_CADSEQ.NEXTVAL FROM DUAL";
			ResultSet resultSet = CommonUtil.excuteSelect(sql);
			while (resultSet.next()) {
				serialNumber = resultSet.getString("NEXTVAL");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (serialNumber.length() < 6) {
			StringBuilder serialNumberSB = new StringBuilder(serialNumber);
			while (serialNumberSB.length() < 6) {
				serialNumberSB.insert(0, '0'); // 在前面添加零
			}
			serialNumber = serialNumberSB.toString();
		}

		return classification + serialNumber;
	}

	private static void reEPMNameNumber(EPMDocument epm, String newNumber, String newName, String newFileName) {

		try {
			ContentHolder contentHolder = ContentHelper.service.getContents((ContentHolder) epm);
			ContentItem contentitem = ContentHelper.getPrimary((FormatContentHolder) contentHolder);
			ApplicationData applicationdata = (ApplicationData) contentitem;
			applicationdata.setFileName(newFileName);
			PersistenceServerHelper.manager.update(applicationdata);// 更新文件名
			Identified identified = (Identified) epm.getMaster();
			String masterId = String.valueOf(identified.getPersistInfo().getObjectIdentifier().getId());
			String sql = "UPDATE EPMDOCUMENTMASTER SET CADNAME= ? , NAME = ?, DOCUMENTNUMBER = ? WHERE IDA2A2 = ?";
			CommonUtil.excuteUpdate(sql, newFileName, newName, newNumber, masterId);
		} catch (WTException e) {
			e.printStackTrace();
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}

	}

	public static List<EPMDocument> getEPMDocumentByPart(WTPart part) throws Exception {
		System.out.println("001-rename cad : get all 2d/3d from part " + part.getNumber());
		List<EPMDocument> docs = new ArrayList<EPMDocument>();
		WTPrincipal currentUser = null;
		try {
			currentUser = SessionHelper.manager.getPrincipal();
			SessionHelper.manager.setAdministrator();
			Collection cadDocumentsAndLinks = PartDocServiceCommand.getAssociatedCADDocumentsAndLinks(part);
			for (Object object : cadDocumentsAndLinks) {
				if (object instanceof AssociationLinkObject) {
					AssociationLinkObject alo = (AssociationLinkObject) object;
					EPMDocument epm = alo.getCadObject();
					String version = epm.getVersionIdentifier().getValue();
					if (StringUtils.equalsIgnoreCase(version, "A")) {
						System.out.println("001-rename cad : EPMDocument " + epm.getNumber());
						docs.add(epm);
					}
				}
			}

		} catch (WTException e) {
			e.printStackTrace();
			throw new Exception("No 3D CAD Document on Part :" + part.getName());
		} finally {
			if (currentUser != null) {
				SessionHelper.manager.setPrincipal(currentUser.getName());
			}
		}
		return docs;
	}

}
