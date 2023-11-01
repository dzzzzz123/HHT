package ext.ait.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ptc.core.lwc.common.view.EnumerationDefinitionReadView;
import com.ptc.core.lwc.common.view.EnumerationEntryReadView;
import com.ptc.core.lwc.server.LWCLocalizablePropertyValue;
import com.ptc.core.lwc.server.LWCTypeDefinition;
import com.ptc.core.lwc.server.TypeDefinitionServiceHelper;

import wt.change2.ChangeException2;
import wt.change2.ChangeHelper2;
import wt.change2.WTChangeOrder2;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.WTObject;
import wt.log4j.LogR;
import wt.maturity.MaturityHelper;
import wt.maturity.PromotionNotice;
import wt.method.MethodContext;
import wt.method.RemoteAccess;
import wt.org.OrganizationServicesHelper;
import wt.org.WTGroup;
import wt.org.WTUser;
import wt.pds.StatementSpec;
import wt.pom.WTConnection;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTException;

public class CommonUtil implements RemoteAccess {

	private static Logger LOGGER = LogR.getLogger(CommonUtil.class.getName());

	/**
	 * 转换中文格式，避免中文乱码
	 * 
	 * @param value
	 * @return
	 * @throws WTException
	 */
	public static String formatString(String value) throws WTException {
		try {
			if (value != null && value.trim().length() > 0) {
				byte[] tembyte = value.getBytes("gb2312");
				return new String(tembyte);
			} else {
				return value;
			}
		} catch (Exception e) {
			throw new WTException(e);
		}
	}

