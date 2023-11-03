package ext.epm;

import java.beans.PropertyVetoException;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

import com.ptc.windchill.enterprise.part.commands.AssociationLinkObject;
import com.ptc.windchill.enterprise.part.commands.PartDocServiceCommand;

import ext.ait.util.ClassificationUtil;
import ext.ait.util.CommonUtil;
import ext.ait.util.IBAUtil;
import ext.ait.util.PersistenceUtil;
import wt.change2.WTChangeActivity2;
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
import wt.fc.WTObject;
import wt.iba.value.IBAHolder;
import wt.maturity.MaturityHelper;
import wt.maturity.PromotionNotice;
import wt.method.MethodContext;
import wt.org.WTPrincipal;
import wt.part.WTPart;
import wt.pom.Transaction;
import wt.pom.WTConnection;
import wt.session.SessionHelper;
import wt.util.WTException;

/**
 * 在升级流程的升级对象中或ECA流程中受影响的对象中查找A版本的WTPart,对其3D或2D进行重命名，重命名规则如下：
 * 
 * 配置项：1）2d前缀和后缀的开关 2）2d前缀或后缀的值 3）是否改3D的文件名开关
 * 3D重命名的逻辑：一种是根据WTPart的编号进行修改编号和文件名，一种是编号不改，文件名修改成编号。
 * 2D重命名逻辑：总是按3D的编号来重命名编号和文件名 Creo的3D和2D文件名和编号要改成小写，其他为大写
 * 
 * @author samuel @2019-6-12
 *
 */
public class RenameHelper {

	private static EPMDocumentType CADCOMPONENT = EPMDocumentType.toEPMDocumentType("CADCOMPONENT"); //

	private static EPMDocumentType CADASSEMBLY = EPMDocumentType.toEPMDocumentType("CADASSEMBLY"); //

	private static EPMDocumentType CADDRAWING = EPMDocumentType.toEPMDocumentType("CADDRAWING"); //

	private static Properties CONFIG = new Properties();

	static {
		try {
			InputStream is = EPMDocumentFileNameListener.class.getResourceAsStream("/ext/epm/config.properties");
			CONFIG.load(is);
			is.close();
			System.out.println("Load change CAD Name config.properties :" + CONFIG);
		} catch (Exception e) {
			System.out.println("## WARNING: " + e.getLocalizedMessage());
		}
	}

	public static void epmUpdateIBA(WTObject obj) throws Exception {
		if (obj instanceof PromotionNotice) {// 升级流程
			PromotionNotice pn = (PromotionNotice) obj;
			System.out.println("000-updateIBA cad : PromotionNotice " + pn.getNumber());
			// 获取流程的多个对象
			QueryResult qr = MaturityHelper.service.getPromotionTargets(pn);
			while (qr.hasMoreElements()) {
				Object object = qr.nextElement();
				if (object instanceof WTPart) {
					WTPart part = (WTPart) object;
					setEPMDocumentIBAType(part);
				}
			}
		}
	}

