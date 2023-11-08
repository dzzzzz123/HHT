package ext.HHT;

import java.io.File;
import java.io.FileInputStream;

import wt.content.ApplicationData;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.doc.DocumentType;
import wt.doc.WTDocument;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.folder.Folder;
import wt.folder.FolderEntry;
import wt.folder.FolderHelper;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerRef;
import wt.pom.Transaction;

public class InputWTDOC {
	// 230620710A 140310104B 0000001240
	public static WTDocument createAndUpload(String name, String number, WTContainerRef product, Folder folder,
			String filePath) {
		Transaction tx = new Transaction();
		try {
			// 创建WTDocument对象
			WTDocument doc = WTDocument.newWTDocument(name, number, DocumentType.getDocumentTypeDefault());
			doc.setContainerReference(product);
			doc.setDomainRef(((WTContainer) product.getObject()).getDefaultDomainReference());
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
}
