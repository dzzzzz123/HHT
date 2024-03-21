package ext.HHT.SRM.acknowledgment;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ext.HHT.Config;
import ext.HHT.SRM.acknowledgment.Acknowledgment.Body;
import ext.HHT.SRM.acknowledgment.Acknowledgment.Header;
import ext.ait.util.CommonUtil;
import ext.ait.util.DocumentUtil;
import ext.ait.util.PersistenceUtil;
import ext.ait.util.VersionUtil;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.content.ContentItem;
import wt.content.ContentServerHelper;
import wt.content.FormatContentHolder;
import wt.doc.WTDocument;
import wt.fc.WTObject;
import wt.part.WTPart;
import wt.query.QueryException;
import wt.util.WTException;

public class Service {

	public static List<String> process(WTObject pbo) {
		List<WTPart> list = CommonUtil.getListFromPBO(pbo, WTPart.class);
		List<String> msg = new ArrayList<>();
		String resultJson = Service.requestAcknowledgment(list);
		msg.add(getResultFromJson(resultJson));
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
			body.setItemVersion(StringUtils.substring(VersionUtil.getVersion(part), 0, 1));

			WTDocument document = getAcknowledgmentDoc(part);
			body.setAttachmentVersion(StringUtils.substring(VersionUtil.getVersion(document), 0, 1));
			File file = getAcknowledgmentFile(document);
			String uuid = requestUUid();
			String netPath = requestMultipart(file, uuid);
			body.setAttachmentUuid(uuid);
			String HHT_SupplierDisplay = Config.getHHT_Supplier(document);
			String HHT_SupplierInternal = getSupplier(HHT_SupplierDisplay);
			body.setSupplierCompanyCode(HHT_SupplierInternal);
			body.setSupplierCompanyName(HHT_SupplierDisplay);
			bodys.add(body);
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
		HashMap<String, String> headers = new HashMap<>() {
			{
				put("Authorization", "Bearer " + requestSRMToken());
			}
		};
		return CommonUtil.requestInterface(Config.getUUidUrl(), "POST", null, headers);
	}

	/**
	 * 上传文件到SRM系统
	 * 
	 * @param file
	 * @param uuid
	 * @return
	 */
	public static String requestMultipart(File file, String uuid) {
		HashMap<String, String> formData = new HashMap<>() {
			{
				put("attachmentUUID", uuid);
				put("bucketName", Config.getBucketName());
				put("directory", Config.getDirectory());
				put("fileName", file.getName());
			}
		};
		return uploadFile(Config.getMultipartUrl(), file, formData);
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
			String type = PersistenceUtil.getSubTypeInternal(doc);
			String docType = doc.getDocType().toString();
//			if (type.equals(ackTypeName) && docType.equals(ackDocType)) {
			if (type.equals(ackTypeName)) {
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
			ContentHolder contentHolder = ContentHelper.service.getContents(doc);
			FormatContentHolder formatContentHolder = (FormatContentHolder) contentHolder;
			ContentItem ci = ContentHelper.getPrimary(formatContentHolder);
			ApplicationData applicationData = (ApplicationData) ci;
			file = new File(file, applicationData.getFileName());
			ContentServerHelper.service.writeContentStream((ApplicationData) ci, file.getCanonicalPath());
		} catch (QueryException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		return file;
	}

	public static String uploadFile(String apiUrl, File file, Map<String, String> additionalParams) {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + requestSRMToken());
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
		body.add("file", new FileSystemResource(file));

		if (additionalParams != null) {
			for (Map.Entry<String, String> entry : additionalParams.entrySet()) {
				body.add(entry.getKey(), entry.getValue());
			}
		}

		HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> responseEntity = restTemplate.exchange(apiUrl, HttpMethod.POST, requestEntity,
				String.class);
		// ResponseEntity<String> responseEntity = restTemplate.postForEntity(apiUrl,
		// requestEntity, String.class);

		System.out.println("Response code: " + responseEntity.getStatusCode());
		System.out.println("Response body: " + responseEntity.getBody());
		return responseEntity.getBody().toString();
	}

	public static String getSupplier(String HHT_SupplierDisplay) {
		String HHT_SupplierInternal = "";
		String sql = "SELECT INTERNALNAME FROM CUS_SUPPLIER WHERE DISPLAYNAME = ?";
		ResultSet resultSet = CommonUtil.excuteSelect(sql, HHT_SupplierDisplay);
		try {
			while (resultSet.next()) {
				HHT_SupplierInternal = resultSet.getString("INTERNALNAME");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return HHT_SupplierInternal;
	}

	/**
	 * 从SRM返回的信息中获取需要的信息
	 * 
	 * @param json
	 * @return
	 */
	public static String getResultFromJson(String json) {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			JsonNode rootNode = objectMapper.readTree(json);
			if (rootNode != null && rootNode.has("responseStatus")) {
				String typeValue = rootNode.get("responseStatus").asText();
				String msgValue = rootNode.get("responseMessage").asText();
				if ("ERROR".equals(typeValue)) {
					return "发送失败！" + msgValue;
				}
			} else {
				return "发送失败！";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
