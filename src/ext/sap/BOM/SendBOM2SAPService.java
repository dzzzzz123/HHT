package ext.sap.BOM;

import java.nio.charset.Charset;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import ext.ait.util.Result;

public class SendBOM2SAPService {

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

	@Bean
	public RestTemplate restTemplate() {
		RestTemplate template = new RestTemplate();
		// messageConverters是RestTemplate的一个final修饰的List类型的成员变量
		// messageConverters的第二个元素存储的是StringHttpMessageConverter类型的消息转换器
		// StringHttpMessageConverter的默认字符集是ISO-8859-1,在此处设置utf-8字符集避免产生乱码
		template.getMessageConverters().set(1, new StringHttpMessageConverter(Charset.forName("utf-8")));
		return template;
	}

	@Autowired
	private static RestTemplate restTemplate;

	public static String SendBOM2SAPUseUrl(String param) {
		String url = "";
		// 自定义请求头
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
