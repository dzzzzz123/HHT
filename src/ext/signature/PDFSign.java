package ext.signature;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;
import java.util.Vector;

import org.apache.commons.lang3.StringUtils;

import com.lowagie.text.DocumentException;
import com.ptc.core.lwc.common.view.TypeDefinitionReadView;
import com.ptc.core.lwc.server.TypeDefinitionServiceHelper;
import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.core.meta.server.TypeIdentifierUtility;
import com.ptc.wvs.common.ui.VisualizationHelper;

import ext.ait.util.VersionUtil;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.content.ContentItem;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.epm.EPMDocumentMaster;
import wt.epm.build.EPMBuildRule;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.lifecycle.LifeCycleManaged;
import wt.part.WTPart;
import wt.pom.Transaction;
import wt.query.QueryException;
import wt.representation.Representation;
import wt.util.FileUtil;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.util.WTPropertyVetoException;

public class PDFSign {
	private static WTPart getPartbyEPM(EPMDocument epm) {
		EPMDocument epmDoc = null;
		WTPart part = null;
		try {
			QueryResult qr = PersistenceHelper.manager.navigate(epm, "references",
					wt.epm.structure.EPMReferenceLink.class);
			System.out.println("qr" + qr.size());
			while (qr.hasMoreElements()) {
				Object object = qr.nextElement();
				System.out.println("object: " + object);
				if (object instanceof EPMDocumentMaster) {
					EPMDocumentMaster master = (EPMDocumentMaster) object;
					epmDoc = (EPMDocument) VersionUtil.getLatestObjectByMaster(master);
					System.out.println("EPMDocument: " + epmDoc);
				}
				if (!epmDoc.getDocType().toString().equals("FORMAT")) {
					QueryResult qr2 = PersistenceHelper.manager.navigate(epmDoc, EPMBuildRule.BUILD_TARGET_ROLE,
							EPMBuildRule.class, true);
					System.out.println("qr2: " + qr2.size());
					while (qr2.hasMoreElements()) {
						Object object2 = qr2.nextElement();
						System.out.println("object2: " + object2);
						if (object2 instanceof WTPart) {
							part = (WTPart) object2;
							System.out.println("part: " + part);
						}
					}
				}
			}
		} catch (QueryException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}
		return part;
	}