	/**
	 * 从工作流模板中添加表达式入口
	 * 
	 * @param pbo
	 */
	public static void renameCAD(WTObject pbo) {
		try {
			System.out.println("000-rename cad : start ");
			if (pbo instanceof PromotionNotice) {// 升级流程
				PromotionNotice pn = (PromotionNotice) pbo;
				System.out.println("000-rename cad : PromotionNotice " + pn.getNumber());
				// 获取流程的多个对象
				QueryResult qr = MaturityHelper.service.getPromotionTargets(pn);
				while (qr.hasMoreElements()) {
					Object obj = qr.nextElement();
					if (obj instanceof WTPart) {
						WTPart part = (WTPart) obj;
						String version = part.getVersionIdentifier().getValue();
						setEPMDocumentIBAType(part);
						if (StringUtils.equalsIgnoreCase(version, "A")) {
							System.out.println("000-rename cad : WTPart " + part.getNumber() + " " + version);
							renameCADFromWTPart(part);
						}
					}
				}
			} else if (pbo instanceof WTChangeActivity2) {// ECA流程
				WTChangeActivity2 eca = (WTChangeActivity2) pbo;
				System.out.println("000-rename cad : WTChangeActivity2 " + eca.getNumber());
				wt.fc.QueryResult changeables = wt.change2.ChangeHelper2.service.getChangeablesAfter(eca);
				if (changeables != null) {
					while (changeables.hasMoreElements()) {
						Object localObject = changeables.nextElement();
						if (localObject instanceof WTPart) {
							WTPart part = (WTPart) localObject;
							String version = part.getVersionIdentifier().getValue();
							if (StringUtils.equalsIgnoreCase(version, "A")) {
								System.out.println("000-rename cad : WTPart " + part.getNumber() + " " + version);
								renameCADFromWTPart(part);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void renameCADFromWTPart(WTPart part) throws Exception {
		List<EPMDocument> list = getEPMDocumentByPart(part);
//		String number3d = null;
		// 2023.11.02 DZ 修改3d和2d的名称和编号规则
		// 3d编号为分类码+下划线+流水码 23083_00092
		// 2d编号为分类码+下划线+流水码+下划线+D 23083_00092_D
		// 3d与2d名称都为其所关联部件的名称
		// 3d与2d文件名为3d的编号加上.prt/.drw后缀
		String new3DNumber = getSerial3DNumber(part);
		String new2DNumber = new3DNumber + "_D";
		String new3DName = part.getName();
		String new2DName = part.getName();
		String new3DFileName = new3DNumber + ".prt";
		String new2DFileName = new3DNumber + ".drw";

		// 3d
		for (EPMDocument epm : list) {
			EPMDocumentType epmType = epm.getDocType();
			if ((CADCOMPONENT.equals(epmType) || CADASSEMBLY.equals(epmType))) {
				reEPMNameNumber(epm, new3DNumber, new3DName, new3DFileName);
//				System.out.println("001-rename cad : find 3d " + epm.getNumber());
//				// 3D
//				String renameFromPart = CONFIG.getProperty("rename.3d.reference.from.wtpart");
//				if ("false".equalsIgnoreCase(StringUtils.trim(renameFromPart))) {
//					renameEPMDocument(epm, null, null);
//					number3d = epm.getNumber();
//				} else {
//					renameEPMDocument(epm, part.getNumber(), part.getNumber());
//					number3d = part.getNumber();
//				}
//				break;
			}
			// 2D
			else if (CADDRAWING.equals(epmType)) {
				reEPMNameNumber(epm, new2DNumber, new2DName, new2DFileName);
//				System.out.println("001-rename cad : find 2d " + epm.getNumber());
//				String appendOption = CONFIG.getProperty("rename.2d.append.option");
//				String appendValue = CONFIG.getProperty("rename.2d.append.value");
//				String cadNameAppend = CONFIG.getProperty("rename.2d.append.cadname");
//				System.out.println("001-rename cad : rename.2d.append.option=" + appendOption);
//				System.out.println("001-rename cad : rename.2d.append.value=" + appendValue);
//				System.out.println("001-rename cad : rename.2d.append.cadname=" + cadNameAppend);
//				String newCADName = number3d;
//				if ("suffix".equalsIgnoreCase(StringUtils.trim(appendOption))) {
//					number3d = number3d + appendValue;
//				} else if ("prefix".equalsIgnoreCase(StringUtils.trim(appendOption))) {
//					number3d = appendValue + number3d;
//				} else {
//					number3d = appendValue + number3d;
//				}
//				if (!"true".equalsIgnoreCase(StringUtils.trim(cadNameAppend))) {
//					renameEPMDocument(epm, number3d, newCADName);
//				} else {
//					renameEPMDocument(epm, number3d, number3d);
//				}
//				break;
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

	/**
	 * 根据WTPart获取3D
	 * 
	 * @param part
	 * @throws Exception
	 */
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

	public static void renameEPMDocument(EPMDocument epm, String wtpartNumber, String newCADName) throws Exception {
		/*
		 * if(WorkInProgressHelper.isCheckedOut(epm)){ return; }
		 */
		Transaction transaction = new Transaction();
		try {
			System.out.println("001-Start rename EPMDocument:" + epm.getNumber());
			/*
			 * WTContainer container = epm.getContainer(); if(!(container instanceof
			 * PDMLinkProduct)){//只有产品库的才刷新文件名 System.out.
			 * println("001-The EPMDocument is not in container PDMLinkProduct,but "
			 * +container.getContainerName()+",so return."); return ; }
			 */
			/*
			 * String version = epm.getVersionIdentifier().getValue();
			 * if(!StringUtils.equalsIgnoreCase(version, "A")){
			 * System.out.println("001-The EPMDocument is revised to version :"
			 * +version+",so return."); return; }
			 */
			EPMDocumentType docType = epm.getDocType();
			System.out.println("001-The DocType of EPMDocument:" + docType);
			String toolName = epm.getAuthoringApplication().toString();
			System.out.println("001-The Authoring Application(Tool) of EPMDocument:" + toolName);
			/*
			 * if(!("CADCOMPONENT".equalsIgnoreCase(docType.toString()) ||
			 * "CADASSEMBLY".equalsIgnoreCase(docType.toString())||
			 * "CADDRAWING".equalsIgnoreCase(docType.toString()))){ System.out.
			 * println("001-The Type of EPMDocument is not CADCOMPONENT or CADASSEMBLY or CADDRAWING ,so return."
			 * ); return ;//只有零件图或装配图才更新文件名称 }
			 */
			System.out.println("001-Getting Application(CAD File) of EPMDocument...");
			ContentHolder contentHolder = ContentHelper.service.getContents((ContentHolder) epm);
			ContentItem contentitem = ContentHelper.getPrimary((FormatContentHolder) contentHolder);
			ApplicationData applicationdata = (ApplicationData) contentitem;
			if (applicationdata != null) {
				String fileName = applicationdata.getFileName();
				System.out.println("001-EPMDocument old filename :" + fileName);
				// 如果wtpartnumber不为空，则要按wtpart进行更新number
				String number = StringUtils.isBlank(wtpartNumber) ? epm.getNumber() : wtpartNumber;
				String extension = CONFIG.getProperty(toolName + "." + docType);
				if (StringUtils.isBlank(extension)) {
					System.out.println("001-extension of filename is not supported, so return.");
					return;
				}
				System.out.println("001-New filename extension:" + extension);
				if (number.equalsIgnoreCase(StringUtils.substringBefore(fileName, extension))) {
					System.out.println("001-New filename is the same of number+" + extension + ", so return.");
					return;// 如果编号和cadname一致则忽略
				}
				newCADName = (StringUtils.isNotBlank(newCADName) ? newCADName : number) + extension;
				// if proe ,use lowercase小写
				if (toolName.equalsIgnoreCase("PROE")) {
					newCADName = newCADName.toLowerCase();
				}
				String lowerUpper = CONFIG.getProperty("rename.3d2d.number.lower.upper");
				System.out.println("001-rename cad : rename.3d2d.number.lower.upper=" + lowerUpper);
				if ("0".equalsIgnoreCase(lowerUpper)) {
					number = number.toLowerCase();
				} else if ("1".equalsIgnoreCase(lowerUpper)) {
					number = number.toUpperCase();
				}
//				EPMDocumentHelper.service.changeCADName((EPMDocumentMaster) epm.getMaster(), newCADName);
				if (!StringUtils.equalsIgnoreCase(fileName, "{$CAD_NAME}")) {
					System.out.println("001-Filename will be setted by new name:" + newCADName);
					applicationdata.setFileName(newCADName);
					PersistenceServerHelper.manager.update(applicationdata);// 更新文件名
				}
				Identified identified = (Identified) epm.getMaster();
				MethodContext methodContext = MethodContext.getContext();
				WTConnection connection = (WTConnection) methodContext.getConnection();
				PreparedStatement prepareStatement = connection
						.prepareStatement("update EPMDocumentMaster set CADName=?,documentnumber=? where ida2a2="
								+ identified.getPersistInfo().getObjectIdentifier().getId());
				prepareStatement.setString(1, newCADName);// 更新CADName
				prepareStatement.setString(2, number);// 更新number
				prepareStatement.execute();
				transaction.commit();
				System.out.println("001-Filename has been setted by new name:" + newCADName + ",success!!!");
			} else {
				System.out.println("001-The Application of EPMDocument is null, so return.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("001-Error happends when rename EPMDocument:" + epm.getNumber() + ", so rollback.");
			transaction.rollback();
		}
	}

	private static void setEPMDocumentIBAType(WTPart part) throws Exception {
		List<EPMDocument> epms = getEPMDocumentByPart(part);
		String epmTypes = CONFIG.getProperty("epmTypes");
		String ibaAttribute = CONFIG.getProperty("CAD_IBA_Attribute");
		String ibaUnit = CONFIG.getProperty("CAD_IBA_Unit");
		String[] types = epmTypes.split(",");
		String[] attributes = ibaAttribute.split(",");
		String[] ibaUnits = ibaUnit.split(",");
		IBAUtil epmIba = null;
		for (EPMDocument epm : epms) {
			String type = PersistenceUtil.getSubTypeInternal(epm);
			for (String epmType : types) {
				if (StringUtils.equals(type, epmType)) {
					IBAHolder epmHolder = (IBAHolder) epm;
					epmIba = new IBAUtil(epmHolder);
					IBAUtil partIba = new IBAUtil((IBAHolder) part);
					if (StringUtils.isNotBlank(ibaUnit))
						for (String unit : ibaUnits) {
							String partValue = partIba.getIBAValue(unit);
							Double value = Double.valueOf(partValue.split(" ")[0]);
							IBAUtil.newIBAUnitAttribute(epmHolder, unit, value);
						}
					for (String attrName : attributes) {
						String partValue = partIba.getIBAValue(attrName);
						epmIba.setIBAAttribute4AllType(epmHolder, attrName, partValue);
					}
				}
			}
		}
	}

}
