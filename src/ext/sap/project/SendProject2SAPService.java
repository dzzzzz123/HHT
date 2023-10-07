package ext.sap.project;

import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ext.ait.util.PropertiesUtil;
import wt.projmgmt.admin.Project2;

public class SendProject2SAPService {
	private static PropertiesUtil properties = PropertiesUtil.getInstance("config.properties");

	/**
	 * 从WTPart中获取需要的数据并组装为BOMEntity
	 * 
	 * @param WTPart part
	 * @return BOMEntity
	 */
	public static ProjectEntity getProjectEntity(Project2 project) {
		ProjectEntity projectEntity = new ProjectEntity();
		projectEntity.setProjectNumber(project.getProjectNumber());
		projectEntity.setProjectName(project.getName());
		projectEntity.setProjectCategory(project.getCategory().getDisplay());
		projectEntity.setProjectOwner(project.getOwner().getName());
		projectEntity.setProjectCreateStamp(project.getCreateTimestamp().toString());
		Timestamp endTime = project.getEstimatedEndDate();
		if (endTime == null) {
			projectEntity.setProjectEndStamp("无结束日期");
		} else {
			projectEntity.setProjectEndStamp(endTime.toString());
		}
		projectEntity.setFactoryCode(project.getBusinessUnit());
		projectEntity.setProjectDescription(project.getDescription());
		projectEntity.setFinishFlag(project.getContainerTeamManagedInfo().getState().getDisplay());
		return projectEntity;
	}

	/**
	 * 从entity中解析获取SAP需要的json
	 * @param entity
	 * @return
	 */
	public static String getJsonByEntity(ProjectEntity entity) {
		ObjectMapper objectMapper = new ObjectMapper();
		Map<String, Object> rootMap = new HashMap<>();
		Map<String, Object> nodeMap = new HashMap<>();

		nodeMap.put("PSPNR", entity.getProjectNumber());
		nodeMap.put("POST1", entity.getProjectName());
		nodeMap.put("PRART", entity.getProjectCategory());
		nodeMap.put("USR01", entity.getProjectOwner());
		nodeMap.put("PLFAZ", entity.getProjectCreateStamp());
		nodeMap.put("PLSEZ", entity.getProjectEndStamp());
		nodeMap.put("BUKRS", entity.getFactoryCode());
		nodeMap.put("POSID", entity.getProjectDescription());
		nodeMap.put("ZWJBS", entity.getFinishFlag());

		rootMap.put("IS_OBJECT", nodeMap);
		try {
			return objectMapper.writeValueAsString(rootMap);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 使用springframe的模板发送post请求传输数据给sap对应接口
	 * 
	 * @param param
	 * @return String
	 */
	public static String sendProject2SAPUseUrl(String param) {
		String url = properties.getValueByKey("sap.url");
		String username = properties.getValueByKey("sap.username");
		String password = properties.getValueByKey("sap.password");

		// 自定义请求头
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getInterceptors().add(new BasicAuthenticationInterceptor(username, password));
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