	public static String PDF_TEMP = "";
	static {
		String wtHome = null;
		try {
			WTProperties props = WTProperties.getLocalProperties();
			wtHome = props.getProperty("wt.temp");
			PDF_TEMP = wtHome + File.separator + "pdf";
			File pdfFolder = new File(PDF_TEMP);
			if (!pdfFolder.exists()) {
				pdfFolder.mkdir();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 把pdf表示法更新为新的pdf
	 * 
	 * @param doc
	 * @param documentType
	 * @param reviewers
	 * @return
	 * @throws WTException
	 */
	@SuppressWarnings("deprecation")
	public static Persistable signPDFVisualization(Persistable persistable,
			HashMap<String, Vector<String[]>> signInfoMap, String documentType) throws WTException {

		Transaction trx = new Transaction();

		try {
			trx.start();

			VisualizationHelper vizHelper = new VisualizationHelper();
			QueryResult result = vizHelper.getRepresentations(persistable);

			Representation rep = null;
			ContentHolder ch = null;
			Vector<?> appDatas = null;

			ApplicationData appData = null;
			ApplicationData foundPDF = null;
			ApplicationData foundDXF = null;
			String fileName = null;
			InputStream is = null;

			while (result.hasMoreElements()) {
				rep = (Representation) result.nextElement();
				ch = ContentHelper.service.getContents(rep);
				String name = rep.getName();
				Boolean defaultRepresentation = rep.getDefaultRepresentation();
				System.out.println("002-Representation : " + name);
				System.out.println("002-Representation defaultRepresentation: " + defaultRepresentation);
				// 必须从默认表示法中获取PDF
				if (!defaultRepresentation) {
					continue;
				}
				appDatas = ContentHelper.getContentListAll(ch);
				for (int i = 0; i < appDatas.size(); i++) {
					appData = (ApplicationData) appDatas.get(i);
					fileName = appData.getFileName();
					System.out.println("002-Representation FileName: " + fileName);
					ContentRoleType role = appData.getRole();
					System.out.println("002-Representation ContentRoleType: " + role.toString());
					// 检查表示法中是否包含PDF
					if ((role.equals(ContentRoleType.ADDITIONAL_FILES) || role.equals(ContentRoleType.SECONDARY))
							&& FileUtil.getExtension(fileName).equalsIgnoreCase("pdf")) {
						foundPDF = appData;
						System.out.println("002-Representation foundPDF: " + foundPDF.toString());
						break;
					}
					// 检查表示法中是否包含DXF
					if ((role.equals(ContentRoleType.ADDITIONAL_FILES) || role.equals(ContentRoleType.SECONDARY))
							&& FileUtil.getExtension(fileName).equalsIgnoreCase("dxf")) {
						foundDXF = appData;
						System.out.println("DXFname: " + fileName);
					}
				}
				if (foundPDF != null) {
					break;
				}
			}

			// 如果表示法中包含PDF
			if (foundPDF != null) {
				System.out
						.println("002-Representation oid: " + foundPDF.getPersistInfo().getObjectIdentifier().getId());
				if (persistable instanceof WTDocument) {
					// 判断子类型，根据子类型从signature.properties获取子类型的签名配置，如
					// 质量文档.PDF文档.默认签名页数=1
					// 质量文档.PDF文档.A4.图纸校对.COORDINATE.X+Y=380,761
					// 质量文档是文档子类型的显示名称
					// 如果是WTDocument的父类型，则取没有前缀的签名配置
					WTDocument doc = (WTDocument) persistable;
					TypeIdentifier typeIdentifier = TypeIdentifierUtility.getTypeIdentifier(doc);
					TypeDefinitionReadView typeDefView = TypeDefinitionServiceHelper.service
							.getTypeDefView(typeIdentifier);
					String docType = doc.getDocType().getDisplay(Locale.CHINA);
					if (typeDefView != null) {
						String typename = typeIdentifier.getTypename();
						String displayName = typeDefView.getDisplayName();
						System.out.println("002-check PDF Document Type: " + typename);
						System.out.println("002-check PDF Document Type Display name: " + displayName);
						System.out.println("002-check PDF Document Type docType: " + docType);
						if (!StringUtils.equalsIgnoreCase("wt.doc.WTDocument", typename)) {
							// documentType = docType + "." + displayName + "." + documentType;
							documentType = docType + "." + displayName + "." + documentType;
						}
					}
				}

				// 获取表示法中的PDF，转换为输入流IS
				is = ContentServerHelper.service.findContentStream(foundPDF);
				String newPdfName = UUID.randomUUID().toString() + ".pdf";
				String signatureFilePath = PDF_TEMP + File.separator + newPdfName;
				// 调用签名方法
				System.out.println("003-PDF 签名开始 : " + signatureFilePath);
				String state = "";
				// 获取生命周期状态
				if (persistable instanceof LifeCycleManaged) {
					LifeCycleManaged lcm = (LifeCycleManaged) persistable;
					state = lcm.getState().toString();
				}
				SignaturePDF.WriteSignatureToPDF(signInfoMap, is, signatureFilePath, "", state, documentType);

				// 读取本地签名成功的PDF
				FileInputStream fis = new FileInputStream(signatureFilePath);
				// 重命名表示法中的PDF和DXF文档
				if (persistable instanceof EPMDocument) {
					// 将表示法中的PDF替换为签名的PDF
					foundPDF = ContentServerHelper.service.updateContent(ch, foundPDF, fis);

					// 将表示法中的PDF复制到附件中，并设置其名词
					// 更新表示法中的PDF名称
					EPMDocument lcm = (EPMDocument) persistable;
					WTPart part = getPartbyEPM(lcm);
					String newFileName = part.getNumber() + "_" + part.getName() + "_"
							+ part.getVersionIdentifier().getValue() + "." + part.getIterationIdentifier().getValue()
							+ "_" + part.getLifeCycleState().getDisplay(Locale.CHINA) + ".pdf";

					// 删除EPM附件中的PDF和DXF
					deleteEPMSecondaryContent(lcm);
					// 将表示法中的PDF和DXF复制到EPM附件中
					ApplicationData applicationData = copyApplicationData(lcm, foundPDF, newFileName);

					// 更新表示法中的DXF名称
					String newFileDXFName = part.getNumber() + "_" + part.getName() + "_"
							+ part.getVersionIdentifier().getValue() + "." + part.getIterationIdentifier().getValue()
							+ "_" + part.getLifeCycleState().getDisplay(Locale.CHINA) + ".dxf";
					foundDXF.setFileName(newFileDXFName);
					PersistenceHelper.manager.save(foundDXF);
				} else if (persistable instanceof WTDocument) {
					// Update wvs pdf FileName jpj 2024
					WTDocument wtDocument = (WTDocument) persistable;
					String newFileName = wtDocument.getNumber() + "_" + wtDocument.getName() + "_"
							+ wtDocument.getVersionIdentifier().getValue() + "_"
							+ wtDocument.getState().getState().getDisplay(Locale.CHINA) + "_" + "签章版.pdf";

					foundPDF.setFileName(newFileName);
					foundPDF.setRole(ContentRoleType.SECONDARY);
					foundPDF.setFileSize(foundPDF.getFileSize());
					System.out.println("foundPDF--FileName:" + foundPDF.getFileName());
					System.out.println("foundPDF--FileSize:" + foundPDF.getFileSizeKB());

					PersistenceHelper.manager.save(foundPDF);

					QueryResult qr = ContentHelper.service.getContentsByRole(wtDocument, ContentRoleType.SECONDARY);

					int b = 0;

					while (qr.hasMoreElements()) {
						ContentItem ci = (ContentItem) qr.nextElement();
						System.out.println("-----ci----" + ci);
						ApplicationData applicationData = (ApplicationData) ci;
						System.out.println("J01--" + applicationData.getFileName());
						System.out.println("J02--" + ci.getFormatName());
						String FileName = applicationData.getFileName();
						String FormatName = applicationData.getFormat().getFormatName();
						int a = FileName.lastIndexOf(".");
						String FormatName2 = FileName.substring(a + 1);
						System.out.println("J03--" + FormatName);
						System.out.println("J04--" + FormatName2);
						if (FormatName.equals("PDF")) {
							++b;
							if (FileName.contains("签章版")) {
								ContentServerHelper.service.deleteContent(wtDocument, ci);
								foundPDF = ContentServerHelper.service.updateContent(wtDocument, foundPDF, fis);
							} else {
								foundPDF = ContentServerHelper.service.updateContent(wtDocument, foundPDF, fis);
							}
						}
					}
					if (b == 0) {
						foundPDF = ContentServerHelper.service.updateContent(wtDocument, foundPDF, fis);
					}
				}
				fis.close();
				// 删除临时文件
				System.out.println("005-删除临时文件 : " + signatureFilePath);
				new File(signatureFilePath).delete();
			} else {
				System.out.println("002- No PDF ");
			}

			trx.commit();
			trx = null;
		} catch (PropertyVetoException e) {
			throw new WTException(e);
		} catch (FileNotFoundException e) {
			throw new WTException(e);
		} catch (IOException e) {
			throw new WTException(e);
		} catch (DocumentException e) {
			e.printStackTrace();
			throw new WTException(e);
		} catch (Exception e) {
			e.printStackTrace();
			throw new WTException(e);
		} finally {
			if (trx != null) {
				trx.rollback();
				trx = null;
			}
		}

		return persistable;
	}

	// 删除EPM全部附件
	private static void deleteEPMSecondaryContent(ContentHolder ch) throws WTException, PropertyVetoException {
		QueryResult qr = ContentHelper.service.getContentsByRole(ch, ContentRoleType.SECONDARY);
		while (qr.hasMoreElements()) {
			ContentItem ci = (ContentItem) qr.nextElement();
			ContentServerHelper.service.deleteContent((ContentHolder) ch, ci);
		}
	}

	// 复制表示法中的PDF及DXF
	private static ApplicationData copyApplicationData(ContentHolder ch, ApplicationData source, String sFileName)
			throws WTException, WTPropertyVetoException {

		ApplicationData appDataNew = ApplicationData.newApplicationData(ch);
		appDataNew.setFileName(sFileName);
		appDataNew.setFileSize(source.getFileSize());
		appDataNew.setRole(ContentRoleType.SECONDARY);
		appDataNew.setCategory("IMAGE");
		appDataNew = (ApplicationData) PersistenceHelper.manager.save(appDataNew);

		return appDataNew;
	}
}
