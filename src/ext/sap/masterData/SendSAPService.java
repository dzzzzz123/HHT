package ext.sap.masterData;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

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
		// 构建实体类
		SendSAPPartEntity sapPartEntity = new SendSAPPartEntity();
		// 获取WTPart IBA属性
		String HHT_Bonded = Config.getHHT_Bonded(part);
		String HHT_ClassificationCode = Config.getHHT_Classification(part);
		String HHT_Classification = HHT_ClassificationCode;
		String HHT_INValue = Config.getHHT_INValue(part);
		String HHT_SerialNumber = Config.getHHT_SerialNumber(part);
		String NonbondedNumber = Config.getNonbondedNumber(part);
		String fullPath = ClassificationUtil.getFullPathByInternal(HHT_Classification);
		fullPath = processClassPath(fullPath);
		String partPath = processPartPath(fullPath);

		// 获取WTPart基础属性
		String number = part.getNumber();
		String name = part.getName();
		String version = VersionUtil.getVersion(part);
		String unit = Config.getValue(part.getDefaultUnit().toString());
		String state = part.getState().getState().getDisplay();
		String source = part.getSource().getDisplay(Locale.CHINA);

		// 根据条件修改获取的属性以满足传入SAP的需求
		boolean HHT_INValueBoolean = HHT_INValue.equals("True") || HHT_INValue.equals("真") || HHT_INValue.equals("是")
				? true
				: false;
		String PartType = mapClassificationToPartType(HHT_ClassificationCode, HHT_INValueBoolean);
		String HHT_ClassificationName = ClassificationUtil.getClassificationdDisPlayName(HHT_ClassificationCode);
		version = StringUtils.substring(version, 0, 1);
		state = getSAPState(state);
		HHT_Bonded = processHHT_Bonded(HHT_Bonded);
		NonbondedNumber = StringUtils.isBlank(NonbondedNumber) ? "" : NonbondedNumber;
		HHT_SerialNumber = HHT_SerialNumber.equals("True") || HHT_SerialNumber.equals("是")
				|| HHT_SerialNumber.equals("真") ? "0001" : "";
		HHT_Classification = processClassification(HHT_Classification, number);
		String Year = ClassificationUtil.getDisplayByInternal(Config.getHHT_Year(part));
		String LargeScreenSize = ClassificationUtil.getDisplayByInternal(Config.getLargeScreenSize(part));
		String HHT_PDOwnership = ClassificationUtil.getDisplayByInternal(Config.getHHT_PDOwnership(part));

		sapPartEntity.setPartType(PartType);
		sapPartEntity.setHHT_Classification(HHT_Classification);
		sapPartEntity.setNumber(number);
		sapPartEntity.setName(name);
		sapPartEntity.setRevision(version);
		sapPartEntity.setUnit(unit);
		sapPartEntity.setHHT_Bonded(HHT_Bonded);
		sapPartEntity.setNonbondedNumber(NonbondedNumber);
		sapPartEntity.setState(state);
		sapPartEntity.setHHT_Year(Year);
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
		sapPartEntity.setLargeScreenSize(LargeScreenSize);
		sapPartEntity.setHHT_FinishedSeries(Config.getHHT_FinishedSeries(part));
		sapPartEntity.setHHT_Industry(Config.getHHT_Industry(part));
		sapPartEntity.setHHT_ProductDevelopmentType(Config.getHHT_ProductDevelopmentType(part));
		sapPartEntity.setHHT_CustomizedProductIdentifier(Config.getHHT_CustomizedProductIdentifier(part));
		sapPartEntity.setHHT_SupplierSku(Config.getHHT_SupplierSku(part));
		sapPartEntity.setHHT_SerialNumber(HHT_SerialNumber);
		sapPartEntity.setHHT_Factory(Config.getHHT_Factory(part));
		sapPartEntity.setHHT_Price(Config.getHHT_Price(part));
		sapPartEntity.setHHT_PriceUnit(Config.getHHT_PriceUnit(part));
		sapPartEntity.setHHT_INValue(HHT_INValue);
		sapPartEntity.setHHT_ProductNumber(Config.getHHT_ProductNumber(part));
		sapPartEntity.setClassDescription(fullPath);
		sapPartEntity.setClassPartDescription(partPath);
		sapPartEntity.setHHT_SapMark(Config.getHHT_SapMark(part));
		sapPartEntity.setSource(source);
		sapPartEntity.setHHT_LongtDescription(Config.getHHT_LongtDescription(part));
		sapPartEntity.setHHT_PDOwnership(HHT_PDOwnership);

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
			// return "M1";
			return "P1";
		case "Obsolescence":
			return "Z1";
		default:
			return "D1";
		}
	}

	/**
	 * PLM处理物料组逻辑
	 * 
	 * @param hHT_Classification
	 * @param number
	 * @return
	 */
	private static String processClassification(String hHT_Classification, String number) {
		if (number.startsWith("6") && hHT_Classification.startsWith("5")) {
			return "6" + hHT_Classification.substring(1);
		} else {
			return hHT_Classification;
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
			case '4':
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
	 * 处理分类组描述
	 * 
	 * @param fullPath
	 * @return
	 */
	private static String processClassPath(String fullPath) {
		fullPath = fullPath.substring(1);
		fullPath = fullPath.replaceAll("\\\\", "/");
		String[] parts = fullPath.split("/");
		for (int i = 0; i < parts.length; i++) {
			parts[i] = parts[i].replaceAll(".*_", "");
		}
		return String.join("/", parts);
	}

	/**
	 * 获取部分的分类组描述
	 * 
	 * @param fullPath
	 * @return
	 */
	private static String processPartPath(String fullPath) {
		String[] parts = fullPath.split("/");
		String newPartPath = "";
		for (int i = 1; i < parts.length; i++) {
			newPartPath += parts[i];
			if (i != parts.length - 1) {
				newPartPath += "/";
			}
		}
		return newPartPath;
	}

	/**
	 * 处理关于是否保税的内容
	 * 
	 * @param HHT_Bonded
	 * @return
	 */
	private static String processHHT_Bonded(String HHT_Bonded) {
		if (StringUtils.isBlank(HHT_Bonded)) {
			return "";
		} else {
			return HHT_Bonded.equals("True") || HHT_Bonded.equals("是") || HHT_Bonded.equals("真") ? "Y" : "N";
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
		isMatnrMap.put("MAKTX1", entity.getHHT_LongtDescription());
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
		isMatnrMap.put("SERNP", entity.getHHT_SerialNumber());
		isMatnrMap.put("ZZCD", entity.getHHT_Factory());
		isMatnrMap.put("ZZJG", entity.getHHT_Price());
		isMatnrMap.put("PEINH", entity.getHHT_PriceUnit());
		isMatnrMap.put("PEINH", entity.getHHT_PriceUnit());
		isMatnrMap.put("ZZJHBS", entity.getHHT_Bonded());
		isMatnrMap.put("ZZFBSLH", entity.getNonbondedNumber());
		isMatnrMap.put("WGBEZ", entity.getClassDescription());
		isMatnrMap.put("WGBEZ60", entity.getClassPartDescription());
		isMatnrMap.put("ZWLBS", entity.getHHT_SapMark());
		isMatnrMap.put("ZCGLX", entity.getSource());
		isMatnrMap.put("ZZCPGS", entity.getHHT_PDOwnership());

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

	/**
	 * 从SAP返回的信息中获取需要的信息
	 * 
	 * @param json
	 * @return
	 */
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
