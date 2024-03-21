package ext.classification.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.Base64;
import java.util.Locale;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ptc.core.lwc.server.PersistableAdapter;
import com.ptc.core.meta.common.DisplayOperationIdentifier;

import ext.ait.util.PersistenceUtil;
import ext.ait.util.PropertiesUtil;
import wt.fc.Persistable;
import wt.part.WTPart;
import wt.session.SessionHelper;

public class DisplayByInternal {

	private static PropertiesUtil pUtil = PropertiesUtil.getInstance("auth.properties");

	// 获取部件的分类属性的枚举显示名称；入参：部件实例，部件IBA属性（分类属性）
	public static String getDisplayByInternal(WTPart part, String ibaName) {
		String ClassificationPartAttribute = "HHT_Classification";
		String displayName = "";
		try {
			Persistable targetObj = part;
			System.out.println("001-部件名称: " + part.getName());
			Locale loc = SessionHelper.getLocale();
			System.out.println("002-地址:" + loc.toString());
			String type = PersistenceUtil.getSubTypeInternal(targetObj);
			System.out.println("003-部件类型:" + type);
			PersistableAdapter persistableAdapter = new PersistableAdapter(targetObj, type, loc,
					new DisplayOperationIdentifier());

			System.out.println("004-IBA Key ：" + ibaName);
			persistableAdapter.load(new String[] { ClassificationPartAttribute, ibaName });

			System.out.println("0041-ok ：" + ibaName);
			String node = (String) persistableAdapter.get(ClassificationPartAttribute);
			System.out.println("005 node:" + node.toString());

			Object value = persistableAdapter.get(ibaName);
			System.out.println("006 value:" + value);

			String url = "https://hhplm.honghe-tech.com/Windchill/servlet/odata/v2/ClfStructure/GetEnumTypeConstraintOnClfAttributes(nodeInternalName='"
					+ node + "',clfStructureNameSpace='com.ptc.csm.default_clf_namespace',attributeInternalName='"
					+ ibaName + "')";
			String result = sendJsonGet(url);
			System.out.println("009 JSON字符串: " + result);
			JSONObject valueParamJsonObject = JSON.parseObject(result);
			String valueSql = valueParamJsonObject.getString("LegalValues");
			System.out.println("0091 枚举显示名称: " + valueSql);
			JSONArray resjson = JSONArray.parseArray(valueSql);
			for (int i = 0; i < resjson.size(); i++) {
				JSONObject jsonObj = resjson.getJSONObject(i);
				String og = jsonObj.getString("Value");
				System.out.println(og);
				if (og.equals(value)) {
					displayName = jsonObj.getString("Display");
					System.out.println(displayName);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return displayName;
	}

	public static String sendJsonGet(String url) {
		System.out.println("\n==============================POST请求开始==============================");
		PrintWriter out = null;
		BufferedReader in = null;
		String result = "";
		try {
			URL realUrl = new URL(url);
			// 打开和URL之间的连接
			URLConnection conn = realUrl.openConnection();
			// 设置通用的请求属性
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");

			// 构造Authorization标头
			String auth = pUtil.getValueByKey("auth");
			System.out.println("auth:" + auth);

			// Base64加密
			String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
			System.out.println("encodedAuth:" + encodedAuth);

			String authHeader = "Basic " + encodedAuth;
			conn.setRequestProperty("Authorization", authHeader);

			// 设置Content-type 为 application/json
			conn.addRequestProperty("Content-type", "application/json");

			conn.getInputStream();
			in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			System.out.println("发送 POST 请求出现异常！" + e);
			e.printStackTrace();
		}
		// 使用finally块来关闭输出流、输入流
		finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		System.out.println("url:" + url);
		System.out.println("POST请求结果：" + result);
		System.out.println("==============================POST请求结束==============================\n");
		return result;
	}

}
