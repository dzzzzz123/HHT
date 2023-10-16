package ext.sap.masterData;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ext.ait.util.ClassificationUtil;
import ext.ait.util.CommonUtil;
import ext.ait.util.PropertiesUtil;
import ext.ait.util.VersionUtil;
import wt.part.WTPart;

public class SendSAPService {

	private static PropertiesUtil pUtil = PropertiesUtil.getInstance("config.properties");

	public static SendSAPPartEntity SendSAPPart(WTPart part) {
		SendSAPPartEntity sapPartEntity = new SendSAPPartEntity();
		String HHT_Bonded = pUtil.getValueByKey(part, "HHT_Bonded");
		String HHT_GrossWeight = pUtil.getValueByKey(part, "HHT_GrossWeight");
		String HHT_NetWeight = pUtil.getValueByKey(part, "HHT_NetWeight");
		String HHT_WeightUnit = pUtil.getValueByKey(part, "HHT_WeightUnit");
		String HHT_Traffic = pUtil.getValueByKey(part, "HHT_Traffic");
		String HHT_VolumeUnit = pUtil.getValueByKey(part, "HHT_VolumeUnit");
		String HHT_Length = pUtil.getValueByKey(part, "HHT_Length");
		String HHT_Width = pUtil.getValueByKey(part, "HHT_Width");
		String HHT_Height = pUtil.getValueByKey(part, "HHT_Height");
		String HHT_SizeUnits = pUtil.getValueByKey(part, "HHT_SizeUnits");
		String HHT_ClassificationCode = ClassificationUtil.getClassificationInternal(part,
				pUtil.getValueByKey("HHT_Classification"));
		String HHT_ProductLineNumber = pUtil.getValueByKey(part, "HHT_ProductLineNumber");
		String HHT_ProductLineName = pUtil.getValueByKey(part, "HHT_ProductLineName");
		String HHT_ProductNumber = pUtil.getValueByKey(part, "HHT_ProductNumber");
		String HHT_Productdescription = pUtil.getValueByKey(part, "HHT_Productdescription");
		String HHT_ModelSpecification = pUtil.getValueByKey(part, "HHT_ModelSpecification");
		String HHT_CommodityName = pUtil.getValueByKey(part, "HHT_CommodityName");
		String HHT_Brand = pUtil.getValueByKey(part, "HHT_Brand");
		String HHT_Year = pUtil.getValueByKey(part, "HHT_Year");
		String HHT_Size = pUtil.getValueByKey(part, "HHT_Size");
		String HHT_FinishedSeries = pUtil.getValueByKey(part, "HHT_FinishedSeries");
		String HHT_Industry = pUtil.getValueByKey(part, "HHT_Industry");
		String HHT_ProductDevelopmentType = pUtil.getValueByKey(part, "HHT_ProductDevelopmentType");
		String HHT_CustomizedProductIdentifier = pUtil.getValueByKey(part, "HHT_CustomizedProductIdentifier");
		String HHT_SupplierSku = pUtil.getValueByKey(part, "HHT_SupplierSku");
		String HHT_Factory = pUtil.getValueByKey(part, "HHT_Factory");
		String HHT_Price = pUtil.getValueByKey(part, "HHT_Price");
		String HHT_PriceUnit = pUtil.getValueByKey(part, "HHT_PriceUnit");
		String HHT_INValue = pUtil.getValueByKey(part, "HHT_INValue");

		String source = part.getSource().getDisplay();
		String number = part.getNumber();
		String name = part.getName();
		String version = VersionUtil.getVersion(part);
		String unit = part.getDefaultUnit().getDisplay();
		String state = part.getState().getState().getDisplay();
		String defaultTraceCode = part.getDefaultTraceCode().getDisplay();

		boolean HHT_INValueBoolean = HHT_INValue.equals("是") ? true : false;
		String HHT_Classification = getClassificaiton(HHT_ClassificationCode, source);
		String PartType = mapClassificationToPartType(HHT_ClassificationCode, HHT_INValueBoolean);
		String HHT_ClassificationName = ClassificationUtil.getClassificationdDisPlayName(HHT_ClassificationCode);
		state = getSAPState(state);
		HHT_Bonded = HHT_Bonded.equals("是") ? "Y" : "N";

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
		sapPartEntity.setDefaultTraceCode(defaultTraceCode);
		sapPartEntity.setHHT_Factory(HHT_Factory);
		sapPartEntity.setHHT_Price(HHT_Price);
		sapPartEntity.setHHT_PriceUnit(HHT_PriceUnit);
		sapPartEntity.setHHT_INValue(HHT_INValue);
		sapPartEntity.setHHT_ProductNumber(HHT_ProductNumber);

		System.out.println("sapPartEntity" + sapPartEntity);

		return sapPartEntity;
	}

	/**
	 * 获取SAP生命周期状态的显示名称
	 * 
	 * @param state PLM生命周期状态
	 * @return String SAP生命周期状态
	 */
	private static String getSAPState(String state) {
		switch (state) {
		case "EVT":
			return "EV";
		case "DVT":
			return "DV";
		case "PVT":
			return "D1";
		case "MP":
			return "M1";
		case "废弃":
			return "Z1";
		default:
			return state;
		}
	}

	private static String getClassificaiton(String classificationCode, String source) {
		String buy = pUtil.getValueByKey("BUY");
		if (classificationCode.startsWith("5") && source.equals(buy)) {
			return "6" + classificationCode.substring(1);
		}
		return classificationCode;
	}

