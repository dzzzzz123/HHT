package ext.HHT.singleSignOn;

import java.sql.ResultSet;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import ext.ait.util.CommonUtil;

@RestController
public class UserAccess {

	@PostMapping("/userAccess")
	public ResponseEntity<String> handleRequest(@RequestBody String requestBody) throws Exception {
		JSONArray jsonArray = new JSONArray(requestBody.toString());
		System.out.println("J01_jsonArray" + jsonArray);
		JSONArray resultJson = new JSONArray();
		String password = "";
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			String name = (String) jsonObject.get("name");
			System.out.println("J01_name:" + name);
			password = getUserAccess(name);
			System.out.println("J01_password:" + password);
			JSONObject item = new JSONObject();
			item.put("password", password);
			System.out.println("J01_item:" + item);
			resultJson.put(item);
			System.out.println("J01_resultJson:" + resultJson);
		}
		JSONObject responseJson = new JSONObject();
		responseJson.put("data", resultJson);
		System.out.println("J01_responseJson:" + responseJson);
		System.out.println("ResponseEntity.ok(responseJson):" + ResponseEntity.ok(responseJson));
		return ResponseEntity.ok(password);

	}

	public static String getUserAccess(String name) throws Exception {
		String sql = "SELECT PASSWORD FROM TCUSERACCESS WHERE USERID = ?";
		ResultSet resultSet = CommonUtil.excuteSelect(sql, name);
		System.out.println("resultSet:" + resultSet);
		String password = "";
		while (resultSet.next()) {
			password = resultSet.getString("PASSWORD");
			System.out.println("password:" + password);
		}
		String decryptedString = AESUtil.Decryption(password);
		System.out.println("decryptedString:" + decryptedString);
		return decryptedString;

	}

}