	/**
	 * 将一个实体类中的所有字段转换为Map
	 * 
	 * @param entity 实体类
	 * @return 输出的Map
	 */
	public static <T> Map<String, Object> entityToMap(T entity) {
		Map<String, Object> resultMap = new HashMap<>();

		// 使用反射获取类的所有字段
		Field[] fields = entity.getClass().getDeclaredFields();

		try {
			for (Field field : fields) {
				// 设置字段为可访问，以便获取私有字段的值
				field.setAccessible(true);

				// 将字段名和字段值添加到Map中
				resultMap.put(field.getName(), field.get(entity));
			}
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		return resultMap;
	}

	/**
	 * 根据用户id获取WTUser对象
	 * 
	 * @param id
	 * @return
	 * @throws WTException
	 */
	@SuppressWarnings("deprecation")
	public static WTUser getUserById(String id) throws WTException {
		WTUser user = null;
		try {
			if (id != null && id.trim().length() > 0) {
				QuerySpec qs = new QuerySpec(WTUser.class);
				SearchCondition sc1 = new SearchCondition(WTUser.class, WTUser.NAME, SearchCondition.EQUAL, id);
				SearchCondition sc2 = new SearchCondition(WTUser.class, WTUser.FULL_NAME, SearchCondition.EQUAL, id);
				qs.appendWhere(sc1);
				qs.appendOr();
				qs.appendWhere(sc2);
				LOGGER.debug("searchUsers sql where --->" + qs.getWhere());
				QueryResult qr = new QueryResult();
				qr = PersistenceHelper.manager.find(qs);
				while (qr.hasMoreElements()) {
					user = (WTUser) qr.nextElement();
				}
			}
			return user;
		} catch (Exception e) {
			throw new WTException(e);
		}
	}

	/**
	 * 通过类型的Key获取国际化的名称
	 * 
	 * @param key
	 * @return
	 * @throws WTException
	 */
	@SuppressWarnings("deprecation")
	public static String getTypeDisplayName(String key) throws WTException {
		String typeDisplayName = "";
		try {
			LWCTypeDefinition lwcType = null;
			QuerySpec lwcSpec = new QuerySpec(LWCTypeDefinition.class);
			lwcSpec.appendSearchCondition(
					new SearchCondition(LWCTypeDefinition.class, LWCTypeDefinition.NAME, SearchCondition.EQUAL, key));
			QueryResult qr = PersistenceHelper.manager.find(lwcSpec);
			while (qr.hasMoreElements()) {
				lwcType = (LWCTypeDefinition) qr.nextElement();
			}
			if (lwcType != null) {
				/**
				 * LWCLocalizablePropertyValue记录所有的国际化字段
				 */
				QuerySpec valueSpec = new QuerySpec(LWCLocalizablePropertyValue.class);
				valueSpec.appendSearchCondition(
						new SearchCondition(LWCLocalizablePropertyValue.class, "contextReference.key.id",
								SearchCondition.EQUAL, lwcType.getPersistInfo().getObjectIdentifier().getId()));
				valueSpec.appendAnd();
				valueSpec.appendSearchCondition(
						new SearchCondition(LWCLocalizablePropertyValue.class, "holderReference.key.id",
								SearchCondition.EQUAL, lwcType.getPersistInfo().getObjectIdentifier().getId()));
				QueryResult vqr = PersistenceHelper.manager.find(valueSpec);
				while (vqr.hasMoreElements()) {
					LWCLocalizablePropertyValue value = (LWCLocalizablePropertyValue) vqr.nextElement();
					typeDisplayName = value.getValue(Locale.CHINA);
					if (typeDisplayName == null || typeDisplayName.trim().length() == 0) {
						typeDisplayName = value.getValue();
					}
				}
			}
			return typeDisplayName;
		} catch (Exception e) {
			throw new WTException(e);
		}
	}

	/**
	 * 根据用户名称/用户全名获取用户对象
	 * 
	 * @param String
	 * @param boolean
	 * @return WTUser
	 */
	@SuppressWarnings("deprecation")
	public static WTUser getUserByName(String name, boolean IsFull) {
		String parm = IsFull ? WTUser.FULL_NAME : WTUser.NAME;
		if (StringUtils.isBlank(name)) {
			return null;
		}
		try {
			Enumeration enumUser = OrganizationServicesHelper.manager.findUser(parm, name.trim());
			while (enumUser.hasMoreElements()) {
				return (WTUser) enumUser.nextElement();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 根据组名获取组对象
	 * 
	 * @param String
	 * @return WTGroup
	 */
	public static WTGroup queryGroupByName(String groupName) {
		try {
			ChangeSession.administratorSession();
			if (StringUtils.isBlank(groupName)) {
				return null;
			}
			QuerySpec qs = new QuerySpec(WTGroup.class);
			SearchCondition sc = new SearchCondition(WTGroup.class, WTGroup.NAME, SearchCondition.EQUAL,
					groupName.trim());
			int[] index = { 0 };
			qs.appendWhere(sc, index);
			QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
			while (qr.hasMoreElements()) {
				WTGroup group = (WTGroup) qr.nextElement();
				return group;
			}
		} catch (WTException e) {
			e.printStackTrace();
		} finally {
			ChangeSession.goPreviousSession();
		}
		return null;
	}

	/**
	 * 根据全局枚举的内部名称获取其中所有的子枚举的内部名称与外部名称
	 * 
	 * @param String
	 * @return Map<String, String>
	 */
	public static Map<String, String> getEnumTypeByInternalName(String internalName) {
		Map<String, String> map = new HashMap<>();
		try {
			EnumerationDefinitionReadView edr = TypeDefinitionServiceHelper.service.getEnumDefView(internalName);
			if (edr != null) {
				Map<String, EnumerationEntryReadView> views = edr.getAllEnumerationEntries();
				Set<String> keysOfView = views.keySet();
				for (String key : keysOfView) {
					EnumerationEntryReadView view = views.get(key);
					String enumKey = view.getName();
					String enumName = view.getPropertyValueByName("displayName").getValue().toString();
					// 此方法判断枚举值是否在可用列表
					if (view.getPropertyValueByName("selectable").getValue().equals(true)) {
						map.put(enumKey, enumName);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * 连接Windchill数据库来运行自定义sql
	 * 
	 * @return WTConnection 数据库连接
	 * @throws Exception
	 */
	public static WTConnection getWTConnection() throws Exception {
		MethodContext methodcontext = MethodContext.getContext();
		WTConnection wtconnection = (WTConnection) methodcontext.getConnection();
		return wtconnection;
	}

	/**
	 * 执行查询的SQL语句并返回结果 SQL示例：SELECT WTPARTNUMBER FROM WTPARTMASTER WHERE
	 * WTPARTNUMBER LIKE ?
	 * 
	 * @param sql    SQL语句
	 * @param params 参数集
	 * @return ResultSet 返回结果集
	 */
	public static ResultSet excuteSelect(String sql, String... params) {
		try {
			WTConnection connection = CommonUtil.getWTConnection();
			PreparedStatement statement = connection.prepareStatement(sql);
			for (int i = 0; i < params.length; i++) {
				statement.setString(i + 1, params[i]);
			}

			// 输出当前执行查询操作的SQL语句
			String fullSql = sql;
			for (String param : params) {
				param = param.length() > 250 ? "此属性太长，不显示内容" : param;
				fullSql = fullSql.replaceFirst("\\?", "'" + param + "'");
			}
			System.out.println("--------当前执行查询操作的SQL语句为--------");
			System.out.println(fullSql);

			ResultSet resultSet = statement.executeQuery();
			return resultSet;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 执行更新的SQL语句并返回被更新的条数 SQL示例：UPDATE WTPARTMASTER SET WTPARTNUMBER = ? WHERE
	 * IDA2A2 = ?
	 * 
	 * @param sql    SQL语句
	 * @param params 参数集
	 * @return int 数据库表被影响的行数
	 */
	public static int excuteUpdate(String sql, String... params) {
		try {
			WTConnection connection = CommonUtil.getWTConnection();
			PreparedStatement statement = connection.prepareStatement(sql);
			for (int i = 0; i < params.length; i++) {
				statement.setString(i + 1, params[i]);
			}

			// 输出当前执行更新操作的SQL语句
			String fullSql = sql;
			for (String param : params) {
				param = param.length() > 250 ? "此属性太长，不显示内容" : param;
				fullSql = fullSql.replaceFirst("\\?", "'" + param + "'");
			}
			System.out.println("--------当前执行更新操作的SQL语句为--------");
			System.out.println(fullSql);

			return statement.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 执行插入的SQL语句
	 * 
	 * @param sql    被执行的SQL语句
	 * @param params SQL语句中的参数
	 * @return 是否执行成功
	 */
	public static int excuteInsert(String sql, String... params) {
		try {
			WTConnection connection = CommonUtil.getWTConnection();
			PreparedStatement statement = connection.prepareStatement(sql);
			for (int i = 0; i < params.length; i++) {
				statement.setString(i + 1, params[i]);
			}

			// 输出当前执行更新操作的SQL语句
			String fullSql = sql;
			for (String param : params) {
				param = param.length() > 250 ? "此属性太长，不显示内容" : param;
				fullSql = fullSql.replaceFirst("\\?", "\"" + param + "\"");
			}
			System.out.println("--------当前执行插入操作的SQL语句为--------");
			System.out.println(fullSql);
			return statement.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 携带信息并用POST请求外部系统（如SAP，OA）中的某个接口 存在账户和密码时则设置验证否则不设置 map为添加到Headers上的内容，无则填null
	 * 
	 * @param url      访问目标接口的URL
	 * @param username 访问目标接口需要验证的用户名
	 * @param password 访问目标接口需要验证的密码
	 * @param json     携带的json信息
	 * @param method   请求的方式 GET/POST
	 * @param map      请求头中需要添加的信息,没有填null
	 * @return 返回的json信息
	 */
	public static String requestInterface(String url, String username, String password, String json, String method,
			HashMap<String, String> map) {
		System.out.println("--------当前执行的请求接口的参数列表--------");
		System.out.println("URL: " + url);
		System.out.println("USERNAME: " + username + " PASSWORD:" + password);
		System.out.println("JSON: " + processJson(json));
		System.out.println("METHOD: " + method);
		System.out.println("HEADERS: ");

		// 自定义请求头
		RestTemplate restTemplate = new RestTemplate();
		if (StringUtils.isNotBlank(password) && StringUtils.isNotBlank(username)) {
			restTemplate.getInterceptors().add(new BasicAuthenticationInterceptor(username, password));
		}
		restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(Charset.forName("utf-8")));
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAcceptCharset(Collections.singletonList(Charset.forName("utf-8")));
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		if (map != null) {
			Set<String> set = map.keySet();
			if (set.size() > 0) {
				for (String key : set) {
					String value = map.get(key);
					System.out.println("key:" + key + " value:" + map.get(key));
					headers.add(key, value);
				}
			}
		}
		// 参数
		HttpEntity<String> entity = new HttpEntity<String>(json, headers);
		ResponseEntity<String> responseEntity = method.equalsIgnoreCase("GET")
				? restTemplate.exchange(url, HttpMethod.GET, entity, String.class)
				: restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

		if (responseEntity == null) {
			return null;
		}
		String resultJson = responseEntity.getBody().toString();
		System.out.println("RESULTJSON: " + resultJson);
		return resultJson;
	}

	private static String processJson(String json) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			ObjectNode rootNode = objectMapper.createObjectNode();

			JsonNode originalRootNode = objectMapper.readTree(json);
			processJsonNode(rootNode, originalRootNode);

			return objectMapper.writeValueAsString(rootNode);
		} catch (Exception e) {
			e.printStackTrace();
			return json; // Return the original JSON in case of an error
		}
	}

	private static void processJsonNode(ObjectNode targetNode, JsonNode sourceNode) {
		if (sourceNode.isObject()) {
			Iterator<Map.Entry<String, JsonNode>> fields = sourceNode.fields();
			while (fields.hasNext()) {
				Map.Entry<String, JsonNode> entry = fields.next();
				String fieldName = entry.getKey();
				JsonNode fieldNode = entry.getValue();
				if (fieldNode.isTextual()) {
					String fieldValue = fieldNode.asText();
					if (fieldValue.length() > 250) {
						targetNode.put(fieldName, "此属性太长，不显示内容");
					} else {
						targetNode.set(fieldName, fieldNode);
					}
				} else if (fieldNode.isObject() || fieldNode.isArray()) {
					ObjectNode childTargetNode = targetNode.putObject(fieldName);
					processJsonNode(childTargetNode, fieldNode);
				}
			}
		} else if (sourceNode.isArray()) {
			// Handle arrays if necessary
		}
	}

	/**
	 * 获取CSRF_NONCE（token）
	 * 
	 * @return CSRF_NONCE
	 */
	public static String getCSRF_NONCE(String url) {
		String result = CommonUtil.requestInterface(url, "wcadmin", "wcadmin", "", "GET", null);
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			JsonNode rootNode = objectMapper.readTree(result);
			JsonNode esMessgNode = rootNode.get("NonceValue");
			return esMessgNode.asText();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 从request中获取字节流的信息将其中的json转换为实体类列表
	 * 
	 * @param <T>            实体类
	 * @param request        传递的请求参数
	 * @param clazz          实体类类型
	 * @param rootNodeString 是否有根节点
	 * @return T 实体类列表
	 */
	public static <T> List<T> getEntitiesFromRequest(HttpServletRequest request, Class<T> clazz,
			String rootNodeString) {
		try {
			BufferedReader reader = request.getReader();
			StringBuilder jsonInput = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				jsonInput.append(line);
			}

			return getEntitiesFromJson(jsonInput.toString(), clazz, rootNodeString);
		} catch (IOException e) {
			e.printStackTrace();
			return Collections.emptyList();
		}
	}

	/**
	 * 将json转换为实体类列表
	 * 
	 * @param <T>            实体类
	 * @param json           需要转换的json
	 * @param clazz          实体类类型
	 * @param rootNodeString 是否有根节点
	 * @return T 实体类列表
	 */
	public static <T> List<T> getEntitiesFromJson(String json, Class<T> clazz, String rootNodeString) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode rootNode = objectMapper.readTree(json.toString());
			List<T> entities = new ArrayList<>();
			// 如果没有指定根节点字符串，则直接尝试解析为实体对象
			// 如果指定了根节点字符串，则尝试从根节点中获取指定的节点
			rootNode = StringUtils.isNotBlank(rootNodeString) ? rootNode.get(rootNodeString) : rootNode;

			if (rootNode.isArray()) {
				for (JsonNode node : rootNode) {
					T entity = objectMapper.treeToValue(node, clazz);
					entities.add(entity);
				}
			} else {
				T entity = objectMapper.treeToValue(rootNode, clazz);
				entities.add(entity);
			}
			return entities;
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return Collections.emptyList();
	}

	/**
	 * 将实体类转换为json
	 * 
	 * @param data 可以为entity，map，Object几乎任何可以转换为json的类型
	 * @return json
	 */
	public static <T> String getJsonFromObject(T data) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			return objectMapper.writeValueAsString(data);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 将传入的PBO解析为方便处理的List<T>
	 * 
	 * @param <T>        泛型
	 * @param obj        PBO
	 * @param targetType 结果的类型
	 * @return
	 */
	public static <T> List<T> getListFromPBO(WTObject obj, Class<T> targetType) {
		List<T> list = new ArrayList<>();
		try {
			if (targetType.isInstance(obj)) {
				list.add(targetType.cast(obj));
			} else if (obj instanceof PromotionNotice) {
				PromotionNotice pn = (PromotionNotice) obj;
				QueryResult qr = MaturityHelper.service.getPromotionTargets(pn);
				while (qr.hasMoreElements()) {
					Object object = qr.nextElement();
					if (targetType.isInstance(object)) {
						list.add(targetType.cast(object));
					}
				}
			} else if (obj instanceof WTChangeOrder2) {
				WTChangeOrder2 co = (WTChangeOrder2) obj;
				QueryResult qr = ChangeHelper2.service.getChangeablesAfter(co);
				while (qr.hasMoreElements()) {
					Object object = qr.nextElement();
					if (targetType.isInstance(object)) {
						list.add(targetType.cast(object));
					}
				}
			} else {
				System.out.println("数据不正确!");
			}
		} catch (ChangeException2 e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}
		return list;
	}

}
