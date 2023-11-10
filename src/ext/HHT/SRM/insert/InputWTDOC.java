package ext.HHT.SRM.insert;

import java.io.File;
import java.io.FileInputStream;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

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
import wt.part.WTPart;
import wt.pom.PersistenceException;
import wt.pom.Transaction;

public class InputWTDOC implements RemoteAccess {

	private static HashMap<String, String> docTypeMap = new HashMap<>() {
		{
			put("承认书", "HHT_AcknowledgmentDoc");
			put("测试报告", "HHT_TestReport");
			put("ROHS证书", "HHT_ROHSReport");
		}
	};

	public static void insertData() throws PersistenceException {
		Transaction tx = new Transaction();
		try {
			tx.start();
			ArrayList<SQLData> dataList = getData();
			for (SQLData sqlData : dataList) {
				WTPart part = PartUtil.getWTPartByNumber(sqlData.getPartNumber());
				String filePath = "/opt/acknowledgment/" + sqlData.getFilePath().split("/")[2];
				DocumentType docType = DocumentType.toDocumentType(docTypeMap.get(sqlData.getDocType()));
				WTDocument doc = createAndUpload(sqlData.getName(), sqlData.getDocNumber(), filePath, part, docType);
				DocumentUtil.createDocPartLink(part, doc, "Describe");
			}
		} catch (Exception e) {
			tx.rollback();
			e.printStackTrace();
		} finally {
			tx.commit();
		}
	}

	public static WTDocument createAndUpload(String name, String number, String filePath, WTPart part,
			DocumentType docType) throws Exception {
		// 创建WTDocument对象
		WTDocument doc = WTDocument.newWTDocument();
		doc.setName(name);
		doc.setNumber("OUT" + CommonUtil.addLead0(number, 8));
		doc.setDocType(docType);
		doc.setContainerReference(part.getContainerReference());
		doc.setDomainRef(part.getDomainRef());
		doc.setDepartment(DepartmentList.toDepartmentList("HHTSystemDesign"));
		WTContainer container = part.getContainer();
		Folder folder = container.getIdentity().contains("PDMLinkProduct")
				? ContainerUtil.getFolder("08-采购文档", container)
				: ContainerUtil.getFolder("/", container);
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

	}

	public static ArrayList<SQLData> getData() throws Exception {
		String sql = "SELECT * FROM ACKNOWLEDGMENTINPUT";
		String sql2 = "SELECT  HHT_ACKNOWLEDGMENT.NEXTVAL FROM DUAL";
		ArrayList<SQLData> list = new ArrayList<>();

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
			list.add(data);
		}

		return list;
	}

}
