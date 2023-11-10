package ext.sap.masterData;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ext.ait.util.ClassificationUtil;
import ext.ait.util.CommonUtil;
import ext.ait.util.VersionUtil;
import ext.sap.Config;
import wt.part.WTPart;

public class SendSAPService {

	public static SendSAPPartEntity SendSAPPart(WTPart part) {
		SendSAPPartEntity sapPartEntity = new SendSAPPartEntity();
		String HHT_Bonded = Config.getHHT_Bonded(part);
		String HHT_ClassificationCode = Config.getHHT_Classification(part);
		String HHT_INValue = Config.getHHT_INValue(part);

		String number = part.getNumber();
		String name = part.getName();
		String version = VersionUtil.getVersion(part);
		String unit = part.getDefaultUnit().toString();
		String state = part.getState().getState().getDisplay();
		String defaultTraceCode = part.getDefaultTraceCode().toString();

		boolean HHT_INValueBoolean = HHT_INValue.equals("是") ? true : false;
		String HHT_Classification = HHT_ClassificationCode;
		String PartType = mapClassificationToPartType(HHT_ClassificationCode, HHT_INValueBoolean);
		String HHT_ClassificationName = ClassificationUtil.getClassificationdDisPlayName(HHT_ClassificationCode);
		state = getSAPState(state);
		unit = Config.getValue(unit);
		HHT_Bonded = HHT_Bonded.equals("是") ? "Y" : "N";
		defaultTraceCode = defaultTraceCode.equals("S") ? "0001" : "";

		sapPartEntity.setPartType(PartType);
		sapPartEntity.setHHT_Classification(HHT_Classification);
		sapPartEntity.setNumber(number);
		sapPartEntity.setName(name);
		sapPartEntity.setRevision(version);
		sapPartEntity.setUnit(unit);
		sapPartEntity.setHHT_Bonded(HHT_Bonded);
		sapPartEntity.setState(state);
		sapPartEntity.setHHT_GrossWeight(Config.getHHT_GrossWeight(part));
		sapPartEntity.setHHT_NetWeight(Config.getHHT_NetWeight(part));
		sapPartEntity.setHHT_WeightUnit(Config.getHHT_WeightUnit(part));
		sapPartEntity.setHHT_Traffic(Config.getHHT_Traffic(part));
		sapPartEntity.setHHT_VolumeUnit(Config.getHHT_VolumeUnit(part));
		sapPartEntity.setHHT_Length(Config.getHHT_Length(part));
		sapPartEntity.setHHT_Width(Config.getHHT_Width(part));
		sapPartEntity.setHHT_Height(Config.getHHT_Height(part));
		sapPartEntity.setHHT_SizeUnits(Config.getHHT_SizeUnits(part));
		sapPartEntity.setHHT_ClassificationCode(HHT_ClassificationCode);
		sapPartEntity.setHHT_ClassificationName(HHT_ClassificationName);
		sapPartEntity.setHHT_ProductLineNumber(Config.getHHT_ProductLineNumber(part));
		sapPartEntity.setHHT_ProductLineName(Config.getHHT_ProductLineName(part));
		sapPartEntity.setHHT_Productdescription(Config.getHHT_Productdescription(part));
		sapPartEntity.setHHT_ModelSpecification(Config.getHHT_ModelSpecification(part));
		sapPartEntity.setHHT_CommodityName(Config.getHHT_CommodityName(part));
		sapPartEntity.setHHT_Brand(Config.getHHT_Brand(part));
		sapPartEntity.setHHT_Year(Config.getHHT_Year(part));
		sapPartEntity.setLargeScreenSize(Config.getLargeScreenSize(part));
		sapPartEntity.setHHT_FinishedSeries(Config.getHHT_FinishedSeries(part));
		sapPartEntity.setHHT_Industry(Config.getHHT_Industry(part));
		sapPartEntity.setHHT_ProductDevelopmentType(Config.getHHT_ProductDevelopmentType(part));
		sapPartEntity.setHHT_CustomizedProductIdentifier(Config.getHHT_CustomizedProductIdentifier(part));
		sapPartEntity.setHHT_SupplierSku(Config.getHHT_SupplierSku(part));
		sapPartEntity.setDefaultTraceCode(defaultTraceCode);
		sapPartEntity.setHHT_Factory(Config.getHHT_Factory(part));
		sapPartEntity.setHHT_Price(Config.getHHT_Price(part));
		sapPartEntity.setHHT_PriceUnit(Config.getHHT_PriceUnit(part));
		sapPartEntity.setHHT_INValue(HHT_INValue);
		sapPartEntity.setHHT_ProductNumber(Config.getHHT_ProductNumber(part));

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
		case "Obsolescence":
			return "Z1";
		default:
			return "D1";
		}
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
		isMatnrMap.put("AESZN", entity.getRevision());
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
		isMatnrMap.put("ZZCC", entity.getLargeScreenSize());
		isMatnrMap.put("ZZCPXL", entity.getHHT_FinishedSeries());
		isMatnrMap.put("ZZHY", entity.getHHT_Industry());
		isMatnrMap.put("ZZCPKFLX", entity.getHHT_ProductDevelopmentType());
		isMatnrMap.put("ZZDZCPBS", entity.getHHT_CustomizedProductIdentifier());
		isMatnrMap.put("ZZGYSHH", entity.getHHT_SupplierSku());
		isMatnrMap.put("SERNP", entity.getDefaultTraceCode());
		isMatnrMap.put("ZZCD", entity.getHHT_Factory());
		isMatnrMap.put("ZZJG", entity.getHHT_Price());
		isMatnrMap.put("PEINH", entity.getHHT_PriceUnit());

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
		String url = Config.getMasterDataUrl();
		String username = Config.getUsername();
		String password = Config.getPassword();
		return CommonUtil.requestInterface(url, username, password, masterDataJson, "POST", null);
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
