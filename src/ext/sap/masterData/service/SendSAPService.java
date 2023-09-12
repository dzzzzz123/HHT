package ext.sap.masterData.service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

import com.google.gson.Gson;

import ext.ait.util.IBAUtil;
import ext.ait.util.PartUtil;
import ext.sap.masterData.config.SAPConfig;
import ext.sap.masterData.entity.SendSAPPartEntity;
import ext.sinoboom.ppmService.util.CommUtil;
import wt.part.WTPart;
import wt.pom.WTConnection;

public class SendSAPService {

	public static String SendSAPPart(WTPart wtPart) throws Exception {
		IBAUtil ibaUtil = new IBAUtil(wtPart);
		String ibaAttributes = SAPConfig.getConfig("IBAAttributes");
		SendSAPPartEntity sapPartEntity = new SendSAPPartEntity();

		String HHT_ClassificationCode = ibaUtil.getIBAValue("HHT_ClassificationCode");

		boolean HHT_INValue = ibaUtil.getBooleanValueByName("HHT_INValue").isValue();

		String PartType = mapClassificationToPartType(HHT_ClassificationCode, HHT_INValue);

		String source = wtPart.getSource().getDisplay();
		String HHT_Classification = getHHT_Classification(HHT_ClassificationCode, source);
		String number = wtPart.getNumber();
		String name = wtPart.getName();

		String HHT_LongtDescription = ibaUtil.getIBAValue("HHT_LongtDescription");

		String version = PartUtil.getVersion(wtPart);
		String unit = PartUtil.getUnit(wtPart);

		boolean HHT_Bonded = ibaUtil.getBooleanValueByName("HHT_Bonded").isValue();

		String HHT_GrossWeight = ibaUtil.getIBAValue("HHT_GrossWeight");
		String HHT_NetWeight = ibaUtil.getIBAValue("HHT_NetWeight");
		String HHT_WeightUnit = ibaUtil.getIBAValue("HHT_WeightUnit");
		String HHT_Traffic = ibaUtil.getIBAValue("HHT_Traffic");
		String HHT_VolumeUnit = ibaUtil.getIBAValue("HHT_VolumeUnit");
		String HHT_Length = ibaUtil.getIBAValue("HHT_Length");
		String HHT_Width = ibaUtil.getIBAValue("HHT_Width");
		String HHT_Height = ibaUtil.getIBAValue("HHT_Height");
		String HHT_SizeUnits = ibaUtil.getIBAValue("HHT_SizeUnits");

		String state = wtPart.getState().getState().getDisplay();

		String HHT_ClassificationName = getClassificationdDisPlayName(HHT_ClassificationCode);
		String HHT_ProductLineNumber = ibaUtil.getIBAValue("HHT_ProductLineNumber");
		String HHT_ProductLineName = ibaUtil.getIBAValue("HHT_ProductLineName");
		String HHT_ProductNumber = ibaUtil.getIBAValue("HHT_ProductNumber");
		String HHT_Productdescription = ibaUtil.getIBAValue("HHT_Productdescription");
		String HHT_ModelSpecification = ibaUtil.getIBAValue("HHT_ModelSpecification");
		String HHT_CommodityName = ibaUtil.getIBAValue("HHT_CommodityName");
		String HHT_Brand = ibaUtil.getIBAValue("HHT_Brand");
		String HHT_Year = ibaUtil.getIBAValue("HHT_Year");
		String HHT_Size = ibaUtil.getIBAValue("HHT_Size");
		String HHT_FinishedSeries = ibaUtil.getIBAValue("HHT_FinishedSeries");
		String HHT_Industry = ibaUtil.getIBAValue("HHT_Industry");
		String HHT_ProductDevelopmentType = ibaUtil.getIBAValue("HHT_ProductDevelopmentType");
		String HHT_CustomizedProductIdentifier = ibaUtil.getIBAValue("HHT_CustomizedProductIdentifier");
		String HHT_SupplierSku = ibaUtil.getIBAValue("HHT_SupplierSku");

		String defaultTraceCode = wtPart.getDefaultTraceCode().getDisplay();

		String HHT_Factory = ibaUtil.getIBAValue("HHT_Factory");
		String HHT_Price = ibaUtil.getIBAValue("HHT_Price");
		String HHT_PriceUnit = ibaUtil.getIBAValue("HHT_PriceUnit");

		sapPartEntity.setPartType(PartType);
		sapPartEntity.setHHT_Classification(HHT_Classification);
		sapPartEntity.setNumber(number);
		sapPartEntity.setName(name);
		sapPartEntity.setHHT_Length(HHT_Length);
		sapPartEntity.setRevision(version);
		sapPartEntity.setUnit(unit);
		sapPartEntity.setHHT_Bonded(HHT_Bonded);
		sapPartEntity.setHHT_GrossWeight(HHT_GrossWeight);
		sapPartEntity.setHHT_NetWeight(HHT_NetWeight);
		sapPartEntity.setHHT_WeightUnit(HHT_WeightUnit);
		sapPartEntity.setHHT_Traffic(HHT_Traffic);
		sapPartEntity.setHHT_VolumeUnit(HHT_VolumeUnit);
		sapPartEntity.setHHT_Length(HHT_Length);
		sapPartEntity.setHHT_Width(HHT_Width);
		sapPartEntity.setHHT_Height(HHT_Height);
		sapPartEntity.setHHT_SizeUnits(HHT_SizeUnits);
		sapPartEntity.setState(state);
		sapPartEntity.setHHT_ClassificationCode(HHT_ClassificationCode);
		sapPartEntity.setHHT_ClassificationName(HHT_ClassificationName);
		sapPartEntity.setHHT_ProductLineNumber(HHT_ProductLineNumber);
		sapPartEntity.setHHT_ProductLineName(HHT_ProductLineName);
		sapPartEntity.setHHT_ProductLineNumber(HHT_ProductNumber);
		sapPartEntity.setHHT_Productdescription(HHT_Productdescription);
		sapPartEntity.setHHT_ModelSpecification(HHT_ModelSpecification);
		sapPartEntity.setHHT_CommodityName(HHT_CommodityName);
		sapPartEntity.setHHT_Brand(HHT_Brand);
		sapPartEntity.setHHT_Year(HHT_Year);
		sapPartEntity.setHHT_Size(HHT_Size);
		sapPartEntity.setHHT_FinishedSeries(HHT_FinishedSeries);
		sapPartEntity.setHHT_Industry(HHT_Industry);
		sapPartEntity.setHHT_ProductDevelopmentType(HHT_ProductDevelopmentType);
		sapPartEntity.setHHT_CustomizedProductIdentifier(HHT_CustomizedProductIdentifier);
		sapPartEntity.setHHT_SupplierSku(HHT_SupplierSku);

		String setDefaultTraceCode = null;
		if ("S".equals(defaultTraceCode)) {
			setDefaultTraceCode = "0001";
		}
		sapPartEntity.setDefaultTraceCode(setDefaultTraceCode);
		sapPartEntity.setHHT_Factory(HHT_Factory);
		sapPartEntity.setHHT_Price(HHT_Price);
		sapPartEntity.setHHT_PriceUnit(HHT_PriceUnit);
		sapPartEntity.setHHT_INValue(HHT_INValue);

		Gson gson = new Gson();
		String json = gson.toJson(sapPartEntity);
		System.out.println(json);

		return null;
	}

