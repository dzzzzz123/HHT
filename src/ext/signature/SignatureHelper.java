package ext.signature;

import java.io.File;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.apache.logging.log4j.Logger;

import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfReader;
import com.ptc.netmarkets.workflow.NmWorkflowHelper;

import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentItem;
import wt.content.FormatContentHolder;
import wt.epm.EPMDocument;
import wt.fc.ObjectIdentifier;
import wt.fc.ObjectReference;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.inf.team.ContainerTeamHelper;
import wt.log4j.LogR;
import wt.method.RemoteAccess;
import wt.org.WTGroup;
import wt.org.WTPrincipal;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.workflow.engine.WfActivity;
import wt.workflow.engine.WfBlock;
import wt.workflow.engine.WfProcess;
import wt.workflow.engine.WfVotingEventAudit;

/**
 * CAD或Office文档pdf签名
 * 
 * @author luoxiaomin
 *
 */
public class SignatureHelper implements RemoteAccess {
	private static final Logger logger = LogR.getLogger(SignatureHelper.class.getName());
	public static SimpleDateFormat DATEFORMATE = new SimpleDateFormat(
			PropertiesHelper.getStrFromProperties("默认签名日期格式"));
	private static WTProperties properties = null;

	static {
		try {
			properties = WTProperties.getLocalProperties();
		} catch (Exception t) {
			t.printStackTrace();
		}
	}

	public static String getPrimaryFileExtsion(FormatContentHolder contentholder) {
		String result = "";
		String fileName = getPrimaryFileName(contentholder);
		if (!fileName.equals("{$CAD_NAME}")) {
			result = fileName;
		} else {
			EPMDocument epm = (EPMDocument) contentholder;
			result = epm.getCADName();
		}
		if ((fileName != null) && (fileName.length() > 0) && (fileName.lastIndexOf(".") != -1))
			result = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
		return result;
	}

	public static String getPrimaryFileName(FormatContentHolder contentholder) {
		String result = "";
		try {
			ContentItem contentitem = ContentHelper.service.getPrimary(contentholder);
			ApplicationData applicationdataPrimary = null;
			if (contentitem != null) {
				applicationdataPrimary = (ApplicationData) contentitem;
				String fileName = applicationdataPrimary.getFileName();
				if (!fileName.equals("{$CAD_NAME}")) {
					result = fileName;
				} else {
					EPMDocument epm = (EPMDocument) contentholder;
					result = epm.getCADName();
				}
			}
		} catch (Exception wte) {
			wte.printStackTrace();
		}
		return result;
	}

