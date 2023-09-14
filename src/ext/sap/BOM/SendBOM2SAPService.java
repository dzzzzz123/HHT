package ext.sap.BOM;

import java.nio.charset.Charset;
import java.util.Collections;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import ext.ait.util.PropertiesUtil;
import ext.ait.util.Result;

public class SendBOM2SAPService {
	private static PropertiesUtil properties = PropertiesUtil.getInstance("config.properties");

	/**
	 * 请求SAP接口传输BOM结构数据主方法
	 * 
	 * @param BOMEntity bom
	 */
	public static void SendBOM2SAP(BOMEntity bom) {
		Result result = Result.success(bom);
		String str = result.toString();
		String httpResult = SendBOM2SAPUseUrl(str);
		System.out.println(str);
		System.out.println("httpResult" + httpResult);
	}

	public static String SendBOM2SAPUseUrl(String param) {
		String url = properties.getStr("sap.url");

		// 自定义请求头
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(Charset.forName("utf-8")));
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAcceptCharset(Collections.singletonList(Charset.forName("utf-8")));
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

		// 参数
		HttpEntity<String> entity = new HttpEntity<String>(param, headers);
		// POST方式请求
		ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
		if (responseEntity == null) {
			return null;
		}

		return responseEntity.getBody().toString();
	}

}