	/**
	 * 获取分类显示名称
	 */
	private static String getClassificationdDisPlayName(String classificationCode) throws Exception {

		String SelectQuery = "SELECT value \r\n" + "FROM LWCLOCALIZABLEPROPERTYVALUE\r\n" + "WHERE ida3b4 IN\r\n"
				+ "  (\r\n"
				+ "	  SELECT ida2a2 FROM LWCStructEnumAttTemplate WHERE name= ?  AND NAMESPACE = 'MechParts'\r\n"
				+ "  )";

		WTConnection con = CommUtil.getWTConnection();
		PreparedStatement statement = con.prepareStatement(SelectQuery);
		// 设置参数值
		statement.setString(1, classificationCode);
		ResultSet executeQuery = statement.executeQuery();

		executeQuery.next();
		String displayName = executeQuery.getString("value");

		return displayName;

	}

	/**
	 * 返回字段HHT_Classification处理逻辑
	 */
	public static String getHHT_Classification(String HHT_ClassificationCode, String source) {
		char firstChar = HHT_ClassificationCode.charAt(0);

		if (firstChar == '5') {
			if ("make".equals(source)) {
				return HHT_ClassificationCode; // 采购类型为E自制，不改变物料分类编码
			} else if ("buy".equals(source)) {
				return "6" + HHT_ClassificationCode.substring(1); // 采购类型为F外购，将首位编号改成6
			}
		}

		// 其他情况不改变物料分类编码
		return HHT_ClassificationCode;
	}

	/**
	 * PLM物料类型处理逻辑
	 */
	public static String mapClassificationToPartType(String classificationCode, boolean b) {
		char firstChar = classificationCode.charAt(0);

		if (firstChar == '1' || firstChar == '2' || firstChar == '3') {
			return "Z001";
		} else if (firstChar == '4') {
			return "Z002";
		} else if (firstChar == '5' || firstChar == '6') {
			return "Z003";
		} else if (firstChar == '7') {
			return "Z004";
		} else if (firstChar == '8') {
			return "Z005";
		} else if (classificationCode.startsWith("91") || classificationCode.startsWith("92")) {
			if (b) {
				return "Z006";
			} else {
				return "Z007";
			}
		} else if (classificationCode.startsWith("93")) {
			if (b) {
				return "Z008";
			} else {
				return "Z009";
			}
		} else if (firstChar == 'A') {
			return "Z010";
		} else {
			return "未知物料类型";
		}
	}

	/**
	 * 发送物料主数据到SAP
	 */
	private static String sendPartSAP(String partJson) {

		String url = SAPConfig.getConfig("sendPartUrl");
		PostMethod method = null;

		try {

			method = new PostMethod(url);

			HttpClient client = new HttpClient();
			method.setRequestHeader("accept", "*/*");
			method.setRequestHeader("connection", "Keep-Alive");
			method.setRequestHeader("Content-Type", "application/json; charset=UTF-8");

			// 设置为默认的重试策略
			method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
			// 设置请求参数(请求内容)

			method.setRequestBody(partJson);
			int rspCode = client.executeMethod(method);
			System.out.println(">>>getProjects rspCode>>>" + rspCode);
			StringBuffer stringBuffer = new StringBuffer();
			InputStream is = method.getResponseBodyAsStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			String line = "";
			while ((line = br.readLine()) != null) {
				stringBuffer.append(line);
			}
			String ret = stringBuffer.toString();

			return ret;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (method != null) {
				method.releaseConnection();
			}
		}

	}

}
