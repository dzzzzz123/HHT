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
import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;

import ext.ait.util.IBAUtil;
import ext.ait.util.PersistenceUtil;
import ext.sap.masterData.config.SAPConfig;
import ext.sap.masterData.entity.SendSAPPartEntity;
import wt.method.MethodContext;
import wt.part.WTPart;
import wt.pom.WTConnection;
import wt.util.WTException;

public class SendSAPService {

	public static String SendSAPPart(WTPart wtPart) {
		IBAUtil ibaUtil;
		String resultJson = null;

		try {
			String HHT_Classification = SAPConfig.getConfig("HHT_Classification");
			String HHT_LongtDescription = SAPConfig.getConfig("HHT_LongtDescription");
			String HHT_Bonded = SAPConfig.getConfig("HHT_Bonded");
			String HHT_GrossWeight = SAPConfig.getConfig("HHT_GrossWeight");
			String HHT_NetWeight = SAPConfig.getConfig("HHT_NetWeight");
			String HHT_WeightUnit = SAPConfig.getConfig("HHT_WeightUnit");
			String HHT_Traffic = SAPConfig.getConfig("HHT_Traffic");
			String HHT_VolumeUnit = SAPConfig.getConfig("HHT_VolumeUnit");
			String HHT_Length = SAPConfig.getConfig("HHT_Length");
			String HHT_Width = SAPConfig.getConfig("HHT_Width");
			String HHT_Height = SAPConfig.getConfig("HHT_Height");
			String HHT_SizeUnits = SAPConfig.getConfig("HHT_SizeUnits");
			String HHT_ClassificationCode = SAPConfig.getConfig("HHT_ClassificationCode");
			String HHT_ClassificationName = SAPConfig.getConfig("HHT_ClassificationName");
			String HHT_ProductLineNumber = SAPConfig.getConfig("HHT_ProductLineNumber");
			String HHT_ProductLineName = SAPConfig.getConfig("HHT_ProductLineName");
			String HHT_ProductNumber = SAPConfig.getConfig("HHT_ProductNumber");
			String HHT_Productdescription = SAPConfig.getConfig("HHT_Productdescription");
			String HHT_ModelSpecification = SAPConfig.getConfig("HHT_ModelSpecification");
			String HHT_CommodityName = SAPConfig.getConfig("HHT_CommodityName");
			String HHT_Brand = SAPConfig.getConfig("HHT_Brand");
			String HHT_Year = SAPConfig.getConfig("HHT_Year");
			String HHT_Size = SAPConfig.getConfig("HHT_Size");
			String HHT_FinishedSeries = SAPConfig.getConfig("HHT_FinishedSeries");
			String HHT_Industry = SAPConfig.getConfig("HHT_Industry");
			String HHT_ProductDevelopmentType = SAPConfig.getConfig("HHT_ProductDevelopmentType");
			String HHT_CustomizedProductIdentifier = SAPConfig.getConfig("HHT_CustomizedProductIdentifier");
			String HHT_SupplierSku = SAPConfig.getConfig("HHT_SupplierSku");
			String HHT_Factory = SAPConfig.getConfig("HHT_Factory");
			String HHT_Price = SAPConfig.getConfig("HHT_Price");
			String HHT_PriceUnit = SAPConfig.getConfig("HHT_PriceUnit");
			String HHT_INValue = SAPConfig.getConfig("HHT_INValue");

			ibaUtil = new IBAUtil(wtPart);
			SendSAPPartEntity sapPartEntity = new SendSAPPartEntity();

			String set_ClassificationCode = ibaUtil.getIBAValue(HHT_Classification);
			boolean set_INValue = ibaUtil.getBooleanValueByName(HHT_INValue).isValue();
			String PartType = mapClassificationToPartType(set_ClassificationCode, set_INValue);

			String source = wtPart.getSource().getDisplay();
			String set_Classification = getHHT_Classification(set_ClassificationCode, source);
			String number = wtPart.getNumber();
			String name = wtPart.getName();
			String set_LongtDescription = ibaUtil.getIBAValue(HHT_LongtDescription);

			String version_1 = wtPart.getVersionInfo().getIdentifier().getValue();
			String value_2 = wtPart.getIterationInfo().getIdentifier().getValue();
			String version = new StringBuffer().append(version_1).append(".").append(value_2).toString();

			String unit = getUnit(wtPart);

			boolean set_Bonded = ibaUtil.getBooleanValueByName(HHT_Bonded).isValue();
			String set_GrossWeight = ibaUtil.getIBAValue(HHT_GrossWeight);
			String set_NetWeight = ibaUtil.getIBAValue(HHT_NetWeight);
			String set_WeightUnit = ibaUtil.getIBAValue(HHT_WeightUnit);
			String set_Traffic = ibaUtil.getIBAValue(HHT_Traffic);
			String set_VolumeUnit = ibaUtil.getIBAValue(HHT_VolumeUnit);
			String set_Length = ibaUtil.getIBAValue(HHT_Length);
			String set_Width = ibaUtil.getIBAValue(HHT_Width);
			String set_Height = ibaUtil.getIBAValue(HHT_Height);
			String set_SizeUnits = ibaUtil.getIBAValue(HHT_SizeUnits);

			String state = wtPart.getState().getState().getDisplay();

			String set_ClassificationName = getClassificationdDisPlayName(set_ClassificationCode);
			String set_ProductLineNumber = ibaUtil.getIBAValue(HHT_ProductLineNumber);
			String set_ProductLineName = ibaUtil.getIBAValue(HHT_ProductLineName);
			String set_ProductNumber = ibaUtil.getIBAValue(HHT_ProductNumber);
			String set_Productdescription = ibaUtil.getIBAValue(HHT_Productdescription);
			String set_ModelSpecification = ibaUtil.getIBAValue(HHT_ModelSpecification);
			String set_CommodityName = ibaUtil.getIBAValue(HHT_CommodityName);
			String set_Brand = ibaUtil.getIBAValue(HHT_Brand);
			String set_Year = ibaUtil.getIBAValue(HHT_Year);
			String set_Size = ibaUtil.getIBAValue(HHT_Size);
			String set_FinishedSeries = ibaUtil.getIBAValue(HHT_FinishedSeries);
			String set_Industry = ibaUtil.getIBAValue(HHT_Industry);
			String set_ProductDevelopmentType = ibaUtil.getIBAValue(HHT_ProductDevelopmentType);
			String set_CustomizedProductIdentifier = ibaUtil.getIBAValue(HHT_CustomizedProductIdentifier);
			String set_SupplierSku = ibaUtil.getIBAValue(HHT_SupplierSku);
			String defaultTraceCode = wtPart.getDefaultTraceCode().getDisplay();

			String set_Factory = ibaUtil.getIBAValue(HHT_Factory);
			String set_Price = ibaUtil.getIBAValue(HHT_Price);
			String set_PriceUnit = ibaUtil.getIBAValue(HHT_PriceUnit);

			sapPartEntity.setPartType(PartType);
			sapPartEntity.setHHT_Classification(set_Classification);
			sapPartEntity.setNumber(number);
			sapPartEntity.setName(name);
			sapPartEntity.setHHT_Length(HHT_Length);
			sapPartEntity.setRevision(version);
			sapPartEntity.setUnit(unit);
			sapPartEntity.setHHT_Bonded(set_Bonded);
			sapPartEntity.setHHT_GrossWeight(set_GrossWeight);
			sapPartEntity.setHHT_NetWeight(set_NetWeight);
			sapPartEntity.setHHT_WeightUnit(set_WeightUnit);
			sapPartEntity.setHHT_Traffic(set_Traffic);
			sapPartEntity.setHHT_VolumeUnit(set_VolumeUnit);
			sapPartEntity.setHHT_Length(set_Length);
			sapPartEntity.setHHT_Width(set_Width);
			sapPartEntity.setHHT_Height(set_Height);
			sapPartEntity.setHHT_SizeUnits(set_SizeUnits);
			sapPartEntity.setState(state);
			sapPartEntity.setHHT_ClassificationCode(set_ClassificationCode);
			sapPartEntity.setHHT_ClassificationName(set_ClassificationName);
			sapPartEntity.setHHT_ProductLineNumber(set_ProductLineNumber);
			sapPartEntity.setHHT_ProductLineName(set_ProductLineName);
			sapPartEntity.setHHT_ProductLineNumber(set_ProductNumber);
			sapPartEntity.setHHT_Productdescription(set_Productdescription);
			sapPartEntity.setHHT_ModelSpecification(set_ModelSpecification);
			sapPartEntity.setHHT_CommodityName(set_CommodityName);
			sapPartEntity.setHHT_Brand(set_Brand);
			sapPartEntity.setHHT_Year(set_Year);
			sapPartEntity.setHHT_Size(set_Size);
			sapPartEntity.setHHT_FinishedSeries(set_FinishedSeries);
			sapPartEntity.setHHT_Industry(set_Industry);
			sapPartEntity.setHHT_ProductDevelopmentType(set_ProductDevelopmentType);
			sapPartEntity.setHHT_CustomizedProductIdentifier(set_CustomizedProductIdentifier);
			sapPartEntity.setHHT_SupplierSku(set_SupplierSku);

			String DefaultTraceCode = null;
			if ("S".equals(defaultTraceCode)) {
				DefaultTraceCode = "0001";
			} else {
				DefaultTraceCode = "";
			}
			sapPartEntity.setDefaultTraceCode(DefaultTraceCode);
			sapPartEntity.setHHT_Factory(set_Factory);
			sapPartEntity.setHHT_Price(set_Price);
			sapPartEntity.setHHT_PriceUnit(set_PriceUnit);
			sapPartEntity.setHHT_INValue(set_INValue);

			Gson gson = new Gson();
			String json = gson.toJson(sapPartEntity);
			System.out.println(json);
			resultJson = sendPartSAP(json);
			System.out.println(resultJson);
		} catch (WTException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultJson;
	}

	/**
	 * 获取分类显示名称
	 */
	private static String getClassificationdDisPlayName(String classificationCode) throws Exception {

		String namenameSpaceSpace = SAPConfig.getConfig("classificationNamespace");

		String SelectQuery = "SELECT value \r\n" + "FROM LWCLOCALIZABLEPROPERTYVALUE\r\n" + "WHERE ida3b4 IN\r\n"
				+ "  (\r\n" + "	  SELECT ida2a2 FROM LWCStructEnumAttTemplate WHERE name= ?  AND NAMESPACE = '"
				+ namenameSpaceSpace + "'\r\n" + "  )";

		MethodContext methodcontext = MethodContext.getContext();
		WTConnection con = (WTConnection) methodcontext.getConnection();
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

	/**
	 * 获取部件的单位
	 * 
	 * @param WTPart
	 * @return String
	 * @throws WTException
	 */
	public static String getUnit(WTPart part) throws WTException {

		if (part == null) {
			return "";
		}
		String defaultUnit = part.getDefaultUnit().toString().toUpperCase();// 默认单位
		IBAUtil ibautil = new IBAUtil(part);
		String jldw = ibautil.getIBAValue("net.haige.jldw");// 计量单位
		String P_UNIT = ibautil.getIBAValue("net.haige.P_UNIT");// 结构件单位
		String typeName = PersistenceUtil.getTypeName(part);
		if (StringUtils.equalsIgnoreCase(typeName, "com.ptc.ElectricalPart")) {// 电子元器件
			if (StringUtils.isNotBlank(jldw)) {
				return jldw.toUpperCase();
			} else {
				return defaultUnit;
			}
		} else if (StringUtils.equalsIgnoreCase(typeName, "Part")
				|| StringUtils.equalsIgnoreCase(typeName, "wt.part.WTPart")) {
			if (StringUtils.isNotBlank(P_UNIT)) {
				return P_UNIT.toUpperCase();
			} else {
				return defaultUnit;
			}
		}
		if (StringUtils.isNotBlank(jldw)) {
			return jldw.toUpperCase();
		} else if (StringUtils.isNotBlank(P_UNIT)) {
			return P_UNIT.toUpperCase();
		} else {
			return defaultUnit;
		}
	}

}
