package ext.signature;

import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.lang3.StringUtils;

import com.ptc.core.meta.common.impl.TypeIdentifierUtilityHelper;
import com.ptc.core.meta.type.mgmt.common.TypeDefinitionDefaultView;
import com.ptc.core.meta.type.mgmt.server.impl.WTTypeDefinition;
import com.ptc.windchill.enterprise.attachments.server.AttachmentsHelper;
import com.ptc.windchill.enterprise.part.commands.PartDocServiceCommand;
import com.ptc.wvs.common.ui.VisualizationHelper;

import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.content.ContentRoleType;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.epm.EPMDocumentType;
import wt.epm.util.EPMSoftTypeServerUtilities;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.part.WTPart;
import wt.part.WTPartHelper;
import wt.part.WTPartMaster;
import wt.part.WTPartUsageLink;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.representation.Representation;
import wt.type.TypeDefinitionReference;
import wt.util.FileUtil;
import wt.util.WTException;
import wt.vc.config.LatestConfigSpec;

public class PartPDFHelper {

	private static EPMDocumentType CADCOMPONENT = EPMDocumentType.toEPMDocumentType("CADCOMPONENT"); //

	private static EPMDocumentType CADASSEMBLY = EPMDocumentType.toEPMDocumentType("CADASSEMBLY"); //

	private static EPMDocumentType CADDRAWING = EPMDocumentType.toEPMDocumentType("CADDRAWING"); //

	private static Properties CONFIG = new Properties();
	private static List<String> subDocTypeList = new ArrayList<String>();

	static {
		try {

			InputStream is = PartPDFHelper.class.getResourceAsStream("/ext/signature/config.properties");
			CONFIG.load(is);
			is.close();
			System.out.println("Load config.properties :" + CONFIG);
			// 获取文档类型清单
			String subDocTypeNames = CONFIG.getProperty("wtpart.describe.wtdocument.type.list",
					"cn.com.sinoboom.DWGDrawing");
			String[] split = StringUtils.split(subDocTypeNames, ",");
			if (split != null && split.length > 0) {
				for (int index = 0; index < split.length; index++) {
					String docType = split[index];
					subDocTypeList.add(docType);
					subDocTypeList.add(StringUtils.remove(docType, "cn.com.sinoboom."));
				}
			}
		} catch (Exception e) {
			System.out.println("## WARNING: " + e.getLocalizedMessage());
		}
	}

	/**
	 * 根据WTPart获取 pdf表示法
	 * 
	 * @param part
	 * @return
	 * @throws Exception
	 */
	public static List<ApplicationData> findPDFRepFromPart(WTPart part) throws Exception {
		System.out.println("005-batch download pdf : findPDFRepFromPart " + part.getNumber());
		List<ApplicationData> pdfList = new ArrayList<ApplicationData>();
		List<Persistable> drawingList = get2DDrawingByWTPart(part);
//		List<Persistable> drawingList = getPDFDocByWTPart(part);
		if (drawingList == null) {
			return pdfList;
		}
		VisualizationHelper vizHelper = new VisualizationHelper();

		for (Persistable drawing : drawingList) {
			QueryResult result = vizHelper.getRepresentations(drawing);
			Representation rep = null;
			ContentHolder ch = null;
			Vector<?> appDatas = null;

			ApplicationData appData = null;
			ApplicationData foundPDF = null;
			String fileName = null;
			while (result.hasMoreElements()) {
				rep = (Representation) result.nextElement();
				ch = ContentHelper.service.getContents(rep);
				Boolean defaultRepresentation = rep.getDefaultRepresentation();
				// 必须从默认表示法中获取pdf
				if (!defaultRepresentation) {
					continue;
				}
				appDatas = ContentHelper.getContentListAll(ch);
				for (int i = 0; i < appDatas.size(); i++) {
					appData = (ApplicationData) appDatas.get(i);
					fileName = appData.getFileName();
					ContentRoleType role = appData.getRole();
					// 检查表示法中是否包含pdf
					if ((role.equals(ContentRoleType.ADDITIONAL_FILES) || role.equals(ContentRoleType.SECONDARY))
							&& FileUtil.getExtension(fileName).equalsIgnoreCase("pdf")) {
						foundPDF = appData;
						break;
					}
				}
				if (foundPDF != null) {
					break;
				}
			}
			if (foundPDF != null) {
				System.out.println("005-batch download pdf : findPDFRepFromPart " + foundPDF.getFileName());
				pdfList.add(foundPDF);
			}
		}
		return pdfList;
	}

