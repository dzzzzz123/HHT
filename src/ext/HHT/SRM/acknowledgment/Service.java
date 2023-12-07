package ext.HHT.SRM.acknowledgment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ext.HHT.Config;
import ext.HHT.SRM.acknowledgment.Acknowledgment.Body;
import ext.HHT.SRM.acknowledgment.Acknowledgment.Header;
import ext.ait.util.CommonUtil;
import ext.ait.util.DocumentUtil;
import ext.ait.util.PersistenceUtil;
import ext.ait.util.VersionUtil;
import wt.content.ApplicationData;
import wt.content.ContentItem;
import wt.content.ContentServerHelper;
import wt.doc.WTDocument;
import wt.fc.WTObject;
import wt.part.WTPart;
import wt.query.QueryException;
import wt.util.WTException;

public class Service {

	public static List<String> process(WTObject pbo) {
		List<WTPart> list = CommonUtil.getListFromPBO(pbo, WTPart.class);
		List<String> msg = new ArrayList<>();
		Service.requestAcknowledgment(list);
		return msg;
	}

	/**
	 * 发送承认书给SRM
	 * 
	 * @param parts
	 * @return
	 */
	public static String requestAcknowledgment(List<WTPart> parts) {
		Acknowledgment acknowledgment = getAcknowledgment(parts);
		String json = CommonUtil.getJsonFromObject(acknowledgment);
		HashMap<String, String> headers = new HashMap<>() {
			{
				put("Authorization", "Bearer " + requestSRMToken());
			}
		};
		return CommonUtil.requestInterface(Config.getAcknowLedgmentUrl(), "", "", json, "POST", headers);
	}

	/**
	 * 从部件列表中获取需要发送给SRM的JSON
	 * 
	 * @param parts
	 * @return
	 */
	public static Acknowledgment getAcknowledgment(List<WTPart> parts) {
		Acknowledgment acknowledgment = new Acknowledgment();
		Header header = acknowledgment.new Header();
		header.setApplicationCode(Config.getApplicationCode());
		header.setApplicationGroupCode(Config.getApplicationGroupCode());
		header.setBatchNum(String.valueOf(System.currentTimeMillis()));
		header.setExternalSystemCode(Config.getExternalSystemCode());
		header.setInterfaceCode(Config.getInterfaceCode());
		ArrayList<Body> bodys = new ArrayList<>();
		for (WTPart part : parts) {
			Body body = acknowledgment.new Body();
			body.setItemCode(part.getNumber());
			body.setItemName(part.getName());
			body.setItemVersion(VersionUtil.getVersion(part));

			WTDocument document = getAcknowledgmentDoc(part);
			body.setAttachmentVersion(VersionUtil.getVersion(document));
			File file = getAcknowledgmentFile(document);
			String uuid = requestUUid();
			String netPath = requestMultipart(file, uuid);
			body.setAttachmentUuid(uuid);
		}
		acknowledgment.setHeader(header);
		acknowledgment.setBody(bodys);
		return acknowledgment;
	}

	/**
	 * 获取SRM TOKEN
	 * 
	 * @return
	 */
	public static String requestSRMToken() {
		HashMap<String, Object> formData = new HashMap<>() {
			{
				put("grant_type", Config.getGrant_type());
				put("client_id", Config.getClient_id());
				put("client_secret", Config.getClient_secret());
				put("scope", Config.getScope());
			}
		};
		String jsonResult = CommonUtil.requestInterface(Config.getTokenUrl(), "POST", formData, null);
		return CommonUtil.getEntitiesFromJson(jsonResult, String.class, "access_token").get(0);
	}

	/**
	 * 获取SRM文件服务器uuid
	 * 
	 * @return
	 */
	public static String requestUUid() {
		return requestBase(Config.getUUidUrl(), null);
	}

	/**
	 * 上传文件到SRM系统
	 * 
	 * @param file
	 * @param uuid
	 * @return
	 */
	public static String requestMultipart(File file, String uuid) {
		HashMap<String, Object> formData = new HashMap<>() {
			{
				put("attachmentUUID", uuid);
				put("bucketName", Config.getBucketName());
				put("directory", Config.getDirectory());
				put("file", file);
				put("fileName", file.getName());
			}
		};
		return requestBase(Config.getMultipartUrl(), formData);
	}

	/**
	 * 根据部件获取其相关联的说明方文档对象
	 * 
	 * @param part
	 * @return
	 */
	public static WTDocument getAcknowledgmentDoc(WTPart part) {
		List<WTDocument> docList = DocumentUtil.getDescOrRefByPart(part, "Described");
		String ackTypeName = Config.getAcknowLedgmentTypeName();
		String ackDocType = Config.getAcknowLedgmentDocType();
		for (WTDocument doc : docList) {
			String type = PersistenceUtil.getTypeName(doc);
			String docType = doc.getDocType().toString();
			if (type.equals(ackTypeName) && docType.equals(ackDocType)) {
				return doc;
			}
		}
		return null;
	}

	/**
	 * 获取文档其中主要内容的文件
	 * 
	 * @param doc
	 * @return
	 */
	public static File getAcknowledgmentFile(WTDocument doc) {
		File file = new File("/opt/acknowledgment/");
		try {
			ContentItem ci = doc.getPrimary();
			ApplicationData applicationData = (ApplicationData) ci;
			file = new File(file, applicationData.getFileName());
			ContentServerHelper.service.writeContentStream((ApplicationData) ci, file.getCanonicalPath());
		} catch (QueryException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return file;
	}

	/**
	 * 访问SRM通用访问接口
	 * 
	 * @param url
	 * @param formData
	 * @return
	 */
	private static String requestBase(String url, HashMap<String, Object> formData) {
		HashMap<String, String> headers = new HashMap<>() {
			{
				put("Authorization", "Bearer " + requestSRMToken());
			}
		};
		return CommonUtil.requestInterface(url, "POST", formData, headers);
	}

}
