package ext.sap.supply;

import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ext.ait.util.CommonUtil;
import ext.ait.util.PropertiesUtil;

public class SupplyChainService {

	private static PropertiesUtil properties = PropertiesUtil.getInstance("config.properties");

	public static SupplyEntity getSupplyChainFromSAP(String partNumber) {
		String url = properties.getValueByKey("sap.url");
		String username = properties.getValueByKey("sap.username");
		String password = properties.getValueByKey("sap.password");
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			String json = objectMapper.writeValueAsString(new HashMap<String, String>() {
				{
					put("I_MATNR", partNumber);
				}
			});
			String resultJson = CommonUtil.requestInterface(url, username, password, json, "POST", null);
			List<SupplyEntity> entities = CommonUtil.getEntitiesFromJson(resultJson, SupplyEntity.class, "IS_MRP1");
			return entities.get(0);
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}
}
