package ext.ait.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import ext.HHT.SRM.insert.SQLData;
import wt.admin.AdminDomainRef;
import wt.content.ApplicationData;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.doc.DepartmentList;
import wt.doc.DocumentType;
import wt.doc.WTDocument;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.folder.Folder;
import wt.folder.FolderEntry;
import wt.folder.FolderHelper;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerRef;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.org.WTOrganization;
import wt.part.WTPart;
import wt.pom.Transaction;
import wt.util.WTException;
import wt.util.WTInvalidParameterException;
import wt.util.WTPropertyVetoException;
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
public class RemoteTest12 implements RemoteAccess, Serializable {

	private static final long serialVersionUID = 1L;

	public static void main(String[] args) throws WTRuntimeException, WTException {
//		ReferenceFactory rf = new ReferenceFactory();
//		WTPart part = (WTPart) rf.getReference("OR:wt.part.WTPart:746398").getObject();
//		EPMDocument epm = (EPMDocument) rf.getReference("OR:wt.epm.EPMDocument:633628").getObject();
//		WTDocument doc = (WTDocument) rf.getReference("OR:wt.doc.WTDocument:747942").getObject();
//		WorkItem wi = (WorkItem) rf.getReference("OR:wt.workflow.work.WorkItem:652084").getObject();

		String flag = (String) invoke("insertData", RemoteTest12.class.getName(), null, new Class[] {},
				new Object[] {});
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

	private static HashMap<String, String> docTypeMap = new HashMap<>() {
		{
			put("承认书", "HHT_AcknowledgmentDoc");
			put("测试报告", "HHT_TestReport");
			put("ROHS证书", "HHT_ROHSReport");
		}
	};

	public static void insertData() {
		ArrayList<SQLData> dataList = getData();
		for (SQLData sqlData : dataList) {
			WTPart part = PartUtil.getWTPartByNumber(sqlData.getPartNumber());
			WTContainer container = part.getContainer();
			AdminDomainRef domainRef = part.getDomainRef();
			WTOrganization org = part.getOrganization();
			Vector content = part.getContentVector();
			if (container.getIdentity().contains("PDMLinkProduct")) {
				Folder folder = ContainerUtil.getFolder("08-采购文档", container);
				String filePath = "/opt/acknowledgment/" + sqlData.getFilePath().split("/")[2];
				try {
					DocumentType docType = DocumentType.toDocumentType(docTypeMap.get(sqlData.getDocType()));
					WTDocument doc = createAndUpload(sqlData.getName(), sqlData.getDocNumber(),
							container.getContainerReference(), folder, filePath, docType, domainRef, org, content);
					doc.setDepartment(DepartmentList.toDepartmentList("HHTSystemDesign"));
					DocumentUtil.createDocPartLink(part, doc, "Describe");
				} catch (WTInvalidParameterException e) {
					e.printStackTrace();
				} catch (WTPropertyVetoException e) {
					e.printStackTrace();
				}
			}
		}
	}

	// 230620710A 140310104B 0000001240 放到与部件同产品目录下的08-采购文档
	// 承认书HHT_AcknowledgmentDoc|HHT_TestReport|HHT_ROHSReport
	public static WTDocument createAndUpload(String name, String number, WTContainerRef product, Folder folder,
			String filePath, DocumentType docType, AdminDomainRef domainRef, WTOrganization org, Vector content) {
		Transaction tx = new Transaction();
		try {
			// 创建WTDocument对象
			WTDocument doc = WTDocument.newWTDocument();
			doc.setContentVector(content);
			doc.setOrganization(org);
			doc.setName(name);
			doc.setNumber("OUT" + addLead0(number, 8));
			doc.setDocType(docType);
			doc.setContainerReference(product);
			doc.setDomainRef(domainRef);
			FolderHelper.assignLocation((FolderEntry) doc, (Folder) folder);
			// WTDoc needs to be stored before content may be added
			doc = (WTDocument) PersistenceHelper.manager.store(doc);

			// 存储文件到文档中去
			ApplicationData theContent = ApplicationData.newApplicationData(doc);
			File file = new File(filePath);
			theContent.setFileName(file.getName());
			theContent.setRole(ContentRoleType.toContentRoleType("PRIMARY"));
			theContent.setFileSize(file.length());
			FileInputStream fis = new FileInputStream(file);

			tx.start();
			theContent = ContentServerHelper.service.updateContent(doc, theContent, fis);
			ContentServerHelper.service.updateHolderFormat(doc);
			tx.commit();

			doc = (WTDocument) PersistenceHelper.manager.refresh((Persistable) doc, true, true);
			fis.close();
			return doc;
		} catch (Exception e) {
			e.printStackTrace();
			tx.rollback();
		}
		return null;
	}

	public static ArrayList<SQLData> getData() {
		String sql = "SELECT * FROM ACKNOWLEDGMENTINPUT";
		String sql2 = "SELECT  HHT_ACKNOWLEDGMENT.NEXTVAL FROM DUAL";
		ArrayList<SQLData> list = new ArrayList<>();
		try {

			ResultSet resultSet = CommonUtil.excuteSelect(sql);
			while (resultSet.next()) {
				SQLData data = new SQLData();
				data.setName(resultSet.getString("name"));
				data.setDocType(resultSet.getString("docType"));
				data.setPartNumber(resultSet.getString("partNumber"));
				data.setSupplier(resultSet.getString("supplier"));
				data.setDepartment(resultSet.getString("department"));
				data.setVersion(resultSet.getString("version"));
				data.setFilePath(resultSet.getString("filePath"));
				ResultSet resultSet2 = CommonUtil.excuteSelect(sql2);
				while (resultSet2.next()) {
					data.setDocNumber(resultSet2.getString("NEXTVAL"));
				}
				System.out.println(data);
				list.add(data);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public static String addLead0(String attr, int length) {
		if (attr.length() < length) {
			StringBuilder resultSB = new StringBuilder(attr);
			while (resultSB.length() < length) {
				resultSB.insert(0, '0'); // 在前面添加零
			}
			return resultSB.toString();
		}
		return attr;
	}

}
