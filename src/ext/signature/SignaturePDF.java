package ext.signature;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.commons.lang3.StringUtils;

import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.windchill.enterprise.workflow.WorkflowCommands;

import wt.change2.ChangeHelper2;
import wt.change2.Changeable2;
import wt.change2.WTChangeActivity2;
import wt.epm.EPMDocument;
import wt.fc.ObjectReference;
import wt.fc.Persistable;
import wt.fc.QueryResult;
import wt.fc.WTObject;
import wt.fc.collections.WTCollection;
import wt.fc.collections.WTHashSet;
import wt.inf.container.WTContained;
import wt.inf.container.WTContainerHelper;
import wt.inf.container.WTContainerRef;
import wt.maturity.MaturityHelper;
import wt.maturity.PromotionNotice;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.workflow.engine.WfEngineHelper;
import wt.workflow.engine.WfProcess;

public class SignaturePDF {
	private static String tempPath = "";

	static {
		try {
			WTProperties wtprop = WTProperties.getLocalProperties();
			tempPath = wtprop.getProperty("wt.codebase.location");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String processPDFSignByEPMDocumentDrawing(EPMDocument drawing, InputStream is, String pdfPath,
			String deleteKeyword) throws Exception {
		System.out.println("003-signature : signature " + (drawing != null ? drawing.getName() : "") + " start ...");
		try {
			List wfProcessFromPBO = getWfProcessFromPBO(drawing);
			System.out.println(
					"003-signature : getAssociatedProcesses from " + drawing.getName() + ":" + wfProcessFromPBO.size());
			if (wfProcessFromPBO.size() > 0) {
				WfProcess wfProcess = (WfProcess) wfProcessFromPBO.get(0);
				System.out.println("003-signature : the first  associatedProcesses is "
						+ wfProcess.getTemplate().getName() + ">" + wfProcess.getName());
				String oid = wfProcess.getBusinessObjReference();
				System.out.println("003-signature : process businessObjReference : " + oid);

				HashMap signInfoMap = SignatureHelper.getSignInfo(wfProcess);
				System.out.println("003-signature : getSignInfo from process : " + MapToString(signInfoMap));
				System.out.println("003-signature : WriteContentToPDF start 。。。 ");
				WriteSignatureToPDF(signInfoMap, is, pdfPath, deleteKeyword, drawing.getState().toString(), "PDF图纸");
				System.out.println("003-signature : WriteContentToPDF success.");
				return "SUCCESS";
			}
			System.out.println("003-signature : no signinfo because of no process");
			System.out.println("003-signature : WriteContentToPDF start 。。。 ");
			WriteSignatureToPDF(new HashMap(), is, pdfPath, deleteKeyword, null, "PDF图纸");
			System.out.println("003-signature : WriteContentToPDF success.");
			return "SUCCESS";
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("003-signature : signature " + (drawing != null ? drawing.getName() : "") + " error:"
					+ e.getMessage());
			throw e;
		}
	}

	public static List<WfProcess> getWfProcessFromPBO(WTObject pbo) throws WTException {
		List processes = new ArrayList();

		if ((pbo instanceof Changeable2)) {
			QueryResult changingChangeActivities = ChangeHelper2.service.getChangingChangeActivities((Changeable2) pbo);

			if (changingChangeActivities.hasMoreElements()) {
				Object eca = changingChangeActivities.nextElement();
				if ((eca instanceof WTChangeActivity2)) {
					WTChangeActivity2 ca = (WTChangeActivity2) eca;
					QueryResult qr = WfEngineHelper.service.getAssociatedProcesses(ca, null,
							WTContainerRef.newWTContainerRef(WTContainerHelper.getContainer((WTContained) pbo)));
					if (qr.hasMoreElements()) {
						WfProcess wfprocess = (WfProcess) qr.nextElement();
						System.out.println("003-signature :getWfProcessFromPBO() ECA process found [" + ca.getNumber()
								+ "-" + ca.getName() + "-" + wfprocess.getName() + "]");
						processes.add(wfprocess);
					}
				}

			}

		}

		WTCollection localList = new WTHashSet();
		localList.add(pbo);
		WTCollection promotionNotices = MaturityHelper.service.getPromotionNotices(localList);
		for (Iterator qr = promotionNotices.iterator(); qr.hasNext();) {
			Object object = qr.next();
			if ((object instanceof ObjectReference)) {
				ObjectReference pn = (ObjectReference) object;
				Persistable object2 = pn.getObject();
				if ((object2 instanceof PromotionNotice)) {
					PromotionNotice promotion = (PromotionNotice) object2;
					QueryResult localQueryResult = WorkflowCommands.getRoutingHistory(new NmOid(promotion));
					while (localQueryResult.hasMoreElements()) {
						Object nextElement = localQueryResult.nextElement();
						if ((nextElement instanceof WfProcess)) {
							WfProcess process = (WfProcess) nextElement;
							processes.add(process);
							System.out.println("003-signature :getWfProcessFromPBO promotion notice process found ["
									+ process.getName() + "]");
							break;
						}
					}
				}
			}
		}
		return processes;
	}

	public static String MapToString(Map map) throws Exception {
		StringBuffer sb = new StringBuffer();
		Set keySet = map.keySet();
		for (Iterator localIterator = keySet.iterator(); localIterator.hasNext();) {
			Object object = localIterator.next();
			sb.append("{" + object + "}").append("->");
			Object object2 = map.get(object);
			Vector vec = (Vector) object2;
			if ((vec != null) && (vec.size() >= 1)) {
				String[] strings = (String[]) vec.get(0);
				sb.append("[");
				sb.append("userName=" + strings[0]);
				sb.append(",");
				sb.append("loginName=" + strings[2]);
				sb.append(",");
				sb.append("signDate=" + strings[1]);
				sb.append(",");
				sb.append("comment=" + strings[3]);
				sb.append("]");
			}
			sb.append(",");
		}
		return StringUtils.removeEnd(sb.toString(), ",");
	}

	public static void WriteSignatureToPDF(HashMap signInfoMap, InputStream is, String pdfPath, String deleteKeyword,
			String state, String objectType) throws Exception {
		try {
			if (is == null) {
				System.out.println("003-signature : error of no pdf stream ");
				return;
			}
			PdfReader reader = new PdfReader(is);
			System.out.println("003-signature : loading pdf");
			System.out.println("003-pdfPath:" + pdfPath);
			int totalPage = reader.getNumberOfPages();
			ArrayList<String> drawFormat = SignatureHelper.mapSheet(reader, totalPage);
			String fromStr = "[";
			for (String format : drawFormat) {
				fromStr = fromStr + format + ",";
			}
			fromStr = StringUtils.removeEnd(fromStr, ",") + "]";
			System.out.println("003-signature : each page format :" + fromStr);

			String[] pdfSignPage = new String[totalPage];
			String strFromProperties = PropertiesHelper.getStrFromProperties(objectType + ".默认签名页数");
			if (StringUtils.isBlank(strFromProperties))
				pdfSignPage[0] = "N";
			else {
				pdfSignPage = strFromProperties.split(",");
			}
			PdfStamper stamp = new PdfStamper(reader, new FileOutputStream(pdfPath));

			for (int j = 1; j < totalPage + 1; j++) {

				if (!StringUtils.isNotBlank(state))
					continue;
				String tufu = (String) drawFormat.get(j - 1);
				PdfContentByte cb = stamp.getOverContent(j);
				cb.beginText();
				if ("N".equalsIgnoreCase(pdfSignPage[0])) {
					System.out.println("003-signature : signature page [" + j + "] start ...");
					WritePDFCoordinateContent(objectType, tufu, signInfoMap, cb, state);
					System.out.println("003-signature : signature page [" + j + "] success.");
				} else {
					for (int k = 0; k < pdfSignPage.length; k++) {
						try {
							int signaturePage = Integer.parseInt(pdfSignPage[k]);
							if (j == signaturePage) {
								System.out.println("003-signature : signature page [" + j + "] start ...");
								WritePDFCoordinateContent(objectType, tufu, signInfoMap, cb, state);
								System.out.println("003-signature : signature page [" + j + "] success.");
							}
						} catch (NumberFormatException e) {
							System.out.println("003-signature : signpage config from signature.properties has error :["
									+ pdfSignPage[k] + "] is not Integer number");
						}
					}
				}

				cb.endText();
			}

			stamp.close();
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("003-signature : signpage  error :" + e.getMessage());
			throw e;
		}
	}

	public static void WritePDFCoordinateContent(String objectType, String tufu, HashMap signInfoMap, PdfContentByte cb,
			String state) throws Exception {
		try {
			String allNodeName = PropertiesHelper.getStrFromProperties(objectType + ".SIGN.PROCEDURE");
			if ((allNodeName != null) && (!"".equals(allNodeName))) {
				for (StringTokenizer st = new StringTokenizer(allNodeName, ";"); st.hasMoreTokens();) {
					String nodeName = st.nextToken().trim();
					String signUserKey = "";
					String signDateKey = "";
					Vector vec = (Vector) signInfoMap.get(nodeName);
					if ((vec == null) || (vec.size() < 1)) {
						continue;
					}
					signUserKey = objectType + "." + tufu + "." + nodeName + ".COORDINATE";
					signDateKey = objectType + "." + tufu + "." + nodeName + ".COORDINATE.Date";
					String[] signInfo = (String[]) vec.elementAt(0);
					contentWriteToPDF(vec, signInfo, objectType, signUserKey, cb, st, signDateKey);
				}

			} else {
				System.out.println("003-signature : " + objectType
						+ ".SIGN.PROCEDURE config in signature.properties, no signature content.");
			}
			System.out.println("003-signature : start sign state and controlled image...");

			String imagePath = SignatureHelper.jgpExists(state);
			String defaultImageSize = PropertiesHelper.getStrFromProperties("默认状态章图片大小");
			String signStateKey = objectType + "." + tufu + "." + "状态章" + ".COORDINATE";
			if (StringUtils.isNotBlank(imagePath)) {
				System.out.println("003-signature : sign state image [" + state + ".jpg]");
				SignatureHelper.writeOtherImage(imagePath, cb, signStateKey, defaultImageSize);
			} else {
				System.out.println("003-signature : no state image [" + state + ".jpg]");
			}

			String defaultImageName = PropertiesHelper.getStrFromProperties("默认受控章图片名称");
			imagePath = SignatureHelper.jgpExists(defaultImageName);
			defaultImageSize = PropertiesHelper.getStrFromProperties("默认受控章图片大小");
			signStateKey = objectType + "." + tufu + "." + "受控章" + ".COORDINATE";
			if (StringUtils.isNotBlank(imagePath)) {
				System.out.println("003-signature : sign controlled image [" + defaultImageName + ".jpg]");
				SignatureHelper.writeOtherImage(imagePath, cb, signStateKey, defaultImageSize);
			} else {
				System.out.println("003-signature : no controlled image [" + defaultImageName + ".jpg]");
			}
			System.out.println("003-signature : end sign state and controlled image.");
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
	}

	public static void contentWriteToPDF(Vector vec, String[] signInfo, String objectType, String signContentKey,
			PdfContentByte cb, StringTokenizer st, String signDateKey) {
		String userName = signInfo[0];
		String userLoginName = "";
		try {
			BaseFont bf = BaseFont.createFont(tempPath + PropertiesHelper.getStrFromProperties("PDF_FONT_PATH") + ",1",
					"Identity-H", true);
			userLoginName = signInfo[2];
			System.out.println("用户登陆账号:" + userLoginName);
			String imagePath = SignatureHelper.jgpExists(userLoginName);
			float fontSize = Float.parseFloat(PropertiesHelper.getStrFromProperties(objectType + ".默认签名字体大小"));
			// 签图片
			if (!"".equals(imagePath)) {
				System.out.println("003-signature : sign user image [" + userLoginName + ".jpg]");
				SignatureHelper.writeIndividuationImage(imagePath, cb, signContentKey, objectType);
			} else {
				System.out.println("003-signature : sign user name [" + userName + "]");
				SignatureHelper.writeStringContentToPDF(userName, cb, signContentKey, bf, fontSize, objectType);
			}
			// 签日期
			fontSize = Float.parseFloat(PropertiesHelper.getStrFromProperties(objectType + ".Date.字体大小"));
			System.out.println("003-signature : sign date [" + signInfo[1] + "]");
			SignatureHelper.writeStringContentToPDF(signInfo[1], cb, signDateKey, bf, fontSize, objectType);

			// 签备注
			System.out.println("003-signature : sign comment [" + signInfo[3] + "]");
			SignatureHelper.writeStringContentToPDF(signInfo[3], cb, signContentKey + ".Comment", bf, fontSize,
					objectType);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {
		FileInputStream oldPdf = new FileInputStream("D:\\88 个人资料\\will\\test.pdf");
		String newPDF = "D:\\88 个人资料\\will\\test-new.pdf";
		HashMap signInfoMap = new HashMap();
		String[] info1 = { "chenqiang", "2016.03.29", "chenqiang", "coment" };
		String[] info2 = { "chenqiang2", "2016.03.29", "chenqiang2", "coment" };
		Vector vec1 = new Vector();
		vec1.addElement(info1);
		Vector vec2 = new Vector();
		vec2.addElement(info2);
		signInfoMap.put("图纸校对", vec1);
		signInfoMap.put("图纸审核", vec2);
		System.out.println(MapToString(signInfoMap));
		WriteSignatureToPDF(signInfoMap, oldPdf, newPDF, "FORMAT", "released", "PDT图纸");
	}
}