	/**
	 * 根据WTPart获取说明文档的主内容和附件
	 * 
	 * @param part
	 * @return
	 * @throws Exception
	 */
	public static List<ApplicationData> findDescriptedFromPart(WTPart part) throws Exception {
		System.out.println("005-batch download pdf : findDescriptedFromPart " + part.getNumber());
		List<ApplicationData> pdfList = new ArrayList<ApplicationData>();
		List<Persistable> drawingList = getPDFDocByWTPart(part);
		if (drawingList == null) {
			return pdfList;
		}

		// 获取主内容和附件
		for (Persistable drawing : drawingList) {
			// cn.com.sinoboom.DWGDrawing
			if (drawing instanceof WTDocument) {
				String typeName = getTypeName((WTDocument) drawing);
				System.out.println("005-batch download pdf : descripted document type: " + typeName);
				if (!subDocTypeList.contains(typeName)) {
					continue;
				}
			}
			QueryResult attachments = AttachmentsHelper.service.getAttachments(drawing, ContentRoleType.PRIMARY);
			if (attachments.hasMoreElements()) {
				ApplicationData applicationData = (ApplicationData) attachments.nextElement();
				pdfList.add(applicationData);
			}
			attachments = AttachmentsHelper.service.getAttachments(drawing, ContentRoleType.SECONDARY);
			while (attachments.hasMoreElements()) {
				ApplicationData applicationData = (ApplicationData) attachments.nextElement();
				pdfList.add(applicationData);
			}
		}
		return pdfList;
	}

	/**
	 * @description 得到文档对象的自定义类型
	 * @param obj
	 * @return String
	 * @throws WTException
	 */
	public static String getTypeName(WTDocument doc) {
		String typeDisplayName = null;
		TypeDefinitionReference ref = null;
		WTTypeDefinition definition = null;
		ref = doc.getTypeDefinitionReference();
		try {
			TypeDefinitionDefaultView view = EPMSoftTypeServerUtilities.getTypeDefinition(ref);
			definition = (WTTypeDefinition) PersistenceHelper.manager.refresh(view.getObjectID());
			typeDisplayName = definition.getName(); // 类型的名称
			// System.out.println(typeDisplayName+"-------------");
		} catch (WTException e) {
			e.printStackTrace();
		}
		return typeDisplayName;
	}

	/**
	 * BOM
	 * 
	 * @param part
	 * @return
	 * @throws Exception
	 */
	public static Object[] findBOMPDFRepFromPart(WTPart part) throws Exception {
		System.out.println("005-batch download pdf : findBOMPDFRepFromPart " + part.getNumber());
		Object[] returnData = new Object[2];
		List<ApplicationData> list = new ArrayList<ApplicationData>();
		List<WTPart> noPdfPartList = new ArrayList<WTPart>();
		Set<WTPart> set = new HashSet<WTPart>();
		getHasBomPartsByPart(part, set);
		set.add(part);
		System.out.println("005-batch download pdf : getHasBomPartsByPart " + set.size());
		for (WTPart wtPart : set) {
			List<ApplicationData> pdfRep = findPDFRepFromPart(wtPart);// drw的可视化pdf附件
			List<ApplicationData> pdfAttachemnt = findDescriptedFromPart(wtPart);// 说明文档的主内容和附件
			pdfRep.addAll(pdfAttachemnt);
			if (pdfRep.isEmpty()) {
				noPdfPartList.add(wtPart);
			} else {
				list.addAll(pdfRep);
			}
		}
		// no pdf part list
		returnData[0] = noPdfPartList;
		// pdf list
		returnData[1] = list;
		return returnData;
	}

