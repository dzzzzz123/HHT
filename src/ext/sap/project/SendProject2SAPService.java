package ext.sap.project;

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

public class SendProject2SAPService {
	private static PropertiesUtil properties = PropertiesUtil.getInstance("config.properties");

	/**
	 * 请求SAP接口传输项目数据主方法
	 * 
	 * @param project
	 */
	public static void sendProject2SAP(ProjectEntity project) {
		Result result = Result.success(project);
		String str = result.toString();
		String httpResult = sendProject2SAPUseUrl(str);
		System.out.println(str);
		System.out.println("httpResult" + httpResult);
	}

	/**
	 * 使用springframe的模板发送post请求传输数据给sap对应接口
	 * 
	 * @param param
	 * @return String
	 */
	public static String sendProject2SAPUseUrl(String param) {
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