	public static WfProcess getWfProcess(ObjectReference self) throws WTException {
		WfProcess wfp = null;
		try {
			Object obj = self.getObject();
			if ((obj instanceof WfProcess))
				wfp = (WfProcess) obj;
			if ((obj instanceof WfActivity))
				wfp = ((WfActivity) obj).getParentProcess();
			if ((obj instanceof WfBlock))
				wfp = ((WfBlock) obj).getParentProcess();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return wfp;
	}

	public static HashMap getSignInfo(WfProcess wfprocess) throws WTException {
		HashMap signMap = new HashMap();
		List voteInfos = getVoteInfos(wfprocess);

		for (int i = 0; i < voteInfos.size(); i++) {
			HashMap map = (HashMap) voteInfos.get(i);
			String[] signInfo = new String[4];
			signInfo[0] = ((String) map.get("user"));
			signInfo[1] = ((String) map.get("date"));
			signInfo[2] = ((String) map.get("name"));
			signInfo[3] = ((String) map.get("comment"));
			String actName = (String) map.get("activityName");
			Vector vec = (Vector) signMap.get(actName);
			if (vec == null) {
				vec = new Vector();
				vec.addElement(signInfo);
				signMap.put(actName, vec);
			} else {
				vec.addElement(signInfo);
				signMap.put(actName, vec);
			}
		}

		return signMap;
	}

	public static List getVoteInfos(WfProcess process) {
		List voteInfos = new ArrayList();
		try {
			HashMap map;
			for (QueryResult qr = NmWorkflowHelper.service.getVotingEventsForProcess(process); qr
					.hasMoreElements(); voteInfos.add(map)) {
				WfVotingEventAudit wfvotingeventaudit = (WfVotingEventAudit) qr.nextElement();
				String activityName = wfvotingeventaudit.getActivityName();
				String vote = wfvotingeventaudit.getEventList().toString();
				String nonEffectiveRoute = PropertiesHelper.getStrFromProperties("NONEFFECTIVEROUTE");
				if ((vote != null) && (nonEffectiveRoute.contains(vote))) {
					removeLastVoteInfo(voteInfos, "activityName", activityName);
					break;
				}
				WTPrincipalReference wtprincipalreference = wfvotingeventaudit.getUserRef();
				WTPrincipal wtprincipal = (WTPrincipal) wtprincipalreference.getObject();
				String userName = "";
				String name = "";
				if ((wtprincipal instanceof WTUser)) {
					userName = ((WTUser) wtprincipal).getFullName();
					// name = ((WTUser) wtprincipal).getName();
					name = ((WTUser) wtprincipal).getName();
					System.out.println("qqqqqqqqqqqqq" + name);
				} else if ((wtprincipal instanceof WTGroup)) {
					userName = ContainerTeamHelper.getDisplayName((WTGroup) wtprincipal, null);
				}
				Timestamp timestamp = wfvotingeventaudit.getTimestamp();
				String date = DATEFORMATE.format(timestamp);
				map = new HashMap();
				map.put("activityName", activityName);
				map.put("date", date);
				map.put("user", userName.replace(",", "").trim());
				map.put("name", name);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return voteInfos;
	}

	private static ObjectIdentifier getOid(Object obj) {
		if (obj == null)
			return null;
		if ((obj instanceof ObjectReference)) {
			return (ObjectIdentifier) ((ObjectReference) obj).getKey();
		}
		return PersistenceHelper.getObjectIdentifier((Persistable) obj);
	}

	private static void removeLastVoteInfo(List voteInfos, String key, String value) {
		for (int i = voteInfos.size() - 1; i >= 0; i--) {
			HashMap map = (HashMap) voteInfos.get(i);
			String activityName = (String) map.get(key);
			if ((value == null) || (!value.equalsIgnoreCase(activityName)))
				break;
			voteInfos.remove(i);
		}
	}

	public static List getVoteInfos(List list, WfProcess process) {
		try {
			for (QueryResult qr = NmWorkflowHelper.service.getVotingEventsForProcess(process); qr.hasMoreElements();) {
				WfVotingEventAudit wfvotingeventaudit = (WfVotingEventAudit) qr.nextElement();
				String activityName = wfvotingeventaudit.getActivityName();
				if (!activityName.equals("����"))
					continue;
				WTPrincipalReference wtprincipalreference = wfvotingeventaudit.getUserRef();
				WTPrincipal wtprincipal = (WTPrincipal) wtprincipalreference.getObject();
				String userName = null;
				String name = null;
				if ((wtprincipal instanceof WTUser)) {
					userName = ((WTUser) wtprincipal).getFullName();
					name = ((WTUser) wtprincipal).getName();
				} else if ((wtprincipal instanceof WTGroup)) {
					userName = ContainerTeamHelper.getDisplayName((WTGroup) wtprincipal, null);
				}
				Timestamp timestamp = wfvotingeventaudit.getTimestamp();
				String date = DATEFORMATE.format(timestamp);
				HashMap map = new HashMap();
				map.put("activityName", activityName);
				map.put("date", date);
				map.put("user", userName.replace(",", "").trim());
				map.put("name", name);
				list.add(map);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public static boolean isHaveActivity(List list) {
		for (int i = 0; i < list.size(); i++) {
			HashMap map = (HashMap) list.get(i);
			String name = (String) map.get("activityName");
			if (name.equals("����")) {
				return true;
			}
		}
		return false;
	}

	public static ArrayList<String> mapSheet(PdfReader reader, int totalpage) {
		ArrayList alist = new ArrayList();
		try {
			for (int i = 1; i < totalpage + 1; i++) {
				Rectangle rect = reader.getPageSize(i);
				// float width = Math.max(rect.getWidth(), rect.getHeight());
				float width = rect.getWidth();
				String[] docTufu = PropertiesHelper.getStrFromProperties("DOC_TUFU").split(";");
				String tufu = "";
				for (int j = 0; j < docTufu.length; j++) {
					String[] docTufuArray = docTufu[j].split(",");
					if ((Float.parseFloat(docTufuArray[1]) < width) && (width < Float.parseFloat(docTufuArray[2]))) {
						tufu = docTufuArray[0];
					}
				}
				alist.add(tufu);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return alist;
	}

	public static String jgpExists(String name) {
		String picturePath = properties.getProperty("wt.codebase.location")
				+ PropertiesHelper.getStrFromProperties("PICTURE_PATH") + name + ".jpg";
		File file = new File(picturePath);
		if (file.exists()) {
			return picturePath;
		}
		return "";
	}

	public static void writeIndividuationImage(String imagePath, PdfContentByte cb, String keyPrefix, String docType) {
		String xy = PropertiesHelper.getStrFromProperties(keyPrefix + ".X+Y");
		if (xy == null) {
			System.out.println(
					"003-signature : no [" + keyPrefix + "] config in signature.properties, no signature content.");
			return;
		}
		try {
			cb.endText();
			String[] xyArray = xy.split(",");
			int positionX = Integer.parseInt(xyArray[0]);
			int positionY = Integer.parseInt(xyArray[1]);
			Image jpeg = Image.getInstance(imagePath);
			String defaultImageSize = PropertiesHelper.getStrFromProperties(docType + ".默认个性化图片签名大小");
			int imageWidth = Integer.parseInt(defaultImageSize.split(",")[0]);
			int imageHeight = Integer.parseInt(defaultImageSize.split(",")[1]);
			jpeg.scaleAbsolute(imageWidth, imageHeight);
			jpeg.setAbsolutePosition(positionX, positionY);
			cb.addImage(jpeg);
			cb.beginText();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void writeStringContentToPDF(String content, PdfContentByte cb, String keyPrefix, BaseFont bf,
			float fontSize, String docType) {
		String xy = PropertiesHelper.getStrFromProperties(keyPrefix + ".X+Y");
		if (xy == null) {
			System.out.println(
					"003-signature : no [" + keyPrefix + "] config in signature.properties, no signature content.");
			return;
		}
		String[] xyArray = xy.split(",");
		int positionX = Integer.parseInt(xyArray[0]);
		int positionY = Integer.parseInt(xyArray[1]);
		cb.setFontAndSize(bf, fontSize);
		/*
		 * if(docType.contains("图纸")){ String defaultImageSize =
		 * PropertiesHelper.getStrFromProperties(docType+".默认个性化图片签名大小"); int imageWidth
		 * = Integer.parseInt(defaultImageSize.split(",")[0]); //增加相对签名图片的偏移量
		 * cb.showTextAligned(0, content, positionX + imageWidth + 5, positionY + 3,
		 * 0.0F); }else if(docType.contains("文档")){ //去掉相对图片签名位置的偏移量
		 * cb.showTextAligned(0, content, positionX, positionY, 0.0F); }
		 */
		cb.showTextAligned(0, content, positionX, positionY, 0.0F);
	}

	public static void writeOtherImage(String imagePath, PdfContentByte cb, String keyPrefix, String defaultImageSize) {
		String xy = PropertiesHelper.getStrFromProperties(keyPrefix + ".X+Y");
		if (xy == null) {
			System.out.println(
					"003-signature : no [" + keyPrefix + "] config in signature.properties, no signature content.");
			return;
		}
		try {
			cb.endText();
			String[] xyArray = xy.split(",");
			int positionX = Integer.parseInt(xyArray[0]);
			int positionY = Integer.parseInt(xyArray[1]);
			Image jpeg = Image.getInstance(imagePath);
			int imageWidth = Integer.parseInt(defaultImageSize.split(",")[0]);
			int imageHeight = Integer.parseInt(defaultImageSize.split(",")[1]);
			jpeg.scaleAbsolute(imageWidth, imageHeight);
			jpeg.setAbsolutePosition(positionX, positionY);
			cb.addImage(jpeg);
			cb.beginText();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}