	/**
	 * 根据part得到其包含子部件 的所有部件(包括其本身)
	 * 
	 * @param WTPart
	 * @param Set    <WTPart>
	 * @return Set<WTPart>
	 * @throws WTException
	 * @throws RemoteException
	 */
	public static Set<WTPart> getHasBomPartsByPart(WTPart productPart, Set<WTPart> set)
			throws RemoteException, WTException {
//		WTPart sPart = null;
//		QueryResult qr2 = null;
		String configPartType = CONFIG.getProperty("partType");
		String currentPartType = TypeIdentifierUtilityHelper.service.getTypeIdentifier(productPart)
				.getTypeInternalName();
		if (configPartType.equals(currentPartType))
			return new HashSet<WTPart>();
		try {
			QueryResult qr = WTPartHelper.service.getUsesWTPartMasters(productPart);
			while (qr.hasMoreElements()) {
				WTPartUsageLink usageLink = (WTPartUsageLink) qr.nextElement();
				WTPartMaster uses = usageLink.getUses();
				WTPart part = getPartByNumber(uses.getNumber(), productPart.getViewName());
//				qr2 = VersionControlHelper.service.allVersionsOf(usageLink.getUses());
//				if (qr2.hasMoreElements()) {
//					sPart = (WTPart) qr2.nextElement();
//					set.add(sPart);
//					getHasBomPartsByPart(sPart, set);
//				}
				if (part != null) {
					set.add(part);
					getHasBomPartsByPart(part, set);
				}
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		return set;
	}

	public static WTPart getPartByNumber(String number, String view) throws WTException {
		WTPart part = null;
		QuerySpec qs = new QuerySpec(WTPart.class);
		SearchCondition sc = new SearchCondition(WTPart.class, "master>number", "=", number);
		qs.appendWhere(sc);
		qs.appendAnd();
		qs.appendWhere(new SearchCondition(WTPart.class, "interopInfo.iopState", "<>", "terminal"));
		QueryResult qr = PersistenceHelper.manager.find(qs);
		LatestConfigSpec lcs = new LatestConfigSpec();
		qr = lcs.process(qr);

		if (qr.size() > 0) {
			part = (WTPart) qr.nextElement();
		}
		if ((part != null) && (StringUtils.equalsIgnoreCase(view, part.getViewName()))) {
			return part;
		}
		return null;
	}

	/**
	 * 根据3D获取2D drawing
	 * 
	 * @param cad
	 * @throws Exception
	 */
	public static List<Persistable> getPDFDocByWTPart(WTPart part) throws Exception {
		List<Persistable> result = new ArrayList<Persistable>();
		QueryResult qr = null;
		WTDocument doc = null;
		try {
			qr = WTPartHelper.service.getDescribedByDocuments(part);// shuoming文档
			while (qr.hasMoreElements()) {
				Object obj = qr.nextElement();
				if (obj instanceof WTDocument) {
					doc = (WTDocument) obj;
					result.add(doc);
				}
			}

		} catch (WTException e) {
			e.printStackTrace();
			throw e;
		}
		return result;
	}

	/**
	 * 根据3D获取2D drawing
	 * 
	 * @param cad
	 * @throws Exception
	 */
	public static List<Persistable> get2DDrawingByWTPart(WTPart part) throws Exception {
		List<Persistable> result = new ArrayList<Persistable>();
		QueryResult qr = null;
		EPMDocument doc = null;
		try {
			qr = PartDocServiceCommand.getAssociatedCADDocuments(part);// CAD文档
			while (qr.hasMoreElements()) {
				Object obj = qr.nextElement();
				if (obj instanceof EPMDocument) {
					doc = (EPMDocument) obj;
					EPMDocumentType docType = doc.getDocType();
//					EPMAuthoringAppType appType = doc.getAuthoringApplication();
					if (CADDRAWING.equals(docType) && doc.getNumber().contains(part.getNumber())) {
						System.out.println("005-batch download pdf : get2DDrawingByWTPart " + doc.getNumber());
						result.add(doc);
					}
				}
			}

		} catch (WTException e) {
			e.printStackTrace();
			throw e;
		}
		return result;
	}

	/**
	 * 根据WTPart获取3D
	 * 
	 * @param part
	 * @throws Exception
	 */
	public static EPMDocument get3DCADByPart(WTPart part) throws Exception {
		EPMDocument doc = null;
		try {
			// 3D
			QueryResult qr = PartDocServiceCommand.getAssociatedCADDocuments(part);// CAD文档
			while (qr.hasMoreElements()) {
				Object obj = qr.nextElement();
				if (obj instanceof EPMDocument) {
					doc = (EPMDocument) obj;
					EPMDocumentType docType = doc.getDocType();
					if ((CADCOMPONENT.equals(docType) || CADASSEMBLY.equals(docType))) {
						return doc;
					}
				}
			}

		} catch (WTException e) {
			e.printStackTrace();
			throw new Exception("No 3D CAD Document on Part :" + part.getName());
		}
		return null;
	}
}
