package ext.HHT.SRM.insert;

import java.io.File;
import java.io.FileInputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import ext.ait.util.CommonUtil;
import ext.ait.util.ContainerUtil;
import ext.ait.util.DocumentUtil;
import ext.ait.util.PartUtil;
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
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.part.WTPart;
import wt.pom.Transaction;
import wt.type.ClientTypedUtility;
import wt.type.TypeDefinitionReference;
import wt.util.WTException;
import wt.util.WTRuntimeException;

public class InputWTDOC implements RemoteAccess, Serializable {

	private static final long serialVersionUID = 1L;

	public static void main(String[] args) throws WTRuntimeException, WTException {
		String flag = (String) invoke("insertData", InputWTDOC.class.getName(), null, new Class[] {}, new Object[] {});
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
		HashMap<String, ArrayList<SQLData>> dataMap = getData();

		for (Entry<String, ArrayList<SQLData>> entry : dataMap.entrySet()) {
			String partNumber = entry.getKey();
			ArrayList<SQLData> dataList = entry.getValue();
			String filePath = "";
			Transaction tx = new Transaction();
			try {
				tx.start();
				// 获取部件
				WTPart part = PartUtil.getWTPartByNumber(partNumber);
				ArrayList<WTDocument> docList = new ArrayList<>();

				// 为每个 SQLData 创建并上传文档
				for (SQLData sqlData : dataList) {
					filePath = sqlData.getFilePath();
					int lastSlashIndex = filePath.lastIndexOf('/');
					filePath = lastSlashIndex != -1 && lastSlashIndex < filePath.length() - 1
							? "/opt/acknowledgment/" + filePath.substring(lastSlashIndex + 1)
							: sqlData.getName();

//					Pattern pattern = Pattern.compile(".*/([^/]+)$");
//					Matcher matcher = pattern.matcher(filePath);
//					filePath = matcher.find() ? "/opt/acknowledgment/" + matcher.group(1)
//							: "/opt/acknowledgment/" + sqlData.getName();
//					filePath = "/opt/acknowledgment/" + sqlData.getFilePath().split("/")[2];
//					DocumentType docType = DocumentType.toDocumentType(docTypeMap.get(sqlData.getDocType()));
					DocumentType docType = DocumentType.toDocumentType("$$Document");
					WTDocument doc = createAndUpload(sqlData.getName(), sqlData.getDocNumber(), filePath, part,
							docType);
					docList.add(doc);
				}

				// 创建文档与部件之间的链接
				DocumentUtil.createDocPartLink(part, docList, "Describe");
				tx.commit();
			} catch (Exception e) {
				// 处理异常，回滚事务
				tx.rollback();
				System.out.println("--------创建与部件(" + partNumber + ")之间的说明方文档关联时，系统本地承认书文件地址为" + filePath + "--------");
			}
		}
	}

	private static WTDocument createAndUpload(String name, String number, String filePath, WTPart part,
			DocumentType docType) {
		try {
			// 创建WTDocument对象
			WTDocument doc = WTDocument.newWTDocument();

			TypeDefinitionReference tdr = ClientTypedUtility
					.getTypeDefinitionReference("com.honghe_tech.HHT_Acknowledgment");
			doc.setTypeDefinitionReference(tdr);
			doc.setName(name);
			doc.setNumber("OUT" + CommonUtil.addLead0(number, 8));
			doc.setDocType(docType);
			doc.setDomainRef(part.getDomainRef());
			doc.setDepartment(DepartmentList.toDepartmentList("HHTSystemDesign"));
			WTContainer container = part.getContainer();
			doc.setContainer(container);
			Folder folder = container.getIdentity().contains("PDMLinkProduct")
					? ContainerUtil.getFolder("08-采购文档", container)
					: ContainerUtil.getFolder("/" + part.getFolderPath().split("/")[2], container);
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

			theContent = ContentServerHelper.service.updateContent(doc, theContent, fis);
			ContentServerHelper.service.updateHolderFormat(doc);

			doc = (WTDocument) PersistenceHelper.manager.refresh((Persistable) doc, true, true);
			fis.close();
			return doc;
		} catch (Exception e) {
			System.out.println("--------创建名称为 " + name + " ,部件编号为 " + part.getNumber() + " 的WTDocument对象时出现问题--------");
		}
		return null;
	}

	public static HashMap<String, ArrayList<SQLData>> getData() {
		try {
			String sql = "SELECT * FROM ACKNOWLEDGMENTINPUT";
			String sql2 = "SELECT  HHT_ACKNOWLEDGMENT.NEXTVAL FROM DUAL";
			HashMap<String, ArrayList<SQLData>> map = new HashMap<>();

			ResultSet resultSet = CommonUtil.excuteSelect(sql);
			while (resultSet.next()) {
				ArrayList<SQLData> list = new ArrayList<>();
				SQLData data = new SQLData();
				data.setName(resultSet.getString("name"));
				data.setDocType(resultSet.getString("docType"));
				String partNumber = resultSet.getString("partNumber");
				data.setPartNumber(partNumber);
//				data.setSupplier(resultSet.getString("supplier"));
				data.setDepartment(resultSet.getString("department"));
				data.setVersion(resultSet.getString("version"));
				data.setFilePath(resultSet.getString("filePath"));
				ResultSet resultSet2 = CommonUtil.excuteSelect(sql2);
				while (resultSet2.next()) {
					data.setDocNumber(resultSet2.getString("NEXTVAL"));
				}
				list.add(data);
				map.compute(partNumber, (key, existingList) -> {
					if (existingList == null) {
						return list;
					} else {
						existingList.addAll(list);
						return existingList;
					}
				});
			}
			return map;
		} catch (Exception e) {
			throw new RuntimeException("查询数据库出现问题！");
		}
	}

}