	/**
	 * PLM处理物料类型
	 * 
	 * @param classificationCode 分类编码
	 * @param inValue            是否有价值
	 * @return 物料类型
	 */
	public static String mapClassificationToPartType(String classificationCode, boolean inValue) {
		char firstChar = classificationCode.charAt(0);
		char secondChar = classificationCode.charAt(1);
		switch (firstChar) {
		case '1':
			return "Z001";
		case '2':
			return "Z001";
		case '3':
			return "Z001";
		case '4':
			return "Z002";
		case '5':
			return "Z003";
		case '6':
			return "Z003";
		case '7':
			return "Z004";
		case '8':
			return "Z005";
		case '9':
			switch (secondChar) {
			case '1':
				return inValue ? "Z006" : "Z007";
			case '2':
				return inValue ? "Z006" : "Z007";
			case '3':
				return inValue ? "Z008" : "Z009";
			default:
				return "未知物料类型";
			}
		case 'A':
			return "Z010";
		default:
			return "未知物料类型";
		}
	}

	/**
	 * 将实体类转换为发送给SAP的json
	 * 
	 * @param entity 物料主数据实体类
	 * @return String json格式数据
	 */
	public static String entityToJson(SendSAPPartEntity entity) {
		ObjectMapper objectMapper = new ObjectMapper();
		Map<String, Object> rootMap = new HashMap<>();
		Map<String, Object> isMatnrMap = new HashMap<>();

		isMatnrMap.put("MTART", entity.getPartType());
		isMatnrMap.put("MATKL", entity.getHHT_Classification());
		isMatnrMap.put("MATNR", entity.getNumber());
		isMatnrMap.put("MAKTX", entity.getName());
		isMatnrMap.put("ZEIVR", entity.getRevision());
		isMatnrMap.put("MEINS", entity.getUnit());
		isMatnrMap.put("BRGEW", entity.getHHT_GrossWeight());
		isMatnrMap.put("NTGEW", entity.getHHT_NetWeight());
		isMatnrMap.put("GEWEI", entity.getHHT_WeightUnit());
		isMatnrMap.put("VOLUM", entity.getHHT_Traffic());
		isMatnrMap.put("VOLEH", entity.getHHT_VolumeUnit());
		isMatnrMap.put("LAENG", entity.getHHT_Length());
		isMatnrMap.put("BREIT", entity.getHHT_Width());
		isMatnrMap.put("HOEHE", entity.getHHT_Height());
		isMatnrMap.put("MEABM", entity.getHHT_SizeUnits());
		isMatnrMap.put("MSTAE", entity.getState());
		isMatnrMap.put("ZZWLFLBM", entity.getHHT_ClassificationCode());
		isMatnrMap.put("ZZWLFLMC", entity.getHHT_ClassificationName());
		isMatnrMap.put("ZZCPXBM", entity.getHHT_ProductLineNumber());
		isMatnrMap.put("ZZCPXMC", entity.getHHT_ProductLineName());
		isMatnrMap.put("ZZCPBM", entity.getHHT_ProductNumber());
		isMatnrMap.put("ZZCPMS", entity.getHHT_Productdescription());
		isMatnrMap.put("ZZCPXH", entity.getHHT_ModelSpecification());
		isMatnrMap.put("ZZHPMC", entity.getHHT_CommodityName());
		isMatnrMap.put("ZZPP", entity.getHHT_Brand());
		isMatnrMap.put("ZZNF", entity.getHHT_Year());
		isMatnrMap.put("ZZCC", entity.getHHT_Size());
		isMatnrMap.put("ZZCPXL", entity.getHHT_FinishedSeries());
		isMatnrMap.put("ZZHY", entity.getHHT_Industry());
		isMatnrMap.put("ZZCPKFLX", entity.getHHT_ProductDevelopmentType());
		isMatnrMap.put("ZZDZCPBS", entity.getHHT_CustomizedProductIdentifier());
		isMatnrMap.put("ZZGYSHH", entity.getHHT_SupplierSku());
		isMatnrMap.put("SERNP", entity.getDefaultTraceCode());
		isMatnrMap.put("ZZCD", entity.getHHT_Factory());
		isMatnrMap.put("ZZJG", entity.getHHT_Price());
		isMatnrMap.put("PEINH", entity.getHHT_PriceUnit());
		isMatnrMap.put("INValue", entity.getHHT_INValue());

		rootMap.put("IS_MATNR", isMatnrMap);
		try {
			return objectMapper.writeValueAsString(rootMap);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 发送物料主数据json到SAP的发送代码
	 * 
	 * @param masterDataJson
	 * @return
	 */
	public static String sendPartSAP(String masterDataJson) {

		String url = pUtil.getValueByKey("sap.url");
		String username = pUtil.getValueByKey("sap.username");
		String password = pUtil.getValueByKey("sap.password");

		return CommonUtil.requestInterface(url, username, password, masterDataJson, "POST");
	}

	public static String getResultFromJson(String json) {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			JsonNode rootNode = objectMapper.readTree(json);
			JsonNode esMessgNode = rootNode.get("ES_MESSG");
			if (esMessgNode != null && esMessgNode.has("TYPE")) {
				String typeValue = esMessgNode.get("TYPE").asText();
				String msgValue = esMessgNode.get("MESSG").asText();
				if ("E".equals(typeValue)) {
					return "发送失败！" + msgValue;
				}
			} else {
				return "发送失败！SAP未给出错误信息!";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
