package ext.HHT.SRM.acknowledgment;

import java.util.HashMap;
import java.util.Map;

import ext.ait.util.CommonUtil;
import ext.ait.util.PropertiesUtil;
import wt.part.WTPart;

public class Service {

	private static PropertiesUtil properties = PropertiesUtil.getInstance("config.properties");

	public static String process(WTPart part) {
		String result = "";
		String token = getSRMToken();
		return result;
	}

	private static String getSRMToken() {
		String result = "";
		String url = properties.getValueByKey("srm.token.url");
		String grant_type = properties.getValueByKey("srm.token.grant_type");
		String client_id = properties.getValueByKey("srm.token.client_id");
		String client_secret = properties.getValueByKey("srm.token.client_secret");
		String scope = properties.getValueByKey("srm.token.scope");
		Map<String, String> formData = new HashMap<>() {
			{
				put("grant_type", grant_type);
				put("client_id", client_id);
				put("client_secret", client_secret);
				put("scope", scope);
			}
		};
		String jsonResult = CommonUtil.requestInterface(url, "", "", formData, "POST", null);
		result = CommonUtil.getEntitiesFromJson(jsonResult, String.class, "access_token").get(0);
		return result;
	}
}
